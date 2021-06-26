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
package avaliacao.desenvolvedor.senior.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        String securitySchemeName = "Auth JWT";

        return new OpenAPI() //
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) //
                .components( //
                        new Components() //
                                .addSecuritySchemes(securitySchemeName, //
                                        new SecurityScheme() //
                                                .name(securitySchemeName) //
                                                .type(SecurityScheme.Type.HTTP) //
                                                .scheme("bearer") //
                                                .bearerFormat("JWT") //
                                ) //
                ) //
                .info( //
                        new Info() //
                                .title("Avaliação para Seleção - Desenvolvedor Sênior API") //
                                .description(
                                        "Projeto de CRUD de Cliente solicitado para a avaliação de seleção para desenvolvedor sênior.") //
                                .version("1.0.0.0") //
                                .contact( //
                                        new Contact() //
                                                .name("Francisco Ernesto Teixeira") //
                                                .url("https://francisco.pro") //
                                                .email("me@francisco.pro?subject=Avaliação+para+Seleção+-+Desenvolvedor+Sênior+API") //
                                ) //
                ); //
    }

}