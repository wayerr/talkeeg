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
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.common.io.Closeables;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import talkeeg.bf.Bf;
import talkeeg.bf.BinaryData;
import talkeeg.common.barcode.BarcodeService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * activity reading barcode for clients acquaintance
 *
 * Created by wayerr on 05.12.14.
 */
public final class ReadBarcodeActivity extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private File takenImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_barcode_activity);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && this.takenImageFile != null) {
            final App app = (App)getApplication();
            final BarcodeService barcodeService = app.get(BarcodeService.class);

            Bitmap bitmap = ImageUtils.loadImage(this.takenImageFile, 640);

            this.takenImageFile.delete();
            this.takenImageFile = null;

            final ImageView image = (ImageView)findViewById(R.id.imageView);
            image.setImageBitmap(bitmap);
            BinaryBitmap binaryBitmap = toBinaryBitmap(bitmap);
            BinaryData binaryData;
            try {
                binaryData = barcodeService.decode(binaryBitmap);
            } catch(Exception e) {
                Toast.makeText(app, "no barcode data " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            final Bf bf = app.get(Bf.class);
            final Object message;
            try {
                message = bf.read(ByteBuffer.wrap(binaryData.getData()));
            } catch(Exception e) {
                Log.e(getLocalClassName(), "can not decode barcode data: ", e);
                Toast.makeText(app, "can not decode barcode data", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(app, "Message: " + message, Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static BinaryBitmap toBinaryBitmap(Bitmap bitmap) {
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();
        final int pixels[] = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        LuminanceSource luminanceSource = new RGBLuminanceSource(w, h, pixels);
        return new BinaryBitmap(new HybridBinarizer(luminanceSource));
    }

    /**
     * show application fot taking image with barcode
     * @param view
     */
    public void takeBarcodeImage(View view) {
        Intent takeImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.takenImageFile = ((App)getApplication()).get(CacheDirManager.class).createTempFile("barcode");
        takeImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takenImageFile));
        final ComponentName takeImageComponentName = takeImageIntent.resolveActivity(getPackageManager());
        if (takeImageComponentName == null) {
            Log.e(getLocalClassName(), "can not resolve activity for " + takeImageIntent);
            return;
        }
        startActivityForResult(takeImageIntent, REQUEST_IMAGE_CAPTURE);
    }
}
