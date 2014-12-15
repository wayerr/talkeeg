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

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;
import talkeeg.common.util.Printable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * client identity card
 *
 * Created by wayerr on 10.12.14.
 */
@StructInfo(id = 15)
public final class ClientIdentityCard implements Printable {
    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder  get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };


    /**
     * displayable name of client (i.e. hostname of PC )
     */
    public static final String ATTR_NAME = "name";

    public static final class Builder implements BuilderInterface {

        private Int128 userId;
        private final Map<String, Object> attrs = new HashMap<>();
        private BinaryData key;

        public Int128 getUserId() {
            return userId;
        }

        public Builder userId(Int128 userId) {
            setUserId(userId);
            return this;
        }

        public void setUserId(Int128 userId) {
            this.userId = userId;
        }

        public BinaryData getKey() {
            return key;
        }

        public Builder key(BinaryData key) {
            setKey(key);
            return this;
        }

        public void setKey(BinaryData key) {
            this.key = key;
        }

        public Map<String, Object> getAttrs() {
            return attrs;
        }

        public Builder putAttr(String name, Object value) {
            this.attrs.put(name, value);
            return this;
        }

        public void setAttrs(Map<String, Object> attrs) {
            this.attrs.clear();
            this.attrs.putAll(attrs);
        }

        @Override
        public ClientIdentityCard build() {
            return new ClientIdentityCard(this);
        }
    }

    private final BinaryData key;
    private final Int128 userId;
    private final Map<String, Object> attrs;

    private ClientIdentityCard(Builder b) {
        this.key = b.key;
        Preconditions.checkNotNull(this.key, "key is null");
        this.userId = b.userId;
        Preconditions.checkNotNull(this.userId, "userId is null");
        this.attrs = Collections.unmodifiableMap(new HashMap<>(b.attrs));
    }

    public static Builder builder() {
        return new Builder();
    }


    /**
     * id of user, in other words fingerprint of user public key
     * @return
     */
    public Int128 getUserId() {
        return userId;
    }

    /**
     * client public key
     * @return
     */
    public BinaryData getKey() {
        return key;
    }

    /**
     * unmodifiable map of attributes
     * @see #ATTR_NAME
     * @return
     */
    public Map<String, Object> getAttrs() {
        return attrs;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ClientIdentityCard)) {
            return false;
        }

        ClientIdentityCard that = (ClientIdentityCard)o;

        if(attrs != null? !attrs.equals(that.attrs) : that.attrs != null) {
            return false;
        }
        if(key != null? !key.equals(that.key) : that.key != null) {
            return false;
        }
        if(userId != null? !userId.equals(that.userId) : that.userId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null? key.hashCode() : 0;
        result = 31 * result + (userId != null? userId.hashCode() : 0);
        result = 31 * result + (attrs != null? attrs.hashCode() : 0);
        return result;
    }

    @Override
    public void print(StringBuilder sb) {
        sb.append("key: ").append(talkeeg.bf.Arrays.toHexString(this.key.getData())).append("\n");
        for(Map.Entry<String, Object> attr: this.attrs.entrySet()) {
            sb.append(attr.getKey()).append('=')
              .append(attr.getValue()).append('\n');
        }
    }
}
