package com.alc.journal.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alc.journal.models.Entry;

import java.util.List;

/**
 * Created with love by Dozie on 6/30/2018.
 */

@Dao
public interface EntryDAO {
    @Query("SELECT * FROM entries ORDER BY updatedAt DESC")
    LiveData<List<Entry>> getEntries();

    @Query("SELECT * FROM entries WHERE id = :id LIMIT 1")
    LiveData<Entry> getEntryById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addEntry(Entry entry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEntry(Entry entry);

    @Delete
    void deleteEntry(Entry entry);
}
