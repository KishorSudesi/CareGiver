package com.hdfc.dbconfig;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import net.sqlcipher.Cursor;

public class DbCon {

    public static boolean isDbOpened = false;
    private static DbHelper dbHelper;
    private static DbCon dbConInstance = null;
    private static Handler dbOpenHandler;
    private Context context;

    private DbCon(Context context) {
        this.context = context;
        open();
    }

    public static synchronized DbCon getInstance(Context ctx) {

        if (dbConInstance == null) {
            dbConInstance = new DbCon(ctx.getApplicationContext());
        }
        return dbConInstance;
    }

    public DbCon open() {
        try {
            dbOpenHandler = new DbOpenHandler();
            Thread dbOpenThread = new DbOpenThread();
            dbOpenThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void closeCursor(Cursor cursor) {
        dbHelper.closeCursor(cursor);
    }

    public long insert(String tbl, String values[], String names[]) {
        return dbHelper.insert(values, names, tbl);
    }

    public Cursor fetch(String tbl, String names[], String where, String args[], String order,
                        String limit, boolean isDistinct, String groupBy, String having) {
        return dbHelper.fetch(tbl, names, where, args, order, limit, isDistinct, groupBy, having);
    }

    public boolean delete(String tbl, String where, String args[]) {
        return dbHelper.delete(tbl, where, args);
    }

    public boolean update(String tbl, String where, String values[], String names[], String args[]) {
        return dbHelper.update(where, values, names, tbl, args);
    }

    public void deleteFiles() {
        delete(DbHelper.strTableNameFiles, null, null);
    }

    /*public void deleteTempUsers() {

        if (isDbOpened) {
            try {
                dbHelper.delete("dependant", "status<=?", new String[]{"1"});
                //dbHelper.delete("user", "status=?", new String[]{"0"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

   /*
    public String retrieveDependantPersonal(String strCustomerEmail, EditText editName, EditText editContactNo, EditText editAddress, EditText editRelation, String strName, EditText editEmail) {

        String strImg = "";

        if (isDbOpened && !strCustomerEmail.equalsIgnoreCase("")) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("dependant", new String[]{"name", "contact_no", "address", "relationship", "image_path", "email"}, "customer_email=? and name=?", new String[]{strCustomerEmail, strName}, "name DESC", "0,1", true, null, null);

                if (cur.getCount() > 0) {
                    cur.moveToFirst();

                    while (!cur.isAfterLast()) {

                        strImg = cur.getString(4);
                        editName.setText(cur.getString(0));
                        editContactNo.setText(cur.getString(1));
                        editAddress.setText(cur.getString(2));
                        editRelation.setText(cur.getString(3));
                        editEmail.setText(cur.getString(5));

                        Libs.log(strImg + " 2", "Image 2");

                        cur.moveToNext();
                    }
                }

                dbHelper.closeCursor(cur);
            } catch (Exception e) {
                dbHelper.closeCursor(cur);
            }
        }
        return strImg;
    }

    public boolean updateDependantMedicalDetails(String strName, String strAge, String strDiseases, String strNotes, String strCustomerEmail, String strImagePathServer) {

        boolean isUpdated = false;

        if (isDbOpened) {

            try {
                isUpdated = dbHelper.update("name=? and customer_email=?", new String[]{strAge, strDiseases, strNotes, "1", strImagePathServer},
                        new String[]{"age", "diseases", "notes", "status", "image_path_server"}, "dependant", new String[]{strName, strCustomerEmail});

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isUpdated;
    }*/


    private static class DbOpenHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //
            isDbOpened = true;
        }
    }

    private class DbOpenThread extends Thread {
        @Override
        public void run() {
            try {
                dbHelper = DbHelper.getInstance(context);
                dbHelper.open();
                dbOpenHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}