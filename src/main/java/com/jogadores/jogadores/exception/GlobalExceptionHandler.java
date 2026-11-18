package com.jogadores.jogadores.exception;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jogadores.jogadores.dto.ErroResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> corpoInvalido(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> argumentoInvalido(IllegalArgumentException ex) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErroResponse> proibido(SecurityException ex) {
        return responder(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(NoSuchElementException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Mensagem fixa: a original traz o SQL
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> conflito(DataIntegrityViolationException ex) {
        return responder(HttpStatus.CONFLICT, "Conflito com dados já cadastrados");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> geral(Exception ex) {
        // ErrorResponse é interface (ResponseStatusException, 404/405 do Spring), não dá para usá-la em @ExceptionHandler
        if (ex instanceof ErrorResponse erro) {
            return responder(erro.getStatusCode(), erro.getBody().getDetail());
        }
        // Resolve @ResponseStatus de forma genérica, sem depender de classes de outros pacotes
        ResponseStatus anotacao = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);
        if (anotacao != null) {
            return responder(anotacao.code(), ex.getMessage());
        }
        log.error("Erro inesperado", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
    }

    private ResponseEntity<ErroResponse> responder(HttpStatusCode status, String mensagem) {
        ErroResponse corpo = new ErroResponse(
                status.value(), HttpStatus.valueOf(status.value()).name(), mensagem, LocalDateTime.now());
        return ResponseEntity.status(status).body(corpo);
    }
}
