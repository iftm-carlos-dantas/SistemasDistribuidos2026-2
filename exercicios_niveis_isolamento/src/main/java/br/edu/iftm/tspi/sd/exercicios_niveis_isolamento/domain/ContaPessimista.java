package br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Table(name="tb_conta_pessimista")
@Data 
public class ContaPessimista {

    @Id 
    @Column(name = "num_conta")
    private String numero;

    @Column (name="vlr_saldo")
    private Double saldo;

}
