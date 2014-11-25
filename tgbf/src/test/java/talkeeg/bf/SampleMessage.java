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

import talkeeg.bf.StructInfo;

import java.util.Arrays;

/**
 * Created by wayerr on 24.11.14.
 */
@StructInfo(id = 1)
public class SampleMessage {
    private long longValue;
    private byte[] bytesValue;
    private String stringValue;

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public byte[] getBytesValue() {
        return bytesValue;
    }

    public void setBytesValue(byte[] bytesValue) {
        this.bytesValue = bytesValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return "SampleMessage{" +
                "longValue=" + longValue +
                ", bytesValue=" + Arrays.toString(bytesValue) +
                ", stringValue='" + stringValue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof SampleMessage)) {
            return false;
        }

        final SampleMessage that = (SampleMessage)o;

        if(longValue != that.longValue) {
            return false;
        }
        if(!Arrays.equals(bytesValue, that.bytesValue)) {
            return false;
        }
        if(stringValue != null ? !stringValue.equals(that.stringValue) : that.stringValue != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int)(longValue ^ (longValue >>> 32));
        result = 31 * result + (bytesValue != null ? Arrays.hashCode(bytesValue) : 0);
        result = 31 * result + (stringValue != null ? stringValue.hashCode() : 0);
        return result;
    }
}
