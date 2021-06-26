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
package avaliacao.desenvolvedor.senior.controller;

import avaliacao.desenvolvedor.senior.dto.AcessoDto;
import avaliacao.desenvolvedor.senior.dto.EnderecoPeloCepDto;
import avaliacao.desenvolvedor.senior.exception.CepInvalidoException;
import avaliacao.desenvolvedor.senior.service.AcessoService;
import avaliacao.desenvolvedor.senior.service.ViaCepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/util")
@Tag(name = "util", description = "Utilitários")
public class UtilController {

    @Autowired
    private AcessoService acessoService;

    @Autowired
    private ViaCepService viaCepService;

    @Operation(summary = "Obter Endereço Pelo CEP.", description = "Obtém o endereço a partir do CEP fornecido.", tags = {
            "util"})
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(schema = @Schema(implementation = EnderecoPeloCepDto.class))), //
            @ApiResponse(responseCode = "403", description = "Acesso não permitido"), //
            @ApiResponse(responseCode = "404", description = "Não encontrado"), //
            @ApiResponse(responseCode = "422", description = "Validação não processada. Quando o CEP fornecido é inválido."), //
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro") //
    })
    @RequestMapping(value = "/cep/{cep}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> obterEnderecoPeloCep(
            @Parameter(description = "Código de Endereçamento Postal, o qual deve ser encontrado as informações detalhadas. Informação obrigatória.", required = true) @PathVariable("cep") String cep) {

        // Permite somente acesso administrativo
        AcessoDto acessoDto = acessoService.getCurrentUser();
        if (!acessoDto.getAdministrador()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        try {
            EnderecoPeloCepDto endereco = viaCepService.obterEnderecoPeloCep(cep);

            if (endereco != null) {
                return new ResponseEntity<>(endereco, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (CepInvalidoException e) {
            return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
