package com.proyecto.invengest.service;

import com.proyecto.invengest.dto.AlertaDTO;
import com.proyecto.invengest.dto.ProductoDTO;
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


import java.time.LocalDate;
import java.util.*;
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
                .filter(alerta -> alerta.getIdProducto().getStock() < 10)
                .map(this::convertirADTO)
                .collect(Collectors.toList());


    }

    public List<Map<String, Object>> listarAlertasConProducto() {
        List<Alerta> alertas = alertaRepositorio.findAll()
                .stream()
                .filter(alerta -> alerta.getIdProducto().getStock() < 10)
                .collect(Collectors.toList());

        List<Map<String, Object>> alertasConProducto = new ArrayList<>();

        for (Alerta alerta : alertas) {
            Map<String, Object> alertaMap = new HashMap<>();
            alertaMap.put("idAlerta", alerta.getIdAlerta());
            alertaMap.put("idProducto", alerta.getIdProducto().getIdProducto());
            alertaMap.put("nombreProducto", alerta.getIdProducto().getNombre()); // Agregar nombre sin modificar DTO
            alertaMap.put("fecha", alerta.getFecha());
            alertaMap.put("idTipo", alerta.getIdTipo().getIdTipo());
            alertaMap.put("leida", alerta.getLeida());

            alertasConProducto.add(alertaMap);
        }

        return alertasConProducto;
    }

    public void limpiarAlertasInnecesarias() {
        List<Alerta> alertas = alertaRepositorio.findAll();

        for (Alerta alerta : alertas) {
            Producto producto = productoRepositorio.obtenerProductoPorId(alerta.getIdProducto().getIdProducto()).orElse(null);

            if (producto != null) {
                if (Objects.equals(producto.getIdEstado(), 4) || producto.getStock() >= 10) {
                    System.out.println("✅ Eliminando alerta ID: " + alerta.getIdAlerta() + " porque el producto está descontinuado o tiene suficiente stock");
                    alertaRepositorio.deleteById(alerta.getIdAlerta());
                }
            }
        }
    }

    // Obtener alertas con Id
    public AlertaDTO obtenerAlerta(@PathVariable int id) {
        Alerta alerta = alertaRepositorio.findById(id)
                .orElseThrow(() -> new AlertaNoEncontradaException("Alerta no encontrada con el ID: " + id));
        return convertirADTO(alerta);
    }


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
