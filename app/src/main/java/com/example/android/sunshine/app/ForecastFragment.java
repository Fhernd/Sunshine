package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

    /**
     * A placeholder fragment containing a simple view.
     */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events:
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml:
        int id = item.getItemId();

        if (id == R.id.action_refresh){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            String currentPostal = sharedPref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            new FetchWeatherTask(getActivity(), mForecastAdapter).execute(currentPostal);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView forecast = (ListView) rootView.findViewById(R.id.listview_forecast);

        // Event which is triggered when the user clicks over a list's element:
        forecast.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = mForecastAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });

        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, new ArrayList<String>());

        forecast.setAdapter(mForecastAdapter);

        return rootView;
    }

    private void updateWeather(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String currentPostal = sharedPref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        new FetchWeatherTask(getActivity(), mForecastAdapter).execute(currentPostal);
    }

	@Override
	public void onStart() {
		super.onStart();
		updateWeather();
	}

//	public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{
//
//		private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
//
//		protected String[] doInBackground(String... params) {
//		// These two need to be declared outside the try/catch
//		// so that they can be closed in the finally block.
//		HttpURLConnection urlConnection = null;
//		BufferedReader reader = null;
//
//		// Will contain the raw JSON response as a string.
//		String forecastJsonStr = null;
//
//		try {
//			// Construct the URL for the OpenWeatherMap query
//			// Possible parameters are avaiable at OWM's forecast API page, at
//			// http://openweathermap.org/API#forecast
//			Uri.Builder forecastUri = new Uri.Builder();
//			forecastUri.scheme("http")
//					.authority("api.openweathermap.org")
//					.appendPath("data")
//					.appendPath("2.5")
//					.appendPath("forecast")
//					.appendPath("daily")
//					.appendQueryParameter("q", params[0])
//					.appendQueryParameter("mode", "json")
//					.appendQueryParameter("units", "metric")
//					.appendQueryParameter("cnt", "7")
//					.appendQueryParameter("APPID", "40aabb59f41e9e88db7be4bab11f49f8");
//
//			URL url = new URL(forecastUri.build().toString());
//
//			// Create the request to OpenWeatherMap, and open the connection
//			urlConnection = (HttpURLConnection) url.openConnection();
//			urlConnection.setRequestMethod("GET");
//			urlConnection.connect();
//
//			// Read the input stream into a String
//			InputStream inputStream = urlConnection.getInputStream();
//			StringBuffer buffer = new StringBuffer();
//			if (inputStream == null) {
//				// Nothing to do.
//				return null;
//			}
//			reader = new BufferedReader(new InputStreamReader(inputStream));
//
//			String line;
//			while ((line = reader.readLine()) != null) {
//				// Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//				// But it does make debugging a *lot* easier if you print out the completed
//				// buffer for debugging.
//				buffer.append(line + "\n");
//			}
//
//			if (buffer.length() == 0) {
//				// Stream was empty.  No point in parsing.
//				return null;
//			}
//			forecastJsonStr = buffer.toString();
//
////                Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);
//
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error ", e);
//                // If the code didn't successfully get the weather data, there's no point in attemping
//                // to parse it.
//                return null;
//            } finally{
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e("ForecastFragment", "Error closing stream", e);
//                    }
//                }
//            }
//
//            try {
//                return getWeatherDataFromJson(forecastJsonStr, 7);
//            } catch (JSONException e) {
//                Log.e("ForecastFragment", "Error returning weather data", e);
//            }
//
//            // This will only happen if there was an error getting or parsing the forecast:
//            return null;
//        }
//
//        /* The date/time conversion code is going to be moved outside the asynctask later,
//         * so for convenience we're breaking it out into its own method now.
//         */
//        private String getReadableDateString(long time){
//            // Because the API returns a unix timestamp (measured in seconds),
//            // it must be converted to milliseconds in order to be converted to valid date.
//            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//            return shortenedDateFormat.format(time);
//        }
//
//        /**
//         * Prepare the weather high/lows for presentation.
//         */
//        private String formatHighLows(double high, double low) {
//            // For presentation, assume the user doesn't care about tenths of a degree.
//
//            // Data is fetched in Celsius by default.
//            // If user prefers to see in Fahrenheit, convert the values here.
//            // We do this rather than fetching in Fahrenheit so that the user can
//            // change this options without us having to re-fetch the data once we start storing the values in a database:
//            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            String unitType = sharedPreferences.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_metric));
//
//            if (unitType.equals(getString(R.string.pref_units_imperial))){
//                high = (high * 1.8) + 32;
//                low = (low * 1.8) + 32;
//            } else if (!unitType.equals(getString(R.string.pref_units_metric))){
//                Log.d(LOG_TAG, "Unit type not found: " + unitType);
//            }
//            long roundedHigh = Math.round(high);
//            long roundedLow = Math.round(low);
//
//            String highLowStr = roundedHigh + "/" + roundedLow;
//            return highLowStr;
//        }
//
//        /**
//         * Take the String representing the complete forecast in JSON Format and
//         * pull out the data we need to construct the Strings needed for the wireframes.
//         *
//         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
//         * into an Object hierarchy for us.
//         */
//        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String OWM_LIST = "list";
//            final String OWM_WEATHER = "weather";
//            final String OWM_TEMPERATURE = "temp";
//            final String OWM_MAX = "max";
//            final String OWM_MIN = "min";
//            final String OWM_DESCRIPTION = "main";
//
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            // OWM returns daily forecasts based upon the local time of the city that is being
//            // asked for, which means that we need to know the GMT offset to translate this data
//            // properly.
//
//            // Since this data is also sent in-order and the first day is always the
//            // current day, we're going to take advantage of that to get a nice
//            // normalized UTC date for all of our weather.
//
//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();
//
//            String[] resultStrs = new String[numDays];
//            for(int dayNo = 0; dayNo < weatherArray.length(); dayNo++) {
//                // For now, using the format "Day, description, hi/low"
//                String day;
//                String description;
//                String highAndLow;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(dayNo);
//
//                // The date/time is returned as a long.  We need to convert that
//                // into something human-readable, since most people won't read "1400356800" as
//                // "this saturday".
//                long dateTime;
//                // Cheating to convert this to UTC time, which is what we want anyhow
//                dateTime = dayTime.setJulianDay(julianStartDay + dayNo);
//                day = getReadableDateString(dateTime);
//
//                // description is in a child array called "weather", which is 1 element long.
//                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                double high = temperatureObject.getDouble(OWM_MAX);
//                double low = temperatureObject.getDouble(OWM_MIN);
//
//                highAndLow = formatHighLows(high, low);
//                resultStrs[dayNo] = day + " - " + description + " - " + highAndLow;
//            }
//
//           /* for (String s : resultStrs) {
//                Log.v(LOG_TAG, "Forecast entry: " + s);
//            }*/
//
//            return resultStrs;
//        }
//
//        @Override
//        protected void onPostExecute(String[] result) {
//            if (result != null){
//                mForecastAdapter.clear();
//
//                for(String dayForecastStr : result){
//                    mForecastAdapter.add(dayForecastStr);
//                }
//
//                // New data is back from the server. Hooray!
//            }
//        }
//    }
}
