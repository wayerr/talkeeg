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

package talkeeg.httpserver.fs;

import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * abstract HTTP file handler
 *
 * Created by wayerr on 30.01.15.
 */
public final class HttpFileHandler implements HttpAsyncRequestHandler<HttpRequest> {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final VirtualFileSystem<?> vfs;

    public HttpFileHandler(final VirtualFileSystem<?> vfs) {
        super();
        this.vfs = vfs;
    }

    public HttpAsyncRequestConsumer<HttpRequest> processRequest(
      final HttpRequest request,
      final HttpContext context) {
        // Buffer request content in memory for simplicity
        return new BasicAsyncRequestConsumer();
    }

    public void handle(HttpRequest request, HttpAsyncExchange httpexchange, HttpContext context) throws HttpException, IOException {
        HttpResponse response = httpexchange.getResponse();
        try {
            handleInternal(request, response, context);
        } catch(IOException | HttpException | RuntimeException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        httpexchange.submitResponse(new BasicAsyncResponseProducer(response));
    }

    private void handleInternal(HttpRequest request, HttpResponse response, HttpContext context) throws Exception {

        HttpCoreContext coreContext = HttpCoreContext.adapt(context);

        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if(!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
            throw new MethodNotSupportedException(method + " method not supported");
        }

        final String target = request.getRequestLine().getUri();
        final String fileName = URLDecoder.decode(target, "UTF-8");

        final VirtualFile file = this.vfs.get(fileName);
        if(file == null) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            NStringEntity entity = new NStringEntity(
              "<html><body><h1>File" + fileName +
                " not found</h1></body></html>",
              ContentType.create("text/html", "UTF-8"));
            response.setEntity(entity);
            logger.info("File " + fileName + " not found");
        } else {
            NHttpConnection conn = coreContext.getConnection(NHttpConnection.class);
            response.setStatusCode(HttpStatus.SC_OK);
            logger.info(conn + ": serving file " + fileName);
            if(file.isDirectory()) {
                StringBuilder sb = new StringBuilder();
                sb.append("<body><html>\n");
                sb.append("list files:<br/>\n");
                sb.append("<table>\n");
                sb.append("<tr><th>name</th><th>size</th><th>type</th></tr>\n");
                List<VirtualFile> virtualFiles = new ArrayList<>();
                file.getChilds(virtualFiles);
                for(VirtualFile childFile : virtualFiles) {
                    sb.append("<tr><td>");
                    sb.append("<a href=\"").append(childFile).append("\">").append(childFile);
                    if(childFile.isDirectory()) {
                        sb.append('/');
                    }
                    sb.append("</a><br/></th><th>");
                    file.getSize();
                    sb.append("</th><th>type</th></tr>\n");
                }
                sb.append("</body></html>");
                response.setEntity(new NStringEntity(sb.toString(), ContentType.create("text/html", "UTF-8")));
            } else {
                InputStreamEntity body = new InputStreamEntity(file.openInputStream(), ContentType.APPLICATION_OCTET_STREAM);
                response.setEntity(body);
            }
        }
    }

}
