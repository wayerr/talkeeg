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

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;

/**
 * generator for public and private keys <p/>
 * use alg defined in {@link talkeeg.common.core.CryptoConstants#ALG_ASYMMETRIC } with
 *
 * Created by wayerr on 28.11.14.
 */
final class KeyPairGen implements KeyPairSource {
    /**
     * user key used as root key for subscribing client public keys
     */
    public static final AlgorithmParameterSpec RSA_USER = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);
    /**
     * client key used very often, therefore may be shorter for improving performance
     */
    public static final AlgorithmParameterSpec RSA_CLIENT = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
    private final KeyPairGenerator generator;

    KeyPairGen() {
        try {
            this.generator = KeyPairGenerator.getInstance(CryptoConstants.ALG_ASYMMETRIC);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public KeyPair create(OwnedKeyType keyType) {
        try {
            final AlgorithmParameterSpec params;
            if(keyType == OwnedKeyType.CLIENT) {
                params = RSA_CLIENT;
            } else if(keyType == OwnedKeyType.USER) {
                params = RSA_USER;
            } else {
                throw new RuntimeException("Unsupported key type: " + keyType);
            }
            this.generator.initialize(params);
        } catch(InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        return this.generator.generateKeyPair();
    }
}
