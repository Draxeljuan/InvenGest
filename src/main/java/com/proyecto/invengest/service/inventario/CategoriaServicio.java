package com.proyecto.invengest.service.inventario;


import com.proyecto.invengest.dto.CategoriaDTO;
import com.proyecto.invengest.entities.Categoria;
import com.proyecto.invengest.exceptions.CategoriaNoEncontradaException;
import com.proyecto.invengest.repository.CategoriaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServicio {

    private final CategoriaRepositorio categoriaRepositorio;

    public CategoriaServicio(CategoriaRepositorio categoriaRepositorio) {
        this.categoriaRepositorio = categoriaRepositorio;
    }

    // Listar Categorias con DTO
    public List<CategoriaDTO> listarCategorias() {
        return categoriaRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Buscar Categorias por Id
    public CategoriaDTO obtenerCategoria(@PathVariable int id) {
        Categoria categoria = categoriaRepositorio.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoria no encontrada con el ID: " + id));
        return convertirADTO(categoria);
    }

    // Crear una nueva Categoria
    public CategoriaDTO crearCategoria(CategoriaDTO categoriaDTO) {
        Categoria nuevaCategoria = convertirAEntidad(categoriaDTO);
        Categoria categoriaGuardada = categoriaRepositorio.save(nuevaCategoria);
        return convertirADTO(categoriaGuardada);

    }

    // Eliminar una Categoria por Id
    public void eliminarCategoria(@PathVariable int id) {
        if(!categoriaRepositorio.existsById(id)) {
            throw new CategoriaNoEncontradaException("Categoria no encontrada con el ID: " + id);
        }
        categoriaRepositorio.deleteById(id);
    }

    // Modificar una Categoria por Id
    public CategoriaDTO modificarCategoria(@PathVariable int id, @RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoriaExistente = categoriaRepositorio.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoria no encontrada con el ID: " + id));

        categoriaExistente.setNombre(categoriaDTO.getNombre());
        categoriaExistente.setDescripcion(categoriaDTO.getDescripcion());

        categoriaRepositorio.save(categoriaExistente);
        return convertirADTO(categoriaExistente);
    }



    // Convertir CategoriaDTO a Categoria
    private Categoria convertirAEntidad(CategoriaDTO categoriaDTO) {
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        System.out.println("Datos en la entidad antes de guardar: " + categoria);
        return categoria;
    }

    // Metodo para convertir a DTO
    private CategoriaDTO convertirADTO(Categoria categoria) {
        return new CategoriaDTO(
                categoria.getIdCategoria(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }

}
