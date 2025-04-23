package com.proyecto.invengest.service;

import com.proyecto.invengest.dto.AlertaDTO;
import com.proyecto.invengest.entities.Alerta;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.entities.TipoAlerta;
import com.proyecto.invengest.enumeradores.leidaAlerta;
import com.proyecto.invengest.exceptions.AlertaNoEncontradaException;
import com.proyecto.invengest.exceptions.TipoAlertaNoEncontradaException;
import com.proyecto.invengest.repository.AlertaRepositorio;
import com.proyecto.invengest.repository.ProductoRepositorio;
import com.proyecto.invengest.repository.TipoAlertaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlertaServicio {

    private final AlertaRepositorio alertaRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final TipoAlertaRepositorio tipoAlertaRepositorio;

    public AlertaServicio(AlertaRepositorio alertaRepositorio, ProductoRepositorio productoRepositorio, TipoAlertaRepositorio tipoAlertaRepositorio) {
        this.alertaRepositorio = alertaRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.tipoAlertaRepositorio = tipoAlertaRepositorio;
    }

    // Obtener lista de alertas con DTO
    public List<AlertaDTO> listarAlertas() {
        return alertaRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener alertas con Id
    public AlertaDTO obtenerAlerta(@PathVariable int id) {
        Alerta alerta = alertaRepositorio.findById(id)
                .orElseThrow(() -> new AlertaNoEncontradaException("Alerta no encontrada con el ID: " + id));
        return convertirADTO(alerta);
    }

    // Crear una nueva alerta
//    public AlertaDTO crearAlerta(AlertaDTO alertaDTO) {
//        Alerta alerta = new Alerta();
//
//        // Resolver idProducto
//        alerta.setIdProducto(productoRepositorio.findById(alertaDTO.getIdProducto())
//                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + alertaDTO.getIdProducto())));
//
//        // Resolver idTipo
//        alerta.setIdTipo(tipoAlertaRepositorio.findById(alertaDTO.getTipo())
//                .orElseThrow(() -> new RuntimeException("Tipo de alerta no encontrado con ID: " + alertaDTO.getTipo())));
//
//        alerta.setFecha(alertaDTO.getFecha());
//        alerta.setLeida(alertaDTO.getLeida());
//
//        Alerta nuevaAlerta = alertaRepositorio.save(alerta);
//
//        return convertirADTO(nuevaAlerta);
//    }

    // Generacion de Alertas
    // Terminar de arreglar para evaluar las alertas correctamente
    public void generarAlertaStock(Producto producto) {
        // Buscar si ya existe una alerta activa para este producto
        Optional<Alerta> alertaExistente = alertaRepositorio.findByIdProductoAndLeida(producto, leidaAlerta.no_visto);

        // Verificar si el producto está sin stock o con stock bajo
        TipoAlerta tipoAlerta;
        if (producto.getStock() == 0) {
            tipoAlerta = tipoAlertaRepositorio.findById(2)
                    .orElseThrow(() -> new TipoAlertaNoEncontradaException("Alerta no encontrada con ID 2"));
        } else if (producto.getStock() < 10) { // Se usa el umbral fijo de 10
            tipoAlerta = tipoAlertaRepositorio.findById(1)
                    .orElseThrow(() -> new TipoAlertaNoEncontradaException("Alerta no encontrada con ID 1"));
        } else {
            return; // Si el stock es mayor o igual a 10, no hay alerta
        }

        // Si ya existe una alerta y el producto sigue en el mismo estado, no crear una nueva
        if (alertaExistente.isPresent()) {
            Alerta alerta = alertaExistente.get();

            // Si el producto pasó de stock bajo a sin stock, actualizar la alerta existente
            if (producto.getStock() == 0 && alerta.getIdTipo().getIdTipo() == 1) {
                alerta.setIdTipo(tipoAlerta); // Cambiar alerta a "Sin Stock"
                alertaRepositorio.save(alerta);
            }
            return; // Evitar crear alertas duplicadas en el mismo estado
        }

        // Crear una nueva alerta si no existía previamente
        Alerta nuevaAlerta = new Alerta();
        nuevaAlerta.setIdProducto(producto);
        nuevaAlerta.setIdTipo(tipoAlerta);
        nuevaAlerta.setFecha(LocalDate.now());
        nuevaAlerta.setLeida(leidaAlerta.no_visto); // Alerta no leída inicialmente

        alertaRepositorio.save(nuevaAlerta);
    }

    // Eliminar una alerta
    public void eliminarAlerta(@PathVariable int id) {
        if(!alertaRepositorio.existsById(id)) {
            throw new AlertaNoEncontradaException("Alerta no encontrada con el ID: " + id);
        }
        alertaRepositorio.deleteById(id);
    }


    // Metodo para convertir a DTO
    private AlertaDTO convertirADTO(Alerta alerta) {
        return new AlertaDTO(
                alerta.getIdAlerta(),
                alerta.getIdProducto().getIdProducto(),
                alerta.getFecha(),
                alerta.getIdTipo().getIdTipo(),
                alerta.getLeida()
        );
    }

}
