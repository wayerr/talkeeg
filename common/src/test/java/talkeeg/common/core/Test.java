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
import talkeeg.common.ipc.IpcEntryHandler;
import talkeeg.common.ipc.IpcService;
import talkeeg.common.ipc.IpcServiceLoopback;
import talkeeg.common.ipc.Parcel;
import talkeeg.common.model.*;
import talkeeg.common.util.Closeable;

import javax.annotation.PreDestroy;
import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wayerr on 21.11.14.
 */
public class Test {


    @org.junit.BeforeClass
    public static void postConstruct() {
        Env.getInstance();
    }
    @org.junit.AfterClass
    public static void preDestroy() {
        Env.getInstance().close();
    }

    @org.junit.Test
    public void acquaint() throws Exception {
        System.out.println("acquaint");
        final Env env = Env.getInstance();
        final AcquaintService acquaintService = env.get(AcquaintService.class);
        acquaintService.acquaint(IpcServiceLoopback.LOCALHOST);
    }

    public void save() throws Exception {
        System.out.println("save");
        final Env env = Env.getInstance();
        final Bf bf = env.get(Bf.class);
        final AcquaintService acquaintService = env.get(AcquaintService.class);
        acquaintService.acquaint(IpcServiceLoopback.LOCALHOST);

        SingleMessage.Builder smbuilder = SingleMessage.builder();
        smbuilder.setId((short)0);
        smbuilder.setSrc(SampleMessages.CLIENT_2_ID);
        smbuilder.setDst(SampleMessages.CLIENT_1_ID);
        smbuilder.setCipherType(MessageCipherType.NONE);
        UserIdentityCard uic = SampleMessages.USER_2_UIC;
        ClientAddresses addresses = ClientAddresses.builder()
                .addAddress(IpcServiceLoopback.LOCALHOST)
                .build();
        List<Object> list = java.util.Arrays.<Object>asList(uic, addresses);
        ByteBuffer buffer = bf.write(list);
        smbuilder.setData(new BinaryData(buffer));
        smbuilder.setClientSign(BinaryData.fromString(""));
        SingleMessage sm = smbuilder.build();

        SingleMessage res = (SingleMessage)writeAndRead(sm, bf);
        BinaryData data = res.getData();
        Object readedData = bf.read(ByteBuffer.wrap(data.getData()));
        List<?> readedList = (List<?>)readedData;
        assertEquals(uic, readedList.get(0));
        assertEquals(addresses, readedList.get(1));
    }

    protected Object writeAndRead(Object uic, Bf bf) throws Exception {
        ByteBuffer buffer = bf.write(uic);
        System.out.println(uic + " (" + buffer.remaining() + " bytes) :" + Arrays.toHexString(buffer));
        final Object r = bf.read(buffer);
        assertEquals(uic, r);
        return r;
    }
}
