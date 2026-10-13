package es.safareyes.educago.repositorios;

import es.safareyes.educago.modelo.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICursoRepository extends JpaRepository<Curso, Long> {

    Optional<Curso> findByNombreIgnoreCase(String nombre);
    Optional<Curso> findByNombre(String nombre);
}