package br.edu.iftm.tspi.sd.mensageria.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    @RabbitListener(queues = "notificacoes")
    public void receberMensagem(String email) {

        System.out.println("Enviando e-mail para: " + email);
        System.out.println("E-mail enviado!");
    }
}