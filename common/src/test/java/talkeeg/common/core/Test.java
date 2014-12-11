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

import talkeeg.bf.*;
import talkeeg.common.model.*;

import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wayerr on 21.11.14.
 */
public class Test {

    @org.junit.Test
    public void save() throws Exception {
        System.out.println("save");
        Bf bf = Env.getInstance().getBf();

        SingleMessage.Builder smbuilder = SingleMessage.builder();
        smbuilder.setId((short)0);
        smbuilder.setSrc(SampleMessages.CLIENT_2_ID);
        smbuilder.setDst(SampleMessages.CLIENT_1_ID);
        smbuilder.setCipherType(MessageCipherType.NONE);
        UserIdentityCard uic = SampleMessages.USER_2_UIC;
        ClientAddresses addresses = ClientAddresses.builder()
                .addAddress(ClientAddress.builder()
                  .type(BasicAddressType.IPV6)
                  .external(true)
                  .value("fc71:0:0:0:131:1ace:a61:4aa0%eth0")
                  .build())
                .build();
        List<Object> list = java.util.Arrays.<Object>asList(uic, addresses);
        ByteBuffer buffer = bf.write(list);
        smbuilder.setData(new BinaryData(buffer));
        smbuilder.setClientSign(BinaryData.fromString(""));
        SingleMessage sm = smbuilder.build();


        writeAndRead(uic, bf);

        writeAndRead(sm, bf);
    }

    protected void writeAndRead(Object uic, Bf bf) throws Exception {
        ByteBuffer buffer = bf.write(uic);
        System.out.println(uic + " (" + buffer.remaining() + " bytes) :" + Arrays.toHexString(buffer));
        final Object r = bf.read(buffer);
        assertEquals(uic, r);
    }
}
