package com.hdfc.libs;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hdfc.app42service.App42GCMService;
import com.hdfc.caregiver.FeatureActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.models.Action;
import com.hdfc.models.NotificationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by balamurugan@adstringo.in on 23-12-2015.
 */
public class Utils {

    public static final Locale locale = Locale.ENGLISH;

    public final static SimpleDateFormat readFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);

    public final static SimpleDateFormat queryFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);

    public final static SimpleDateFormat writeFormat = new
            SimpleDateFormat("kk:mm dd MMM yyyy", locale);

    public final static SimpleDateFormat writeFormatDate = new
            SimpleDateFormat("dd-MMM-yyyy", locale);

    public final static SimpleDateFormat writeFormatDateDB = new
            SimpleDateFormat("yyyy-MM-dd", locale);

    public final static SimpleDateFormat writeFormatTime = new
            SimpleDateFormat("kk:mm", locale); // aa

  /*  public final static SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Config.locale);*/

   /* public final static SimpleDateFormat readFormatDate =
            new SimpleDateFormat("yyyy-MM-dd", Config.locale);
*/

    public static Uri customerImageUri;
    private static Context _ctxt;

    static {
        System.loadLibrary("stringGen");
        //log("Loaded 0", "NDK");
    }

    public Utils(Context context) {
        _ctxt = context;

        WindowManager wm = (WindowManager) _ctxt.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Config.intScreenWidth = metrics.widthPixels;
        Config.intScreenHeight = metrics.heightPixels;

        readFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        //queryFormat.setTimeZone(TimeZone.getDefault());
    }

    public static native String getString();

    public static String getStringJni() {
        //log("Loaded 1", "NDK");
        //return "KaEO19Fc";
        return getString();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //creating scaled bitmap with required width and height
    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight) {

        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight);

        Bitmap scaledBitmap = null;

        try {
            scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        }

        if (scaledBitmap != null) {
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
        }

        return scaledBitmap;
    }

    /*public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("image") == 0;
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("video") == 0;
    }

    public static boolean isAudioFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("audio") == 0;
    }

    public static boolean getAllFilesOfDirSize(File directory) {

        final File[] files = directory.listFiles();

        try {

            if (files != null) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {  // it is a folder.
                            getAllFilesOfDirSize(file);

                        } else {  // it is a file...

                            if (file.exists() && file.canRead() && file.canWrite()) {


                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

    }*/

    //
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bmp = null;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions

            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, reqWidth,
                    reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inDither = false;

            bmp = createScaledBitmap(BitmapFactory.decodeResource(res, resId, options), reqWidth,
                    reqHeight);

        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }

        return bmp;
    }//

    //
    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {

        //check this logic

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
        //
        /*
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }/////////////////*/
    }
    //

    //source and destinatino rectangular regions to decode
    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        //for crop
            /*final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int)(srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int)(srcWidth / dstAspect);
                final int scrRectTop = (srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }*/

        return new Rect(0, 0, srcWidth, srcHeight);
    }

    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {

        final float srcAspect = (float) srcWidth / (float) srcHeight;
        final float dstAspect = (float) dstWidth / (float) dstHeight;

        if (srcAspect > dstAspect) {
            return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
        } else {
            return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
        }
        //for crop
        //return new Rect(0, 0, dstWidth, dstHeight);

    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static void log(String message, String tag) {

        if ((tag == null || tag.equalsIgnoreCase("")) && _ctxt != null)
            tag = _ctxt.getClass().getName();

        if (Config.isDebuggable)
            Log.e(tag, message);

    }

    public static String sha512(final String toEncrypt) {

        try {

            final MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();

            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString().toLowerCase();

        } catch (Exception exc) {
            return "";
        }
    }

    /*public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return (availableBlocks * blockSize) / 1024;
        } else {
            return 0;
        }
    }

    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return (totalBlocks * blockSize) / 1024;
        } else {
            return 0;
        }
    }*/

   /* public static ArrayList<String> getExternals() {

        ArrayList<String> pathExternals;
        try {

            pathExternals = new ArrayList<String>();

            final String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {  // we can read the External Storage...
                //Retrieve the primary External Storage:
                final File primaryExternalStorage = Environment.getExternalStorageDirectory();

                //Retrieve the External Storages root directory:
                final String externalStorageRootDir;
                if ((externalStorageRootDir = primaryExternalStorage.getParent()) == null) {  // no parent...
                    pathExternals.add(primaryExternalStorage.getAbsolutePath());
                } else {
                    final File externalStorageRoot = new File(externalStorageRootDir);
                    final File[] files = externalStorageRoot.listFiles();
                    for (final File file : files) {
                        if (file.isDirectory() && file.canRead() && (file.listFiles().length > 0)) {  // it is a real directory (not a USB drive)...
                            pathExternals.add(file.getAbsolutePath());
                        }
                    }
                }
            } else pathExternals = null;

        } catch (Exception e) {
            e.printStackTrace();
            pathExternals = null;
        }

        return pathExternals;
    }*/

    public static boolean deleteAllFiles(File directory) {

        final File[] files = directory.listFiles();

        try {

            if (files != null) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {  // it is a folder.
                            deleteAllFiles(file);
                        } else {
                            if (file.exists() && file.canRead() && file.canWrite()) {
                                file.delete();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    /*public static void recordAudio(String fileName) {

        MediaRecorder mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorder.start();

    }*/
    //

  /*  public static String getDeviceID(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);

    }

    public static void hideSoftKeyboard(Activity activity) {
        try {

            if (activity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String loadJSONFromFile(String path) {
        String json = null;
        try {

            File f = new File(path);
            InputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public static void setBtnDrawable(Button btn, Drawable drw) {
        if (Build.VERSION.SDK_INT <= 16)
            btn.setBackgroundDrawable(drw);
        else
            btn.setBackground(drw);
    }*/

    public static File createFileInternal(String strFileName) {

        File file = null;
        try {
            file = new File(_ctxt.getFilesDir(), strFileName);
            file.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    /*public static String encrypt(String Data) {

        String encryptedValue = null;
        Cipher c = null;
        try {
            Key key = generateKey();
            c = Cipher.getInstance(mode);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(Data.getBytes());
            encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedValue;
    }*/


   /* public static String decrypt(String encryptedData) {
        String decryptedValue = null;
        Cipher c = null;

        try {
            Key key = generateKey();
            c = Cipher.getInstance(mode);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decode(encryptedData, Base64.DEFAULT);
            byte[] decValue = c.doFinal(decordedValue);
            decryptedValue = new String(decValue);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(Config.string.getBytes(), mode);
        return key;
    }*/

    public static void cancelNotification(int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) _ctxt.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

   /* public String getMonthLastDate(String strFromDate) {

        String strLastDateMonth = "";

        Date today = null;
        try {
            today = readFormatDate.parse(strFromDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        Date lastDayOfMonth = calendar.getTime();

        strLastDateMonth = dateFormat.format(lastDayOfMonth) + "T23:59:59.999+0000";
        log(strLastDateMonth, "LAST DATE ");

        return strLastDateMonth;
    }*/

    //
    public boolean compressImageFromPath(String strPath, int reqWidth, int reqHeight, int iQuality) {

        boolean b = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bmp = null;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(strPath, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, reqWidth,
                    reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inDither = false;

          /*  reqWidth = options.outWidth;
            reqHeight = options.outHeight;*/

            log(String.valueOf(reqWidth), " WIDTH ");
            log(String.valueOf(reqHeight), " HEIGHT ");

            bmp = createScaledBitmap(BitmapFactory.decodeFile(strPath), reqWidth,
                    reqHeight);

            //jpeg compress
            byte[] bmpPicByteArray;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.reset();
            bmp.compress(Bitmap.CompressFormat.JPEG, iQuality, bos);
            bmpPicByteArray = bos.toByteArray();
            bmp.recycle();

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(new File(strPath));
                fos.write(bmpPicByteArray);
                fos.flush();
                fos.close();
                bos.reset();

            } catch (IOException e) {
                fos.flush();
                fos.close();
                bos.reset();
                e.printStackTrace();
                b = false;
            }
            //

        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            b = false;
        }

        return b;
    }

    public void createNotificationModel(String strDocumentId, String strDocument) {
        try {

            JSONObject jsonObjectProvider = new JSONObject(strDocument);

            if (jsonObjectProvider.has("message")) {

                NotificationModel notificationModel = new NotificationModel(
                        jsonObjectProvider.getString(App42GCMService.ExtraMessage),
                        jsonObjectProvider.getString("time"),
                        jsonObjectProvider.getString("user_type"),
                        jsonObjectProvider.getString("created_by_type"),
                        jsonObjectProvider.getString("user_id"),
                        jsonObjectProvider.getString("created_by"), strDocumentId);

                if (jsonObjectProvider.getString("created_by_type").equalsIgnoreCase("customer")) {
                    if (!Config.customerIdsAdded.contains(jsonObjectProvider.getString("created_by")))
                        Config.customerIds.add(jsonObjectProvider.getString("created_by"));
                }

                if (jsonObjectProvider.getString("created_by_type").equalsIgnoreCase("dependent")) {
                    if (!Config.dependentIdsAdded.contains(jsonObjectProvider.getString("created_by")))
                        Config.dependentIds.add(jsonObjectProvider.getString("created_by"));
                }

                if (!Config.strNotificationIds.contains(strDocumentId)) {
                    Config.strNotificationIds.add(strDocumentId);
                    Config.notificationModels.add(notificationModel);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void toast(int type, int duration, String message) {

        String strColor = "#ffffff";

        if (type == 2)
            strColor = "#fcc485";

        try {
            LayoutInflater inflater = ((Activity) _ctxt).getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) ((Activity) _ctxt).
                    findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(message);
            text.setTextColor(Color.parseColor(strColor));

            Toast toast = new Toast(_ctxt);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

            //if (duration == 2)
                toast.setDuration(Toast.LENGTH_LONG);
           /* else
                toast.setDuration(Toast.LENGTH_SHORT);*/

            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
            log(" 1 ", " 1 ");
            Toast.makeText(_ctxt, message, Toast.LENGTH_SHORT).show();
        }
    }

    public File getInternalFileImages(String strFileName) {

        File file = null;
        try {
            file = new File(_ctxt.getFilesDir(), "images/" + strFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //load image from url
    void loadImageFromWeb(String strFileName, String strFileUrl) {

        strFileName = replaceSpace(strFileName.trim());
        strFileUrl = replaceSpace(strFileUrl.trim());

        File fileImage = createFileInternal("images/" + strFileName);

        log(strFileName + " ~ " + strFileUrl, " PATHS ");

        if (fileImage.length() <= 0) {

            InputStream input;
            try {

                URL url = new URL(strFileUrl); //URLEncoder.encode(fileModel.getStrFileUrl(), "UTF-8")
                input = url.openStream();
                byte[] buffer = new byte[1500];
                OutputStream output = new FileOutputStream(fileImage);
                try {
                    int bytesRead;
                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                        output.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void moveFile(File file, File newFile) throws IOException {
        //File newFile = new File(dir, file.getName());
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(file).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            file.delete();
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }

  /*  public void moveFileDir(File file, File dir) throws IOException {
        File newFile = new File(dir, file.getName());
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(file).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            file.delete();
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }*/

    public void copyFile(File file, File newFile) throws IOException {
        //File newFile = new File(dir, file.getName());
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(file).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            //file.delete();
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }

    public boolean isEmailValid(String email) {
        boolean b;

        b = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (b) {
            //^[\w\-]([\.\w])+[\w]+@([\w\-]+\.)+[A-Z]{2,4}$
            Pattern p = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(email);
            b = m.matches();
        }

        return b;
    }


   /* public String getUUID() {
        final TelephonyManager tm = (TelephonyManager) _ctxt.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;


        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(_ctxt.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        return deviceId;
    }*/


    /*public void getMemory() {
        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int) rt.maxMemory() / (1024 * 1024);
        int totalMemory = (int) rt.totalMemory() / (1024 * 1024);
    }

    public void createFolder(String path) {
        File root = new File(path);
        if (!root.exists()) {
            root.mkdirs();
        }
    }

    public void setExifData(String pathName) throws Exception {

        try {
            //working for Exif defined attributes
            ExifInterface exif = new ExifInterface(pathName);
            exif.setAttribute(ExifInterface.TAG_MAKE, "1000");
            exif.saveAttributes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public String convertDateToString(Date dtDate) {

        String date = null;

        try {
            date = readFormat.format(dtDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //log("Utils", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        return date; //
    }

    public String convertDateToStringQuery(Date dtDate) {

        String date = null;

        try {
            date = queryFormat.format(dtDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //log("Utils", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        return date; //
    }

    public Date convertStringToDate(String strDate) {

        Date date = null;
        try {
            //log("erro r", String.valueOf(strDate));
            date = readFormat.parse(strDate);
            //log("error", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date; //
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager)
                _ctxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    /*public boolean isPasswordValid(String password) {
        return password.length() > 1;
    }*/

    /**
     * Shows the progress UI and hides the login form.
     */
  /*  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View mFormView, final View mProgressView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = _ctxt.getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }*/

    public boolean validCellPhone(String number) {
        //return android.util.Patterns.PHONE.matcher(number).matches();

        boolean isValid = false;

        if (number.length() >= 6 && number.length() <= 15)
            isValid = true;

        return isValid;
    }

    public File createFileInternalImage(String strFileName) {

        File file = null;
        try {
            file = new File(_ctxt.getExternalFilesDir(Environment.DIRECTORY_PICTURES), strFileName);
            file.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public void selectImage(final String strFileName, final Fragment fragment,
                            final Activity activity, final boolean isSingle) {

        try {
            final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
            // System.out.println("Pilu : " + FeatureActivity.IMAGE_COUNT);

            AlertDialog.Builder builder = new AlertDialog.Builder(_ctxt);

            builder.setTitle("Select a Image");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    //System.out.println(items[item].equals("Take Photo"));
                    if (items[item].equals("Take Photo")) {
                        openCamera(strFileName, fragment, activity);
                        //System.out.println("DDDDDDDIC DIC DIC DIC ::: " + strFileName);

                    } else if (items[item].equals("Choose from Library")) {

                        Intent intent;

                        if (isSingle) {
                            intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);

                            if (fragment != null)
                                fragment.startActivityForResult(Intent.createChooser(intent,
                                        "Select a Picture"), Config.START_GALLERY_REQUEST_CODE);
                            else
                                activity.startActivityForResult(Intent.createChooser(intent,
                                        "Select a Picture"), Config.START_GALLERY_REQUEST_CODE);
                        } else {
                            intent = new Intent(Action.ACTION_MULTIPLE_PICK);

                            if (fragment != null)
                                fragment.startActivityForResult(intent,
                                        Config.START_GALLERY_REQUEST_CODE);
                            else
                                activity.startActivityForResult(intent,
                                        Config.START_GALLERY_REQUEST_CODE);
                        }


                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openCamera(String strFileName, Fragment fragment, final Activity activity) {

        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            FeatureActivity.IMAGE_COUNT = FeatureActivity.IMAGE_COUNT + 1;
            File file = createFileInternalImage(strFileName);
            customerImageUri = Uri.fromFile(file);
            if (file != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, customerImageUri);

                if (fragment != null)
                    fragment.startActivityForResult(cameraIntent, Config.START_CAMERA_REQUEST_CODE);
                else
                    activity.startActivityForResult(cameraIntent, Config.START_CAMERA_REQUEST_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyInputStreamToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EditText traverseEditTexts(ViewGroup v, Drawable all, Drawable current,
                                      EditText editCurrent) {
        EditText invalid = null;
        for (int i = 0; i < v.getChildCount(); i++) {
            Object child = v.getChildAt(i);
            if (child instanceof EditText) {
                EditText e = (EditText) child;

                if (e.getId() == editCurrent.getId())
                    setEditTextDrawable(e, current);
                else
                    setEditTextDrawable(e, all);
            } else if (child instanceof ViewGroup) {
                invalid = traverseEditTexts((ViewGroup) child, all, current, editCurrent);  // Recursive call.
                if (invalid != null) {
                    break;
                }
            }
        }
        return invalid;
    }

    public void setEditTextDrawable(EditText editText, Drawable drw) {
        if (Build.VERSION.SDK_INT <= 16)
            editText.setBackgroundDrawable(drw);
        else
            editText.setBackground(drw);
    }

 /*public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }*/

    /*public void setDrawable(View v, Drawable drw) {
        if (Build.VERSION.SDK_INT <= 16)
            v.setBackgroundDrawable(drw);
        else
            v.setBackground(drw);
    }

    public void setStatusBarColor(String strColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) _ctxt).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(strColor));
        }
    }*/

    public Bitmap roundedBitmap(Bitmap bmp){
        Bitmap output = null;

        try {
            output = Bitmap.createBitmap(bmp.getWidth(),
                    bmp.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.parseColor("#BAB399"));
            canvas.drawCircle(bmp.getWidth() / 2 + 0.7f, bmp.getHeight() / 2+ 0.7f,//
                    bmp.getWidth() / 2+ 0.1f, paint); //
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bmp, rect, rect, paint);
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }
        return output;
    }

    public String replaceSpace(String string) {
        string = string.replace(" ", "_");
        return string;
    }

    /*public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }*/

    //Application Specigfic Start

   /* public Bitmap getBitmapFromFile(String strPath, int intWidth, int intHeight) {
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
    }*/


    //Application Specig=fic End

    public Bitmap getBitmapFromFile(String strPath, int intWidth, int intHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap original = null;
        if (strPath != null && !strPath.equalsIgnoreCase("")) {
            try {
                options.inJustDecodeBounds = true;
                original = BitmapFactory.decodeFile(strPath, options);
                options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight,
                        intWidth, intHeight);
                options.inJustDecodeBounds = false;
                original = BitmapFactory.decodeFile(strPath, options);
            } catch (OutOfMemoryError | Exception oOm) {
                oOm.printStackTrace();
            }
        }

        return original;
    }

    public ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }
        }
        return views;
    }

    public ArrayList<String> getEditTextValueByTag(ViewGroup root, String tag) {
        ArrayList<String> strValues = new ArrayList<>();
        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; i++) {

            final View child = root.getChildAt(i);

            if (child instanceof EditText) {
                final Object tagObj = child.getTag();
                if (tagObj != null && tagObj.equals(tag)) {
                    strValues.add(((EditText) child).getText().toString().trim());
                }
            } else {
                if (child instanceof LinearLayout) {
                    strValues.addAll(getEditTextValueByTag((ViewGroup) child, tag));
                }
            }
        }
        return strValues;
    }

    public String formatDate(String strDate){

        String strDisplayDate="06-03-2016 20:55:00";

        if(strDate!=null&&!strDate.equalsIgnoreCase("")) {
            Date date = convertStringToDate(strDate);

            if(date!=null)
                strDisplayDate = writeFormat.format(date);
        }

        return strDisplayDate;
    }

    public String formatDateTime(String strDate) {

        String strDisplayDate = "06-03-2016 20:55:00";

        if (strDate != null && !strDate.equalsIgnoreCase("")) {
            Date date = convertStringToDate(strDate);

            if (date != null)
                strDisplayDate = writeFormatTime.format(date);
        }

        return strDisplayDate;
    }

    public String[] jsonToStringArray(JSONArray jsonArray) {

        String strings[] = new String[0];

        try {
            int iLength = jsonArray.length();

            strings = new String[iLength];

            for (int i = 0; i < iLength; i++) {
                strings[i] = jsonArray.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return strings;
    }

    public int[] jsonToIntArray(JSONArray jsonArray) {

        int ints[] = new int[0];

        try {
            int iLength = jsonArray.length();

            ints = new int[iLength];

            for (int i = 0; i < iLength; i++) {
                ints[i] = jsonArray.getInt(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ints;
    }

    public JSONArray intToJsonArray(int ints[]) {

        JSONArray jsonArray = new JSONArray();

        try {
            int iLength = ints.length;

            for (int i = 0; i < iLength; i++) {
                jsonArray.put(ints[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray stringToJsonArray(String string[]) {

        JSONArray jsonArray = new JSONArray();

        try {
            int iLength = string.length;

            for (int i = 0; i < iLength; i++) {
                jsonArray.put(string[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

}
