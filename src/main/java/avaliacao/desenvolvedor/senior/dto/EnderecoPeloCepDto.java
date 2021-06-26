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
package avaliacao.desenvolvedor.senior.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "Informações detalhadas do endereço obtidas quando se procura através de seu CEP.")
public class EnderecoPeloCepDto implements Serializable {

    private static final long serialVersionUID = -6819834147111526415L;

    @Schema(description = "Código de Endereçamento Postal.", example = "70610-432", required = true)
    private String cep;

    @Schema(description = "Logradouro.", example = "SIG Quadra 3 Bloco B", required = true)
    private String logradouro;

    @Schema(description = "Bairro.", example = "Zona Industrial", required = true)
    private String bairro;

    @Schema(description = "Cidade.", example = "Brasília", required = true)
    private String cidade;

    @Schema(description = "Unidade Federativa.", example = "DF", required = true)
    private String uf;

}
