package com.jonhvtr.todolist.exception.key;

public class RsaConversionException extends RuntimeException {
    private final String keyType;

    public RsaConversionException(String message, String keyType, Throwable cause) {
        super(String.format("Error converting RSA %s key %s", keyType, message), cause);
        this.keyType = keyType;
    }

    public String getKeyType() {
        return keyType;
    }
}
