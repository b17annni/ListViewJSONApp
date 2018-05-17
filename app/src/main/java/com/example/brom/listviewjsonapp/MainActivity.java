package com.example.brom.listviewjsonapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.attr.start;


// Create a new class, Mountain, that can hold your JSON data

// Create a ListView as in "Assignment 1 - Toast and ListView"

// Retrieve data from Internet service using AsyncTask and the included networking code

// Parse the retrieved JSON and update the ListView adapter

// Implement a "refresh" functionality using Android's menu system


public class MainActivity extends AppCompatActivity {
    private String[] mountainNames = {"Matterhorn","Mont Blanc","Denali"};
    private String[] mountainLocations = {"Alps","Alps","Alaska"};
    private int[] mountainHeights ={4478,4808,6190};
    private List<Mountain_class> myberg = new ArrayList<Mountain_class>();
    private ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new FetchData().execute();

        for (int start=0;start<mountainNames.length;start++){
            Mountain_class m = new Mountain_class(mountainNames[start],mountainLocations[start],mountainHeights[start]);
            myberg.add(m);
        }

        String[] mountains = {"K2","Mount Rainier","Aconcagua"};
        List<String> listData = new ArrayList<String>(Arrays.asList(mountains));
        adapter = new ArrayAdapter(getApplicationContext(),R.layout.list_item_textview,
                R.id.my_item_textview,myberg);
        ListView myListView = (ListView)findViewById(R.id.myListView);
        myListView.setAdapter(adapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Mountain_class m = myberg.get(position);
                Toast.makeText(getApplicationContext(), m.myStrings(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.meny_refresh:
                new FetchData().execute();
                return true;

            case R.id.meny_clear:
                adapter.clear();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class FetchData extends AsyncTask<Void,Void,String>{



        @Override
        protected String doInBackground(Void... params) {
            // These two variables need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a Java string.
            String jsonStr = null;

            try {
                // Construct the URL for the Internet service
                URL url = new URL("http://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=brom");

                // Create the request to the PHP-service, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
                return jsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in
                // attempting to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Network error", "Error closing stream", e);
                    }
                }
            }
        }
        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            Log.d("mylog","DataFetched"+o);
            try {
                JSONArray allaberg = new JSONArray(o);
                adapter.clear();
                for (int start=0;start<allaberg.length();start++){
                    JSONObject woow = allaberg.getJSONObject(start);
                    int mountainid = woow.getInt("ID");
                    String mountainname = woow.getString("name");
                    String mountaintype = woow.getString("type");
                    String mountaincompany = woow.getString("company");
                    String mountainlocation = woow.getString("location");
                    String mountaincategory = woow.getString("category");
                    int mountainheight = woow.getInt("size");
                    int mountaincost = woow.getInt("cost");
                    String mountainauxdata = woow.getString("auxdata");

                    Mountain_class m = new Mountain_class(mountainname,mountainlocation,mountainheight);
                    adapter.add(m);

                    Log.d("mylog","forloopvarv "+start+mountainname);
                }
            } catch (JSONException e) {
                Log.e("mylog","E:"+e.getMessage());
            }

            // This code executes after we have received our data. The String object o holds
            // the un-parsed JSON string or is null if we had an IOException during the fetch.

            // Implement a parsing code that loops through the entire JSON and creates objects
            // of our newly created Mountain class.
        }
    }
}