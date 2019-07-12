package edu.vt.cs.cs5254.dreamcatcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.text.DateFormat;
import java.util.List;
import java.util.UUID;

import edu.vt.cs.cs5254.dreamcatcher.model.Dream;
import edu.vt.cs.cs5254.dreamcatcher.model.DreamEntry;
import edu.vt.cs.cs5254.dreamcatcher.model.DreamEntryKind;
import edu.vt.cs.cs5254.dreamcatcher.model.DreamEntryLab;
import edu.vt.cs.cs5254.dreamcatcher.model.DreamLab;
import edu.vt.cs.cs5254.dreamcatcher.model.PictureUtils;

public class DreamFragment extends Fragment {

    private final static int REVEALED_COLOR = 0xff0076ba;
    private final static int REALIZED_COLOR = 0xff008f00;
    private final static int DEFERRED_COLOR = 0xffb51700;
    private final static int COMMENT_COLOR = 0xffffd479;

    public static final String ARG_DREAM_ID = "dream_id";
    private static final String DIALOG_ADD_DREAM_ENTRY = "Dialog_Add_Dream_Entry";

    private static final int REQUEST_COMMENT = 0;
    private static final int REQUEST_PHOTO = 1;

    // Model fields
    private Dream mDream;
    private File mPhotoFile;

    // View fields
    private EditText mTitleField;
    private ImageView mPhotoView;
    private CheckBox mRealizedCheckBox;
    private CheckBox mDeferredCheckBox;
    private FloatingActionButton mAddCommentFAB;
    private Button mButton;
    private DreamEntry mDreamEntry;

    // Recycler view fields
    private RecyclerView mDreamRecyclerView;
    private DreamEntryAdapter mAdapter;

    public static Fragment newInstance(UUID dreamId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DREAM_ID, dreamId);
        DreamFragment fragment = new DreamFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID dreamId = (UUID) getArguments().getSerializable(ARG_DREAM_ID);
        mDream = DreamLab.getInstance(getActivity()).getDream(dreamId);
        mPhotoFile = DreamLab.getInstance(getActivity()).getPhotoFile(mDream);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dream, container, false);

        mDreamRecyclerView = view.findViewById(R.id.dream_entry_recycler_view);
        mDreamRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //*******************************************************
        // initialize view fields
        //*******************************************************

        // find view title
        mTitleField = view.findViewById(R.id.dream_title);
        // listener
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mDream.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // find view revealed checkbox
        mRealizedCheckBox = view.findViewById(R.id.dream_realized);
        // listener
        mRealizedCheckBox.setOnCheckedChangeListener(
                (compoundButton, checked) -> {
                    if (checked) {
                        mDream.selectDreamRealized();
                    } else {
                        mDream.deselectDreamRealized();
                    }
                    refreshView();
                });

        // find view revealed checkbox
        mDeferredCheckBox = view.findViewById(R.id.dream_deferred);
        // listener
        mDeferredCheckBox.setOnCheckedChangeListener(
                (compoundButton, checked) -> {
                    if (checked) {
                        mDream.selectDreamDeferred();
                    } else {
                        mDream.deselectDreamDeferred();
                    }
                    refreshView();
                });

        // find view floating action button
        mAddCommentFAB = view.findViewById(R.id.add_comment_fab);
        mAddCommentFAB.setOnClickListener(v -> {
            if (mDream.isDeferred() || mDream.isRealized()) {
                return;
            } else {
                FragmentManager manager =
                        DreamFragment.this.getFragmentManager();
                AddDreamEntryFragment dialog = new AddDreamEntryFragment();
                dialog.setTargetFragment(
                        DreamFragment.this, REQUEST_COMMENT);
                dialog.show(manager, DIALOG_ADD_DREAM_ENTRY);
            }
            refreshView();
        });

        mPhotoView = view.findViewById(R.id.dream_photo);

        refreshView();
        refreshDreamEntries();
        return view;
    }

    public class DreamEntryHolder extends RecyclerView.ViewHolder {
        public DreamEntryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_entry, parent, false));
            mButton = itemView.findViewById(R.id.dream_entry_button);
        }

        public void bind(DreamEntry dreamEntry) {
            mDreamEntry = dreamEntry;

            switch (dreamEntry.getEntryKind()) {
                case REVEALED:
                    setRevealedStyle(mButton);
                    mButton.setText(dreamEntry.getEntryText());
                    break;

                case DEFERRED:
                    setDeferredStyle(mButton);
                    mButton.setText(dreamEntry.getEntryText());
                    break;

                case REALIZED:
                    setRealizedStyle(mButton);
                    mButton.setText(dreamEntry.getEntryText());
                    break;

                case COMMENT:
                    setCommentStyle(mButton);
                    String comment = dreamEntry.getEntryText();

                    java.text.DateFormat dateFormat;
                    dateFormat = java.text.DateFormat.getDateInstance(DateFormat.MEDIUM);
                    String currentDate = dateFormat.format(dreamEntry.getEntryDate());
                    mButton.setText(comment + " (" + currentDate + ")");

            }

        }

    }

    public class DreamEntryAdapter extends RecyclerView.Adapter<DreamEntryHolder> {

        List<DreamEntry> mEntries;
        int mRecentlyDeletedItemPosition = 0;

        public DreamEntryAdapter(List<DreamEntry> entries) {
            mEntries = entries;
        }

        @Override
        public DreamEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new DreamEntryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(DreamEntryHolder holder, int position) {
            holder.bind(mEntries.get(position));
        }

        @Override
        public int getItemCount() {
            return mEntries.size();
        }

        public void setEntries(List<DreamEntry> entries) {
            mEntries = entries;
        }

        public void deleteItem(int position) {

            mDreamEntry = mEntries.get(position);
            mRecentlyDeletedItemPosition = position;

            if (mDreamEntry.getEntryKind() == DreamEntryKind.COMMENT) {
                mEntries.remove(position);
                notifyItemRemoved(position);
            } else {
                refreshDreamEntries();
            }

        }

    }


    @Override
    public void onPause() {
        super.onPause();
        DreamLab.getInstance(getActivity()).updateDream(mDream);
        DreamEntryLab.getInstance(getActivity()).updateDreamEntries(mDream);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_COMMENT) {
            String comment =
                    (String) intent.getSerializableExtra(AddDreamEntryFragment.EXTRA_COMMENT);
            mDream.addComment(comment);
            refreshDreamEntries();
        }

        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "edu.vt.cs.cs5254.dreamcatcher.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            refreshPhotoView();
            refreshDreamEntries();

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_dream, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_dream:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getDreamReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        mDream.getTitle());
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);

            case R.id.take_dream_photo:
                Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "edu.vt.cs.cs5254.dreamcatcher.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private String getDreamReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(mDream.getTitle());
        sb.append(System.getProperty("line.separator"));

        for (DreamEntry e : mDream.getEntries()) {
            sb.append(e.getEntryText());
            sb.append(System.getProperty("line.separator"));

        }

        return sb.toString();
    }

    //*******************************************************
    // refreshing view
    //*******************************************************

    private void refreshView() {
        mTitleField.setText(mDream.getTitle());
        refreshPhotoView();
        refreshCheckBoxes();
        refreshDreamEntries();

    }

    private void refreshPhotoView() {

        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }

    }

    private void refreshCheckBoxes() {
        if (mDream.isRealized() && !mDream.isDeferred()) {
            mRealizedCheckBox.setChecked(mDream.isRealized());
            mDeferredCheckBox.setEnabled(false);

        }

        if (mDream.isDeferred() && !mDream.isRealized()) {
            mDeferredCheckBox.setChecked(mDream.isDeferred());
            mRealizedCheckBox.setEnabled(false);
        }


        if (!mDream.isRealized() && !mDream.isDeferred()) {
            mDeferredCheckBox.setEnabled(true);
            mRealizedCheckBox.setEnabled(true);
        }


    }

    private void refreshDreamEntries() {

        List<DreamEntry> entries = mDream.getEntries();

        if (mAdapter == null) {
            mAdapter = new DreamEntryAdapter(entries);
        } else {
            mAdapter.setEntries(entries);
            mAdapter.notifyDataSetChanged();
        }

        mDreamRecyclerView.setAdapter(mAdapter);
        mDreamRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new DreamEntrySwipeToDeleteCallback(mAdapter));
        itemTouchHelper.attachToRecyclerView(mDreamRecyclerView);
    }

    private void setRevealedStyle(Button button) {
        button.getBackground().setColorFilter(REVEALED_COLOR, PorterDuff.Mode.MULTIPLY);
        button.setTextColor(Color.WHITE);
    }

    private void setDeferredStyle(Button button) {
        button.getBackground().setColorFilter(DEFERRED_COLOR, PorterDuff.Mode.MULTIPLY);
        button.setTextColor(Color.WHITE);
    }

    private void setRealizedStyle(Button button) {
        button.getBackground().setColorFilter(REALIZED_COLOR, PorterDuff.Mode.MULTIPLY);
        button.setTextColor(Color.WHITE);
    }

    private void setCommentStyle(Button button) {
        button.getBackground().setColorFilter(COMMENT_COLOR, PorterDuff.Mode.MULTIPLY);
        button.setTextColor(Color.BLACK);
    }

}
