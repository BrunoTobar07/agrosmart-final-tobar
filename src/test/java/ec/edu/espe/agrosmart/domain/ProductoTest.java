package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductoTest {

    @Test
    void deberiaRetornarLosValoresAsignadosEnSusGetters() {
        // Arrange
        List<String> correos = List.of("ventas@agrosmart.ec");

        // Act
        Producto producto = new Producto(
                1L,
                "Rosas premium",
                "Flores",
                new BigDecimal("120.50"),
                correos
        );

        // Assert
        assertEquals(1L, producto.getId());
        assertEquals("Rosas premium", producto.getNombre());
        assertEquals("Flores", producto.getCategoria());
        assertEquals(
                new BigDecimal("120.50"),
                producto.getPrecioUsd()
        );
        assertEquals(correos, producto.getCorreosNotificacion());
    }

    @Test
    void deberiaProtegerSuEstadoCuandoSeModificaLaListaOriginal() {
        // Arrange
        List<String> correosOriginales = new ArrayList<>();
        correosOriginales.add("ventas@agrosmart.ec");

        Producto producto = new Producto(
                1L,
                "Rosas premium",
                "Flores",
                new BigDecimal("120.50"),
                correosOriginales
        );

        // Act
        correosOriginales.add("ataque@externo.com");

        // Assert
        assertEquals(1, producto.getCorreosNotificacion().size());
        assertEquals(
                "ventas@agrosmart.ec",
                producto.getCorreosNotificacion().getFirst()
        );
        assertNotSame(
                correosOriginales,
                producto.getCorreosNotificacion()
        );
    }

    @Test
    void deberiaEntregarUnaCopiaDeSoloLecturaDesdeElGetter() {
        // Arrange
        Producto producto = new Producto(
                1L,
                "Rosas premium",
                "Flores",
                new BigDecimal("120.50"),
                List.of("ventas@agrosmart.ec")
        );

        // Act
        List<String> primeraLectura =
                producto.getCorreosNotificacion();
        List<String> segundaLectura =
                producto.getCorreosNotificacion();

        // Assert
        assertNotSame(primeraLectura, segundaLectura);
        assertThrows(
                UnsupportedOperationException.class,
                () -> primeraLectura.add("ataque@externo.com")
        );
        assertEquals(
                List.of("ventas@agrosmart.ec"),
                producto.getCorreosNotificacion()
        );
    }
}