package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductoFiltersTest {

    @Test
    void deberiaAceptarProductoConPrecioPositivoYCorreos() {
        Producto producto = crearProducto(
                new BigDecimal("120.50"),
                List.of("ventas@agrosmart.ec")
        );

        boolean resultado = ProductoFilters.IS_VALID.test(producto);

        assertTrue(resultado);
    }

    @Test
    void deberiaRechazarProductoConPrecioCero() {
        Producto producto = crearProducto(
                BigDecimal.ZERO,
                List.of("ventas@agrosmart.ec")
        );

        boolean resultado = ProductoFilters.IS_VALID.test(producto);

        assertFalse(resultado);
    }

    @Test
    void deberiaRechazarProductoSinCorreosDeNotificacion() {
        Producto producto = crearProducto(
                new BigDecimal("120.50"),
                List.of()
        );

        boolean resultado = ProductoFilters.IS_VALID.test(producto);

        assertFalse(resultado);
    }

    @Test
    void deberiaCrearNuevoProductoConNombreEnMayusculas() {
        Producto original = crearProducto(
                new BigDecimal("120.50"),
                List.of("ventas@agrosmart.ec")
        );

        Producto transformado =
                ProductoFilters.A_MAYUSCULAS.apply(original);

        assertNotSame(original, transformado);
        assertEquals("ROSAS PREMIUM", transformado.getNombre());
        assertEquals("Rosas premium", original.getNombre());
    }

    private Producto crearProducto(
            BigDecimal precio,
            List<String> correos
    ) {
        return new Producto(
                1L,
                "Rosas premium",
                "Flores",
                precio,
                correos
        );
    }
}