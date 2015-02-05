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

package talkeeg.common.barcode;

import com.google.zxing.common.BitMatrix;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

/**
 * barcode utils for Java SE <p/>
 * Created by wayerr on 05.02.15.
 */
public class BarcodeUtilsSE {

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        return toBufferedImage(matrix, BarcodeRenderConfig.DEFAULT);
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix, BarcodeRenderConfig config) {
        final int width = matrix.getWidth();
        final int height = matrix.getHeight();
        final int factor = config.getFactor();

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
}
