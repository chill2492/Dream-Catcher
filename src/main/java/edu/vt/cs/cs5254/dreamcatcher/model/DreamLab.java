
package edu.vt.cs.cs5254.dreamcatcher.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.vt.cs.cs5254.dreamcatcher.database.DreamBaseHelper;
import edu.vt.cs.cs5254.dreamcatcher.database.DreamCursorWrapper;
import edu.vt.cs.cs5254.dreamcatcher.database.DreamDbSchema;

public class DreamLab {

    private static DreamLab sDreamLab;

    public static final int REALIZED_DREAMS = 0;
    public static final int DEFERRED_DREAMS = 1;
    public static final int ACTIVE_DREAMS = 2;
    public static final int ALL_DREAMS = 3;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DreamLab getInstance(Context context) {
        if (sDreamLab == null) {
            sDreamLab = new DreamLab(context);
        }
        return sDreamLab;
    }

    private DreamLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DreamBaseHelper(mContext).getWritableDatabase();

    }

    public File getPhotoFile(Dream dream) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, dream.getPhotoFilename());
    }

    public List<Dream> getDreams(int filter) {
        List<Dream> dreams = new ArrayList<>();
        DreamCursorWrapper cursor = queryDreams(null, null);

        switch (filter) {
            case REALIZED_DREAMS:
                cursor = queryDreams(DreamDbSchema.DreamTable.Cols.REALIZED + " = ?",
                        new String[]{"1"});
                break;

            case DEFERRED_DREAMS:
                cursor = queryDreams(DreamDbSchema.DreamTable.Cols.DEFERRED + " = ?",
                        new String[]{"1"});
                break;

            case ACTIVE_DREAMS:
                cursor = queryDreams(DreamDbSchema.DreamTable.Cols.REALIZED + " = ? AND " +
                        DreamDbSchema.DreamTable.Cols.DEFERRED + "= ? ", new String[]{"0", "0"});
                break;

            case ALL_DREAMS:
                cursor = queryDreams(null, null);
                break;
        }

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                dreams.add(cursor.getDream());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return dreams;
    }


    public Dream getDream(UUID id) {
        DreamCursorWrapper cursor = queryDreams(
                DreamDbSchema.DreamTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            Dream dream = cursor.getDream();
            List<DreamEntry> entries =
                    DreamEntryLab.getInstance(mContext).getDreamEntries(dream);
            dream.setDreamEntries(entries);
            return dream;
        } finally {
            cursor.close();
        }
    }

    public void addDream(Dream dream) {
        ContentValues values = getDreamValues(dream);
        mDatabase.insert(DreamDbSchema.DreamTable.NAME, null, values);
        for (DreamEntry entry : dream.getEntries()) {
            DreamEntryLab.getInstance(mContext).addDreamEntry(entry, dream);
        }
    }

    public void updateDream(Dream dream) {
        String uuidString = dream.getId().toString();
        ContentValues values = getDreamValues(dream);
        mDatabase.update(DreamDbSchema.DreamTable.NAME, values,
                DreamDbSchema.DreamTable.Cols.UUID + " = ?",
                new String[]{uuidString});
        DreamEntryLab.getInstance(mContext)
                .updateDreamEntries(dream);
    }

    public void clearDatabase() {
        mDatabase.execSQL("delete from dream");
        mDatabase.execSQL("delete from dream_entry");
    }


    private DreamCursorWrapper queryDreams(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DreamDbSchema.DreamTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new DreamCursorWrapper(cursor);
    }

    public static ContentValues getDreamValues(Dream dream) {
        ContentValues values = new ContentValues();
        values.put(DreamDbSchema.DreamTable.Cols.UUID, dream.getId().toString());
        values.put(DreamDbSchema.DreamTable.Cols.TITLE, dream.getTitle());
        values.put(DreamDbSchema.DreamTable.Cols.DATE, dream.getDate().getTime());
        values.put(DreamDbSchema.DreamTable.Cols.DEFERRED, dream.isDeferred() ? 1 : 0);
        values.put(DreamDbSchema.DreamTable.Cols.REALIZED, dream.isRealized() ? 1 : 0);
        return values;
    }

}