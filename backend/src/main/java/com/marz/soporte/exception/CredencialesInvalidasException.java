package com.marz.soporte.exception;
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() { super("Credenciales incorrectas o usuario inactivo"); }
}
