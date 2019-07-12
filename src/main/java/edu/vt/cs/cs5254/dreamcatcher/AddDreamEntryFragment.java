package edu.vt.cs.cs5254.dreamcatcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddDreamEntryFragment extends DialogFragment {

    public static final String EXTRA_COMMENT = "dreamcatcher.comment";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_dream_entry, null);
        EditText text = view.findViewById(R.id.comment_text);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Add Comment")
                .setPositiveButton(
                        android.R.string.ok,
                        (dialog, which) -> {sendResult(Activity.RESULT_OK, text.getText().toString());})
                .setNegativeButton(
                        android.R.string.cancel,
                        (dialog, which) -> { })
                .create();
    }

    private void sendResult(int resultCode, String text){
        if(getTargetFragment() == null){return;}
        Intent intent = new Intent();
        intent.putExtra(EXTRA_COMMENT, text);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
