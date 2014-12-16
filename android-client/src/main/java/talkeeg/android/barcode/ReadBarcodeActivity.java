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
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.google.zxing.BinaryBitmap;
import talkeeg.android.*;
import talkeeg.bf.Bf;
import talkeeg.bf.BinaryData;
import talkeeg.common.barcode.BarcodeService;
import talkeeg.common.core.AcquaintService;
import talkeeg.common.core.AcquaintedUsersService;
import talkeeg.common.core.CacheDirsService;
import talkeeg.common.model.Hello;
import talkeeg.common.model.UserIdentityCard;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * activity reading barcode for clients acquaintance
 * Created by wayerr on 05.12.14.
 */
public final class ReadBarcodeActivity extends Activity {

    /**
     * in this class we save data between configuration changes of activity (in other cases we don`t need state)
     * but code looks awful and need rewriting, especially `syncronized` statements
     */
    private static class Model {
        private class LoadBarcodeDataTask extends AsyncTask<Object, Object, Object> {

            @Override
            protected Object doInBackground(Object ... params) {
                Bitmap bitmapLocal = null;
                synchronized(Model.this) {
                    if(imageFile != null) {
                        bitmap = ImageUtils.loadImage(imageFile, IMAGE_SIZE);

                        imageFile.delete();
                        imageFile = null;
                    }
                    if(bitmap != null && messageData == null) {
                        bitmapLocal = bitmap;
                    }
                }
                final BarcodeService barcodeService = App.get(BarcodeService.class);
                BinaryData messageDataLocal = null;
                if(bitmapLocal != null ) {
                    BinaryBitmap binaryBitmap = BarcodeUtils.toBinaryBitmap(bitmapLocal);
                    try {
                        //most long-run statement in this code
                        messageDataLocal = barcodeService.decode(binaryBitmap);
                    } catch(Exception e) {
                        Log.e(TAG, "no barcode data: ", e);
                    }
                    synchronized(Model.this) {
                        if(bitmapLocal == bitmap) {
                            messageData = messageDataLocal;
                        }
                    }
                }
                synchronized(Model.this) {
                    if(messageData != null && message == null) {
                        final Bf bf = App.get(Bf.class);
                        try {
                            message = bf.read(ByteBuffer.wrap(messageData.getData()));
                        } catch(Exception e) {
                            Log.e(TAG, "can not decode barcode data: ", e);
                        }
                    }

                    if(message instanceof Hello) {
                        final AcquaintService usersService = App.get(AcquaintService.class);
                        final Hello hello = (Hello)message;
                        usersService.acquaint(hello);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                ReadBarcodeActivity activity = null;
                synchronized(Model.this) {
                    if(currentActivity != null) {
                        activity = currentActivity;
                    }
                }
                activity.updateFromModel();
            }
        }

        private ReadBarcodeActivity currentActivity;

        private LoadBarcodeDataTask task;

        private File imageFile;
        private BinaryData messageData;
        private Bitmap bitmap;
        private Object message;

        private Model() {
        }


        private synchronized void loadData(ReadBarcodeActivity activity) {
            this.messageData = null;
            this.bitmap = null;
            this.message = null;
            setActivity(activity);
            this.task = new LoadBarcodeDataTask();
            this.task.execute();
        }

        private synchronized void setActivity(ReadBarcodeActivity activity) {
            this.currentActivity = activity;
            if(activity != null) {
                final ImageView image = (ImageView)activity.findViewById(R.id.imageView);
                image.setImageBitmap(bitmap);
                final StructureViewerFragment textView = (StructureViewerFragment)activity.getFragmentManager().findFragmentById(R.id.structureViewerFragment);
                textView.setObject(activity, message);
            }
        }

        public synchronized void setFile(File file) {
            this.imageFile = file;
        }
    }


    private static final String TAG = ReadBarcodeActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_SIZE = 640;
    private static Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_barcode_activity);
        if(model == null) {
            model = new Model();
        }
        updateFromModel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.setActivity(null);
    }

    private void updateFromModel() {
        model.setActivity(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            model.loadData(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * show application fot taking image with barcode
     *
     * @param view
     */
    public void takeBarcodeImage(View view) {
        Intent takeImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final File tempFile = App.get(CacheDirsService.class).createTempFile("barcode");
        takeImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        model.setFile(tempFile);
        final ComponentName takeImageComponentName = takeImageIntent.resolveActivity(getPackageManager());
        if(takeImageComponentName == null) {
            Log.e(getLocalClassName(), "can not resolve activity for " + takeImageIntent);
            return;
        }
        startActivityForResult(takeImageIntent, REQUEST_IMAGE_CAPTURE);
    }
}
