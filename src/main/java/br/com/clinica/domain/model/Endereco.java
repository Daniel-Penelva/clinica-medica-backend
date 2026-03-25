package br.com.clinica.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Objeto de valor embutido na entidade Paciente.
 * Não possui tabela própria - seus campos ficam na tabela "pacientes".
 * Acesso: paciente.getEndereco().getlogradouro()
 * 
 * Por que @Embeddable? Porque é um objeto de valor, não uma entidade. Ele é parte da entidade Paciente, não tem identidade própria.
 * O endereco não faz sentido existir sem um paciente, ou seja, não faz sentido existir sozinho - sempre pertence a um paciente.
 * Com @Embeddable os campos ficam na tabela "pacientes", não tem tabela própria.
 * Deixa o código organizado em objeto + banco sem tabela desnecessária. 
 * O endereço é um conceito importante, mas não precisa de uma tabela própria, pois não tem identidade própria.
*/

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endereco {

    @Column(length = 150)
    private String logradouro;

    @Column(length = 10)
    private String numero;

    @Column(length = 80)
    private String complemento;

    @Column(length = 80)
    private String bairro;

    @Column(length = 80)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 8)
    private String cep;

}
