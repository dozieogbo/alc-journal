package com.alc.journal;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.alc.journal.daos.EntryDAO;
import com.alc.journal.models.DateConverter;
import com.alc.journal.models.Entry;

/**
 * Created with love by Dozie on 6/30/2018.
 */

@Database(entities = {Entry.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "app_database";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room
                        .databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }
    
    public abstract EntryDAO entryDAO();
}
