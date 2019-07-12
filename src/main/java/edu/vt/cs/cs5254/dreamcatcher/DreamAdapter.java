package edu.vt.cs.cs5254.dreamcatcher;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import edu.vt.cs.cs5254.dreamcatcher.model.Dream;

public class DreamAdapter extends RecyclerView.Adapter<DreamHolder> {

    // model field
    private List<Dream> mDreams;

    public DreamAdapter(List<Dream> dreams){
        mDreams = dreams;
    }

    @NonNull
    @Override
    public DreamHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new DreamHolder(inflater,viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull DreamHolder dreamHolder, int i) {
        dreamHolder.bind(mDreams.get(i));
    }

    @Override
    public int getItemCount() {
        return mDreams.size();
    }

    public void setDreams(List<Dream> dreams) {
        mDreams = dreams;
    }
}
