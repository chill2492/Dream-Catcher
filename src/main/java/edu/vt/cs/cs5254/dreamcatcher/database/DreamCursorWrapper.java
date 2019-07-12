package edu.vt.cs.cs5254.dreamcatcher.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import edu.vt.cs.cs5254.dreamcatcher.model.Dream;

public class DreamCursorWrapper extends CursorWrapper {

    public DreamCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Dream getDream() {
        String uuidString = getString(getColumnIndex(DreamDbSchema.DreamTable.Cols.UUID));
        String title = getString(getColumnIndex(DreamDbSchema.DreamTable.Cols.TITLE));

        long date = getLong(getColumnIndex(DreamDbSchema.DreamTable.Cols.DATE));
        int isDeferred = getInt(getColumnIndex(DreamDbSchema.DreamTable.Cols.DEFERRED));
        int isRealized = getInt(getColumnIndex(DreamDbSchema.DreamTable.Cols.REALIZED));

        Dream dream = new Dream(UUID.fromString(uuidString));
        dream.setTitle(title);
        dream.setDate(new Date(date));
        dream.setDeferred(isDeferred != 0);
        dream.setRealized(isRealized != 0);

        return dream;
    }
}
