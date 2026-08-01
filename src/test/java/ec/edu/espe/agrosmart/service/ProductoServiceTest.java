package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductoServiceTest {

    private ProductoRepository repository;
    private AgroSmartAIService aiService;
    private ProductoService productoService;

    @BeforeEach
    void prepararDependencias() {
        repository = mock(ProductoRepository.class);
        aiService = mock(AgroSmartAIService.class);
        productoService = new ProductoService(repository, aiService);
    }

    @Test
    void obtenerProductosComercializables_deberiaEmitirTresProductosValidos() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(
                crearEntidad(
                        1L,
                        "Rosas premium",
                        new BigDecimal("120.50"),
                        "ventas@agrosmart.ec"
                ),
                crearEntidad(
                        2L,
                        "Gypsophila blanca",
                        new BigDecimal("85.00"),
                        "comercial@agrosmart.ec"
                ),
                crearEntidad(
                        3L,
                        "Clavel selecto",
                        new BigDecimal("65.75"),
                        "pedidos@agrosmart.ec"
                ),
                crearEntidad(
                        4L,
                        "Tulipán promocional",
                        BigDecimal.ZERO,
                        "promociones@agrosmart.ec"
                ),
                crearEntidad(
                        5L,
                        "Orquídea sin contactos",
                        new BigDecimal("150.00"),
                        ""
                )
        ));

        // Act
        Flux<?> flujo =
                productoService.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void obtenerProductosComercializables_deberiaEmitirProductoGenericoCuandoTodosSonInvalidos() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(
                crearEntidad(
                        1L,
                        "Producto con precio cero",
                        BigDecimal.ZERO,
                        "ventas@agrosmart.ec"
                ),
                crearEntidad(
                        2L,
                        "Producto sin correos",
                        new BigDecimal("50.00"),
                        ""
                )
        ));

        // Act
        Flux<?> flujo =
                productoService.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextMatches(elemento ->
                        elemento instanceof ec.edu.espe.agrosmart.domain.Producto producto
                                && producto.getId().equals(0L)
                                && producto.getNombre()
                                .equals("PRODUCTO NO DISPONIBLE")
                )
                .verifyComplete();
    }

    @Test
    void buscarPorId_deberiaTerminarEnErrorCuandoElProductoNoExiste() {
        // Arrange
        when(repository.findById(9999L))
                .thenReturn(Optional.empty());

        // Act
        Mono<?> flujo = productoService.buscarPorId(9999L);

        // Assert
        StepVerifier.create(flujo)
                .verifyError(ProductoNoEncontradoException.class);
    }

    @Test
    void generarPublicidad_deberiaEmitirRespuestaDelModelo() {
        // Arrange
        String respuesta =
                "Rosas premium para floristerías que buscan excelencia.";

        when(aiService.generarPublicidad(
                "Rosas premium",
                "floristerías premium"
        )).thenReturn(respuesta);

        // Act
        Mono<String> flujo = productoService.generarPublicidad(
                "Rosas premium",
                "floristerías premium"
        );

        // Assert
        StepVerifier.create(flujo)
                .expectNext(respuesta)
                .verifyComplete();
    }

    @Test
    void generarPublicidad_deberiaEmitirMensajeAlternativoCuandoElModeloFalla() {
        // Arrange
        when(aiService.generarPublicidad(
                "Rosas premium",
                "floristerías premium"
        )).thenThrow(new IllegalStateException("Proveedor no disponible"));

        // Act
        Mono<String> flujo = productoService.generarPublicidad(
                "Rosas premium",
                "floristerías premium"
        );

        // Assert
        StepVerifier.create(flujo)
                .expectNext(
                        "Publicidad no disponible en este momento "
                                + "(IllegalStateException)"
                )
                .verifyComplete();
    }

    private ProductoEntity crearEntidad(
            Long id,
            String nombre,
            BigDecimal precio,
            String correos
    ) {
        ProductoEntity entity = new ProductoEntity(
                nombre,
                precio,
                100,
                "Flores",
                correos
        );
        entity.setIdProducto(id);
        return entity;
    }
}