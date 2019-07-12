package edu.vt.cs.cs5254.dreamcatcher.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DreamBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "dreamBase.db";

    public DreamBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DreamDbSchema.DreamTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                DreamDbSchema.DreamTable.Cols.UUID + " text not null unique, " +
                DreamDbSchema.DreamTable.Cols.TITLE + " text, " +
                DreamDbSchema.DreamTable.Cols.DATE + " integer, " +
                DreamDbSchema.DreamTable.Cols.DEFERRED + " integer, " +
                DreamDbSchema.DreamTable.Cols.REALIZED + " integer)"
        );
        db.execSQL("create table " + DreamDbSchema.DreamEntryTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                DreamDbSchema.DreamEntryTable.Cols.TEXT + " text, " +
                DreamDbSchema.DreamEntryTable.Cols.DATE + " integer, " +
                DreamDbSchema.DreamEntryTable.Cols.KIND + " text, " +
                DreamDbSchema.DreamEntryTable.Cols.UUID + " text not null, " +
                "foreign key (" + DreamDbSchema.DreamEntryTable.Cols.UUID + ") references " +
                DreamDbSchema.DreamTable.NAME + "(" + DreamDbSchema.DreamTable.Cols.UUID + "))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
