package com.proyecto.invengest.config.test;

import com.proyecto.invengest.security.JwtAuthFilter;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Anotación personalizada para tests de controladores que excluye automáticamente
 * la configuración de seguridad JWT y desactiva los filtros.

 * Uso:
 * CustomWebMvcTest(UsuarioControlador.class)
 * class UsuarioControladorTest { ... }
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebMvcTest(
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
public @interface CustomWebMvcTest {

    /**
     * Especifica los controladores a testear.
     * Ejemplo: @CustomWebMvcTest(UsuarioControlador.class)
     */
    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] value() default {};

    /**
     * Alias alternativo para 'value'
     */
    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] controllers() default {};
}
