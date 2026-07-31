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
import java.time.Duration;
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
    private final AgroSmartAIService aiService;

    public ProductoService(
            ProductoRepository repository,
            AgroSmartAIService aiService
    ) {
        this.repository = repository;
        this.aiService = aiService;
    }

    public Flux<Producto> obtenerProductosComercializables() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(ProductoMapper::toDominio)
                .map(ProductoFilters.A_MAYUSCULAS)
                .filter(ProductoFilters.IS_VALID)
                .doOnNext(ProductoFilters.LOG_PRODUCTO)
                .defaultIfEmpty(PRODUCTO_GENERICO);
    }

    public Mono<Producto> buscarPorId(Long id) {
        return Mono.fromCallable(() -> repository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(Mono::justOrEmpty)
                .map(ProductoMapper::toDominio)
                .switchIfEmpty(
                        Mono.error(new ProductoNoEncontradoException(id))
                );
    }

    public Mono<String> generarPublicidad(
            String producto,
            String audiencia
    ) {
        return Mono.fromCallable(
                        () -> aiService.generarPublicidad(
                                producto,
                                audiencia
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(30))
                .onErrorResume(error -> Mono.just(
                        "Publicidad no disponible en este momento ("
                                + error.getClass().getSimpleName()
                                + ")"
                ));
    }
}