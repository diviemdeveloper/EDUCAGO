package es.safareyes.educago.service.impl;

import es.safareyes.educago.dto.request.LoginRequest;
import es.safareyes.educago.dto.request.RegistroRequest;
import es.safareyes.educago.dto.response.AuthResponse;
import es.safareyes.educago.exception.ConflictoException;
import es.safareyes.educago.exception.RecursoNoEncontradoException;
import es.safareyes.educago.modelo.Curso;
import es.safareyes.educago.modelo.Rol;
import es.safareyes.educago.modelo.Usuario;
import es.safareyes.educago.repositorios.ICursoRepository;
import es.safareyes.educago.repositorios.IUsuarioRepository;
import es.safareyes.educago.security.JwtUtils;
import es.safareyes.educago.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioRepository usuarioRepository;
    private final ICursoRepository cursoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictoException("El email ya está registrado");
        }

        Curso curso = null;
        if (request.cursoId() != null) {
            curso = cursoRepository.findById(request.cursoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado"));
        }

        Usuario usuario = Usuario.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nombre(request.nombre())
                .apellidos(request.apellidos())
                .rol(Rol.ESTUDIANTE) // Alta por defecto como ESTUDIANTE (3.5)
                .curso(curso)
                .build();

        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtUtils.generateToken(userDetails);

        return new AuthResponse(token, "Bearer", usuario.getEmail(), usuario.getRol().name());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        Usuario usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtUtils.generateToken(userDetails);

        return new AuthResponse(token, "Bearer", usuario.getEmail(), usuario.getRol().name());
    }
}