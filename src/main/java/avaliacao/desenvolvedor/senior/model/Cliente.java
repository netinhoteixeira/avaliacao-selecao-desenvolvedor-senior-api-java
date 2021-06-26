/*
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org/>
*/
package avaliacao.desenvolvedor.senior.model;

import br.com.safeguard.constraint.annotations.Verify;
import br.com.safeguard.types.ParametroTipo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cliente")
// DONE: Estava causando StackOverflow
// @Data
public class Cliente {

    @Id
    @GeneratedValue(generator = "seq_cliente", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "seq_cliente")
    @Column(name = "id", unique = true, nullable = false)
    @Setter
    @Getter
    private Long id;

    @Basic(optional = false)
    @Column(name = "nome", nullable = false, length = 100)
    @NotNull(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve possuir no mínimo 3 caracteres e no máximo 100 caracteres.")
    @Verify(ParametroTipo.TEXTO_SEM_CARACTERES_ESPECIAIS)
    @Setter
    @Getter
    private String nome;

    @Basic(optional = false)
    @NotNull(message = "O CPF é obrigatório.")
    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    @Verify(ParametroTipo.CPF)
    @Setter
    @Getter
    private String cpf;

    @Basic(optional = false)
    @NotNull(message = "O CEP do endereço é obrigatório.")
    @Column(name = "endereco_cep", length = 8)
    @Verify(ParametroTipo.CEP)
    @Setter
    @Getter
    private String enderecoCep;

    @Basic(optional = false)
    @NotNull(message = "O logradouro do endereço é obrigatório.")
    @Column(name = "endereco_logradouro", length = 100)
    @Setter
    @Getter
    private String enderecoLogradouro;

    @Column(name = "endereco_complemento", length = 100)
    @Setter
    @Getter
    private String enderecoComplemento;

    @Basic(optional = false)
    @NotNull(message = "O bairro do endereço é obrigatório.")
    @Column(name = "endereco_bairro", length = 100)
    @Setter
    @Getter
    private String enderecoBairro;

    @Basic(optional = false)
    @NotNull(message = "A cidade do endereço é obrigatória.")
    @Column(name = "endereco_cidade", length = 100)
    @Setter
    @Getter
    private String enderecoCidade;

    @Basic(optional = false)
    @NotNull(message = "A UF do endereço é obrigatória.")
    @Column(name = "endereco_uf", length = 2)
    @Setter
    @Getter
    private String enderecoUf;

    @OneToMany(mappedBy = "cliente")
    @Setter
    @Getter
    private Set<ClienteTelefone> telefones = new HashSet<>();

    @OneToMany(mappedBy = "cliente")
    @Setter
    @Getter
    private Set<ClienteEmail> emails = new HashSet<>();

}