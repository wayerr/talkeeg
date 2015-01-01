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

import org.junit.Test;
import talkeeg.bf.Arrays;

import static org.junit.Assert.*;

public class HKDFTest {
    @Test
    public void testHkdf() throws Exception {
        byte[] res = HKDF.createKey("HmacSHA256",
          Arrays.fromHexString("000102030405060708090A0B0C"),
          Arrays.fromHexString("F0F1F2F3F4F5F6F7F8F9"),
          42,
          Arrays.fromHexString("0B0B0B0B0B0B0B0B0B0B0B0B0B0B0B0B0B0B0B0B0B0B"));
        assertArrayEquals(Arrays.fromHexString("3CB25F25FAACD57A90434F64D0362F2A2D2D0A90CF1A5A4C5DB02D56ECC4C5BF34007208D5B887185865"), res);


        res = HKDF.createKey("HmacSHA256",
          Arrays.fromHexString("606162636465666768696A6B6C6D6E6F" +
            "707172737475767778797A7B7C7D7E7F" +
            "808182838485868788898A8B8C8D8E8F" +
            "909192939495969798999A9B9C9D9E9F" +
            "A0A1A2A3A4A5A6A7A8A9AAABACADAEAF"),
          Arrays.fromHexString("B0B1B2B3B4B5B6B7B8B9BABBBCBDBEBF" +
            "C0C1C2C3C4C5C6C7C8C9CACBCCCDCECF" +
            "D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF" +
            "E0E1E2E3E4E5E6E7E8E9EAEBECEDEEEF" +
            "F0F1F2F3F4F5F6F7F8F9FAFBFCFDFEFF"),
          82,
          Arrays.fromHexString("000102030405060708090A0B0C0D0E0F" +
            "101112131415161718191A1B1C1D1E1F" +
            "202122232425262728292A2B2C2D2E2F" +
            "303132333435363738393A3B3C3D3E3F" +
            "404142434445464748494A4B4C4D4E4F"));
        assertArrayEquals(Arrays.fromHexString("B11E398DC80327A1C8E7F78C596A4934" +
          "4F012EDA2D4EFAD8A050CC4C19AFA97C" +
          "59045A99CAC7827271CB41C65E590E09" +
          "DA3275600C2F09B8367793A9ACA3DB71" +
          "CC30C58179EC3E87C14C01D5C1F3434F" +
          "1D87"), res);
    }
}