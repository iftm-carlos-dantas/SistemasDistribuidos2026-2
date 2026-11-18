package br.edu.iftm.tspi.sd.mensageria.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.iftm.tspi.sd.mensageria.dto.PedidoDTO;
import lombok.AllArgsConstructor;

@RestController 
@RequestMapping("/pedidos")
@AllArgsConstructor 
public class PedidoController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping 
    public String criarPedido(@RequestBody PedidoDTO pedido) {

        rabbitTemplate.convertAndSend(
            "",
            "notificacoes",
            pedido.getEmail()
        );

        return "Pedido "+pedido.getId()+" recebido!";
    }
}
