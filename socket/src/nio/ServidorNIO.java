package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ServidorNIO {

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

                if (key.isAcceptable()) {
                    aceitarCliente(key, selector);

                } else if (key.isReadable()) {
                    lerMensagem(key);
                }
            }
        }
    }

    private static ServerSocketChannel criarServidor() throws IOException {
        ServerSocketChannel servidor = ServerSocketChannel.open();
        servidor.bind(
                new InetSocketAddress(PORTA)
        );
        servidor.configureBlocking(false);
        return servidor;
    }

    private static void aceitarCliente(
            SelectionKey key,
            Selector selector) throws IOException {

        ServerSocketChannel servidor =
                (ServerSocketChannel) key.channel();

        SocketChannel cliente = servidor.accept();

        cliente.configureBlocking(false);

        cliente.register(
                selector,
                SelectionKey.OP_READ
        );

        System.out.println("Cliente conectado!");
    }

    private static void lerMensagem(SelectionKey key)
            throws IOException {

        SocketChannel cliente =
                (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesLidos = cliente.read(buffer);

        if (bytesLidos == -1) {
            desconectarCliente(key);
            return;
        }

        buffer.flip();

        String mensagem =
                StandardCharsets.UTF_8
                        .decode(buffer)
                        .toString();

        System.out.println("Recebido: " + mensagem);

        if (mensagem.trim().equals("<fim>")) {
            desconectarCliente(key);
            return;
        }

        enviarMensagem(cliente, mensagem);
    }

    private static void enviarMensagem(
            SocketChannel cliente,
            String mensagem) throws IOException {

        ByteBuffer buffer =
                StandardCharsets.UTF_8.encode(mensagem);

        cliente.write(buffer);
    }

    private static void desconectarCliente(
            SelectionKey key) throws IOException {

        SocketChannel cliente =
                (SocketChannel) key.channel();

        key.cancel();
        cliente.close();

        System.out.println("Cliente desconectado.");
    }
}