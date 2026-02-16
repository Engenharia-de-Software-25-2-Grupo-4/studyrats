package com.example.studyrats.exceptions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class ManipuladorGlobalDeExcecoes {

    /*
    @ExceptionHandler(ExcecaoExemplo.class)
    public ResponseEntity<?> manipularExcecaoExemplo(ExcecaoExemplo ex) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return baseReturn(httpStatus, ex);
    }
    */

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> geralParaTests(Exception ex) {
//        HttpStatus httpStatus = HttpStatus.OK;
//        return testReturn(httpStatus, ex);
//    }

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
    @ExceptionHandler({
            EstudanteNaoEncontrado.class,
            SessaoDeEstudoNaoEncontrado.class,
            GrupoNaoEncontrado.class,
            ConviteNaoEncontrado.class,
            ConviteExpirado.class,
    })
    public ResponseEntity<?> manipularEstudanteNaoEncontrado(RuntimeException ex) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return baseReturn(httpStatus, ex);
    }

    @ExceptionHandler({
            EmailJaCadastrado.class,
            UIDJaCadastrado.class,
            GrupoJaExisteException.class,
            EstudanteJaParticipa.class,
    })
    public ResponseEntity<?> manipularEmailJaCadastrado(RuntimeException ex) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        return baseReturn(httpStatus, ex);
    }

    @ExceptionHandler({
            EstudanteNaoAutenticado.class,
            UsuarioNaoAdmin.class
    })

    public ResponseEntity<?> manipularEstudanteNaoAutenticado(EstudanteNaoAutenticado ex) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        return baseReturn(httpStatus, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> manipularValidacao(MethodArgumentNotValidException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Map<String, String> campos = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> campos.put(error.getField(), error.getDefaultMessage()));

        RespostaDeErroComCampos body = new RespostaDeErroComCampos(httpStatus.value(), httpStatus.getReasonPhrase(), "Campos invalidos", campos);
        return ResponseEntity
                .status(httpStatus)
                .body(body);
    }

    private ResponseEntity<?> testReturn(HttpStatus httpStatus, Exception ex) {
        RespostaDeErro body = new RespostaDeErro(httpStatus.value(), ex.toString(), ex.toString());
        return ResponseEntity
                .status(httpStatus)
                .body(body);
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
        this.status = httpStatus;
        this.erro = nomeDoErro;
        this.mensagem = mensagemDeErro;
    }

}

class RespostaDeErroComCampos {

    private int status;
    private String erro;
    private String mensagem;
    private Instant timestamp;
    private Map<String, String> campos;

    public RespostaDeErroComCampos(int httpStatus, String nomeDoErro, String mensagemDeErro, Map<String, String> camposInvalidos) {
        this.status = httpStatus;
        this.erro = nomeDoErro;
        this.mensagem = mensagemDeErro;
        this.campos = camposInvalidos;
        this.timestamp = Instant.now();
    }
}