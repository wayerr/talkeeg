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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * abstract HTTP file handler
 *
 * Created by wayerr on 30.01.15.
 */
public final class HttpFileHandler implements HttpAsyncRequestHandler<HttpRequest> {
    private static final char FILE_BEGIN = ':';
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final VirtualFileSystem<VirtualFile> vfs;

    @SuppressWarnings("unchecked")
    public HttpFileHandler(final VirtualFileSystem<?> vfs) {
        super();
        this.vfs = (VirtualFileSystem<VirtualFile>)vfs;
    }

    public HttpAsyncRequestConsumer<HttpRequest> processRequest(final HttpRequest request, final HttpContext context) {
        // Buffer request content in memory for simplicity
        return new BasicAsyncRequestConsumer();
    }

    public void handle(HttpRequest request, HttpAsyncExchange httpexchange, HttpContext context) throws HttpException, IOException {
        HttpResponse response = httpexchange.getResponse();
        try {
            handleInternal(request, response, context);
        } catch(Exception e) {
            logger.log(Level.SEVERE, "", e);

        }
        httpexchange.submitResponse(new BasicAsyncResponseProducer(response));
    }

    private void handleInternal(HttpRequest request, HttpResponse response, HttpContext context) throws Exception {

        HttpCoreContext coreContext = HttpCoreContext.adapt(context);

        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if(!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
            throw new MethodNotSupportedException(method + " method not supported");
        }

        final String uri = decodeUri(request);
        final String fileName = resolveFileName(uri);
        final String prefix = resolvePrefix(uri);

        final VirtualFile file = this.vfs.fromPath(fileName);
        if(file == null) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            NStringEntity entity = new NStringEntity(
              "<html><body><h1>File" + fileName +
                " not found</h1></body></html>",
              ContentType.create("text/html", "UTF-8"));
            response.setEntity(entity);
            logger.info("File " + fileName + " not found");
        } else {
            response.setStatusCode(HttpStatus.SC_OK);
            if(file.isDirectory()) {
                listFiles(response, prefix, file);
            } else {
                String mimeType = file.getMimeType();
                ContentType contentType;
                if(mimeType != null) {
                    contentType = ContentType.create(mimeType, "UTF-8");
                } else {
                    contentType = ContentType.APPLICATION_OCTET_STREAM;
                }
                InputStreamEntity body = new InputStreamEntity(file.openInputStream(), contentType);
                response.setEntity(body);
            }
        }
    }

    private void listFiles(HttpResponse response, String prefix, VirtualFile file) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>\n");
        sb.append("list files:<br/>\n");
        sb.append("<table>\n");
        sb.append("<tr><th>name</th><th>size</th><th>type</th></tr>\n");
        VirtualFile parent = file.getParent();
        if(parent != null) {
            sb.append("<tr><td><a href=\"").append(prefix)
              .append(this.vfs.toPath(parent))
              .append("\">..</a><br/></td><td></td><td></td></tr>\n");
        }
        List<VirtualFile> virtualFiles = new ArrayList<>();
        file.getChilds(virtualFiles);
        for(VirtualFile childFile : virtualFiles) {
            sb.append("<tr><td>");
            sb.append("<a href=\"").append(prefix)
              .append(this.vfs.toPath(childFile))
              .append("\">")
              .append(childFile.getName());
            final boolean directory = childFile.isDirectory();
            if(directory) {
                sb.append('/');
            }
            sb.append("</a><br/></td><td>");
            if(!directory) {
                sb.append(childFile.getSize());
            }
            sb.append("</td><td>");
            if(!directory) {
                sb.append(childFile.getMimeType());
            }
            sb.append("</td></tr>\n");
        }
        sb.append("</body></html>");
        response.setEntity(new NStringEntity(sb.toString(), ContentType.create("text/html", "UTF-8")));
    }

    private String resolveFileName(String name) {
        final int i = name.lastIndexOf(FILE_BEGIN);
        if(i < 0) {
            throw new IllegalArgumentException("Bad uri: " + name);
        }
        return name.substring(i + 1);
    }

    private String resolvePrefix(String name) {
        final int i = name.lastIndexOf(FILE_BEGIN);
        if(i < 0) {
            throw new IllegalArgumentException("Bad uri: " + name);
        }
        return name.substring(0, i + 1);
    }

    private String decodeUri(HttpRequest request) throws UnsupportedEncodingException {
        return URLDecoder.decode(request.getRequestLine().getUri(), "UTF-8");
    }

}
