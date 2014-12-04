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

/**
 * activity for displaying barcodes
 *
 * Created by wayerr on 03.12.14.
 */
public class BarcodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ObjectGraph objectGraph = ((App)getApplication()).getObjectGraph();
        BarcodeService barcodeService = objectGraph.get(BarcodeService.class);
        HelloService helloService = objectGraph.get(HelloService.class);
        final ImageView image = (ImageView)findViewById(R.id.imageView);
        BinaryData data = helloService.helloAsBinaryData();
        final BitMatrix matrix = barcodeService.encode(data);

        final int factor = 2;
        final int w = matrix.getWidth() * factor;
        final int h = matrix.getHeight() * factor;
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int color = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
                bitmap.setPixel(x, y, color);
            }
        }
        image.setImageBitmap(bitmap);
    }
}
