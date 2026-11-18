package br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.service;

import org.springframework.stereotype.Service;

import br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.domain.ContaOtimista;
import br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.repository.ContaOtimistaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service 
@AllArgsConstructor 
public class ContaOtimistaService {

    private final ContaOtimistaRepository contaOtimistaRepository;

    @Transactional 
    public void deposita(String numero, Double valor) {
        ContaOtimista conta = contaOtimistaRepository.
                               findById(numero).
                               orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        conta.setSaldo(conta.getSaldo() + valor);

        contaOtimistaRepository.save(conta);
    }

}
