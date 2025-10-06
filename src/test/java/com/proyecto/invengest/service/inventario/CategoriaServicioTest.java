package com.proyecto.invengest.service.inventario;

import com.proyecto.invengest.dto.CategoriaDTO;
import com.proyecto.invengest.entities.Categoria;
import com.proyecto.invengest.exceptions.CategoriaNoEncontradaException;
import com.proyecto.invengest.repository.CategoriaRepositorio;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServicioTest {

    @Mock
    private CategoriaRepositorio categoriaRepositorio;

    @InjectMocks
    private CategoriaServicio categoriaServicio;

    private Categoria categoria;
    private CategoriaDTO categoriaDTO;


    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .idCategoria(1)
                .nombre("Papeleria")
                .descripcion("Productos de miscelania")
                .build();

        categoriaDTO = new CategoriaDTO(1, "Papeleria", "Productos de miscelania");
    }

    @Test
    public void listarCategorias() {
        // Se crea una categoria nueva para probar el listado
        Categoria otraCategoria = Categoria.builder()
                .idCategoria(2)
                .nombre("Arreglos")
                .descripcion("Regalos personalizados")
                .build();
        // Cuando se consulte al repositorio este retorna la categoria inicial y la nueva
        when(categoriaRepositorio.findAll()).thenReturn(List.of(categoria, otraCategoria));
        // se llama al servicio de listar categorias
        List<CategoriaDTO> resultado = categoriaServicio.listarCategorias();
        // se valida los resultados
        assertEquals(2, resultado.size(), "solo existen 2 categorias en el test");
        assertEquals("Papeleria", resultado.get(0).getNombre());
        assertEquals("Arreglos", resultado.get(1).getNombre());
    }

    @Test
    public void obtenerCategoria() {
        // Se consulta la categoria creada inicialmente en setUp
        when(categoriaRepositorio.findById(1)).thenReturn(Optional.of(categoria));

        CategoriaDTO resultado = categoriaServicio.obtenerCategoria(1);
        // Se valida que los datos guardados coincidan
        assertEquals("Papeleria", resultado.getNombre());
        assertEquals("Productos de miscelania", resultado.getDescripcion());
    }

    @Test
    public void obtenerCategoriaNoExiste() {
        // Se establece una consulta para una categoria inexistente
        when(categoriaRepositorio.findById(99)).thenReturn(Optional.empty());
        // Se valida la excepción que debe aparecer cuando no se encuentre la categoria
        assertThrows(CategoriaNoEncontradaException.class, () -> categoriaServicio.obtenerCategoria(99));
    }


    @Test
    void crearCategoria() {
        // Se simula la creación de una categoria en el repositorio
        when(categoriaRepositorio.save(ArgumentMatchers.<Categoria>any())).thenReturn(categoria);
        // Al llamar al servicio se ejecuta el proceso de guardado en el repositorio
        CategoriaDTO resultado = categoriaServicio.crearCategoria(categoriaDTO);
        // Se valida la información almacenada en el repositorio del test
        assertEquals("Papeleria", resultado.getNombre());
        verify(categoriaRepositorio).save(ArgumentMatchers.<Categoria>any());
    }

    @Test
    public void eliminarCategoria() {

        // Cuando se consulte si existe una categoria con id 1 retornamos true
        when(categoriaRepositorio.existsById(1)).thenReturn(true);
        // El servicio elimina la categoria que se asigna al repositorio temporal
        categoriaServicio.eliminarCategoria(1);
        // Se valida la eliminación
        verify(categoriaRepositorio).deleteById(1);

    }

    @Test
    public void eliminarCategoriaNoExiste() {
        // Se asigna una categoria inexistente a la consulta del repositorio
        when(categoriaRepositorio.existsById(99)).thenReturn(false);
        // Como el resultado de la consulta es false
        // Se valida la excepción obtenida
        assertThrows(CategoriaNoEncontradaException.class, () -> categoriaServicio.eliminarCategoria(99));
    }
    @Test
    public void modificarCategoria() {
        // Cuando se consulte o modifique una categoria en el repositorio
        // se usara la categoria creada en SetUp
        when(categoriaRepositorio.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepositorio.save(ArgumentMatchers.<Categoria>any())).thenReturn(categoria);
        // Creación de una nueva categoria
        CategoriaDTO dtoModificado = new CategoriaDTO(1, "Desechables", "Elementos no reutilizables");
        // Llamada al servicio que crea una nueva categoria
        CategoriaDTO resultado = categoriaServicio.modificarCategoria(1, dtoModificado);
        // Validación de la categoria creada
        assertEquals("Desechables", resultado.getNombre());
        assertEquals("Elementos no reutilizables", resultado.getDescripcion());
        verify(categoriaRepositorio).save(categoria);


    }

    @Test
    public void modificarCategoriaNoExiste() {
        // Cuando se consulte una categoria inexistente se retornara empty
        when(categoriaRepositorio.findById(99)).thenReturn(Optional.empty());
        // Parametros a modificar en la categoria que no existe
        CategoriaDTO dto = new CategoriaDTO(99, "X", "Y");
        // Se valida la excepción que arroja el sistema
        // Al intentar modificar una categoria que no existe
        assertThrows(CategoriaNoEncontradaException.class, () -> categoriaServicio.modificarCategoria(99, dto));

    }
}