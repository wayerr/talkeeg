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

package talkeeg.server;

import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.nio.protocol.*;
import org.apache.http.protocol.HttpContext;
import talkeeg.bf.BinaryData;
import talkeeg.common.barcode.BarcodeService;
import talkeeg.common.barcode.BarcodeUtilsSE;
import talkeeg.common.core.HelloService;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
/**
 * http handler which provide barcode data as png file <p/>
 * <p/>
 * Created by wayerr on 05.02.15.
 */
@Singleton
public final class BarcodeProvider implements HttpAsyncRequestHandler<HttpRequest> {

    private final Provider<BarcodeService> barcodeServiceProvider;
    private final Provider<HelloService> helloProvider;

    @Inject
    BarcodeProvider(Provider<BarcodeService> barcodeServiceProvider, Provider<HelloService> helloProvider) {
        this.barcodeServiceProvider = barcodeServiceProvider;
        this.helloProvider = helloProvider;
    }

    @Override
    public HttpAsyncRequestConsumer<HttpRequest> processRequest(final HttpRequest request, final HttpContext context) {
        return new BasicAsyncRequestConsumer();
    }

    @Override
    public void handle(HttpRequest data, HttpAsyncExchange httpExchange, HttpContext context) throws HttpException, IOException {
        final BinaryData barcodeData = this.helloProvider.get().helloAsBinaryData();
        final BitMatrix matrix = this.barcodeServiceProvider.get().encode(barcodeData);
        final BufferedImage image = BarcodeUtilsSE.toBufferedImage(matrix);
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        ImageIO.write(image, "png", tmp);
        final HttpResponse response = httpExchange.getResponse();
        response.setHeader("Content-Type", "image/png");
        response.setEntity(new ByteArrayEntity(tmp.toByteArray()));
        response.setStatusCode(HttpStatus.SC_OK);
        httpExchange.submitResponse(new BasicAsyncResponseProducer(response));
    }
}
