package com.proyecto.invengest.service;


import com.proyecto.invengest.dto.ReporteDTO;
import com.proyecto.invengest.entities.Reporte;
import com.proyecto.invengest.exceptions.ReporteNoEncontradoException;
import com.proyecto.invengest.repository.ReporteRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class ReporteServicio {

    private final ReporteRepositorio reporteRepositorio;

    public ReporteServicio(ReporteRepositorio reporteRepositorio) {
        this.reporteRepositorio = reporteRepositorio;
    }

    // Listar reportes
    public List<ReporteDTO> listarReportes(){
        return reporteRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    // Buscar reporte por Id
    public ReporteDTO obtenerReporte(@PathVariable int id){
        Reporte reporte = reporteRepositorio.findById(id)
                .orElseThrow(() -> new ReporteNoEncontradoException("Reporte no encontrado con el ID: " + id));
        return convertirADTO(reporte);
    }

    // Eliminar un reporte
    public void eliminarReporte(@PathVariable int id){
        if (!reporteRepositorio.existsById(id)) {
            throw new ReporteNoEncontradoException("Reporte no encontrado con el ID: " + id);
        }
        reporteRepositorio.deleteById(id);
    }


    // Metodo para convertir en DTO
    private ReporteDTO convertirADTO (Reporte reporte){
        return new ReporteDTO(
                reporte.getIdReporte(),
                reporte.getUsuario().getIdUsuario(),
                reporte.getNombre(),
                reporte.getTipo(),
                reporte.getParametros(),
                reporte.getFechaCreacion(),
                reporte.getUltimaEjecucion()
        );
    }

}
