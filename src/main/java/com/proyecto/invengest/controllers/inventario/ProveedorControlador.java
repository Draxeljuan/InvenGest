package com.proyecto.invengest.controllers.inventario;



import com.proyecto.invengest.dto.ProveedorDTO;
import com.proyecto.invengest.service.inventario.ProveedorServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedores")
public class ProveedorControlador {

    private final ProveedorServicio proveedorServicio;

    public ProveedorControlador(ProveedorServicio proveedorServicio) {
        this.proveedorServicio = proveedorServicio;
    }
    // Listar todos los proveedores disponibles
    @GetMapping
    public List<ProveedorDTO> listarProveedores(){
        return proveedorServicio.listarProveedores();
    }

    // obtener un proveedor por id
    @GetMapping("/{id}")
    public ProveedorDTO obtenerProveedor(@PathVariable int id) {
        return proveedorServicio.obtenerProveedor(id);
    }

    // obtener proveedor por nombre
    @GetMapping("/buscar")
    public List<ProveedorDTO> obtenerPorNombreProveedor(@RequestParam String nombre) {
        return proveedorServicio.buscarPorNombre(nombre);
    }

    // crear un proveedor

    @PostMapping("/crear")
    public ProveedorDTO crearProveedor (@RequestBody ProveedorDTO proveedorDTO){
        return proveedorServicio.crearProveedor(proveedorDTO);
    }

    // desabilitar un proveedor
    @PutMapping("/desabilitar/{id}")
    public void desabilitarProveedor(@PathVariable int id){
        proveedorServicio.desactivarProveedor(id);
    }

    // modificar un proveedor
    @PutMapping("/{id}")
    public ProveedorDTO modificarProveedor (@PathVariable int id, @RequestBody ProveedorDTO proveedorDTO){
        return proveedorServicio.modificarProveedor(id, proveedorDTO);
    }









}
