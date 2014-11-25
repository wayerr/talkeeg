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

package talkeeg.bf.schema;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * In memory schema representation <p/>
 *
 * Created by wayerr on 21.11.14.
 */
public final class Schema {

    public static final class Builder {
        private final Map<Integer, Struct> messages = new HashMap<>();
        private ByteOrder byteOrder;

        public Map<Integer, Struct> getMessages() {
            return messages;
        }

        public void setMessages(Map<Integer, Struct> messages) {
            this.messages.clear();
            if(messages != null) {
                this.messages.putAll(messages);
            }
        }

        public void putMessage(Struct message) {
            this.messages.put(message.getId(), message);
        }

        public ByteOrder getByteOrder() {
            return byteOrder;
        }

        public void setByteOrder(ByteOrder byteOrder) {
            this.byteOrder = byteOrder;
        }

        public Schema build() {
          return new Schema(this);
        }
    }

    private final Map<Integer, Struct> messages = new HashMap<>();
    private final ByteOrder byteOrder;

    private Schema(Builder b) {
        this.messages.putAll(b.messages);
        this.byteOrder = b.byteOrder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Struct getMessage(int mesageId) {
        return messages.get(mesageId);
    }
}
