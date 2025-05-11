package com.proyecto.invengest.service.ventas;


import com.proyecto.invengest.dto.ClienteDTO;
import com.proyecto.invengest.entities.Cliente;
import com.proyecto.invengest.exceptions.ClienteNoEncontradoException;
import com.proyecto.invengest.repository.ClienteRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteServicio {

    private final ClienteRepositorio clienteRepositorio;

    public ClienteServicio(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    public List<ClienteDTO> obtenerClientes() {
        return clienteRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO obtenerCliente(@PathVariable int id) {
        Cliente cliente = clienteRepositorio.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con id " + id));
        return convertirADTO(cliente);
    }

    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        Cliente clienteNuevo = convertirAEntidad(clienteDTO);
        clienteNuevo = clienteRepositorio.save(clienteNuevo);
        return convertirADTO(clienteNuevo);
    }

    public ClienteDTO actualizarCliente(@PathVariable int id, @RequestBody ClienteDTO clienteDTO) {
        Cliente clienteExistente = clienteRepositorio.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con id " + id));

        // Actualizar datos del cliente
        clienteExistente.setPrimerNombre(clienteDTO.getPrimerNombre());
        clienteExistente.setSegundoNombre(clienteDTO.getSegundoNombre());
        clienteExistente.setPrimerApellido(clienteDTO.getPrimerApellido());
        clienteExistente.setSegundoApellido(clienteDTO.getSegundoApellido());

        Cliente clienteActualizado = clienteRepositorio.save(clienteExistente);
        return convertirADTO(clienteActualizado);

    }



    private ClienteDTO convertirADTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getIdCliente(),
                cliente.getPrimerNombre(),
                cliente.getSegundoNombre(),
                cliente.getPrimerApellido(),
                cliente.getSegundoApellido()
        );
    }

    private Cliente convertirAEntidad(ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente();
        cliente.setPrimerNombre(clienteDTO.getPrimerNombre());
        cliente.setSegundoNombre(clienteDTO.getSegundoNombre());
        cliente.setPrimerApellido(clienteDTO.getPrimerApellido());
        cliente.setSegundoApellido(clienteDTO.getSegundoApellido());
        return cliente;
    }


}
