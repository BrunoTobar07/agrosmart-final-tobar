package ec.edu.espe.agrosmart.config;

import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DatosInicialesConfig {

    @Bean
    CommandLineRunner sembrarProductos(ProductoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new ProductoEntity(
                                "Rosas de exportación premium",
                                new BigDecimal("120.50"),
                                500,
                                "Flores",
                                "ventas@agrosmart.ec"
                        ),
                        new ProductoEntity(
                                "Gypsophila blanca",
                                new BigDecimal("85.00"),
                                350,
                                "Flores",
                                "comercial@agrosmart.ec"
                        ),
                        new ProductoEntity(
                                "Clavel ecuatoriano selecto",
                                new BigDecimal("65.75"),
                                420,
                                "Flores",
                                "pedidos@agrosmart.ec"
                        ),
                        new ProductoEntity(
                                "Tulipán promocional",
                                new BigDecimal("0.00"),
                                200,
                                "Flores",
                                "promociones@agrosmart.ec"
                        ),
                        new ProductoEntity(
                                "Orquídea sin contactos",
                                new BigDecimal("150.00"),
                                100,
                                "Flores",
                                ""
                        )
                ));
            }
        };
    }
}