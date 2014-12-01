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

/**
 * a representation for client network address
 *
 * Created by wayerr on 01.12.14.
 */
public final class ClientAddress {
    private final AddressType type;
    private final boolean external;
    private final String value;

    /**
     * a representation for client network address
     * @param type type of address like ipv4
     * @param external flag for addresses which not directly owned by client
     * @param value string representation of address
     */
    public ClientAddress(AddressType type, boolean external, String value) {
        this.type = type;
        this.external = external;
        this.value = value;
    }

    public AddressType getType() {
        return type;
    }

    public boolean isExternal() {
        return external;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ClientAddress)) {
            return false;
        }

        final ClientAddress that = (ClientAddress)o;

        if(external != that.external) {
            return false;
        }
        if(type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if(value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (external ? 1 : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientAddress{" +
                "type=" + type +
                ", external=" + external +
                ", value='" + value + '\'' +
                '}';
    }
}
