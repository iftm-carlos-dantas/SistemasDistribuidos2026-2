package br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.domain.ContaPessimista;
import jakarta.persistence.LockModeType;

public interface ContaPessimistaRepository extends JpaRepository<ContaPessimista, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ContaPessimista c WHERE c.numero = :numero")
    ContaPessimista findByNumeroWithLock(@Param("numero") String numero);

}
