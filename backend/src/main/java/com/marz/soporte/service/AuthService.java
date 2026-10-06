package com.marz.soporte.service;

import com.marz.soporte.dto.LoginRequest;
import com.marz.soporte.dto.LoginResponse;
import com.marz.soporte.dto.UsuarioResponse;
import com.marz.soporte.entity.Usuario;
import com.marz.soporte.exception.CredencialesInvalidasException;
import com.marz.soporte.repository.UsuarioRepository;
import com.marz.soporte.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    public AuthService(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.correo(), request.password()));
        } catch (AuthenticationException exception) {
            throw new CredencialesInvalidasException();
        }
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.correo())
                .filter(Usuario::isActivo).orElseThrow(CredencialesInvalidasException::new);
        String token = jwtService.generarToken(usuario);
        return new LoginResponse(token, "Bearer", new UsuarioResponse(
                usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol()));
    }
}
