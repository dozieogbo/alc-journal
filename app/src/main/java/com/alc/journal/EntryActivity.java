package com.alc.journal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alc.journal.models.Entry;

import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EntryActivity extends AppCompatActivity {
    private static final String EXTRA_IS_NEW = "Is new";
    private static final String EXTRA_ID = "Entry Id";

    LinearLayout mEditingLayout;
    LinearLayout mViewingLayout;
    TextView mTitleTextView;
    EditText mTitleEditText;
    EditText mContentEditText;
    TextView mContentTextView;

    AppDatabase appDatabase;
    CompositeDisposable disposables = new CompositeDisposable();
    Entry currentEntry;

    boolean isAdding;

    public static Intent getStartIntent(Context context, int id) {
        Intent intent = new Intent(context, EntryActivity.class);
        intent.putExtra(EXTRA_ID, id);
        return intent;
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, EntryActivity.class);
        intent.putExtra(EXTRA_IS_NEW, true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        instantiateDb();
        initializeViews();
        initializeJournal();
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }

    private void instantiateDb() {
        appDatabase = AppDatabase.getInstance(this);
    }

    private void initializeJournal() {
        if (getIntent().hasExtra(EXTRA_IS_NEW)) {
            isAdding = true;
            toggleEdit(true);
        } else {
            int entryId = getIntent().getIntExtra(EXTRA_ID, 0);
            appDatabase.entryDAO().getEntryById(entryId)
                    .observe(this, entry -> {
                        if (entry != null) {
                            currentEntry = entry;
                            mTitleEditText.setText(entry.getTitle());
                            mTitleTextView.setText(entry.getTitle());
                            mContentTextView.setText(entry.getContent());
                            mContentEditText.setText(entry.getContent());
                        }
                    });
            toggleEdit(false);
        }
    }

    private void initializeViews() {
        mEditingLayout = findViewById(R.id.layout_editing);
        mViewingLayout = findViewById(R.id.layout_viewing);
        mTitleEditText = findViewById(R.id.text_title_edit);
        mTitleTextView = findViewById(R.id.text_title);
        mContentEditText = findViewById(R.id.text_body_edit);
        mContentTextView = findViewById(R.id.text_body);
    }

    public void save(View view) {
        String title = mTitleEditText.getText().toString();
        String content = mContentEditText.getText().toString();

        if (TextUtils.isEmpty(title)) {
            Helper.showAlert(this, "Please add a beautiful title");
            return;
        }

        if (TextUtils.isEmpty(content)) {
            Helper.showAlert(this, "Ah. Where are you going with no content?");
            return;
        }

        if (isAdding) createNew(title, content);
        else {
            currentEntry.setTitle(title);
            currentEntry.setContent(content);
            currentEntry.setUpdatedAt(new Date());
            Completable.fromAction(() -> appDatabase.entryDAO()
                    .updateEntry(currentEntry))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(getOnSaveObserver());

        }
    }

    @NonNull
    private CompletableObserver getOnSaveObserver() {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onComplete() {
                Helper.showAlert(EntryActivity.this, "Entry saved successfully.");
                onBackPressed();
            }

            @Override
            public void onError(Throwable e) {
                Helper.showAlert(EntryActivity.this, "An error occurred while saving: " + e.getMessage());
            }
        };
    }

    private void createNew(String title, String content) {
        Entry entry = new Entry();
        entry.setTitle(title);
        entry.setContent(content);

        Completable.fromAction(() -> appDatabase.entryDAO()
                .addEntry(entry))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getOnSaveObserver());

    }

    public void cancel(View view) {
        if (isAdding) {
            onBackPressed();
        } else {
            toggleEdit(false);
        }
    }

    public void edit(View view) {
        toggleEdit(true);
    }

    public void delete(View view) {
        AlertDialog warningDialog = new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", (dialogInterface, i) ->
                        Completable.fromAction(() -> appDatabase.entryDAO().deleteEntry(currentEntry))
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
                disposables.add(d);
            }

            @Override
            public void onComplete() {
                onDeleted();
            }

            @Override
            public void onError(Throwable e) {
                Helper.showAlert(EntryActivity.this, "An error occurred while deleting: " + e.getMessage());
            }
        };
    }

    private void onDeleted() {
        Helper.showAlert(EntryActivity.this, "Entry was deleted successfully.");
        onBackPressed();
    }

    private void toggleEdit(boolean isEditing) {
        mEditingLayout.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        mViewingLayout.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        mTitleEditText.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        mTitleTextView.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        mContentEditText.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        mContentTextView.setVisibility(isEditing ? View.GONE : View.VISIBLE);

    }
}
