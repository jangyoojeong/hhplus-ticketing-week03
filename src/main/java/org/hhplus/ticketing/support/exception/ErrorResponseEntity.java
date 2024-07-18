package org.hhplus.ticketing.support.exception;

import lombok.Builder;
import lombok.Data;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@Data
@Builder
public class ErrorResponseEntity {
    private int status;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> from(ErrorCode e){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getHttpStatus().value())
                        .code(e.name())
                        .message(e.getMessage())
                        .build()
                );
    }

    public static ResponseEntity<ErrorResponseEntity> from(ResponseStatusException ex){
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ErrorResponseEntity.builder()
                        .status(ex.getStatusCode().value())
                        .code(ex.getClass().getSimpleName())
                        .message(ex.getReason())
                        .build()
                );
    }

    public static ResponseEntity<ErrorResponseEntity> from(Exception ex, HttpStatus status){
        return ResponseEntity
                .status(status)
                .body(ErrorResponseEntity.builder()
                        .status(status.value())
                        .code(ex.getClass().getSimpleName())
                        .message(ex.getMessage())
                        .build()
                );
    }
}