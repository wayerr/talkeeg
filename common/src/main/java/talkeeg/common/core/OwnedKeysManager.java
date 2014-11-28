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
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
    private final KeyFactory keyFactory;
    private final KeyPairSource keyPairSource;
    private KeyPair clientKeys;
    private KeyPair userKeys;

    OwnedKeysManager(Config config, KeyPairSource keyPairSource) {
        this.keyPairSource = keyPairSource;

        try {
            this.keyFactory = KeyFactory.getInstance(CryptoConstants.ALG_ASYMMETRIC);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

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
                final KeySpec privateKeySpec = new PKCS8EncodedKeySpec(Files.asByteSource(privateKeyFile).read());
                final PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
                final KeySpec publicKeySpec = new X509EncodedKeySpec(Files.asByteSource(publicKeyFile).read());
                final PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
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
        this.clientKeys = loadKeys(OwnedKeyType.CLIENT);
        this.userKeys = loadKeys(OwnedKeyType.USER);
    }
}
