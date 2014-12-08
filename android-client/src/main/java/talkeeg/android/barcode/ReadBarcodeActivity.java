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
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.zxing.BinaryBitmap;
import talkeeg.android.App;
import talkeeg.android.CacheDirManager;
import talkeeg.android.ImageUtils;
import talkeeg.android.R;
import talkeeg.bf.Bf;
import talkeeg.bf.BinaryData;
import talkeeg.common.barcode.BarcodeService;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * activity reading barcode for clients acquaintance
 * TODO make decoding work in another thread
 * Created by wayerr on 05.12.14.
 */
public final class ReadBarcodeActivity extends Activity {
    private static final String TAG = ReadBarcodeActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_SIZE = 640;
    private static final String SAVE_BITMAP = "bitmap";
    private static final String SAVE_MESSAGE_DATA = "messageData";
    private File takenImageFile;
    private BinaryData messageData;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_barcode_activity);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && this.takenImageFile != null) {
            this.bitmap = ImageUtils.loadImage(this.takenImageFile, IMAGE_SIZE);

            this.takenImageFile.delete();
            this.takenImageFile = null;

            loadBarcodeData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadBarcodeData() {
        final App app = (App)getApplication();
        final BarcodeService barcodeService = app.get(BarcodeService.class);

        final ImageView image = (ImageView)findViewById(R.id.imageView);
        image.setImageBitmap(this.bitmap);
        if(this.bitmap != null && this.messageData == null) {
            BinaryBitmap binaryBitmap = BarcodeUtils.toBinaryBitmap(this.bitmap);
            try {
                this.messageData = barcodeService.decode(binaryBitmap);
            } catch(Exception e) {
                Toast.makeText(app, "no barcode data " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        }

        if(this.messageData != null) {
            final Bf bf = app.get(Bf.class);
            final Object message;
            try {
                message = bf.read(ByteBuffer.wrap(messageData.getData()));
            } catch(Exception e) {
                Log.e(getLocalClassName(), "can not decode barcode data: ", e);
                Toast.makeText(app, "can not decode barcode data", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(app, "Message: " + message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_BITMAP, this.bitmap);
        if(this.messageData != null) {
            outState.putByteArray(SAVE_MESSAGE_DATA, this.messageData.getData());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.bitmap = savedInstanceState.getParcelable(SAVE_BITMAP);
        byte[] byteArray = savedInstanceState.getByteArray(SAVE_MESSAGE_DATA);
        if(byteArray != null) {
            this.messageData = new BinaryData(byteArray);
        }
        loadBarcodeData();
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
