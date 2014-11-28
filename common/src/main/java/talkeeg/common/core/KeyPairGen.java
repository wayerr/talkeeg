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

/**
 * generator for public and private keys <p/>
 * use alg defined in {@link talkeeg.common.core.CryptoConstants#ALG_ASYMMETRIC } with
 *
 * Created by wayerr on 28.11.14.
 */
final class KeyPairGen implements KeyPairSource {

    private final KeyPairGenerator generator;

    KeyPairGen() {
        try {
            this.generator = KeyPairGenerator.getInstance(CryptoConstants.ALG_ASYMMETRIC);
            this.generator.initialize(CryptoConstants.ALG_ASYMMETRIC_KEYGEN_PARAMS);
        } catch(InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public KeyPair create(OwnedKeyType keyType) {
        return this.generator.generateKeyPair();
    }
}
