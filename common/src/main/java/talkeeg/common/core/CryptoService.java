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

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import talkeeg.common.conf.Config;

import java.io.File;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * service which provide some cryptographic functions for ciphering, signing and etc.
 * also this service manage user and client private keys
 *
 * Created by wayerr on 27.11.14.
 */
public final class CryptoService {
    private static final Logger LOG = Logger.getLogger(CryptoService.class.getName());

    private static final String KEYS_CLIENT = "client";
    private static final String KEYS_USER = "user";
    private final File keysDir;
    private final KeyFactory keyFactory;
    private KeyPair clientKeys;
    private KeyPair userKeys;

    public CryptoService(Config config) {
        try {
            this.keyFactory = KeyFactory.getInstance("RSA");
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        this.keysDir = new File(config.getConfigDir(), "keys");
        this.keysDir.mkdirs();// make keys dir if need

    }

    private KeyPair loadKeys(String keyPrefix) {
        final File privateKeyFile = new File(this.keysDir, keyPrefix + "_private.der");
        final File publicKeyFile = new File(this.keysDir, keyPrefix + "_public.der");
        try {
            final KeySpec privateKeySpec = new PKCS8EncodedKeySpec(Files.asByteSource(privateKeyFile).read());
            final PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            final KeySpec publicKeySpec = new X509EncodedKeySpec(Files.asByteSource(publicKeyFile).read());
            final PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            return new KeyPair(publicKey, privateKey);
        } catch(Exception e) {
            LOG.log(Level.INFO, "can not load key from " + privateKeyFile, e);
        }
        return null;
    }

    public void init() {
        this.clientKeys = loadKeys(KEYS_CLIENT);
        this.userKeys = loadKeys(KEYS_USER);
    }
}
