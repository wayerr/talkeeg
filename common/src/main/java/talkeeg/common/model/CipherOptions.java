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
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;

/**
 * options of stream ciphering
 * Created by wayerr on 26.12.14.
 */
@StructInfo(id = 20)
public final class CipherOptions {

    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static final class Builder implements BuilderInterface {
        private SymmetricCipherType cipher;
        private CipherMode mode;
        private MacType mac;
        private PaddingType padding;

        public SymmetricCipherType getCipher() {
            return cipher;
        }

        public Builder cipher(SymmetricCipherType cipher) {
            setCipher(cipher);
            return this;
        }

        public void setCipher(SymmetricCipherType cipher) {
            this.cipher = cipher;
        }

        public CipherMode getMode() {
            return mode;
        }

        public Builder mode(CipherMode mode) {
            setMode(mode);
            return this;
        }

        public void setMode(CipherMode mode) {
            this.mode = mode;
        }

        public MacType getMac() {
            return mac;
        }

        public Builder mac(MacType mac) {
            setMac(mac);
            return this;
        }

        public void setMac(MacType mac) {
            this.mac = mac;
        }

        public PaddingType getPadding() {
            return padding;
        }

        public Builder padding(PaddingType padding) {
            setPadding(padding);
            return this;
        }

        public void setPadding(PaddingType padding) {
            this.padding = padding;
        }

        public CipherOptions build() {
            return new CipherOptions(this);
        }
    }

    private final SymmetricCipherType cipher;
    private final CipherMode mode;
    private final MacType mac;
    private final PaddingType padding;

    private CipherOptions(Builder b) {
        this.cipher = b.cipher;
        this.mode = b.mode;
        this.mac = b.mac;
        this.padding = b.padding;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SymmetricCipherType getCipher() {
        return cipher;
    }

    public CipherMode getMode() {
        return mode;
    }

    public MacType getMac() {
        return mac;
    }

    public PaddingType getPadding() {
        return padding;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof CipherOptions)) {
            return false;
        }

        CipherOptions that = (CipherOptions)o;

        if(cipher != that.cipher) {
            return false;
        }
        if(mac != that.mac) {
            return false;
        }
        if(mode != that.mode) {
            return false;
        }
        if(padding != that.padding) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = cipher != null? cipher.hashCode() : 0;
        result = 31 * result + (mode != null? mode.hashCode() : 0);
        result = 31 * result + (mac != null? mac.hashCode() : 0);
        result = 31 * result + (padding != null? padding.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CipherOptions{" +
          "cipher=" + cipher +
          ", mode=" + mode +
          ", mac=" + mac +
          ", padding=" + padding +
          '}';
    }
}
