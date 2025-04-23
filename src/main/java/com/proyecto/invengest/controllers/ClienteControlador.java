package com.proyecto.invengest.controllers;


import com.proyecto.invengest.dto.ClienteDTO;
import com.proyecto.invengest.service.ClienteServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteControlador {

    private final ClienteServicio clienteServicio;

    public ClienteControlador(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }

    // Obtener listado de clientes
    @GetMapping
    public List<ClienteDTO> listarClientes() {
        return clienteServicio.obtenerClientes();
    }

    // Obtener clientes por id con DTO
    @GetMapping("/{id}")
    public ClienteDTO obtenerCliente(@PathVariable int id) {
        return clienteServicio.obtenerCliente(id);
    }

    // Crear nuevo cliente
    @PostMapping("/crear")
    public ResponseEntity<ClienteDTO> actualizarCliente (@RequestBody @Valid ClienteDTO clienteDTO) {
        ClienteDTO nuevoCliente = clienteServicio.crearCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    // Modificar cliente ya existente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente (@PathVariable int id, @RequestBody @Valid ClienteDTO clienteDTO) {
        ClienteDTO clienteActualizado = clienteServicio.actualizarCliente(id, clienteDTO);
        return ResponseEntity.ok(clienteActualizado);
    }



}
