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

package talkeeg.common.model;

import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;
import com.google.common.base.Supplier;

/**
 * a representation for client network address <p/>
 * address was specified as string, like: <p/>
 * <code>tg:[a.b.c.d/net-prefix-len]:port</code> for ipv4, and <p/>
 * <code>tg:[XX:XX...XX/net-prefix-len]:port</code> for ipv6 <p/>
 * <p/>
 * Created by wayerr on 01.12.14.
 */
@StructInfo(id = 13)
public final class ClientAddress {

    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static final class Builder implements BuilderInterface {
        private boolean external;
        private String value;

        public boolean isExternal() {
            return external;
        }

        public Builder external(boolean external) {
            setExternal(external);
            return this;
        }

        public void setExternal(boolean external) {
            this.external = external;
        }

        public String getValue() {
            return value;
        }

        public Builder value(String value) {
            setValue(value);
            return this;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public ClientAddress build() {
            return new ClientAddress(external, value);
        }
    }

    private final boolean external;
    private final String value;

    /**
     * a representation for client network address
     * @param external flag for addresses which not directly owned by client
     * @param value string representation of address
     */
    public ClientAddress(boolean external, String value) {
        this.external = external;
        this.value = value;
    }

    public static Builder builder() {
        return new Builder();
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
        if(value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (external? 1 : 0);
        result = 31 * result + (value != null? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientAddress{" +
          "external=" + external +
          ", value='" + value + '\'' +
          '}';
    }
}
