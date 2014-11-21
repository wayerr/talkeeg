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

package talkeeg.common.util;

import java.util.Arrays;

/**
 * Immutable wrapper for byte[]
 * Created by wayerr on 21.11.14.
 */
public class BinaryData implements Comparable<BinaryData>, BinaryContainer {

    private final byte[] data;

    /**
     * create instance of Binarydata with copy of data
     * @param data
     */
    public BinaryData(byte[] data) {
        check(data);
        this.data = data.clone();
    }

    /**
     * extension point for check ctor argument before construct. <p/>
     * <b>Note that this method will be invoked in parent ctor, because you must not use non static data from child class</b>
     * @param data
     */
    protected void check(byte[] data) {
        if(data == null) {
            throw new RuntimeException("data is null");
        }
    }

    @Override
    public byte[] getData() {
        return data.clone();
    }

    @Override
    public int compareTo(BinaryData o) {
        return talkeeg.common.util.Arrays.compare(this.data, o.data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryData that = (BinaryData) o;

        if (!Arrays.equals(data, that.data)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data != null ? Arrays.hashCode(data) : 0;
    }

    @Override
    public String toString() {
        return "BinaryData{" +
                "length=" + data.length +
                '}';
    }

    /**
     * create instance from hex string
     * @param string
     * @return
     */
    public static BinaryData fromString(String string) {
        if(string == null) {
            throw new RuntimeException("string is null");
        }
        byte[] data = talkeeg.common.util.Arrays.fromHexString(string);
        return new BinaryData(data);
    }
}
