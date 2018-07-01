package com.alc.journal;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alc.journal.models.Entry;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity implements EntryAdapter.ItemInteractCallback {

    private EntryAdapter mEntryAdapter;

    private AppDatabase appDatabase;

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        instantiateDb();
        instantiateList();
    }

    @Override
    protected void onDestroy() {
        if(mDisposable != null) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }

    private void instantiateList() {
        RecyclerView mEntryRecyclerView = findViewById(R.id.recycler_view_entries);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mEntryRecyclerView.addItemDecoration(itemDecoration);

        mEntryAdapter = new EntryAdapter(this, new ArrayList<>(), this);

        mEntryRecyclerView.setAdapter(mEntryAdapter);
        mEntryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        appDatabase.entryDAO().getEntries()
                .observe(this, entries -> mEntryAdapter.setItems(entries));
    }

    private void instantiateDb() {
        appDatabase = AppDatabase.getInstance(this);
    }

    public void addEntry(View view) {
        startActivity(EntryActivity.getStartIntent(this));
    }

    @Override
    public void onItemClick(int id) {
        startActivity(EntryActivity.getStartIntent(this, id));
    }

    @Override
    public void onItemDelete(Entry entry) {
        AlertDialog warningDialog = new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", (dialogInterface, i) ->
                        Completable.fromAction(() -> appDatabase.entryDAO().deleteEntry(entry))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(getOnDeleteObserver()))
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .create();
        warningDialog.show();
    }

    private CompletableObserver getOnDeleteObserver() {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onComplete() {
                Helper.showAlert(HomeActivity.this, "Entry was deleted successfully.");
            }

            @Override
            public void onError(Throwable e) {
                Helper.showAlert(HomeActivity.this, "An error occurred while deleting: " + e.getMessage());
            }
        };
    }

}
