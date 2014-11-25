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
import talkeeg.bf.schema.Schema;
import talkeeg.bf.schema.SchemaSource;
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
    public void save() throws Exception {
        System.out.println("save");
        SingleMessage.Builder smbuilder = SingleMessage.builder();
        smbuilder.setId((short)0);
        smbuilder.setSrc(Int128.fromString("84B4A939C2624A8F8C821D3C34B79BEA"));
        smbuilder.setDst(Int128.fromString("BE0C19B5E78D44D49DDC55FBE2E0FE88"));
        smbuilder.setCipher(MessageCipherType.NONE);
        smbuilder.setData(BinaryData.fromString("379FE49E55B4EA683A0A3AE81BE60A3CB91FF9DDD9DE6EAB1387F9C7EA56" +
                "31FA68A921430796776541EC2CCEB484A4E604AB4FC697F39DADAEB0DF28" +
                "52DD2976518095E8311879A6808EAA67E9E20561ACF7204055EAFC1F435D" +
                "F5CE709FAEAA0ED1471C76C5F78876A45D7EEA56BE0BE244595345F240CA" +
                "C335EDEC57A3FB6143DA23DCA13A0B409BB330DA7171D92645552BF84EC7" +
                "753F2E55D85BC13286ACE38D3CBEE3DBC9D2C87760ABE2CCE1361BF23875" +
                "4B7E0DEF2E70049253D3DCF90CB6CB2CD72ED863DBFD4554E50D580793FA" +
                "CFBECBC5F918A40BADC3E5E72BEDFA022525E23F896E2F2C98584DFF4FD6" +
                "9760F3D1F4A209DAA715C0BBE5D9F2C810906C0460E378FF089BED29CA9D" +
                "D3FD2404ED9E9F42ABC56AEFFFF887F2FD499E3F6430613E829E9226F9BA" +
                "28312C30206B42292C20302C30303031373830303220632C20352C38204D" +
                "422F630A"));
        smbuilder.setSign(Sign.fromString(""));
        SingleMessage sm = smbuilder.build();

        BfWriter writer =  new BfWriter(SchemaSource.fromResource("protocol.xml"));
        ByteBuffer buffer = writer.write(sm);
    }
}
