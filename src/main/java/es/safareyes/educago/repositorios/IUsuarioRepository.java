package es.safareyes.educago.repositorios;

import es.safareyes.educago.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    // Q1: Método derivado por nombre (comprobar email único en registro)
    boolean existsByEmailIgnoreCase(String email);

    Optional<Usuario> findByEmail(String email);
}