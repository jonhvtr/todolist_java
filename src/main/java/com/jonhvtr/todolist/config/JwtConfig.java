package com.jonhvtr.todolist.config;

import com.jonhvtr.todolist.exception.key.RsaConversionException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtConfig {

    @Bean
    public RSAPublicKey publicKey(@Value("${jwt.public-key}") Resource key) {
        try {
            return RsaKeyConverters.x509().convert(key.getInputStream());
        } catch (Exception e) {
            throw new RsaConversionException("Invalid format or algorithm", "PUBLIC", e);
        }
    }

    @Bean
    public RSAPrivateKey privateKey(@Value("${jwt.private-key}") Resource key) {
        try {
            return RsaKeyConverters.pkcs8().convert(key.getInputStream());
        } catch (Exception e) {
            throw new RsaConversionException("Invalid format or algorithm", "PRIVATE", e);
        }
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

}