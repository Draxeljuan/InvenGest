package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.Alerta;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.enumeradores.leidaAlerta;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertaRepositorio extends JpaRepository<Alerta, Integer> {

    // Busqueda de alerta por prodcuto y estado de leida

    Optional<Alerta> findByIdProductoAndLeida(@NotNull Producto idProducto, leidaAlerta leida);

}
