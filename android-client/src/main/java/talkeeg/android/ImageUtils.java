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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.google.common.io.Closeables;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by wayerr on 05.12.14.
 */
public final class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();

    /**
     * load and scale image to maxSzie
     * @param imageFile
     * @param maxSize max size for each image dimension
     * @return
     */
    public static Bitmap loadImage(File imageFile, final int maxSize) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        loadImage(imageFile, opts);
        int loadedMaxSize = Math.max(opts.outHeight, opts.outWidth);
        if(loadedMaxSize > maxSize) {
            final float factor = (float)loadedMaxSize / (float)maxSize;
            final float factorRemainder = factor % 1;
            if(factorRemainder > .2) {
                opts.inTargetDensity = (int)(1f/ factorRemainder);
            } else {
                opts.inTargetDensity = 1;
            }
            opts.inDensity = (int)(factor * opts.inTargetDensity);
            opts.inScaled = true;
        }
        opts.inJustDecodeBounds = false;
        return loadImage(imageFile, opts);
    }

    private static Bitmap loadImage(File imageFile, BitmapFactory.Options opts) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imageFile);
            return BitmapFactory.decodeStream(fis, null, opts);
        } catch(IOException e) {
            Log.e(TAG, "on file: " + imageFile, e);
        } finally {
            Closeables.closeQuietly(fis);
        }
        return null;
    }
}
