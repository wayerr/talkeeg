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

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;

/**
 * constants for crypto services
 *
 * Created by wayerr on 28.11.14.
 */
final class CryptoConstants {
    /**
     * default algorithm for key asymmetric ciphering
     */
    public static final String ALG_ASYMMETRIC = "RSA";
    /**
     * params for key generation of algorithm defined in {@link #ALG_ASYMMETRIC }
     */
    static final AlgorithmParameterSpec ALG_ASYMMETRIC_KEYGEN_PARAMS = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);
}
