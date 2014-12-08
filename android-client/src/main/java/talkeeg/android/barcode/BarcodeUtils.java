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

package talkeeg.android.barcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import java.util.Arrays;

/**
 * utilites fo zixng library
 *
 * Created by wayerr on 08.12.14.
 */
final class BarcodeUtils {

    private BarcodeUtils() {
    }


    static BinaryBitmap toBinaryBitmap(Bitmap bitmap) {
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();
        final int pixels[] = new int[w * h];
        //TODO
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        LuminanceSource luminanceSource = new RGBLuminanceSource(w, h, pixels);
        return new BinaryBitmap(new HybridBinarizer(luminanceSource));
    }

    static Bitmap toImageBitmap(BitMatrix matrix) {
        final int factor = 2;
        final int w = matrix.getWidth();
        final int h = matrix.getHeight();
        final Bitmap bitmap = Bitmap.createBitmap(w * factor, h * factor, Bitmap.Config.RGB_565);
        final int blackPixels[] = new int[factor * factor];
        Arrays.fill(blackPixels, Color.BLACK);
        final int whitePixels[] = new int[factor * factor];
        Arrays.fill(whitePixels, Color.WHITE);
        for (int x = 0; x < w; x++) {
            final int bitmapx = x * factor;
            for (int y = 0; y < h; y++) {
                int pixels[] = matrix.get(x, y) ? blackPixels : whitePixels;
                bitmap.setPixels(pixels, 0, factor, bitmapx, y * factor, factor, factor);
            }
        }
        return bitmap;
    }
}
