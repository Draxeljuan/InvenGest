package com.proyecto.invengest.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiException extends RuntimeException {
    private final int codigoError;
    private final HttpStatus httpStatus;

    public ApiException(int codigoError, String mensaje, HttpStatus httpStatus) {
        super(mensaje);
        this.codigoError = codigoError;
        this.httpStatus = httpStatus;
    }


}
