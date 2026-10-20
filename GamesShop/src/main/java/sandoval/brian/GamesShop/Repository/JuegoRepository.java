package sandoval.brian.GamesShop.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sandoval.brian.GamesShop.Models.Juego;

import java.util.List;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {
    List<Juego> findByTituloContainingIgnoreCase(String titulo);

    List<Juego> findByDescripcionContainingIgnoreCase(String descripcion);

    List<Juego> findByTituloStratWithIgnoreCase(String letra);

    @Query("SELECT j FROM Juego j WHERE j.precio< :precioComparacion AND LOWER(j.titulo) LIKE CONCAT(:letra,'%')")
    List<Juego> buscarPrecioMenorAQueEmpiecePor(@Param("precioComparacion") Double precioComparacion, @Param("letra") String letra);
}
