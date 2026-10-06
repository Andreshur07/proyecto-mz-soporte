package com.marz.soporte.security;

import com.marz.soporte.entity.Rol;
import com.marz.soporte.entity.Usuario;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    private static final String SECRET =
            "c2lzdGVtYS1zb3BvcnRlLWRlc2Fycm9sbG8tY2xhdmUtand0LTIwMjYtMzItYnl0ZXM=";

    @Test
    void tokenFirmadoIdentificaAlUsuarioYEsValido() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNombre("Solicitante");
        usuario.setCorreo("solicitante@marz.local");
        usuario.setRol(Rol.SOLICITANTE);
        usuario.setActivo(true);
        JwtService service = new JwtService(SECRET, 3_600_000);

        String token = service.generarToken(usuario);

        assertThat(token.split("\\.")).hasSize(3);
        assertThat(service.obtenerCorreo(token)).isEqualTo(usuario.getCorreo());
        assertThat(service.esValido(token, usuario.getCorreo())).isTrue();
    }
}
