package com.alc.journal.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.alc.journal.AppDatabase;

/**
 * Created with love by Dozie on 7/1/2018.
 */

public class EntryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mAppDatabase;
    private final int mEntryId;

    public EntryViewModelFactory(AppDatabase appDatabase, int entryId) {
        this.mAppDatabase = appDatabase;
        this.mEntryId = entryId;
    }

    public <T extends ViewModel> T create(Class<T> modelClass){
        return (T) new EntryViewModel(mAppDatabase, mEntryId);
    }
}
