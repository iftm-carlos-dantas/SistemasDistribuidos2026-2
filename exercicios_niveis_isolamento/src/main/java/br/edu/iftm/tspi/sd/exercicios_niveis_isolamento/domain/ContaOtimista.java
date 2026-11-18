package br.edu.iftm.tspi.sd.exercicios_niveis_isolamento.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Entity 
@Table(name="tb_conta_otimista")
@Data 
public class ContaOtimista {

    @Id 
    @Column(name = "num_conta")
    private String numero;

    @Column (name="vlr_saldo")
    private Double saldo;

    @Version
    @Column (name="cod_versao")
    private Integer versao;
}
