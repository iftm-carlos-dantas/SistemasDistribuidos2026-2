package nio_objeto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import objeto.Pedido;

public class ServidorNIObjeto {

    private static final int PORTA = 2001;

    public static void main(String[] args) throws IOException {

        ServerSocketChannel servidor = criarServidor();
        Selector selector = Selector.open();

        servidor.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Servidor iniciado na porta " + PORTA);

        while (true) {

            selector.select();

            Iterator<SelectionKey> eventos =
                    selector.selectedKeys().iterator();

            while (eventos.hasNext()) {

                SelectionKey key = eventos.next();
                eventos.remove();

                try {

                    if (key.isAcceptable()) {

                        aceitarCliente(key, selector);

                    } else if (key.isReadable()) {

                        lerMensagem(key);
                    }

                } catch (IOException e) {

                    System.err.println(
                            "Erro na comunicação: "
                            + e.getMessage());

                    desconectarCliente(key);
                }
            }
        }
    }

    // =========================================================
    // CRIA O SERVIDOR
    // =========================================================

    private static ServerSocketChannel criarServidor()
            throws IOException {

        ServerSocketChannel servidor =
                ServerSocketChannel.open();

        servidor.bind(new InetSocketAddress(PORTA));

        servidor.configureBlocking(false);

        return servidor;
    }

    // =========================================================
    // ACEITA CLIENTE
    // =========================================================

    private static void aceitarCliente(
            SelectionKey key,
            Selector selector) throws IOException {

        ServerSocketChannel servidor =
                (ServerSocketChannel) key.channel();

        SocketChannel cliente = servidor.accept();

        if (cliente == null) {
            return;
        }

        cliente.configureBlocking(false);

        EstadoCliente estado = new EstadoCliente();

        cliente.register(
                selector,
                SelectionKey.OP_READ,
                estado);

        /*
         * O cliente cria um ObjectInputStream logo após
         * conectar.
         *
         * O construtor dele espera o header de um
         * ObjectOutputStream.
         *
         * Portanto, enviamos esse header imediatamente.
         */
        enviarHeader(cliente);

        System.out.println("Cliente conectado!");
    }

    // =========================================================
    // ENVIA O HEADER QUE O ObjectInputStream DO CLIENTE ESPERA
    // =========================================================

    private static void enviarHeader(SocketChannel cliente)
            throws IOException {

        ByteArrayOutputStream bytes =
                new ByteArrayOutputStream();

        ObjectOutputStream saida =
                new ObjectOutputStream(bytes);

        saida.flush();

        ByteBuffer buffer =
                ByteBuffer.wrap(bytes.toByteArray());

        while (buffer.hasRemaining()) {
            cliente.write(buffer);
        }
    }

    // =========================================================
    // LEITURA
    // =========================================================

    private static void lerMensagem(SelectionKey key)
            throws IOException {

        SocketChannel cliente =
                (SocketChannel) key.channel();

        EstadoCliente estado =
                (EstadoCliente) key.attachment();

        ByteBuffer buffer =
                ByteBuffer.allocate(1024);

        int bytesLidos =
                cliente.read(buffer);

        // Cliente encerrou a conexão
        if (bytesLidos == -1) {

            desconectarCliente(key);
            return;
        }

        if (bytesLidos == 0) {
            return;
        }

        buffer.flip();

        byte[] bytes =
                new byte[buffer.remaining()];

        buffer.get(bytes);

        /*
         * Guarda todos os bytes recebidos deste cliente.
         */
        estado.dados.write(bytes);

        /*
         * Agora verificamos se já existe um Pedido
         * completo dentro desses bytes.
         */
        processarObjetos(cliente, estado);
    }

    // =========================================================
    // PROCESSA OS OBJETOS RECEBIDOS
    // =========================================================

    private static void processarObjetos(
            SocketChannel cliente,
            EstadoCliente estado) {

        try {

            byte[] bytes =
                    estado.dados.toByteArray();

            ByteArrayInputStream entradaBytes =
                    new ByteArrayInputStream(bytes);

            ObjectInputStream entrada =
                    new ObjectInputStream(entradaBytes);

            /*
             * Como reconstruímos o ObjectInputStream desde
             * o começo, precisamos pular os objetos que
             * já foram processados anteriormente.
             */
            for (int i = 0;
                 i < estado.objetosProcessados;
                 i++) {

                entrada.readObject();
            }

            /*
             * Tenta ler o próximo objeto.
             */
            Object objeto =
                    entrada.readObject();

            if (objeto instanceof Pedido pedido) {

                estado.objetosProcessados++;

                System.out.println(
                        "Pedido recebido: " + pedido);

                enviarResposta(cliente, pedido);
            }

        } catch (IOException e) {

            /*
             * Isso é normal no NIO.
             *
             * Significa que provavelmente ainda não
             * chegaram todos os bytes do objeto.
             *
             * Esperamos o próximo OP_READ.
             */

        } catch (ClassNotFoundException e) {

            System.err.println(
                    "Classe desconhecida: "
                    + e.getMessage());
        }
    }

    // =========================================================
    // ENVIA RESPOSTA
    // =========================================================

    private static void enviarResposta(
            SocketChannel cliente,
            Pedido pedido) {

        try {

            /*
             * Serializa o objeto.
             */
            ByteArrayOutputStream bytes =
                    new ByteArrayOutputStream();

            /*
             * Precisamos impedir que um novo header seja
             * enviado, porque o cliente já recebeu o header
             * quando conectou.
             */
            ObjectOutputStream saida =
                    new ObjectOutputStream(bytes) {

                        @Override
                        protected void writeStreamHeader()
                                throws IOException {

                            // Não escreve outro header
                        }
                    };

            saida.writeObject(pedido);
            saida.flush();

            ByteBuffer buffer =
                    ByteBuffer.wrap(
                            bytes.toByteArray());

            while (buffer.hasRemaining()) {
                cliente.write(buffer);
            }

        } catch (IOException e) {

            System.err.println(
                    "Erro ao enviar resposta: "
                    + e.getMessage());
        }
    }

    // =========================================================
    // DESCONECTA CLIENTE
    // =========================================================

    private static void desconectarCliente(
            SelectionKey key) {

        try {

            key.cancel();

            key.channel().close();

            System.out.println(
                    "Cliente desconectado.");

        } catch (IOException e) {

            System.err.println(
                    "Erro ao desconectar cliente.");
        }
    }

    // =========================================================
    // ESTADO DE CADA CLIENTE
    // =========================================================

    private static class EstadoCliente {

        /*
         * Guarda todo o fluxo enviado pelo
         * ObjectOutputStream do cliente.
         */
        ByteArrayOutputStream dados =
                new ByteArrayOutputStream();

        /*
         * Quantos objetos deste fluxo já processamos.
         */
        int objetosProcessados = 0;
    }
}