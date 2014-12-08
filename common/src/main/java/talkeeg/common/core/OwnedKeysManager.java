/*
 * Copyright (c) 2014, wayerr (radiofun@ya.ru).
 *
 *      This file is part of talkeeg-parent.
 *
 *      talkeeg-parent is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      talkeeg-parent is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with talkeeg-parent.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.common.core;

import com.google.common.io.Files;
import talkeeg.common.conf.Config;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class which manages owned keys (client and user)
 * also class responsibly for loading keys
 *
 * Created by wayerr on 28.11.14.
 */
class OwnedKeysManager {
    private static final Logger LOG = Logger.getLogger(OwnedKeysManager.class.getName());
    private final File keysDir;
    private final KeyLoader keyLoader;
    private final KeyPairSource keyPairSource;
    private final Object lock = new Object();
    private KeyPair clientKeys;
    private KeyPair userKeys;

    OwnedKeysManager(Config config, KeyLoader keyLoader, KeyPairSource keyPairSource) {
        this.keyPairSource = keyPairSource;

        this.keyLoader = keyLoader;

        this.keysDir = new File(config.getConfigDir(), "keys");
        this.keysDir.mkdirs();// make keys dir if need
    }

    private KeyPair loadKeys(OwnedKeyType keyType) {
        final String keyFilePrefix = keyType.name().toLowerCase();
        final File privateKeyFile = new File(this.keysDir, keyFilePrefix + "_private.der");
        final File publicKeyFile = new File(this.keysDir, keyFilePrefix + "_public.der");

        KeyPair keyPair = null;
        if(privateKeyFile.exists()) {
            try {
                final PrivateKey privateKey = this.keyLoader.loadPrivate(Files.asByteSource(privateKeyFile).read());
                final PublicKey publicKey = this.keyLoader.loadPublic(Files.asByteSource(publicKeyFile).read());
                keyPair = new KeyPair(publicKey, privateKey);
            } catch(Exception e) {
                LOG.log(Level.INFO, "can not load key from " + privateKeyFile + " and " + publicKeyFile , e);
            }
        }
        if(keyPair == null) {
            keyPair = this.keyPairSource.create(keyType);
            if(keyPair != null) {
                try {
                    Files.write(keyPair.getPublic().getEncoded(), publicKeyFile);
                    Files.write(keyPair.getPrivate().getEncoded(), privateKeyFile);
                } catch(IOException e) {
                    LOG.log(Level.WARNING, "can not save keys to " + privateKeyFile + " and " + publicKeyFile, e);
                }
            }
        }
        return keyPair;
    }

    void loadKeys() {
        synchronized(lock) {
            this.clientKeys = loadKeys(OwnedKeyType.CLIENT);
            this.userKeys = loadKeys(OwnedKeyType.USER);
        }
    }

    PrivateKey getPrivateKey(OwnedKeyType keyType) {
        KeyPair keyPair = getKeyPair(keyType);
        if(keyPair == null) {
            throw new NullPointerException("Key type " + keyType + " is not loaded.");
        }
        return keyPair.getPrivate();
    }

    PublicKey getPublicKey(OwnedKeyType keyType) {
        KeyPair keyPair = getKeyPair(keyType);
        if(keyPair == null) {
            throw new NullPointerException("Key type " + keyType + " is not loaded.");
        }
        return keyPair.getPublic();
    }

    private KeyPair getKeyPair(OwnedKeyType keyType) {
        KeyPair keyPair;
        synchronized(lock) {
            if(keyType == OwnedKeyType.USER) {
                keyPair = this.userKeys;
            } else if(keyType == OwnedKeyType.CLIENT) {
                keyPair = this.clientKeys;
            } else {
                throw new IllegalArgumentException("Key type: " + keyType + " is unsupported.");
            }
        }
        return keyPair;
    }

}
