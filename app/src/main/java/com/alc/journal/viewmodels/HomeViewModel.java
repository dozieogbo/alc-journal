package com.alc.journal.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.alc.journal.AppDatabase;
import com.alc.journal.models.Entry;

import java.util.List;

/**
 * Created with love by Dozie on 7/1/2018.
 */

public class HomeViewModel extends AndroidViewModel {

    private LiveData<List<Entry>> mEntries;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        mEntries = appDatabase.entryDAO().getEntries();
    }

    public LiveData<List<Entry>> getEntries(){
        return mEntries;
    }
}
