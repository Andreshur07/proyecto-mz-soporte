package com.marz.soporte.service;

import com.marz.soporte.dto.LoginRequest;
import com.marz.soporte.entity.Rol;
import com.marz.soporte.entity.Usuario;
import com.marz.soporte.exception.CredencialesInvalidasException;
import com.marz.soporte.repository.UsuarioRepository;
import com.marz.soporte.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {
    private AuthenticationManager authenticationManager;
    private UsuarioRepository usuarioRepository;
    private JwtService jwtService;
    private AuthService service;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        usuarioRepository = mock(UsuarioRepository.class);
        jwtService = mock(JwtService.class);
        service = new AuthService(authenticationManager, usuarioRepository, jwtService);
    }

    @Test
    void loginSolicitanteValidoDevuelveJwt() {
        Usuario usuario = usuario(1L, "solicitante@marz.local", Rol.SOLICITANTE, true);
        when(usuarioRepository.findByCorreoIgnoreCase(usuario.getCorreo())).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken(usuario)).thenReturn("jwt-firmado");
        var response = service.login(new LoginRequest(usuario.getCorreo(), "correcta"));
        assertThat(response.token()).isEqualTo("jwt-firmado");
        assertThat(response.tipo()).isEqualTo("Bearer");
        assertThat(response.usuario().rol()).isEqualTo(Rol.SOLICITANTE);
    }

    @Test
    void loginCoordinadorValidoDevuelveJwt() {
        Usuario usuario = usuario(2L, "coordinador@marz.local", Rol.COORDINADOR, true);
        when(usuarioRepository.findByCorreoIgnoreCase(usuario.getCorreo())).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken(usuario)).thenReturn("jwt-coordinador");
        var response = service.login(new LoginRequest(usuario.getCorreo(), "correcta"));
        assertThat(response.usuario().rol()).isEqualTo(Rol.COORDINADOR);
    }

    @Test
    void passwordIncorrectaEsRechazada() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));
        assertThatThrownBy(() -> service.login(new LoginRequest("x@marz.local", "incorrecta")))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void usuarioInactivoEsRechazado() {
        Usuario usuario = usuario(3L, "inactivo@marz.local", Rol.SOLICITANTE, false);
        when(usuarioRepository.findByCorreoIgnoreCase(usuario.getCorreo())).thenReturn(Optional.of(usuario));
        assertThatThrownBy(() -> service.login(new LoginRequest(usuario.getCorreo(), "correcta")))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    private Usuario usuario(Long id, String correo, Rol rol, boolean activo) {
        Usuario usuario = new Usuario();
        usuario.setId(id); usuario.setNombre("Usuario"); usuario.setCorreo(correo);
        usuario.setPassword("bcrypt"); usuario.setRol(rol); usuario.setActivo(activo);
        return usuario;
    }
}
