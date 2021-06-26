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
package avaliacao.desenvolvedor.senior.security;

import avaliacao.desenvolvedor.senior.model.Acesso;
import avaliacao.desenvolvedor.senior.model.AcessoRegistro;
import avaliacao.desenvolvedor.senior.model.AcessoRegistroTipo;
import avaliacao.desenvolvedor.senior.repository.AcessoRegistroRepository;
import avaliacao.desenvolvedor.senior.repository.AcessoRepository;
import avaliacao.desenvolvedor.senior.service.AcessoRegistroTipoCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AcessoRepository acessoRepository;

    @Autowired
    private AcessoRegistroRepository acessoRegistroRepository;

    @Autowired
    private AcessoRegistroTipoCacheService acessoRegistroTipoCacheRepository;

    @Override
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        Acesso acesso = acessoRepository.findByUsuario(usuario);

        if (acesso == null) {
            throw new UsernameNotFoundException(usuario);
        }

        // Registra o acesso do usuário|
        AcessoRegistroTipo acessoRegistroTipo = acessoRegistroTipoCacheRepository.getById(1L);

        if (acessoRegistroTipo != null) {
            AcessoRegistro acessoRegistro = new AcessoRegistro();
            acessoRegistro.setAcesso(acesso);
            acessoRegistro.setDataHora(new Date());
            acessoRegistro.setTipo(acessoRegistroTipo);
            acessoRegistroRepository.save(acessoRegistro);
        }

        return CustomUserConverter.toUser(acesso);
    }

}
