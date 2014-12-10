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

import com.google.common.base.Supplier;
import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;

/**
 * client identity card
 *
 * Created by wayerr on 10.12.14.
 */
@StructInfo(id = 15)
public final class ClientIdentityCard {
    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder  get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static final class Builder implements BuilderInterface {

        private Int128 userId;
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

        @Override
        public ClientIdentityCard build() {
            return new ClientIdentityCard(this);
        }
    }

    private final BinaryData key;
    private final Int128 userId;

    private ClientIdentityCard(Builder b) {
        this.key = b.key;
        this.userId = b.userId;
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
}
