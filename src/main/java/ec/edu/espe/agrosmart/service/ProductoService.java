package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.domain.ProductoFilters;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.mapper.ProductoMapper;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {

    private static final Producto PRODUCTO_GENERICO = new Producto(
            0L,
            "PRODUCTO NO DISPONIBLE",
            "Flores",
            BigDecimal.ZERO,
            List.of()
    );

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public Flux<Producto> obtenerProductosComercializables() {
        return Mono.fromCallable(repository::findAll)
                // JPA es bloqueante: la consulta se ejecuta fuera del event loop de Netty.
                .subscribeOn(Schedulers.boundedElastic())
                // Convierte el Mono<List<ProductoEntity>> en un Flux<ProductoEntity>.
                .flatMapMany(Flux::fromIterable)
                // Convierte cada entidad JPA en el modelo de dominio inmutable.
                .map(ProductoMapper::toDominio)
                // Crea una nueva instancia con el nombre en mayúsculas.
                .map(ProductoFilters.A_MAYUSCULAS)
                // Conserva únicamente productos con precio positivo y correos.
                .filter(ProductoFilters.IS_VALID)
                // Registra cada producto emitido sin modificarlo.
                .doOnNext(ProductoFilters.LOG_PRODUCTO)
                // Emite un producto genérico si no existen productos válidos.
                .defaultIfEmpty(PRODUCTO_GENERICO);
    }

    public Mono<Producto> buscarPorId(Long id) {
        return Mono.fromCallable(() -> repository.findById(id))
                // findById de JPA también es bloqueante.
                .subscribeOn(Schedulers.boundedElastic())
                // Convierte Optional.empty() en Mono.empty().
                .flatMap(Mono::justOrEmpty)
                // Convierte la entidad encontrada al modelo inmutable.
                .map(ProductoMapper::toDominio)
                // Cambia el flujo vacío por un error de dominio.
                .switchIfEmpty(
                        Mono.error(new ProductoNoEncontradoException(id))
                );
    }
}