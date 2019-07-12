package edu.vt.cs.cs5254.dreamcatcher.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;

import edu.vt.cs.cs5254.dreamcatcher.model.DreamEntry;
import edu.vt.cs.cs5254.dreamcatcher.model.DreamEntryKind;

public class DreamEntryCursorWrapper extends CursorWrapper {

    public DreamEntryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public DreamEntry getDreamEntry() {
        String entryText = getString(getColumnIndex(DreamDbSchema.DreamEntryTable.Cols.TEXT));
        String entryKind = getString(getColumnIndex(DreamDbSchema.DreamEntryTable.Cols.KIND));
        long entryDate = getLong(getColumnIndex(DreamDbSchema.DreamEntryTable.Cols.DATE));

        return new DreamEntry(entryText, new Date(entryDate), DreamEntryKind.valueOf(entryKind));
    }
}
