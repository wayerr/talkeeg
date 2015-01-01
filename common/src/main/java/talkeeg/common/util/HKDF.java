/*
 * Copyright (c) 2015, wayerr (radiofun@ya.ru).
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

package talkeeg.common.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * implementation of HMAC-based Extract-and-Expand Key Derivation Function (HKDF) <p/>
 * https://tools.ietf.org/html/rfc5869 <p/>
 * Created by wayerr on 01.01.15.
 */
public final class HKDF {

    private final Mac mac;
    private final String algorithm;

    private HKDF(String algorithm) throws GeneralSecurityException {
        this.algorithm = algorithm;
        this.mac = Mac.getInstance(this.algorithm);
    }

    private byte[] hmac(Key key, byte[] ... materials) throws GeneralSecurityException {
        mac.init(key);
        for(byte[] material : materials) {
            mac.update(material);
        }
        byte[] res = mac.doFinal();
        mac.reset();
        return res;
    }

    private byte[] extract(byte salt[], byte[] ... keyMaterial) throws GeneralSecurityException {
        SecretKeySpec key = new SecretKeySpec(salt, this.algorithm);
        return hmac(key, keyMaterial);
    }

    private byte[] expand(byte key[], byte info[], final int len) throws GeneralSecurityException {
        final int macLength = this.mac.getMacLength();
        final int count = (int)Math.ceil((double)len / (double)macLength);
        final byte res[] = new byte[len];
        final SecretKeySpec keySpec = new SecretKeySpec(key, this.algorithm);
        byte old[] = new byte[0];
        byte data[] = new byte[info.length + 1];
        System.arraycopy(info, 0, data, 0, info.length);
        for(int i = 1; i <= count; ++i) {
            if(i == 2) {
                data = new byte[macLength + info.length + 1];
                System.arraycopy(info, 0, data, macLength, info.length);
            }
            System.arraycopy(old, 0, data, 0, old.length);
            data[data.length - 1] = (byte)i;
            old = hmac(keySpec, data);
            final int off = macLength * (i - 1);
            System.arraycopy(old, 0, res, off, Math.min(old.length, res.length - off));
        }
        return res;
    }

    public static byte[] createKey(String algorithm, byte salt[], byte info[], int len, byte[] ... keyMaterial) throws GeneralSecurityException {
        HKDF hkdf = new HKDF(algorithm);
        byte[] pseudoRandomKey = hkdf.extract(salt, keyMaterial);
        return hkdf.expand(pseudoRandomKey, info, len);
    }
}
