package com.example.studyrats.exceptions;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class ManipuladorGlobalDeExcecoes {

    @ExceptionHandler(ExcecaoExemplo.class)
    public ResponseEntity<?> manipularExcecaoExemplo(ExcecaoExemplo ex) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return baseReturn(httpStatus, ex);
    }

    @ExceptionHandler(FirebaseJsonNaoEncontrado.class)
    public ResponseEntity<?> manipularFirebaseJsonNaoEncontrado(FirebaseJsonNaoEncontrado ex) {
        HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
        return baseReturn(httpStatus, ex);
    }

    @ExceptionHandler(FirebaseIO.class)
    public ResponseEntity<?> manipularFirebaseIO(FirebaseIO ex) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return baseReturn(httpStatus, ex);
    }

    private ResponseEntity<?> baseReturn(HttpStatus httpStatus, Exception ex) {
        RespostaDeErro body = new RespostaDeErro(httpStatus.value(), httpStatus.getReasonPhrase(), ex.getMessage());
        return ResponseEntity
                .status(httpStatus)
                .body(body);
    }

}

// Essa class garante que o body da resposta vai ter um JSON detalhado para o frontend
// Não é legal enviar no body apenas uma mensagem de erro em ‘string’
// Podemos criar classes para uma resposta detalhada
// Ex: Frontend mandou dados errados para a criação de usuário, criamos uma classe de resposta que
// contém uma lista de variáveis incorretas e o motivo de estarem incorretas.
// :)
class RespostaDeErro {

    private int status;
    private String erro;
    private String mensagem;

    public RespostaDeErro(int httpStatus, String nomeDoErro, String mensagemDeErro) {
        status = httpStatus;
        nomeDoErro = erro;
        mensagem = mensagemDeErro;
    }

}
