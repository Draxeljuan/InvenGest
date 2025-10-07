package com.proyecto.invengest.service.ventas;

import com.proyecto.invengest.dto.ClienteDTO;
import com.proyecto.invengest.entities.Cliente;
import com.proyecto.invengest.exceptions.ClienteNoEncontradoException;
import com.proyecto.invengest.repository.ClienteRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServicioTest {

    @Mock
    private ClienteRepositorio clienteRepositorio;

    @InjectMocks
    private ClienteServicio clienteServicio;

    private Cliente cliente;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .idCliente(1)
                .primerNombre("Juan")
                .segundoNombre("Carlos")
                .primerApellido("Pérez")
                .segundoApellido("Gómez")
                .build();

        clienteDTO = new ClienteDTO(1, "Juan", "Carlos", "Pérez", "Gómez");

    }

    @Test
    public void obtenerClientes() {
        // Creamos un cliente para probar como se regresan los clientes
        Cliente otroCliente = Cliente.builder()
                .idCliente(2)
                .primerNombre("Ana")
                .segundoNombre("Lucía")
                .primerApellido("Ramírez")
                .segundoApellido("Torres")
                .build();
        // Cuando se pida buscar todos los clientes se retorna el nuevo y el ya creado
        when(clienteRepositorio.findAll()).thenReturn(List.of(cliente, otroCliente));
        // El servicio busca los clientes creados
        List<ClienteDTO> resultado = clienteServicio.obtenerClientes();
        // Se validan los resultados
        assertEquals(2, resultado.size(), "Lista debe ser 2 clientes");
        assertEquals("Juan", resultado.get(0).getPrimerNombre());
        assertEquals("Ana", resultado.get(1).getPrimerNombre());
    }

    @Test
    public void obtenerCliente() {
        // Si se busca un cliente por id 1 se retorna el creado inicialmente
        when(clienteRepositorio.findById(1)).thenReturn(Optional.of(cliente));
        // El servicio busca obtener el cliente con id 1
        ClienteDTO resultado = clienteServicio.obtenerCliente(1);
        // Se validan los resultados obtenidos al ejecutar el metodo
        assertEquals("Juan", resultado.getPrimerNombre());
        assertEquals("Pérez", resultado.getPrimerApellido());
    }

    @Test
    public void obtenerClienteNoExiste() {
        // Si se busca un cliente que no existe se retorna Empty
        when(clienteRepositorio.findById(999)).thenReturn(Optional.empty());
        // Se valida la excepcion obtenida al buscar un cliente inexistente
        assertThrows(ClienteNoEncontradoException.class, () -> clienteServicio.obtenerCliente(999));
    }

    @Test
    public void crearCliente() {
        // Al guardar un cliente se almacena el creado inicialmente
        when(clienteRepositorio.save(ArgumentMatchers.<Cliente>any())).thenReturn(cliente);
        // El servicio de crear cliente almacena el cliente creado inicialmente
        ClienteDTO resultado = clienteServicio.crearCliente(clienteDTO);
        // Se validan los resultados
        assertEquals("Juan", resultado.getPrimerNombre());
        verify(clienteRepositorio).save(ArgumentMatchers.<Cliente>any());
    }
    @Test
    public void actualizarCliente() {
        // Cuando se quiera actualizar un cliente se toma de referencia el creado inicialmente
        when(clienteRepositorio.findById(1)).thenReturn(Optional.of(cliente));
        when(clienteRepositorio.save(ArgumentMatchers.<Cliente>any())).thenReturn(cliente);
        // Se pasan los argumentos para modificar el cliente
        ClienteDTO dtoModificado = new ClienteDTO(1, "Juan", "Esteban", "Pérez", "Gómez");
        // Se llama al servicio para modificar el cliente
        ClienteDTO resultado = clienteServicio.actualizarCliente(1, dtoModificado);
        // Se validan los resultados
        assertEquals("Esteban", resultado.getSegundoNombre());
        verify(clienteRepositorio).save(cliente);
    }

    @Test
    public void actualizarClienteNoExiste() {
        // Cuando se consulte un cliente que no existe se retorna empty
        when(clienteRepositorio.findById(99)).thenReturn(Optional.empty());
        // Datos que se pasan para modificar el cliente inexistente
        ClienteDTO dto = new ClienteDTO(99, "X", "Y", "Z", "W");
        // Se valida la excepción obtenida al intentar modificar un cliente que no existe
        assertThrows(ClienteNoEncontradoException.class, () -> clienteServicio.actualizarCliente(99, dto));
    }
}