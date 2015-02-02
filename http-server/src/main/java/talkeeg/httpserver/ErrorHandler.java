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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.nio.entity.NStringEntity;

import javax.inject.Inject;

/**
 * tool for handling error and expose it's to http as status line and human readable text messages
 *
 * Created by wayerr on 02.02.15.
 */
final class ErrorHandler {

    private final HttpServerConfig config;

    @Inject
    ErrorHandler(HttpServerConfig config) {
        this.config = config;
    }

    void handle(HttpResponse response, Exception e) {
        // not in all states we can change status line!
        response.setStatusLine(response.getProtocolVersion(), HttpStatus.SC_INTERNAL_SERVER_ERROR, e.toString());
        response.setEntity(new NStringEntity("<html><body>", config.getCharset()));
    }
}
