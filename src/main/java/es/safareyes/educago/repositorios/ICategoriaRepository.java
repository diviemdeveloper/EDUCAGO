package es.safareyes.educago.repositorios;

import es.safareyes.educago.modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ICategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
}