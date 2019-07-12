package edu.vt.cs.cs5254.dreamcatcher.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Dream {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mRealized;
    private boolean mDeferred;
    private List<DreamEntry> mEntries;


    public Dream() {
        this(UUID.randomUUID());
    }

    public Dream(UUID id){
        mId = id;
        mTitle = null;
        mDate = new Date();
        mRealized = false;
        mDeferred = false;
        mEntries = new ArrayList<>();
        addDreamRevealed();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) { mDate = date; }

    public boolean isRealized() {
        return mRealized;
    }

    public void setRealized(boolean realized) {
        mRealized = realized;
    }

    public boolean isDeferred() {
        return mDeferred;
    }

    public void setDeferred(boolean deferred) {
        mDeferred = deferred;
    }

    public List<DreamEntry> getEntries() {
        return mEntries;
    }

    public void setDreamEntries(List<DreamEntry> dreamEntries){
        mEntries = dreamEntries;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    // ***************************************************************
    // Service Methods
    // ***************************************************************

    public void addComment(String text) {
        if (isRealized() || isDeferred())
            return;

        DreamEntry dreamEntry = new DreamEntry(text, new Date(), DreamEntryKind.COMMENT);
        mEntries.add(dreamEntry);
    }

    public void addDreamRevealed(){
        DreamEntry dreamEntry = new DreamEntry("Dream Revealed", new Date(), DreamEntryKind.REVEALED);
        mEntries.add(dreamEntry);
    }

    public void addDreamRealized(){
        DreamEntry dreamEntry = new DreamEntry("Dream Realized", new Date(), DreamEntryKind.REALIZED);
        mEntries.add(dreamEntry);
    }

    public void addDreamDeferred(){
        DreamEntry dreamEntry = new DreamEntry("Dream Deferred", new Date(), DreamEntryKind.DEFERRED);
        mEntries.add(dreamEntry);
    }

    public void selectDreamRealized() {
        if (isRealized())
            return;

        addDreamRealized();
        setRealized(true);
        setDeferred(false);
    }

    public void selectDreamDeferred() {
        if (isDeferred())
            return;

        addDreamDeferred();
        setRealized(false);
        setDeferred(true);
    }

    public void deselectDreamRealized() {
        if (!isRealized())
            return;

        setRealized(false);
        mEntries.remove(mEntries.size() - 1);
    }

    public void deselectDreamDeferred() {
        if (!isDeferred())
            return;

        setDeferred(false);
        mEntries.remove(mEntries.size() - 1);
    }

}