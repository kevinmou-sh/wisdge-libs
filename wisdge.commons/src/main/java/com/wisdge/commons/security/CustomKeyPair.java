package com.wisdge.commons.security;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Component
public class CustomKeyPair {
    private String file;
    private String alias;
    private String key;

    @Setter(AccessLevel.NONE)
    private KeyPair keyPair;

    public KeyPair getKeyPair() {
        if (keyPair == null) {
            KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(file), key.toCharArray());
            keyPair = keyStoreKeyFactory.getKeyPair(alias, key.toCharArray());
        }
        return keyPair;
    }

    public PublicKey getPublic() {
        return getKeyPair().getPublic();
    }

    public PrivateKey getPrivate() {
        return getKeyPair().getPrivate();
    }
}
