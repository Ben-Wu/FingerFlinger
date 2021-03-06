/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package ca.benwu.fingerflinger.ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import ca.benwu.fingerflinger.R;
import ca.benwu.fingerflinger.data.GameMode;
import ca.benwu.fingerflinger.presenter.GameModePresenter;
import ca.benwu.fingerflinger.utils.Logutils;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int GRID_ITEM_WIDTH = 150;
    private static final int GRID_ITEM_HEIGHT = 150;

    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private BackgroundManager mBackgroundManager;

    public static final ArrayList<String> ANIM_MODES = new ArrayList<>(Arrays.asList("Normal", "Wacky", "Easy", "None"));

    private int mSelectedAnim = 0;

    private View mSelectedAnimView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        loadRows();

        setupEventListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadRows() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        ArrayObjectAdapter animModeAdapter = new ArrayObjectAdapter(new GridItemPresenter());
        animModeAdapter.addAll(0, ANIM_MODES);
        mRowsAdapter.add(new ListRow(0, new HeaderItem("Animation Modes"), animModeAdapter));

        ArrayObjectAdapter gameModeAdapter = new ArrayObjectAdapter(new GameModePresenter());
        gameModeAdapter.add(new GameMode(getActivity(), GameMode.MODE_NORMAL));
        gameModeAdapter.add(new GameMode(getActivity(), GameMode.MODE_FAST));
        gameModeAdapter.add(new GameMode(getActivity(), GameMode.MODE_TIME_ATTACK));
        gameModeAdapter.add(new GameMode(getActivity(), GameMode.MODE_INFINITE));
        mRowsAdapter.add(new ListRow(new HeaderItem("Game Modes"), gameModeAdapter));

        ArrayObjectAdapter scoresRowAdapter = new ArrayObjectAdapter(new GridItemPresenter());
        scoresRowAdapter.add("Scores");
        mRowsAdapter.add(new ListRow(1, new HeaderItem("Leaderboards"), scoresRowAdapter));

        setAdapter(mRowsAdapter);
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        setHeadersState(HEADERS_DISABLED);
        setHeadersTransitionOnBackEnabled(true);

        mBackgroundManager.setDrawable(getResources().getDrawable(R.drawable.bg_tvmain));
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        //setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof GameMode) {
                Intent intent = new Intent(getActivity(), GameActivity.class);

                Bundle options = ActivityOptions.makeCustomAnimation(getActivity(),
                        R.anim.activity_slide_in_from_left,
                        R.anim.activity_slide_out_to_right).toBundle();

                intent.putExtra(GameActivity.KEY_GAME_MODE, ((GameMode) item).getMode());
                intent.putExtra(GameActivity.KEY_ANIM_MODE, mSelectedAnim);

                startActivity(intent, options);
            } else if (item instanceof String) {
                if(row.getId() == 0) {
                    mSelectedAnimView.setBackgroundColor(getResources().getColor(R.color.default_background));
                    mSelectedAnimView = itemViewHolder.view;
                    mSelectedAnimView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mSelectedAnim = ANIM_MODES.indexOf(item);
                    Logutils.d(TAG, "Selected anim mode: " + mSelectedAnim);
                } else if(row.getId() == 1) {
                    Intent intent = new Intent(getActivity(), ScoresActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {

        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
            if(item.equals(ANIM_MODES.get(0))) {
                viewHolder.view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mSelectedAnimView = viewHolder.view;
                mSelectedAnim = 0;
            }
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

}

