package es.safareyes.educago.repositorios;

import es.safareyes.educago.modelo.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ILugarRepository extends JpaRepository<Lugar, Long> {

    Optional<Lugar> findByNombreIgnoreCase(String nombre);
    Optional<Lugar> findByNombre(String nombre);
}