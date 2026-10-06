package com.marz.soporte.config;

import com.marz.soporte.entity.Rol;
import com.marz.soporte.entity.Usuario;
import com.marz.soporte.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.dev-seed.enabled", havingValue = "true")
public class DatosDesarrolloConfig implements ApplicationRunner {
    public static final String PASSWORD_DESARROLLO = "Marz2026!";
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    public DatosDesarrolloConfig(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(ApplicationArguments args) {
        crearSiNoExiste("Solicitante Desarrollo", "solicitante@marz.local", Rol.SOLICITANTE);
        crearSiNoExiste("Agente Desarrollo", "agente@marz.local", Rol.AGENTE);
        crearSiNoExiste("Coordinador Desarrollo", "coordinador@marz.local", Rol.COORDINADOR);
        crearSiNoExiste("Auditor Desarrollo", "auditor@marz.local", Rol.AUDITOR);
    }
    private void crearSiNoExiste(String nombre, String correo, Rol rol) {
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) return;
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(PASSWORD_DESARROLLO));
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }
}
