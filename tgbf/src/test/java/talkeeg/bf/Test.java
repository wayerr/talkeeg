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

package talkeeg.bf;

import com.google.common.io.BaseEncoding;
import talkeeg.bf.schema.SchemaSource;

import java.nio.ByteBuffer;

/**
 * Created by wayerr on 24.11.14.
 */
public class Test {
    @org.junit.Test
    public void test() throws Exception {
        SampleMessage sampleMessage = new SampleMessage();
        sampleMessage.setLongValue(0xff00ff0000ff00ffl);
        sampleMessage.setBytesValue(new byte[]{1, 2, 3, 4, 5, 6, (byte)0xff});
        sampleMessage.setStringValue("simple уникодная строка а-я");

        BfWriter writer =  new BfWriter(SchemaSource.fromResource("protocol.xml"));

        ByteBuffer buffer = writer.write(sampleMessage);
        System.out.println(BaseEncoding.base16().encode(buffer.array()));
    }
}
