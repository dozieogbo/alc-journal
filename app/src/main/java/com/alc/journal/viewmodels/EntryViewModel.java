package com.alc.journal.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.alc.journal.AppDatabase;
import com.alc.journal.models.Entry;

/**
 * Created with love by Dozie on 7/1/2018.
 */

public class EntryViewModel extends ViewModel {

    private LiveData<Entry> mEntry;

    EntryViewModel(AppDatabase appDatabase, int entryId) {
        mEntry = appDatabase.entryDAO().getEntryById(entryId);
    }

    public LiveData<Entry> getEntry(){
        return mEntry;
    }
}
