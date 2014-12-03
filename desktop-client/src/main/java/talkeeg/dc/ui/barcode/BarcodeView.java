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

package talkeeg.dc.ui.barcode;

import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import talkeeg.bf.BinaryData;
import talkeeg.common.barcode.BarcodeService;
import talkeeg.common.core.CurrentAddressesService;
import talkeeg.common.core.HelloService;
import talkeeg.common.core.OwnedIdentityCardsService;
import talkeeg.dc.ui.ComponentOwner;
import talkeeg.dc.ui.ImageViewer;
import talkeeg.dc.ui.View;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

/**
 * view for data barcodes
 *
 * Created by wayerr on 03.12.14.
 */
public final class BarcodeView implements View {

    private final ImageViewer viewer;
    private final BarcodeService service;
    private final HelloService helloService;

    public BarcodeView(BarcodeService service, HelloService helloService) {
        this.viewer = new ImageViewer();
        this.service = service;
        this.helloService = helloService;
    }

    @Override
    public String getTitle() {
        return "barcode";
    }

    public void setData(BinaryData data) {
        BitMatrix matrix = this.service.encode(data);
        Image image = toBufferedImage(matrix);
        this.viewer.setImage(image);
    }

    private Image toBufferedImage(BitMatrix matrix) {
        final int width = matrix.getWidth();
        final int height = matrix.getHeight();
        final int factor = 8;
        final BufferedImage image = new BufferedImage(width * factor, height * factor, BufferedImage.TYPE_BYTE_BINARY);
        final int whiteArr[] = new int[factor * factor];
        final int blackArr[] = new int[factor * factor];
        Arrays.fill(blackArr, 1);
        WritableRaster raster = image.getRaster();
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                final int arr[] = matrix.get(x, y)? whiteArr : blackArr;
                raster.setPixels(x * factor, y * factor, factor, factor, arr);
            }
        }

        return image;
    }

    @Override
    public JComponent getComponent() {
        load();
        return viewer;
    }

    private void load() {
        setData(this.helloService.helloAsBinaryData());
    }
}
