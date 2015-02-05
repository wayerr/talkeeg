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

import com.google.common.io.BaseEncoding;
import talkeeg.bf.Int128;

/**
 * A tools for creating and verifying fingerprints <p/>
 *
 * Created by wayerr on 04.02.15.
 */
public final class Fingerprints {

    private Fingerprints() {
    }

    /**
     * convert data to human friendly fingerprint string. It use base32 encoding as it
     * described in http://tools.ietf.org/html/rfc4648#section-6 and use
     * http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/io/BaseEncoding.html#line.402
     * as implementation.
     * @see #fromFriendly(String)
     * @param data
     * @return
     */
    public static String toFriendly(Int128 data) {
        return BaseEncoding.base32().encode(data.getData());
    }

    /**
     * convert data to human friendly fingerprint string. It use base32 encoding as it
     * described in http://tools.ietf.org/html/rfc4648#section-6 and use
     * http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/io/BaseEncoding.html#line.402
     * as implementation.
     * @see #toFriendly(talkeeg.bf.Int128)
     * @param fingeprint
     * @return
     */
    public static Int128 fromFriendly(String fingeprint) {
        byte[] decoded = BaseEncoding.base32().decode(fingeprint);
        return new Int128(decoded);
    }
}
