package es.safareyes.educago.config;

import es.safareyes.educago.modelo.*;
import es.safareyes.educago.repositorios.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final IUsuarioRepository usuarioRepository;
    private final IEventoRepository eventoRepository;
    private final ICursoRepository cursoRepository;
    private final ILugarRepository lugarRepository;
    private final ICategoriaRepository categoriaRepository; // Inyectamos repo de Categoría
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            log.info("Inicializando datos de prueba...");

            // 1. Obtener o crear Curso
            Curso cursoGuardado = cursoRepository.findByNombre("2º DAM")
                    .orElseGet(() -> cursoRepository.save(Curso.builder()
                            .nombre("2º DAM")
                            .build()));

            // 2. Obtener o crear Lugar
            Lugar lugarGuardado = lugarRepository.findByNombre("Aula Magna")
                    .orElseGet(() -> lugarRepository.save(Lugar.builder()
                            .nombre("Aula Magna")
                            .aforoMaximo(100)
                            .build()));

            // 3. Obtener o crear Categoría
            Categoria categoriaGuardada = categoriaRepository.findByNombre("Tecnología")
                    .orElseGet(() -> categoriaRepository.save(Categoria.builder()
                            .nombre("Tecnología")
                            .color("#3B82F6")
                            .icono("code")
                            .build()));

            // 4. Usuarios base
            Usuario admin = Usuario.builder()
                    .nombre("Admin")
                    .apellidos("Sistema")
                    .email("admin@educago.es")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMIN)
                    .build();

            Usuario organizador = Usuario.builder()
                    .nombre("Laura")
                    .apellidos("Gómez")
                    .email("organizador@educago.es")
                    .password(passwordEncoder.encode("org12345"))
                    .rol(Rol.ORGANIZADOR)
                    .build();

            Usuario estudiante = Usuario.builder()
                    .nombre("Carlos")
                    .apellidos("Pérez")
                    .email("estudiante@educago.es")
                    .password(passwordEncoder.encode("est12345"))
                    .rol(Rol.ESTUDIANTE)
                    .curso(cursoGuardado)
                    .build();

            usuarioRepository.save(admin);
            Usuario orgGuardado = usuarioRepository.save(organizador);
            usuarioRepository.save(estudiante);

            // 5. Evento de prueba
            Evento eventoDemo = Evento.builder()
                    .titulo("Taller de Spring Boot 3 y Java 21")
                    .descripcion("Introducción a la construcción de APIs REST seguras con Spring Boot.")
                    .categoria(categoriaGuardada) // <-- Asignamos la categoría obligatoria
                    .lugar(lugarGuardado)
                    .inicio(LocalDateTime.now().plusDays(2))
                    .fin(LocalDateTime.now().plusDays(2).plusHours(3))
                    .fechaLimiteInscripcion(LocalDateTime.now().plusDays(1))
                    .aforoMaximo(20)
                    .estado(EstadoEvento.PUBLICADO)
                    .organizador(orgGuardado)
                    .build();

            eventoRepository.save(eventoDemo);

            log.info("Datos iniciales cargados con éxito.");
        }
    }
}