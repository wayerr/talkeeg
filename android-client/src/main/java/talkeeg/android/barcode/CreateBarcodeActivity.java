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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import com.google.zxing.common.BitMatrix;
import dagger.ObjectGraph;
import talkeeg.android.App;
import talkeeg.android.R;
import talkeeg.bf.BinaryData;
import talkeeg.common.barcode.BarcodeService;
import talkeeg.common.core.HelloService;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * activity for displaying barcodes
 *
 * Created by wayerr on 03.12.14.
 */
public final class CreateBarcodeActivity extends Activity {

    private static final class CreateBarcodeTask extends AsyncTask<Object, Object, Bitmap> {
        private Activity activity;

        @Override
        protected Bitmap doInBackground(Object[] params) {
            BarcodeService barcodeService = App.get(BarcodeService.class);
            HelloService helloService = App.get(HelloService.class);
            BinaryData data = helloService.helloAsBinaryData();
            final BitMatrix matrix = barcodeService.encode(data);
            final Bitmap bitmap = BarcodeUtils.toImageBitmap(matrix);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            final ImageView image = (ImageView)this.activity.findViewById(R.id.imageView);
            image.setImageBitmap(bitmap);
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }
    }

    private static CreateBarcodeTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_barcode_activity);
        if(task == null) {
            task = new CreateBarcodeTask();
            task.execute();
        }
        task.setActivity(this);
    }
}
