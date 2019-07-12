package edu.vt.cs.cs5254.dreamcatcher;

import android.support.v4.app.Fragment;

public class DreamListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {

        return new DreamListFragment();
    }
}
