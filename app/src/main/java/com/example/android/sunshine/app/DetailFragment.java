package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ShareActionProvider mShareActionProvider;
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecastStr;
    private TextView txvWeather;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private ViewHolder mViewHolder;
    static final String DETAIL_URI = "URI";
    private Uri mUri;

    private static final int DETAIL_LOADER_ID = 2;

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUM = 5;
    private static final int COL_WEATHER_WIN = 6;
    private static final int COL_WEATHER_PRE = 7;
    private static final int COL_WEATHER_DEG = 8;
    private static final int COL_WEATHER_ICON_ID = 9;

    public DetailFragment(){

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present:
        inflater.inflate(R.menu.detail,menu);

        // Locate MenuItem with ShareActionProvider:
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider:
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

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

        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle arguments = getArguments();

        if (arguments != null){
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        mViewHolder = new ViewHolder(rootView);

        return rootView;
    }

    private Intent createShareForecastIntent(){
        Intent shareItt = new Intent(Intent.ACTION_SEND);
        shareItt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareItt.setType("text/plain");
        shareItt.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);

        return shareItt;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri){
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the Data being displayed:
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(LOG_TAG, "In onLoadFinished");

        if(!cursor.moveToFirst()){
            return;
        }

        int weatherId = cursor.getInt(COL_WEATHER_ICON_ID);

        // Read the image resource:
        mViewHolder.ivwIcon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        // Read the date:
        long dateInMillis = cursor.getLong(COL_WEATHER_DATE);
        mViewHolder.tvwDate.setText(Utility.getFriendlyDayString(getActivity(), dateInMillis));

        // Read the weather forecast:
        String forecast = cursor.getString(COL_WEATHER_DESC);
        mViewHolder.tvwDescription.setText(forecast);
        mViewHolder.ivwIcon.setContentDescription(forecast);

        // Read user preference for metric or imperial temperature units:
        boolean isMetric = Utility.isMetric(getActivity());

        // Read the high temperature from cursor:
        double high = cursor.getDouble(COL_WEATHER_MAX_TEMP);
        mViewHolder.tvwHighTemp.setText(Utility.formatTemperature(getActivity(), high, isMetric));

        // Read the high temperature from cursor:
        double low = cursor.getDouble(COL_WEATHER_MIN_TEMP);
        mViewHolder.tvwLowTemp.setText(Utility.formatTemperature(getActivity(), low, isMetric));

        // Read the humidity from cursor:
        float humidity = cursor.getFloat(COL_WEATHER_HUM);
        mViewHolder.tvwHumidity.setText(Utility.getFormattedHumidity(getActivity(), humidity));

        // Read the wind speed from cursor:
        float windSpeed = cursor.getFloat(COL_WEATHER_WIN);
        float winDir = cursor.getFloat(COL_WEATHER_DEG);
        mViewHolder.tvwWinSpeed.setText(Utility.getFormattedWind(getActivity(), windSpeed, winDir));

        // Read the pressure from cursor:
        float pressure = cursor.getFloat(COL_WEATHER_PRE);
        mViewHolder.tvwPressure.setText(Utility.getFormattedPressure(getActivity(), pressure));

        // If OnCreateOptionsMenu has already happened, we need to update the share intent:
        if (mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    public void onLocationChanged(String location) {
        // Replace the URI, since the location has changed:
        Uri uri = mUri;

        if (null != uri){
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updateUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location, date);
            mUri = updateUri;
            getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this);
        }
    }

    public static class ViewHolder{
        public final ImageView ivwIcon;
        public final TextView tvwDate;
        public final TextView tvwDescription;
        public final TextView tvwHighTemp;
        public final TextView tvwLowTemp;
        public final TextView tvwHumidity;
        public final TextView tvwWinSpeed;
        public final TextView tvwPressure;

        public ViewHolder(View view){
            ivwIcon = (ImageView) view.findViewById(R.id.detail_item_icon);
            tvwDate = (TextView) view.findViewById(R.id.detail_item_date_textview);
            tvwDescription = (TextView) view.findViewById(R.id.detail_item_forecast_textview);
            tvwHighTemp = (TextView) view.findViewById(R.id.detail_item_high_textview);
            tvwLowTemp = (TextView) view.findViewById(R.id.detail_item_low_textview);
            tvwHumidity = (TextView) view.findViewById(R.id.detail_item_humidity_textview);
            tvwWinSpeed = (TextView) view.findViewById(R.id.detail_item_wind_textview);
            tvwPressure = (TextView) view.findViewById(R.id.detail_item_pressure_textview);
        }
    }
}
