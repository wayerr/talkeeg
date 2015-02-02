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

import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.apache.http.nio.entity.NStringEntity;

import javax.inject.Inject;

/**
 * template evaluator <p/>
 * Created by wayerr on 02.02.15.
 */
class TemplateEvaluator {

    private HttpServerConfig serverConfig;

    @Inject
    public TemplateEvaluator(HttpServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    HttpAsyncContentProducer evaluate(String templateId, Object arg) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<pre>").append(arg).append("</pre>");
        sb.append("</body></html>");
        return new NStringEntity(sb.toString(), this.serverConfig.getCharset());
    }
}
