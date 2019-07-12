package edu.vt.cs.cs5254.dreamcatcher.database;

public class DreamDbSchema {

    public static final class DreamTable {
        public static final String NAME = "dream";

        public static final class Cols {
            public static final String UUID = "dream_uuid";
            public static final String TITLE = "dream_title";
            public static final String DATE = "dream_date";
            public static final String DEFERRED = "dream_deferred";
            public static final String REALIZED = "dream_realized";
        }
    }

    public static final class DreamEntryTable {
        public static final String NAME = "dream_entry";

        public static final class Cols {
            public static final String TEXT = "entry_text";
            public static final String DATE = "entry_date";
            public static final String KIND = "entry_kind";
            public static final String UUID = "dream_uuid";
        }
    }
}
