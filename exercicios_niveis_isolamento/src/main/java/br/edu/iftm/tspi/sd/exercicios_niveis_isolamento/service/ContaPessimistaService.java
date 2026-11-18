package br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.service;

import org.springframework.stereotype.Service;

import br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.domain.ContaOtimista;
import br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.domain.ContaPessimista;
import br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.repository.ContaOtimistaRepository;
import br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.repository.ContaPessimistaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service 
@AllArgsConstructor 
public class ContaPessimistaService {

    private final ContaPessimistaRepository contaPessimistaRepository;

    @Transactional 
    public void deposita(String numero, Double valor) {
        ContaPessimista conta = contaPessimistaRepository
                            .findByNumeroWithLock(numero);
        
          try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        conta.setSaldo(conta.getSaldo() + valor);

        contaPessimistaRepository.save(conta);
    }

}
