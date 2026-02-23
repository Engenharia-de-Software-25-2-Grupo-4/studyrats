package com.example.studyrats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.studyrats.util.Mensagens;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UsuarioNaoTemPermissaoParaDeletarSessao extends RuntimeException {
    public UsuarioNaoTemPermissaoParaDeletarSessao() {
        super(Mensagens.USUARIO_NAO_TEM_PERMISSAO_PARA_DELETAR_SESSAO);
    }    
}
