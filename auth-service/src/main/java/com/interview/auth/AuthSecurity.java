package com.interview.auth;
import java.security.interfaces.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.core.io.Resource;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.spec.*;
import java.util.Base64;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
@Configuration
public class AuthSecurity {


    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityWebFilterChain chain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
          .authorizeExchange(e -> e.anyExchange().permitAll()).build();
    }

    private byte[] pem(Resource resource) throws Exception {
        String s = resource.getContentAsString(StandardCharsets.UTF_8);
        return Base64.getMimeDecoder().decode(s.replaceAll("-----BEGIN [^-]+-----","")
            .replaceAll("-----END [^-]+-----","").replaceAll("\\s",""));
    }

    @Bean
    JwtEncoder jwtEncoder(@Value("file:${JWT_PRIVATE_KEY_PATH:./keys/private.pem}") Resource privateKey,
                              @Value("file:${JWT_PUBLIC_KEY_PATH:./keys/public.pem}") Resource publicKey) throws Exception {
        var factory=KeyFactory.getInstance("RSA");
        var priv=(RSAPrivateKey)factory.generatePrivate(new PKCS8EncodedKeySpec(pem(privateKey)));
        var pub=(RSAPublicKey)factory.generatePublic(new X509EncodedKeySpec(pem(publicKey)));
        RSAKey key=new RSAKey
                .Builder(pub)
                .privateKey(priv)
                .keyID("task-key")
                .build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
    }
}
