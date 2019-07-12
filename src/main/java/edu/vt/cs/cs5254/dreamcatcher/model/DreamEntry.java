package edu.vt.cs.cs5254.dreamcatcher.model;

import java.util.Date;

public class DreamEntry {

    private String mEntryText;
    private Date mEntryDate;
    private DreamEntryKind mEntryKind;

    public DreamEntry(String entryText, Date entryDate, DreamEntryKind entryKind) {
        mEntryText = entryText;
        mEntryDate = entryDate;
        mEntryKind = entryKind;
    }


    public String getEntryText() {
        return mEntryText;
    }

    public Date getEntryDate() {
        return mEntryDate;
    }

    public DreamEntryKind getEntryKind() {
        return mEntryKind;
    }

}