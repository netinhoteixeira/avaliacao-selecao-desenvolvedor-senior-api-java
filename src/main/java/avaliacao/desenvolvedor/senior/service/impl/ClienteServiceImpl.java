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
package avaliacao.desenvolvedor.senior.service.impl;

import avaliacao.desenvolvedor.senior.dto.*;
import avaliacao.desenvolvedor.senior.model.Cliente;
import avaliacao.desenvolvedor.senior.model.ClienteEmail;
import avaliacao.desenvolvedor.senior.model.ClienteTelefone;
import avaliacao.desenvolvedor.senior.model.ClienteTelefoneTipo;
import avaliacao.desenvolvedor.senior.repository.ClienteEmailRepository;
import avaliacao.desenvolvedor.senior.repository.ClienteRepository;
import avaliacao.desenvolvedor.senior.repository.ClienteTelefoneRepository;
import avaliacao.desenvolvedor.senior.service.ClienteService;
import avaliacao.desenvolvedor.senior.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteTelefoneRepository clienteTelefoneRepository;

    @Autowired
    private ClienteTelefoneTipoCacheServiceImpl clienteTelefoneTipoCacheServiceImpl;

    @Autowired
    private ClienteEmailRepository clienteEmailRepository;

    @Transactional(timeout = 10)
    @Override
    public PaginaDto obterPagina(int pagina, int tamanho) {
        PaginaDto paginaDto = new PaginaDto();

        Pageable paging = PageRequest.of(pagina, tamanho);
        Page<Cliente> pagedResult = clienteRepository.findAll(paging);

        paginaDto.setTotal(clienteRepository.count());
        paginaDto.setPaginas(pagedResult.getTotalPages());

        List<Cliente> clienteList = pagedResult.toList();
        List<ClienteDto> clientesDto = new ArrayList<>();

        for (Cliente cliente : clienteList) {
            ClienteDto clienteDto = preparaResposta(cliente, cliente.getTelefones(), cliente.getEmails());
            clienteDto.setEndereco(null);
            clienteDto.setTelefones(null);
            clienteDto.setEmails(null);

            clientesDto.add(clienteDto);
        }

        paginaDto.setItens(clientesDto);

        return paginaDto;
    }

    @Transactional(timeout = 10)
    @Override
    public ClienteDto obter(Long id) {
        Optional<Cliente> ocliente = clienteRepository.findById(id);

        return ocliente.map(cliente -> preparaResposta(cliente, cliente.getTelefones(), cliente.getEmails()))
                .orElse(null);
    }

    @Transactional(timeout = 10)
    @Override
    public ClienteDto inserir(ClientePostRequestDto clientePost)
            throws IllegalAccessException, InvocationTargetException {
        // Mapeia os valores
        Cliente cliente = new Cliente();
        cliente.setNome(clientePost.getNome());
        cliente.setCpf(NumberUtil.extractNumbers(clientePost.getCpf()));

        if (clientePost.getEndereco() != null) {
            cliente.setEnderecoCep(NumberUtil.extractNumbers(clientePost.getEndereco().getCep()));
            cliente.setEnderecoLogradouro(clientePost.getEndereco().getLogradouro());
            cliente.setEnderecoBairro(clientePost.getEndereco().getBairro());
            cliente.setEnderecoCidade(clientePost.getEndereco().getCidade());
            cliente.setEnderecoUf(clientePost.getEndereco().getUf());
        }

        List<ClienteTelefone> telefones = new ArrayList<>();

        if ((clientePost.getTelefones() != null) && (!clientePost.getTelefones().isEmpty())) {
            for (ClienteTelefonePostRequestDto telefonePost : clientePost.getTelefones()) {
                ClienteTelefoneTipo tipo = clienteTelefoneTipoCacheServiceImpl.getById(telefonePost.getTipo());

                ClienteTelefone telefone = new ClienteTelefone();
                telefone.setCliente(cliente);
                telefone.setTipo(tipo);
                telefone.setValor(NumberUtil.extractNumbers(telefonePost.getValor()));

                telefones.add(telefone);
            }
        }

        List<ClienteEmail> emails = new ArrayList<>();

        if ((clientePost.getEmails() != null) && (!clientePost.getEmails().isEmpty())) {
            for (ClienteEmailPostRequestDto emailPost : clientePost.getEmails()) {
                ClienteEmail email = new ClienteEmail();
                email.setCliente(cliente);
                email.setValor(emailPost.getValor());

                emails.add(email);
            }
        }

        // Valida as informações com o banco de dados

        // TODO: O email é único

        // TODO: O CPF é único

        // Salvar Cliente
        clienteRepository.save(cliente);

        // Salvar Telefones
        for (ClienteTelefone telefone : telefones) {
            clienteTelefoneRepository.save(telefone);
        }

        // Salvar Emails
        for (ClienteEmail email : emails) {
            clienteEmailRepository.save(email);
        }

        // clienteRepository.flush();
        clienteTelefoneRepository.flush();
        clienteEmailRepository.flush();

        return preparaResposta(cliente, telefones, emails);
    }

    @Transactional(timeout = 10)
    @Override
    public ClienteDto editar(ClientePutRequestDto clientePut) throws IllegalAccessException, InvocationTargetException {
        Optional<Cliente> ocliente = clienteRepository.findById(clientePut.getId());

        // TODO: Validar se o registro de fato existe

        Cliente cliente = ocliente.get();

        // Atualiza os valores
        cliente.setNome(clientePut.getNome());
        cliente.setCpf(NumberUtil.extractNumbers(clientePut.getCpf()));

        if (clientePut.getEndereco() != null) {
            cliente.setEnderecoCep(NumberUtil.extractNumbers(clientePut.getEndereco().getCep()));
            cliente.setEnderecoLogradouro(clientePut.getEndereco().getLogradouro());
            cliente.setEnderecoBairro(clientePut.getEndereco().getBairro());
            cliente.setEnderecoCidade(clientePut.getEndereco().getCidade());
            cliente.setEnderecoUf(clientePut.getEndereco().getUf());
        }

        List<ClienteTelefone> telefones = new ArrayList<>();
        List<Long> telefonesParaRemover = new ArrayList<>();

        if ((clientePut.getTelefones() != null) && (!clientePut.getTelefones().isEmpty())) {
            for (ClienteTelefonePutRequestDto telefonePut : clientePut.getTelefones()) {
                if (telefonePut.isRemovido()) {
                    telefonesParaRemover.add(telefonePut.getId());
                } else {
                    ClienteTelefoneTipo tipo = clienteTelefoneTipoCacheServiceImpl.getById(telefonePut.getTipo());

                    // Inicia o telefone como vazio
                    ClienteTelefone telefone = null;

                    // Procura pelo telefone para ser editado suas informações
                    if ((telefonePut.getId() != null) && (telefonePut.getId() > 0)) {
                        telefone = clienteTelefoneRepository.getById(telefonePut.getId());
                    }

                    // É um novo telefone (ou a identificação não foi encontrada)
                    if (telefone == null) {
                        telefone = new ClienteTelefone();
                    }

                    telefone.setCliente(cliente);
                    telefone.setTipo(tipo);
                    telefone.setValor(NumberUtil.extractNumbers(telefonePut.getValor()));

                    telefones.add(telefone);
                }
            }
        }

        List<ClienteEmail> emails = new ArrayList<>();
        List<Long> emailsParaRemover = new ArrayList<>();

        if ((clientePut.getEmails() != null) && (!clientePut.getEmails().isEmpty())) {
            for (ClienteEmailPutRequestDto emailPut : clientePut.getEmails()) {
                if (emailPut.isRemovido()) {
                    emailsParaRemover.add(emailPut.getId());
                } else {
                    // Inicia o email como vazio
                    ClienteEmail email = null;

                    // Procura pelo email para ser editado suas informações
                    if ((emailPut.getId() != null) && (emailPut.getId() > 0)) {
                        email = clienteEmailRepository.getById(emailPut.getId());
                    }

                    // É um novo email (ou a identificação não foi encontrada)
                    if (email == null) {
                        email = new ClienteEmail();
                    }

                    email.setCliente(cliente);
                    email.setValor(emailPut.getValor());

                    emails.add(email);
                }
            }
        }

        // Valida as informações com o banco de dados

        // TODO: O email é único

        // TODO: O CPF é único

        // Salvar Cliente
        clienteRepository.save(cliente);

        // Salvar Telefones
        if (!telefonesParaRemover.isEmpty()) {
            for (Long id : telefonesParaRemover) {
                if (id != null) {
                    try {
                        ClienteTelefone telefone = clienteTelefoneRepository.getById(id);
                        clienteTelefoneRepository.delete(telefone);
                    } catch (Exception e) {
                        // Nada acontece.
                    }
                }
            }
        }

        for (ClienteTelefone telefone : telefones) {
            clienteTelefoneRepository.save(telefone);
        }

        // Salvar Emails
        if (!emailsParaRemover.isEmpty()) {
            for (Long id : emailsParaRemover) {
                if (id != null) {
                    try {
                        ClienteEmail email = clienteEmailRepository.getById(id);
                        clienteEmailRepository.delete(email);
                    } catch (Exception e) {
                        // Nada acontece.
                    }
                }
            }
        }

        for (ClienteEmail email : emails) {
            clienteEmailRepository.save(email);
        }

        // clienteRepository.flush();
        clienteTelefoneRepository.flush();
        clienteEmailRepository.flush();

        return preparaResposta(cliente, telefones, emails);
    }

    @Transactional(timeout = 10)
    @Override
    public void remover(Long id) throws IllegalAccessException, InvocationTargetException {
        clienteRepository.deleteById(id);
        // clienteRepository.flush();
    }

    private ClienteDto preparaResposta(Cliente cliente, Set<ClienteTelefone> telefones, Set<ClienteEmail> emails) {
        List<ClienteTelefone> telefoneList = new ArrayList<>(telefones);
        List<ClienteEmail> emailList = new ArrayList<>(emails);

        return preparaResposta(cliente, telefoneList, emailList);
    }

    private ClienteDto preparaResposta(Cliente cliente, List<ClienteTelefone> telefones, List<ClienteEmail> emails) {
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setId(cliente.getId());
        clienteDto.setNome(cliente.getNome());
        clienteDto.setCpf(cliente.getCpf());

        if (StringUtils.isNotBlank(cliente.getEnderecoCep())) {
            ClienteEnderecoDto endereco = new ClienteEnderecoDto();
            endereco.setCep(cliente.getEnderecoCep());
            endereco.setLogradouro(cliente.getEnderecoLogradouro());
            endereco.setBairro(cliente.getEnderecoBairro());
            endereco.setCidade(cliente.getEnderecoCidade());
            endereco.setUf(cliente.getEnderecoUf());
            clienteDto.setEndereco(endereco);
        }

        if (!telefones.isEmpty()) {
            clienteDto.setTelefones(new HashSet<>());

            for (ClienteTelefone telefone : telefones) {
                ClienteTelefoneTipoDto tipoDto = null;

                if (telefone.getTipo() != null) {
                    tipoDto = new ClienteTelefoneTipoDto();
                    tipoDto.setId(telefone.getTipo().getId());
                    tipoDto.setNome(telefone.getTipo().getNome());
                }

                ClienteTelefoneDto telefoneDto = new ClienteTelefoneDto();
                telefoneDto.setId(telefone.getId());
                telefoneDto.setTipo(tipoDto);
                telefoneDto.setValor(telefone.getValor());
                clienteDto.getTelefones().add(telefoneDto);
            }
        }

        if (!emails.isEmpty()) {
            clienteDto.setEmails(new HashSet<>());

            for (ClienteEmail email : emails) {
                ClienteEmailDto emailDto = new ClienteEmailDto();
                emailDto.setId(email.getId());
                emailDto.setValor(email.getValor());
                clienteDto.getEmails().add(emailDto);
            }
        }

        return clienteDto;
    }

}
