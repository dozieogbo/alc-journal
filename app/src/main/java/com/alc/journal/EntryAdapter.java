package com.alc.journal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alc.journal.models.Entry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created with love by Dozie on 6/30/2018.
 */

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {

    private List<Entry> mEntries;
    private Context mContext;
    private ItemInteractCallback mItemInteractCallback;

    EntryAdapter(Context context, List<Entry> entries, ItemInteractCallback itemInteractCallback) {
        this.mContext = context;
        this.mEntries = entries;
        this.mItemInteractCallback = itemInteractCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext().getApplicationContext())
                .inflate(R.layout.entry_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entry currentEntry = mEntries.get(position);
        String lastUpdated = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
                .format(currentEntry.getUpdatedAt());
        holder.mParentView.setOnClickListener(view -> mItemInteractCallback.onItemClick(currentEntry.getId()));
        holder.mDeleteButton.setOnClickListener(view -> mItemInteractCallback.onItemDelete(currentEntry));
        holder.mTitleTextView.setText(currentEntry.getTitle());
        holder.mSummaryTextView.setText(currentEntry.getContent());
        holder.mDateTextView.setText(mContext.getString(R.string.entry_updated_at_default)
                .replace("%d%", lastUpdated));
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    void setItems(List<Entry> entries) {
        this.mEntries.clear();
        this.mEntries.addAll(entries);
        notifyDataSetChanged();
    }

    interface ItemInteractCallback {
        void onItemClick(int id);

        void onItemDelete(Entry entry);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View mParentView;
        TextView mTitleTextView;
        TextView mSummaryTextView;
        TextView mDateTextView;
        AppCompatImageButton mDeleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            mParentView = itemView;
            mTitleTextView = itemView.findViewById(R.id.text_title);
            mSummaryTextView = itemView.findViewById(R.id.text_summary);
            mDateTextView = itemView.findViewById(R.id.text_date);
            mDeleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}
