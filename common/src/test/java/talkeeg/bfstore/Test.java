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

package talkeeg.bfstore;

import talkeeg.bf.BfWriter;
import talkeeg.common.model.MessageCipherType;
import talkeeg.common.model.Sign;
import talkeeg.common.model.SingleMessage;
import talkeeg.common.util.BinaryData;
import talkeeg.common.util.Int128;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.Pipe;

/**
 * Created by wayerr on 21.11.14.
 */
public class Test {

    @org.junit.Test
    public void save() throws IOException {
        System.out.println("save");
        SingleMessage.Builder smbuilder = SingleMessage.builder();
        smbuilder.setId((short)0);
        smbuilder.setSrc(Int128.fromString("84b4a939c2624a8f8c821d3c34b79bea"));
        smbuilder.setDst(Int128.fromString("be0c19b5e78d44d49ddc55fbe2e0fe88"));
        smbuilder.setCipher(MessageCipherType.NONE);
        smbuilder.setData(BinaryData.fromString("379fe49e55b4ea683a0a3ae81be60a3cb91ff9ddd9de6eab1387f9c7ea56" +
                "31fa68a921430796776541ec2cceb484a4e604ab4fc697f39dadaeb0df28" +
                "52dd2976518095e8311879a6808eaa67e9e20561acf7204055eafc1f435d" +
                "f5ce709faeaa0ed1471c76c5f78876a45d7eea56be0be244595345f240ca" +
                "c335edec57a3fb6143da23dca13a0b409bb330da7171d92645552bf84ec7" +
                "753f2e55d85bc13286ace38d3cbee3dbc9d2c87760abe2cce1361bf23875" +
                "4b7e0def2e70049253d3dcf90cb6cb2cd72ed863dbfd4554e50d580793fa" +
                "cfbecbc5f918a40badc3e5e72bedfa022525e23f896e2f2c98584dff4fd6" +
                "9760f3d1f4a209daa715c0bbe5d9f2c810906c0460e378ff089bed29ca9d" +
                "d3fd2404ed9e9f42abc56aeffff887f2fd499e3f6430613e829e9226f9ba" +
                "28312c30206b42292c20302c30303031373830303220632c20352c38204d" +
                "422f630a"));
        smbuilder.setSign(Sign.fromString(""));
        SingleMessage sm = smbuilder.build();

        BfWriter writer =  new BfWriter();
        Pipe pipe = Pipe.open();
        writer.write(sm, pipe.sink());
    }
}
