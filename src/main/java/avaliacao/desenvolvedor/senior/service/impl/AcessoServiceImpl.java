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

import avaliacao.desenvolvedor.senior.dto.AcessoDto;
import avaliacao.desenvolvedor.senior.model.Acesso;
import avaliacao.desenvolvedor.senior.repository.AcessoRepository;
import avaliacao.desenvolvedor.senior.service.AcessoService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcessoServiceImpl implements AcessoService {

    @Autowired
    private AcessoRepository acessoRepository;

    @Transactional
    @Override
    public Acesso findByUsuario(String usuario) {
        return acessoRepository.findByUsuario(usuario);
    }

    @Transactional
    @Override
    public AcessoDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if ((authentication != null) && (authentication.isAuthenticated())) {
            if (authentication.getPrincipal() != null) {
                try {
                    String principal = (String) authentication.getPrincipal();
                    Acesso acesso = findByUsuario(principal);
                    AcessoDto acessoDto = new AcessoDto();
                    BeanUtils.copyProperties(acessoDto, acesso);

                    return acessoDto;
                } catch (Exception e) {
                    // Nada acontece.
                }
            }

        }

        return null;
    }

}
