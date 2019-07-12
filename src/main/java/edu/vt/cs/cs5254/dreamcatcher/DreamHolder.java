package edu.vt.cs.cs5254.dreamcatcher;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;

import edu.vt.cs.cs5254.dreamcatcher.model.Dream;

public class DreamHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // model field
    private Dream mDream;

    // view field
    TextView mTitleTextView;
    TextView mDateTextView;
    private ImageView mRealizedImage;
    private ImageView mDeferredImage;

    public DreamHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.list_item_dream, parent, false));

        mTitleTextView = itemView.findViewById(R.id.dream_title);
        mDateTextView = itemView.findViewById(R.id.dream_date);

        mRealizedImage = itemView.findViewById(R.id.dream_realized_icon);
        mRealizedImage.setVisibility(View.GONE);

        mDeferredImage = itemView.findViewById(R.id.dream_deferred_icon);
        mDeferredImage.setVisibility(View.GONE);

        itemView.setOnClickListener(this);
    }

    public void bind(Dream dream){
        mDream = dream;
        mTitleTextView.setText(dream.getTitle());

        DateFormat dateFormat;
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String currentDate = dateFormat.format(mDream.getDate());
        mDateTextView.setText(currentDate);

        if (mDream.isRealized()){
            mRealizedImage.setImageResource(R.drawable.dream_realized_icon);
            mRealizedImage.setTag(R.drawable.dream_realized_icon);

            mRealizedImage.setVisibility(View.VISIBLE);
            mDeferredImage.setVisibility(View.GONE);
        }
        else if (mDream.isDeferred()){
            mDeferredImage.setImageResource(R.drawable.dream_deferred_icon);
            mDeferredImage.setTag(R.drawable.dream_deferred_icon);

            mDeferredImage.setVisibility(View.VISIBLE);
            mRealizedImage.setVisibility(View.GONE);
        }
        else {
            mRealizedImage.setImageResource(0);
            mRealizedImage.setTag(0);
            mRealizedImage.setVisibility(View.GONE);

            mDeferredImage.setImageResource(0);
            mDeferredImage.setTag(0);
            mDeferredImage.setVisibility(View.GONE);

        }

    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Intent intent = DreamActivity.newIntent(context, mDream.getId());
        context.startActivity(intent);
    }
}
