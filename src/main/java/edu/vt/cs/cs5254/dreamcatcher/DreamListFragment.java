package edu.vt.cs.cs5254.dreamcatcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import edu.vt.cs.cs5254.dreamcatcher.model.Dream;
import edu.vt.cs.cs5254.dreamcatcher.model.DreamLab;
import static edu.vt.cs.cs5254.dreamcatcher.model.DreamLab.ALL_DREAMS;


public class DreamListFragment extends Fragment {

    // view fields
    private RecyclerView mDreamRecyclerView;
    private DreamAdapter mDreamAdapter;
    private DrawerLayout mDrawerLayout;
    private int mFilter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dream_list, container, false);

        mDreamRecyclerView = view.findViewById(R.id.dream_recycler_view);
        mDreamRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = view.findViewById(R.id.drawer_layout);

        NavigationView navigationView = view.findViewById(R.id.nav_view);

        mFilter = ALL_DREAMS;

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {

                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();

                    switch (menuItem.getItemId()) {

                        case R.id.nav_realized_dreams:
                            mFilter = DreamLab.REALIZED_DREAMS;
                            break;

                        case R.id.nav_deferred_dreams:
                            mFilter = DreamLab.DEFERRED_DREAMS;
                            break;

                        case R.id.nav_active_dreams:
                            mFilter = DreamLab.ACTIVE_DREAMS;
                            break;

                        case R.id.nav_all_dreams:
                            mFilter = DreamLab.ALL_DREAMS;
                            break;
                    }

                    updateUI();
                    return true;
                }
                );

       return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_dream_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_dream:
                Dream dream = new Dream();
                DreamLab.getInstance(getActivity()).addDream(dream);
                Intent intent = DreamActivity
                        .newIntent(getActivity(), dream.getId());
                startActivity(intent);
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void updateUI(){
        DreamLab dreamLab = DreamLab.getInstance(getActivity());
        List<Dream> dreams = dreamLab.getDreams(mFilter);

        if (mDreamAdapter == null) {
            mDreamAdapter = new DreamAdapter(dreams);
            mDreamRecyclerView.setAdapter(mDreamAdapter);

        } else {
            mDreamAdapter.setDreams(dreams);
            mDreamAdapter.notifyDataSetChanged();
        }

    }
}
