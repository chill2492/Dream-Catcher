package edu.vt.cs.cs5254.dreamcatcher.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.vt.cs.cs5254.dreamcatcher.database.DreamBaseHelper;
import edu.vt.cs.cs5254.dreamcatcher.database.DreamDbSchema;
import edu.vt.cs.cs5254.dreamcatcher.database.DreamEntryCursorWrapper;

public class DreamEntryLab {

    private static DreamEntryLab sDreamEntryLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DreamEntryLab getInstance(Context context) {
        if (sDreamEntryLab == null) {
            sDreamEntryLab = new DreamEntryLab(context);
        }
        return sDreamEntryLab;
    }

    private DreamEntryLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DreamBaseHelper(mContext).getWritableDatabase();
    }

    public List<DreamEntry> getDreamEntries(Dream dream) {
        List<DreamEntry> dreamEntries = new ArrayList<>();
        String whereClause = DreamDbSchema.DreamEntryTable.Cols.UUID + " = ?";
        DreamEntryCursorWrapper cursor = queryDreamEntry(whereClause, new String[]{
                         dream.getId().toString()});
         try{
             cursor.moveToFirst();
             while (!cursor.isAfterLast()){
                 dreamEntries.add(cursor.getDreamEntry());
                 cursor.moveToNext();
             }

         } finally {
             cursor.close();
         }
         return dreamEntries;
    }

    public void addDreamEntry(DreamEntry dreamEntry, Dream dream) {
        ContentValues values = getDreamEntryValues(dreamEntry, dream);
        mDatabase.insert(DreamDbSchema.DreamEntryTable.NAME, null, values);

    }

    public void updateDreamEntries(Dream dream) {
        String uuidString = dream.getId().toString();

        mDatabase.delete(DreamDbSchema.DreamEntryTable.NAME,
                DreamDbSchema.DreamEntryTable.Cols.UUID + " = ?",
                new String[] { uuidString });

        for(DreamEntry dreamEntry : dream.getEntries()){
            DreamEntryLab.getInstance(mContext).addDreamEntry(dreamEntry, dream);
        }
    }


    private DreamEntryCursorWrapper queryDreamEntry(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DreamDbSchema.DreamEntryTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new DreamEntryCursorWrapper(cursor);
    }


    public static ContentValues getDreamEntryValues(DreamEntry dreamEntry, Dream dream) {
        ContentValues values = new ContentValues();
        values.put(DreamDbSchema.DreamEntryTable.Cols.UUID, dream.getId().toString());
        values.put(DreamDbSchema.DreamEntryTable.Cols.TEXT, dreamEntry.getEntryText());
        values.put(DreamDbSchema.DreamEntryTable.Cols.DATE, dreamEntry.getEntryDate().getTime());
        values.put(DreamDbSchema.DreamEntryTable.Cols.KIND, dreamEntry.getEntryKind().toString());
        return values;
    }
}
