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

package talkeeg.httpserver;

import com.google.common.base.Preconditions;

/**
 * config of http server <p/>
 * Created by wayerr on 30.01.15.
 */
public class HttpServerConfig {

    public static final class Builder {
        public boolean useTLS = false;
        public int portNumber = 8080;

        public boolean isUseTLS() {
            return useTLS;
        }

        public Builder useTLS(boolean useTLS) {
            setUseTLS(useTLS);
            return this;
        }

        public void setUseTLS(boolean useTLS) {
            this.useTLS = useTLS;
        }

        public int getPortNumber() {
            return portNumber;
        }

        public Builder portNumber(int portNumber) {
            setPortNumber(portNumber);
            return this;
        }

        public void setPortNumber(int portNumber) {
            this.portNumber = portNumber;
        }

        public HttpServerConfig build() {
            return new HttpServerConfig(this);
        }
    }


    private int portNumber;
    private boolean useTLS;

    private HttpServerConfig(Builder b) {
        this.portNumber = b.portNumber;
        Preconditions.checkArgument(this.portNumber > 0 && this.portNumber <= 0xffff, "Invalid portNumber: %s", this.portNumber);
        this.useTLS = b.useTLS;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getPortNumber() {
        return portNumber;
    }

    public boolean isUseTLS() {
        return useTLS;
    }
}
