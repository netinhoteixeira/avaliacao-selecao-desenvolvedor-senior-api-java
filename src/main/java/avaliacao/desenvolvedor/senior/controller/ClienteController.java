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

import avaliacao.desenvolvedor.senior.dto.*;
import avaliacao.desenvolvedor.senior.service.AcessoService;
import avaliacao.desenvolvedor.senior.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;

@RestController
@RequestMapping("/cliente")
@Tag(name = "customer", description = "Cliente")
public class ClienteController {

    @Autowired
    private AcessoService acessoService;

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "Obter Página de Clientes.", description = "Obtém uma página de clientes.", tags = {
            "customer"})
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(schema = @Schema(implementation = ClienteDto.class))), //
            @ApiResponse(responseCode = "403", description = "Acesso não permitido"), //
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro") //
    })
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pagina(
            @Parameter(description = "Página.", required = false) @RequestParam(name = "pagina", required = false) Integer pagina,
            @Parameter(description = "Página.", required = false) @RequestParam(name = "tamanho", required = false) Integer tamanho) {
        try {
            if ((pagina == null) || (pagina < 1)) {
                pagina = 0;
            } else {
                pagina--;
            }

            if ((tamanho == null) || (tamanho < 10)) {
                tamanho = 10;
            }

            PaginaDto paginaDto = clienteService.obterPagina(pagina, tamanho);
            return new ResponseEntity<>(paginaDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obter Cliente.", description = "Obtém um cliente.", tags = {"customer"})
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(schema = @Schema(implementation = ClienteDto.class))), //
            @ApiResponse(responseCode = "403", description = "Acesso não permitido"), //
            @ApiResponse(responseCode = "404", description = "Não encontrado"), //
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro") //
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> obter(
            @Parameter(description = "Identificação do Cliente. Informação obrigatória.", required = true) @PathVariable("id") Long id) {
        try {
            ClienteDto cliente = clienteService.obter(id);

            if (cliente != null) {
                return new ResponseEntity<>(cliente, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Operation(summary = "Inserir Cliente.", description = "Insere um novo cliente.", tags = {"customer"})
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(schema = @Schema(implementation = ClienteDto.class))), //
            @ApiResponse(responseCode = "403", description = "Acesso não permitido"), //
            @ApiResponse(responseCode = "404", description = "Não encontrado"), //
            @ApiResponse(responseCode = "422", description = "Validação não processada. Quando o alguma informação é inválida."), //
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro") //
    })
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> inserir(
            @Parameter(description = "Dados do Cliente. Informação obrigatória.", required = true) @RequestBody ClientePostRequestDto clientePost) throws IllegalAccessException, InvocationTargetException {

        // Permite somente acesso administrativo
        AcessoDto acessoDto = acessoService.getCurrentUser();
        if (!acessoDto.getAdministrador()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

//		try {
        ClienteDto cliente = clienteService.inserir(clientePost);

        return new ResponseEntity<>(cliente, HttpStatus.OK);
//		} catch (Exception e) {
//			return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
//		}
    }

    @Operation(summary = "Editar Cliente.", description = "Edita um cliente.", tags = {"customer"})
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(schema = @Schema(implementation = ClienteDto.class))), //
            @ApiResponse(responseCode = "403", description = "Acesso não permitido"), //
            @ApiResponse(responseCode = "404", description = "Não encontrado"), //
            @ApiResponse(responseCode = "422", description = "Validação não processada. Quando o alguma informação é inválida."), //
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro") //
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editar(
            @Parameter(description = "Identificação do Cliente. Informação obrigatória.", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Dados do Cliente. Informação obrigatória.", required = true) @RequestBody ClientePutRequestDto clientePut) {

        // Permite somente acesso administrativo
        AcessoDto acessoDto = acessoService.getCurrentUser();
        if (!acessoDto.getAdministrador()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        try {
            clientePut.setId(id);
            ClienteDto cliente = clienteService.editar(clientePut);
            return new ResponseEntity<>(cliente, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Operation(summary = "Remover Cliente.", description = "Remove um cliente.", tags = {"customer"})
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Sucesso"), //
            @ApiResponse(responseCode = "403", description = "Acesso não permitido"), //
            @ApiResponse(responseCode = "404", description = "Não encontrado"), //
            @ApiResponse(responseCode = "422", description = "Validação não processada. Quando o alguma informação é inválida."), //
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro") //
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> remover(
            @Parameter(description = "Identificação do Cliente. Informação obrigatória.", required = true) @PathVariable("id") Long id) {

        // Permite somente acesso administrativo
        AcessoDto acessoDto = acessoService.getCurrentUser();
        if (!acessoDto.getAdministrador()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        try {
            clienteService.remover(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (EntityNotFoundException | EmptyResultDataAccessException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
