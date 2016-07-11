package com.hdfc.dbconfig;

import android.content.Context;

import net.sqlcipher.Cursor;

public class DbCon {

    public static boolean isDbOpened = false;
    private DbHelper dbHelper;
    //private DbCon dbConInstance = null;

    public DbCon(Context context) {
        open(context);
    }

   /* public static synchronized DbCon getInstance() {

        if (dbConInstance == null) {
            dbConInstance = new DbCon();
        }
        return dbConInstance;
    }*/

    public DbCon open(Context context) {
        try {
            /*dbOpenHandler = new DbOpenHandler();
            Thread dbOpenThread = new DbOpenThread();
            dbOpenThread.start();*/
            dbHelper = DbHelper.getInstance(context);
            dbHelper.open();
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

    public boolean updateInsert(String tbl, String where, String values[], String names[], String args[]) {

        boolean isUpdated;

        isUpdated = dbHelper.update(where, values, names, tbl, args);

        if (!isUpdated)
            insert(tbl, values, names);

        return isUpdated;
    }

    public Cursor rawQuery(String query) {
        return dbHelper.rawQuery(query);
    }

    //app specific functions
    public void deleteFiles() {
        delete(DbHelper.strTableNameFiles, null, null);
    }

    public Cursor getMaxDate(String strCollectionName) {

        String query = "Select MAX(" + DbHelper.COLUMN_UPDATE_DATE + ") from "
                + DbHelper.strTableNameCollection +
                " where " + DbHelper.COLUMN_COLLECTION_NAME + " = '" + strCollectionName + "'";

        return dbHelper.rawQuery(query);
    }

    public void beginDBTransaction() {
        dbHelper.beginDBTransaction();
    }

    public void endDBTransaction() {
        dbHelper.endDBTransaction();
    }

    public void dbTransactionSuccessFull() {
        dbHelper.dbTransactionSuccessFull();
    }


  /*  private static class DbOpenHandler extends Handler {
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
    }*/
}