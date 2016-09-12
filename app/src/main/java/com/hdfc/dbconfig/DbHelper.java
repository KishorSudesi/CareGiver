package com.hdfc.dbconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.hdfc.libs.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class DbHelper extends SQLiteOpenHelper {

    public static final String strTableNameCollection = "collections";
    public static final String strTableNameMilestone = "milestones";
    //public static final String strTableNameFiles = "files";
    public static final String DEFAULT_DB_DATE = "2016-01-01T06:04:57.691Z";
    public static final String COLUMN_OBJECT_ID = "object_id";
    public static final String COLUMN_CUSTOMER_ID = "customer_id";
    public static final String COLUMN_PROVIDER_ID = "provider_id";
    public static final String COLUMN_MILESTONE_DATE = "milestone_date";
    public static final String COLUMN_MILESTONE_ID = "milestone_id";
    public static final String COLUMN_UPDATE_DATE = "updated_date";
    public static final String COLUMN_DOCUMENT = "document";
    public static final String COLUMN_COLLECTION_NAME = "collection_name";
    public static final String COLUMN_CLIENT_FLAG = "client_flag";
    public static final String COLUMN_NEW_UPDATED = "new_updated";
    public static final String COLLECTION_FIELDS[] = {"object_id", "updated_date", "document",
            "collection_name", "client_flag", "new_updated", "provider_id"};

    public static final String COLLECTION_FIELDS_CD[] = {"object_id", "updated_date", "document",
            "collection_name", "provider_id", "new_updated"};

    public static final String COLLECTION_FIELDS_CLIENTS[] = {"object_id",
            "collection_name", "provider_id"};

    public static final String MILESTONE_FIELDS[] = {"object_id", "milestone_id", "milestone_date",
            "provider_id", "status"};
    public static final String CCARE_FIELDS[] = {"object_id", "milestone_id", "milestone_date",
            "customer_id", "provider_id"};

    private static final int DATABASE_VERSION = 15;
    private static final String DATABASE_NAME = "caregiver";
    //private static String dbPass = ""; //"hdfc@12#$";//
    private static DbHelper dbInstance = null;
    private static SQLiteDatabase db;
    //private Utils utils;
    private String strCollectionsQuery = "CREATE TABLE " + strTableNameCollection
            + " ( id integer primary key autoincrement, object_id VARCHAR(50), updated_date "
            + "datetime, document text, collection_name VARCHAR(50), "
            + "client_flag integer, new_updated integer, provider_id VARCHAR(50))";
    //doc_date datetime,

    private String strMilestoneQuery = "CREATE TABLE " + strTableNameMilestone + " ( id integer "
            + "primary key autoincrement, object_id VARCHAR(50), milestone_id integer,"
            + " milestone_date datetime, customer_id VARCHAR(50), provider_id VARCHAR(50)," +
            " status VARCHAR(50))";

    /*private String strFilesQuery = "CREATE TABLE " + strTableNameFiles + " ( id integer primary key autoincrement," +
            " name VARCHAR(100), url VARCHAR(300), file_type VARCHAR(10),  file_hash VARCHAR(50))";
*/
    private Context _ctxt;

    //private File originalFile = null;

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //utils = new Utils(context);
        //originalFile = context.getDatabasePath(DATABASE_NAME);
        this._ctxt = context;

       /* try {
            dbPass = AESCrypt.decrypt(Config.string, "IqSKDxDO7p2HjCs+8R4Z0A==");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }*/
    }

    static synchronized DbHelper getInstance(Context ctx) {

        if (dbInstance == null) {
            dbInstance = new DbHelper(ctx);
        }
        return dbInstance;
    }

    // Open the database connection.
    public void open() {
        try {
            //SQLiteDatabase.loadLibs(_ctxt);
            db = this.getWritableDatabase(); //dbPass
        } catch (Exception | UnsatisfiedLinkError e1) {
           /* try {
                if (originalFile.exists())
                    encrypt(true);
                e1.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }*/
            e1.printStackTrace();
        }
        Utils.log("DB", "open");
    }

    public synchronized void close() {
        try {
            if (db != null && db.isOpen())
                db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.log("DB", "close");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(strCollectionsQuery);
        //db.execSQL(strFilesQuery);
        db.execSQL(strMilestoneQuery);
        Utils.log("DB", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            /*if(db!=null&&db.isOpen())
                db.close();

            encrypt(false);*/

            //use ALTER for updating without losing data

            if (oldVersion < newVersion) {
                dropDb(db);
                onCreate(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dropDb(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + strTableNameCollection);
        //db.execSQL("DROP TABLE IF EXISTS " + strTableNameFiles);
        db.execSQL("DROP TABLE IF EXISTS " + strTableNameMilestone);
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

    void truncateDatabase() {
        dropDb(db);
        onCreate(db);
    }

    Cursor fetch(String tbl, String names[], String where, String args[], String order,
                 String limit, boolean isDistinct, String groupBy, String having) {

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

    Cursor rawQuery(String query) {

        if (db != null && !db.isOpen())
            open();
        //Cursor cur = null;
        //try {
        return db.rawQuery(query, null);
        /*} catch (Exception e) {
            return null;
        }*/
    }

    void beginDBTransaction() {
        if (db != null && !db.isOpen())
            open();
        db.beginTransaction();
    }

    void endDBTransaction() {
        if (db != null && !db.isOpen())
            open();
        db.endTransaction();
    }

    void dbTransactionSuccessFull() {
        if (db != null && !db.isOpen())
            open();
        db.setTransactionSuccessful();
    }


   /* public boolean backupDatabase() {

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
    }*/

  /*  private void encrypt(boolean isToOpen) throws IOException {

        if (originalFile.exists()) {

            //for encrypting the unencrypted database

            File newFile = File.createTempFile("database", "_tmp_", _ctxt.getFilesDir());

            String dbPath = originalFile.getAbsolutePath();

            SQLiteDatabase db = SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(), "",
                    null, SQLiteDatabase.OPEN_READWRITE); //dbPass

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
    }*/

    void moveToExternal() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            //File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = _ctxt.getDatabasePath(DATABASE_NAME).getAbsolutePath();
                String backupDBPath = "caregiver";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            // Utils.log(sd,"path");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}