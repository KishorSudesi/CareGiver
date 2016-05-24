package com.hdfc.dbconfig;

import android.content.ContentValues;
import android.content.Context;

import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.scottyab.aescrypt.AESCrypt;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

public class DbHelper extends SQLiteOpenHelper {

    public static final String strTableNameCollection = "collections";
    public static final String strTableNameFiles = "files";
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "caregiver";
    private static String dbPass = ""; //"hdfc@12#$";//
    private static DbHelper dbInstance = null;
    private static SQLiteDatabase db;
    private Utils utils;
    private String strCollectionsQuery = "CREATE TABLE " + strTableNameCollection + " ( id integer primary key autoincrement," +
            " object_id VARCHAR(50), updated_date VARCHAR(20), document text,  collection_name VARCHAR(50), status integer)";

    private String strFilesQuery = "CREATE TABLE " + strTableNameFiles + " ( id integer primary key autoincrement," +
            " name VARCHAR(100), url VARCHAR(300), file_type VARCHAR(10),  file_hash VARCHAR(50))";

    private Context _ctxt;

    private File originalFile = null;

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this._ctxt = context;
        utils = new Utils(context);
        originalFile = _ctxt.getDatabasePath(DATABASE_NAME);

        try {
            dbPass = AESCrypt.decrypt(Config.string, "IqSKDxDO7p2HjCs+8R4Z0A==");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    static synchronized DbHelper getInstance(Context ctx) {

        if (dbInstance == null) {
            dbInstance = new DbHelper(ctx.getApplicationContext());
        }
        return dbInstance;
    }

    // Open the database connection.
    public void open() {
        try {
            SQLiteDatabase.loadLibs(_ctxt);
            db = this.getWritableDatabase(dbPass);
        } catch (Exception | UnsatisfiedLinkError e1) {
            try {
                if (originalFile.exists())
                    encrypt(true);
                e1.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        Utils.log("DB", "open");
    }

    public void close() {
        try {
            if (db != null && db.isOpen())
                db.close();
        } catch (Exception e) {
        }
        Utils.log("DB", "close");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(strCollectionsQuery);
        db.execSQL(strFilesQuery);
        Utils.log("DB", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            encrypt(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dropDb(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + strTableNameCollection);
        db.execSQL("DROP TABLE IF EXISTS " + strTableNameFiles);
    }

    void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed())
            cursor.close();
    }

    private ContentValues createContentValues(String values[], String names[]) {
        ContentValues values1 = new ContentValues();

        for (int i = 0; i < values.length; i++) {
            values1.put(names[i], values[i]);
        }

        return values1;
    }

    long insert(String values[], String names[], String tbl) {

        if (db != null && !db.isOpen())
            open();

        ContentValues initialValues = createContentValues(values, names);
        long inserted = 0;
        try {
            inserted = db.insert(tbl, null, initialValues);
        } catch (Exception e) {
        }
        return inserted;
    }

    Cursor fetch(String tbl, String names[], String where, String args[], String order, String limit,
                 boolean isDistinct, String groupBy, String having) {

        if (db != null && !db.isOpen())
            open();

        //Cursor cur = null;
        //try {
        return db.query(isDistinct, tbl, names, where, args, groupBy, having, order, limit);
        /*} catch (Exception e) {
            return null;
        }*/
    }

    boolean delete(String tbl, String where, String args[]) {

        if (db != null && !db.isOpen())
            open();

        boolean isDeleted = false;
        try {
            isDeleted = db.delete(tbl, where, args) > 0;
        } catch (Exception e) {
        }
        return isDeleted;
    }

    boolean update(String where, String values[], String names[], String tbl, String args[]) {

        if (db != null && !db.isOpen())
            open();

        ContentValues updateValues = createContentValues(values, names);

        boolean isUpdated = false;
        try {
            isUpdated = db.update(tbl, updateValues, where, args) > 0;
        } catch (Exception e) {
        }
        return isUpdated;
    }


    public boolean backupDatabase() {

        boolean isSuccess = false;

        try {
            File databaseFile = _ctxt.getDatabasePath(DATABASE_NAME);
            FileInputStream fIs = new FileInputStream(databaseFile);

            Date now = new Date();
            FileOutputStream fOs = _ctxt.openFileOutput(DATABASE_NAME + "_bkp_" + now.getTime(),
                    Context.MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int length;

            try {
                while ((length = fIs.read(buffer)) > 0) {
                    fOs.write(buffer, 0, length);
                }
                fOs.flush();
                fOs.close();
                fIs.close();
            } catch (Exception e) {
                fOs.flush();
                fOs.close();
                fIs.close();
            }
            isSuccess = true;
        } catch (Exception e) {
        }

        return isSuccess;
    }

    private void encrypt(boolean isToOpen) throws IOException {

        if (originalFile.exists()) {

            File newFile = File.createTempFile("database", "_tmp_", _ctxt.getFilesDir());

            String dbPath = originalFile.getAbsolutePath();

            SQLiteDatabase db = SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(), "",
                    null, SQLiteDatabase.OPEN_READWRITE);

            db.rawExecSQL(String.format("ATTACH DATABASE '%s' AS encrypted KEY '%s';",
                    newFile.getAbsolutePath(), dbPass));
            db.rawExecSQL("SELECT sqlcipher_export('encrypted')");
            db.rawExecSQL("DETACH DATABASE encrypted;");

            int version = db.getVersion();

            if (version < DATABASE_VERSION)
                version = DATABASE_VERSION;

            db.close();

            db = SQLiteDatabase.openDatabase(newFile.getAbsolutePath(),
                    dbPass, null,
                    SQLiteDatabase.OPEN_READWRITE);
            db.setVersion(version);
            db.close();

            originalFile.delete();
            newFile.renameTo(originalFile);

            if (isToOpen) {
                DbHelper.db = SQLiteDatabase.openDatabase(dbPath,
                        dbPass, null,
                        SQLiteDatabase.OPEN_READWRITE);
            }
        }
    }
}