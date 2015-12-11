package com.example.android.sunshine.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present:
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml:
        int id = item.getItemId();

        // noninspection SimplifableStatement:
        if (id == R.id.action_settings){
            Intent settingsItt = new Intent(this, SettingsActivity.class);
            startActivity(settingsItt);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment{

        private ShareActionProvider mShareActionProvider;
        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private String mForecastStr;

        public DetailFragment(){

        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);

            inflater.inflate(R.menu.detail,menu);

            // Locate MenuItem with ShareActionProvider:
            MenuItem item = menu.findItem(R.id.action_share);

            // Fetch and store ShareActionProvider:
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            // Attach an intent to this ShareActionProvider. You can update this at any time,
            // like when the user selects a new piece of data they might like to share:
            if (mShareActionProvider != null){
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(DetailFragment.class.getSimpleName(), "Share Action Provider is null?");
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Add this line in order for this segment to handle menu events:
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intInherent = getActivity().getIntent();
            if (intInherent != null && intInherent.hasExtra(Intent.EXTRA_TEXT)) {
                mForecastStr = intInherent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.forecast))
                        .setText(mForecastStr);
            }

            return rootView;
        }

        private Intent createShareForecastIntent(){
            Intent shareItt = new Intent(Intent.ACTION_SEND);
            shareItt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareItt.setType("text/plain");
            shareItt.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);

            return shareItt;
        }
    }
}
