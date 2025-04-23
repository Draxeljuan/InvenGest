package com.proyecto.invengest.controllers;


import com.proyecto.invengest.dto.CategoriaDTO;
import com.proyecto.invengest.entities.Categoria;
import com.proyecto.invengest.service.CategoriaServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/categorias")
public class CategoriaControlador {

    private final CategoriaServicio categoriaServicio;

    public CategoriaControlador(CategoriaServicio categoriaServicio) {
        this.categoriaServicio = categoriaServicio;
    }

    // Obtener las Categorias con DTO
    @GetMapping
    public List<CategoriaDTO> listarCategorias() {
        return categoriaServicio.listarCategorias();
    }

    // Obtener las Categorias por id con DTO
    @GetMapping("/{id}")
    public CategoriaDTO obtenerCategoria(@PathVariable int id) {
        return categoriaServicio.obtenerCategoria(id);
    }

    // Crear una nueva Categoria
    @PostMapping("/crear")
    public ResponseEntity<CategoriaDTO> crearCategoria(@RequestBody @Valid CategoriaDTO categoriaDTO) {
        System.out.println("Datos recibidos: " + categoriaDTO);
        CategoriaDTO nuevaCategoria = categoriaServicio.crearCategoria(categoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    // Eliminar una Categoria por id
    @DeleteMapping("/{id}")
    public void eliminarCategoria(@PathVariable int id) {
        categoriaServicio.eliminarCategoria(id);
    }

    // Modificar una Categoria
    @PutMapping("/{id}")
    public CategoriaDTO modificarCategoria(@PathVariable int id, @RequestBody CategoriaDTO categoriaDTO) {
        return categoriaServicio.modificarCategoria(id, categoriaDTO);
    }


}
