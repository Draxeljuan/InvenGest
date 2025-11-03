package com.proyecto.invengest.service.inventario;


import com.proyecto.invengest.dto.ProveedorDTO;
import com.proyecto.invengest.entities.Proveedor;
import com.proyecto.invengest.exceptions.ProductoNoEncontradoException;
import com.proyecto.invengest.exceptions.ProveedorNoEncontradoException;
import com.proyecto.invengest.repository.EstadoProveedorRepositorio;
import com.proyecto.invengest.repository.ProveedorRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProveedorServicio {

    private final ProveedorRepositorio proveedorRepositorio;
    private final EstadoProveedorRepositorio estadoProveedorRepositorio;

    public ProveedorServicio (ProveedorRepositorio proveedorRepositorio, EstadoProveedorRepositorio estadoProveedorRepositorio) {
        this.proveedorRepositorio = proveedorRepositorio;
        this.estadoProveedorRepositorio = estadoProveedorRepositorio;
    }

    public List<ProveedorDTO> listarProveedores() {
        List<Proveedor> proveedores = proveedorRepositorio.findAll(); // modificar lo del estado para lo inactivos
        return proveedores.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ProveedorDTO obtenerProveedor(@PathVariable int id) {
        Proveedor proveedor = proveedorRepositorio.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("No existe el proveedor"));
        return convertirADTO(proveedor);
    }

    public List<ProveedorDTO> buscarPorNombre(String nombre) {
        return proveedorRepositorio.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ProveedorDTO crearProveedor (ProveedorDTO proveedorDTO){
        try {
            // Creamos una instancia de un proveedor
            Proveedor proveedor = new Proveedor();

            // el ID de momento no lo generamos ya que es un valor Auto Increment en BD

            // Mapeamos los valores del DTO al objeto proveedor
            proveedor.setIdEstado(estadoProveedorRepositorio.findById(1)
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado con id 1"))); // Esto puede cambiar
            // al crear un proveedor su estado siempre deberia ser activo (1)
            return getProveedorDTO(proveedorDTO, proveedor);


        } catch (Exception ex) {
            System.out.println("Error inesperado al crear el proveedor" + ex.getMessage());
            throw new RuntimeException("Ocurrio un error inesperado al crear el proveedor", ex);
        }

    }

    private ProveedorDTO getProveedorDTO(ProveedorDTO proveedorDTO, Proveedor proveedor) {
        proveedor.setNombre(proveedorDTO.getNombre());
        proveedor.setTelefono(proveedorDTO.getTelefono());
        proveedor.setEmail(proveedorDTO.getEmail());
        proveedor.setDireccion(proveedorDTO.getDireccion());
        proveedor.setNit(proveedorDTO.getNit());

        Proveedor proveedorGuardado = proveedorRepositorio.save(proveedor);

        return convertirADTO(proveedorGuardado);
    }

    public void desactivarProveedor(@PathVariable int id) {
        // Primero se valida si el proveedor existe
        Proveedor proveedorADesactivar = proveedorRepositorio.findById(id)
                .orElseThrow(() -> new ProveedorNoEncontradoException("Proveedor no encontrado con el ID "+ id));

        // Se cambia el estado del proveedor a inactivo
        proveedorADesactivar.setIdEstado(estadoProveedorRepositorio.findById(2)
                .orElseThrow(() -> new RuntimeException("Estado con id 2 de inactivo no encontrado")));

        proveedorRepositorio.save(proveedorADesactivar);

    }

    public ProveedorDTO modificarProveedor(int id, ProveedorDTO proveedorDTO) {

        // Se valida que el proveedor exista
        Proveedor proveedorModificado = proveedorRepositorio.findById(id)
                .orElseThrow(() -> new ProveedorNoEncontradoException("No existe el proveedor"));

        // Mapear los nuevos campos DTO al producto existente
        proveedorModificado.setIdEstado(estadoProveedorRepositorio.findById(1).
                orElseThrow(() -> new RuntimeException("Estado con id 1 de inactivo no encontrado")));
        return getProveedorDTO(proveedorDTO, proveedorModificado);

    }



    public ProveedorDTO convertirADTO(Proveedor proveedor) {
        return new ProveedorDTO(
                proveedor.getId(),
                proveedor.getIdEstado().getId(),
                proveedor.getNombre(),
                proveedor.getTelefono(),
                proveedor.getEmail(),
                proveedor.getDireccion(),
                proveedor.getNit()
        );
    }

}
