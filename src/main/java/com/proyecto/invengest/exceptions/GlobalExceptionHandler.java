package com.proyecto.invengest.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;


// Anotacion que indica que esta clase interceptara las excepciones y retornara respuestas
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> usuarioNoEncontradoHandler(UsuarioNoEncontradoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Usuario no encontrado");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @ExceptionHandler(VentaNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> ventaNoEncontradaHandler(VentaNoEncontradaException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Venta no encontrada");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> productoNoEncontradoHandler(ProductoNoEncontradoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Producto no encontrado");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @ExceptionHandler(DetalleInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> detalleInvalidoHandler(DetalleInvalidoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Detalle invalido");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @ExceptionHandler(AlertaNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> alertaNoEncontradaHandler(AlertaNoEncontradaException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Alerta no encontrada");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @ExceptionHandler(CategoriaNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> categoriaNoEncontradaHandler(CategoriaNoEncontradaException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Categoria no encontrada");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @ExceptionHandler(MovimientoInventarioNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> movimientoInventarioNoEncontradoHandler(MovimientoInventarioNoEncontradoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Movimiento de inventario no encontrado");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @ExceptionHandler(ReporteNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> reporteNoEncontradoHandler(ReporteNoEncontradoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Reporte no encontrado");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> clienteNoEncontradoHandler(ClienteNoEncontradoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Cliente no encontrado");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @ExceptionHandler(TipoMovimientoInventarioNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> TipoMovimientoInventarioNoEncontradoHandler(TipoMovimientoInventarioNoEncontradoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Tipo movimiento inventario no encontrado");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @ExceptionHandler(TipoAlertaNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> TipoAlertaNoEncontradaHandler(TipoAlertaNoEncontradaException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Tipo alerta inventario no encontrada");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> runtimeExceptionHandler(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Error en el servidor");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }


}
