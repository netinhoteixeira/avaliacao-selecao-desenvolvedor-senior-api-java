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

import avaliacao.desenvolvedor.senior.dto.EnderecoPeloCepDto;
import avaliacao.desenvolvedor.senior.dto.ViaCepResponseDto;
import avaliacao.desenvolvedor.senior.exception.CepInvalidoException;
import avaliacao.desenvolvedor.senior.service.ViaCepService;
import avaliacao.desenvolvedor.senior.util.HttpClientUtil;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.safeguard.types.ParametroTipo;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
public class ViaCepServiceImpl implements ViaCepService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViaCepServiceImpl.class);

    @Autowired
    private CepCacheServiceImpl cepCacheServiceImpl;

    @Override
    public EnderecoPeloCepDto obterEnderecoPeloCep(String cep) throws CepInvalidoException {
        try {
            ParametroTipo.CEP.getType().assertValid(cep);
        } catch (InvalidStateException e) {
            throw new CepInvalidoException();
        }

        EnderecoPeloCepDto enderecoPeloCep;

        try {
            enderecoPeloCep = cepCacheServiceImpl.findById(cep);
        } catch (NumberFormatException e) {
            throw new CepInvalidoException();
        }

        if (enderecoPeloCep == null) {
            OkHttpClient client = HttpClientUtil.build();
            MediaType applicationJsonMediaType = MediaType.get("application/json");

            String endpoint = "https://viacep.com.br/ws/" + cep + "/json/";
            Request request = new Request.Builder().url(endpoint)
                    .addHeader("Accept", applicationJsonMediaType.toString())
                    .addHeader("Content-Type", applicationJsonMediaType.toString()).build();

            try {
                Response response = client.newCall(request).execute();
                LOGGER.info((response.isSuccessful()) ? "Success on execution" : "Fail on execution");

                ViaCepResponseDto viaCepResponse = null;
                Gson gson = new Gson();

                if (response.body() != null) {
                    String sbody = Objects.requireNonNull(response.body()).string();

                    try {
                        viaCepResponse = gson.fromJson(sbody, ViaCepResponseDto.class);
                    } catch (Exception e) {
                        LOGGER.error("Trouble on fetch CEP. Body: " + sbody, e);
                    }
                }

                if (response.isSuccessful()) {
                    if ((viaCepResponse == null) || (StringUtils.isBlank(viaCepResponse.getCep()))) {
                        // TODO: Exception
                    } else {
                        if (StringUtils.isBlank(viaCepResponse.getLogradouro())) {
                            viaCepResponse.setLogradouro(null);
                        }

                        if (StringUtils.isBlank(viaCepResponse.getComplemento())) {
                            viaCepResponse.setComplemento(null);
                        }

                        if (StringUtils.isBlank(viaCepResponse.getBairro())) {
                            viaCepResponse.setBairro(null);
                        }

                        if (StringUtils.isBlank(viaCepResponse.getLocalidade())) {
                            viaCepResponse.setLocalidade(null);
                        }

                        if (StringUtils.isBlank(viaCepResponse.getUf())) {
                            viaCepResponse.setUf(null);
                        }

                        if (StringUtils.isBlank(viaCepResponse.getIbge())) {
                            viaCepResponse.setIbge(null);
                        }

                        if (StringUtils.isBlank(viaCepResponse.getGia())) {
                            viaCepResponse.setGia(null);
                        }

                        if (StringUtils.isBlank(viaCepResponse.getDdd())) {
                            viaCepResponse.setDdd(null);
                        }

                        if (StringUtils.isBlank(viaCepResponse.getSiafi())) {
                            viaCepResponse.setSiafi(null);
                        }

                        // Prepara a resposta
                        enderecoPeloCep = new EnderecoPeloCepDto();
                        enderecoPeloCep.setCep(viaCepResponse.getCep());
                        enderecoPeloCep.setLogradouro(viaCepResponse.getLogradouro());
                        enderecoPeloCep.setBairro(viaCepResponse.getBairro());
                        enderecoPeloCep.setCidade(viaCepResponse.getLocalidade());
                        enderecoPeloCep.setUf(viaCepResponse.getUf());

                        // Persiste no cachê
                        cepCacheServiceImpl.save(enderecoPeloCep);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Trouble on fetch CEP.", e);
            }
        }

        return enderecoPeloCep;
    }

}
