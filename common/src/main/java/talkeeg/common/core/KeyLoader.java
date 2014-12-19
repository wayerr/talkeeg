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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * tool for load public, private keys from it`s binary representation
 *
 * Created by wayerr on 08.12.14.
 */
@Singleton
public final class KeyLoader {

    private final KeyFactory keyFactory;

    @Inject
    KeyLoader() {
        try {
            this.keyFactory = KeyFactory.getInstance(CryptoConstants.ALG_ASYMMETRIC);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * create private key from it byte PKCS8 representation
     * @param bytes
     * @return
     */
    PrivateKey loadPrivate(byte[] bytes) {
        final KeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytes);
        try {
            return keyFactory.generatePrivate(privateKeySpec);
        } catch(InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * create public key from it byte X509 representation
     * @param bytes
     * @return
     */
    public PublicKey loadPublic(byte[] bytes) {
        final KeySpec publicKeySpec = new X509EncodedKeySpec(bytes);
        try {
            return keyFactory.generatePublic(publicKeySpec);
        } catch(InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
