package es.safareyes.educago.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ModeloUnitTest {

    @Test
    @DisplayName("1. Usuario: Debe crear un usuario con sus atributos y rol")
    void testUsuario() {
        Usuario usuario = Usuario.builder()
                .nombre("Juan")
                .apellidos("García")
                .email("juan@educago.es")
                .password("secret123")
                .rol(Rol.ESTUDIANTE)
                .build();

        assertThat(usuario.getNombre()).isEqualTo("Juan");
        assertThat(usuario.getEmail()).isEqualTo("juan@educago.es");
        assertThat(usuario.getRol()).isEqualTo(Rol.ESTUDIANTE);
    }

    @Test
    @DisplayName("2. Curso: Debe asignar correctamente el nombre del curso")
    void testCurso() {
        Curso curso = Curso.builder()
                .nombre("2º DAM")
                .build();

        assertThat(curso.getNombre()).isEqualTo("2º DAM");
    }

    @Test
    @DisplayName("3. Lugar: Debe instanciar un lugar con aforo máximo")
    void testLugar() {
        Lugar lugar = Lugar.builder()
                .nombre("Aula Magna")
                .aforoMaximo(150)
                .build();

        assertThat(lugar.getNombre()).isEqualTo("Aula Magna");
        assertThat(lugar.getAforoMaximo()).isEqualTo(150);
    }

    @Test
    @DisplayName("4. Categoria: Debe guardar color, icono y nombre")
    void testCategoria() {
        Categoria categoria = Categoria.builder()
                .nombre("Tecnología")
                .color("#3B82F6")
                .icono("code")
                .build();

        assertThat(categoria.getNombre()).isEqualTo("Tecnología");
        assertThat(categoria.getColor()).isEqualTo("#3B82F6");
        assertThat(categoria.getIcono()).isEqualTo("code");
    }

    @Test
    @DisplayName("5. Evento: Debe enlazar organizador, lugar y categoría al evento")
    void testEvento() {
        Usuario organizador = Usuario.builder().nombre("Laura").rol(Rol.ORGANIZADOR).build();
        Lugar lugar = Lugar.builder().nombre("Salón de Actos").aforoMaximo(50).build();
        Categoria categoria = Categoria.builder().nombre("Robótica").color("#FF0000").icono("robot").build();

        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fin = inicio.plusHours(2);

        Evento evento = Evento.builder()
                .titulo("Taller de Robótica")
                .descripcion("Construcción de sensores")
                .aforoMaximo(30)
                .estado(EstadoEvento.PUBLICADO)
                .inicio(inicio)
                .fin(fin)
                .organizador(organizador)
                .lugar(lugar)
                .categoria(categoria)
                .build();

        assertThat(evento.getTitulo()).isEqualTo("Taller de Robótica");
        assertThat(evento.getEstado()).isEqualTo(EstadoEvento.PUBLICADO);
        assertThat(evento.getOrganizador()).isEqualTo(organizador);
        assertThat(evento.getLugar().getNombre()).isEqualTo("Salón de Actos");
    }

    @Test
    @DisplayName("6. Inscripcion: Debe gestionar estado, tipo y asistencia")
    void testInscripcion() {
        Usuario estudiante = Usuario.builder().nombre("Carlos").build();
        Evento evento = Evento.builder().titulo("Conferencia").build();

        Inscripcion inscripcion = Inscripcion.builder()
                .usuario(estudiante)
                .evento(evento)
                .tipo(TipoInscripcion.ASISTIRE)
                .estado(EstadoInscripcion.CON_PLAZA)
                .asistio(false)
                .build();

        assertThat(inscripcion.getUsuario()).isEqualTo(estudiante);
        assertThat(inscripcion.getTipo()).isEqualTo(TipoInscripcion.ASISTIRE);
        assertThat(inscripcion.getEstado()).isEqualTo(EstadoInscripcion.CON_PLAZA);
        assertThat(inscripcion.getAsistio()).isFalse();
    }

    @Test
    @DisplayName("7. Publicacion: Debe asociar texto, autor y evento")
    void testPublicacion() {
        Usuario autor = Usuario.builder().nombre("Admin").build();
        Evento evento = Evento.builder().titulo("Charla").build();

        Publicacion publicacion = Publicacion.builder()
                .texto("Novedades sobre la charla")
                .autor(autor)
                .evento(evento)
                .build();

        assertThat(publicacion.getTexto()).isEqualTo("Novedades sobre la charla");
        assertThat(publicacion.getAutor()).isEqualTo(autor);
        assertThat(publicacion.getEvento()).isEqualTo(evento);
    }

    @Test
    @DisplayName("8. FotoPublicacion: Debe almacenar metadatos de la imagen subida")
    void testFotoPublicacion() {
        Publicacion publicacion = Publicacion.builder().texto("Post").build();

        FotoPublicacion foto = FotoPublicacion.builder()
                .url("http://localhost:8080/uploads/foto1.png")
                .nombreOriginal("foto1.png")
                .tamano(1024L)
                .publicacion(publicacion)
                .build();

        assertThat(foto.getUrl()).contains("foto1.png");
        assertThat(foto.getTamano()).isEqualTo(1024L);
        assertThat(foto.getPublicacion()).isEqualTo(publicacion);
    }

    @Test
    @DisplayName("9. Notificacion: Debe reflejar el mensaje y el estado de lectura")
    void testNotificacion() {
        Usuario usuario = Usuario.builder().nombre("Carlos").build();

        Notificacion notificacion = Notificacion.builder()
                .usuario(usuario)
                .mensaje("Recordatorio: El evento empieza en 24h")
                .tipo(TipoNotificacion.RECORDATORIO_24H)
                .leida(false)
                .build();

        assertThat(notificacion.getMensaje()).isEqualTo("Recordatorio: El evento empieza en 24h");
        assertThat(notificacion.getTipo()).isEqualTo(TipoNotificacion.RECORDATORIO_24H);
        assertThat(notificacion.getLeida()).isFalse();
    }

    @Test
    @DisplayName("10. CambioEvento: Debe registrar las modificaciones realizadas en un evento")
    void testCambioEvento() {
        Usuario autor = Usuario.builder().nombre("Laura").build();
        Evento evento = Evento.builder().titulo("Evento").build();

        CambioEvento cambio = CambioEvento.builder()
                .autor(autor)
                .evento(evento)
                .campo("aforoMaximo")
                .valorAnterior("20")
                .valorNuevo("30")
                .fecha(LocalDateTime.now())
                .build();

        assertThat(cambio.getCampo()).isEqualTo("aforoMaximo");
        assertThat(cambio.getValorAnterior()).isEqualTo("20");
        assertThat(cambio.getValorNuevo()).isEqualTo("30");
        assertThat(cambio.getAutor()).isEqualTo(autor);
    }
}