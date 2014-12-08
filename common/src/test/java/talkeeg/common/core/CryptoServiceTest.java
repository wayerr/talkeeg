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

import com.google.common.io.BaseEncoding;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;
import talkeeg.common.Env;
import talkeeg.bf.Arrays;

import java.security.Signature;

import static org.junit.Assert.assertTrue;

/**
 * Created by wayerr on 28.11.14.
 */
public class CryptoServiceTest {

    private static CryptoService service;

    @BeforeClass
    public static void setup() {
        service = new CryptoService(Env.getInstance().getConfig(), new KeyLoader());
        service.init();
    }

    @AfterClass
    public static void close() {
        Env.getInstance().close();
    }

    @Test
    public void testSign() throws Exception {
        System.out.println("sign");
        byte[] data = BaseEncoding.base64().decode("CIwymlw80QfbmrYwK8iL9HoVbV2OT/P/Le1t3qsv7NjoGhTRv7J+upscoWIVm3UDRIh1s7teHmey" +
                "IHdnAYGlkjErMCDQt9Cw0L/QuNGB0LXQuSDQv9C+0LvRg9GH0LXQvdC+CjErMCDQt9Cw0L/QuNGB" +
                "0LXQuSDQvtGC0L/RgNCw0LLQu9C10L3Qvgog0YHQutC+0L/QuNGA0L7QstCw0L3QviA2NCDQsdCw" +
                "0LnRgtCwICg2NCBCKSwgMCwwMDAxMjIxNDQgYywgNTI0IGtCL2MK");
        final Signature signature = service.getSignService(OwnedKeyType.CLIENT);
        signature.update(data);
        final byte[] sign = signature.sign();
        System.out.println("sign(len=" + sign.length + "):" + Arrays.toHexString(sign));

        final Signature verify = service.getVerifyService(OwnedKeyType.CLIENT);
        verify.update(data);
        final boolean verified = verify.verify(sign);
        assertTrue(verified);
    }
}
