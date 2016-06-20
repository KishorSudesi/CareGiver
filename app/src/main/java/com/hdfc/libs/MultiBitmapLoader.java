package com.hdfc.libs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.hdfc.caregiver.R;
import com.hdfc.config.Config;

import java.lang.ref.WeakReference;

/**
 * Created by balamurugan@adstringo.in on 3/5/2016.
 * link: http://stackoverflow.com/questions/13935040/bitmaps-dont-display-using-lrucache-and-asynctask
 */
public class MultiBitmapLoader {

    private static Context _context;
    private static Bitmap preLoadBitmap;
    private static Utils utils;
    private LruCache<String, Bitmap> mMemoryCache;

    public MultiBitmapLoader(Context context) {
        _context = context;
        utils = new Utils(context);

        final int maxMemory = getMemory();
        preLoadBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_icon);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private static boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        //check here
        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || bitmapData.equalsIgnoreCase("") || !bitmapData.equalsIgnoreCase(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private static Bitmap getBitmapFromFile(String strPath, int intWidth, int intHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap original = null;
        if (strPath != null && !strPath.equalsIgnoreCase("")) {
            try {
                options.inJustDecodeBounds = true;
                original = BitmapFactory.decodeFile(strPath, options);
                options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, intWidth, intHeight);
                options.inJustDecodeBounds = false;
                original = BitmapFactory.decodeFile(strPath, options);
            } catch (OutOfMemoryError | Exception oOm) {
                oOm.printStackTrace();
            }
        }
        return original;
    }

    private static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {

        // Raw height and width of image
        int inSampleSize = 1;

        if (srcHeight > dstHeight || srcWidth > dstWidth) {

            final int halfHeight = srcHeight / 2;
            final int halfWidth = srcWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > dstHeight
                    && (halfWidth / inSampleSize) > dstWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private int getMemory() {
        Runtime rt = Runtime.getRuntime();
        return (int) rt.maxMemory() / 1024;
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key != null && bitmap != null && getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void loadBitmap(String strPath, ImageView imgView) {
        if (strPath != null && !strPath.equalsIgnoreCase("")) {
            if (cancelPotentialWork(strPath, imgView)) {
                final Bitmap bitmap = getBitmapFromMemCache(strPath);
                if (bitmap != null) {
                    imgView.setImageBitmap(bitmap);
                } else {
                    BitmapWorkerTask task = new BitmapWorkerTask(imgView);
                    final AsyncDrawable asyncDrawable = new AsyncDrawable(_context.getResources(), preLoadBitmap, task);
                    imgView.setImageDrawable(asyncDrawable);
                    task.execute(strPath);
                }
            }
        }
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        AsyncDrawable(Resources res, Bitmap bitmap,
                      BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<>(bitmapWorkerTask);
        }

        BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String data = "";

        BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            Bitmap tempBitmap = getBitmapFromFile(data, Config.intWidth, Config.intHeight);
            tempBitmap = utils.roundedBitmap(tempBitmap);
            addBitmapToMemoryCache(String.valueOf(params[0]), tempBitmap);
            return tempBitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
