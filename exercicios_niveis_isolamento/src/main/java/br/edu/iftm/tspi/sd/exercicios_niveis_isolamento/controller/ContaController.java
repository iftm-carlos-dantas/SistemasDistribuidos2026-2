package br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.dto.ContaRequest;
import br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.service.ContaOtimistaService;
import br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.service.ContaPessimistaService;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller 
@RequestMapping("/conta")
@AllArgsConstructor 
public class ContaController {

    private final ContaOtimistaService contaOtimistaService;

    private final ContaPessimistaService contaPessimistaService;

    @PostMapping("{numero}/depositaPessimista")
    public ResponseEntity<String> depositaPessimista(@PathVariable String numero,
        @RequestBody ContaRequest contaRequest) {
        contaPessimistaService.deposita(numero, contaRequest.getValor());
        return ResponseEntity.ok("Depósito pessimista realizado com sucesso");
    }

    @PostMapping("{numero}/depositaOtimista")
    public ResponseEntity<String> depositaOtimista(@PathVariable String numero,
        @RequestBody ContaRequest contaRequest) {
        contaOtimistaService.deposita(numero, contaRequest.getValor());
        return ResponseEntity.ok("Depósito otimista realizado com sucesso");
    }
    

}
