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

package talkeeg.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.zxing.common.BitMatrix;
import dagger.ObjectGraph;
import talkeeg.bf.BinaryData;
import talkeeg.common.barcode.BarcodeService;
import talkeeg.common.core.HelloService;
import java.util.Arrays;

/**
 * activity for displaying barcodes
 *
 * Created by wayerr on 03.12.14.
 */
public class BarcodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_activity);

        final ObjectGraph objectGraph = ((App)getApplication()).getObjectGraph();
        BarcodeService barcodeService = objectGraph.get(BarcodeService.class);
        HelloService helloService = objectGraph.get(HelloService.class);
        final ImageView image = (ImageView)findViewById(R.id.imageView);
        BinaryData data = helloService.helloAsBinaryData();
        final BitMatrix matrix = barcodeService.encode(data);

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
        image.setImageBitmap(bitmap);
    }
}
