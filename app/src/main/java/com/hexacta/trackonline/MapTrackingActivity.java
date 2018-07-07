package com.hexacta.trackonline;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hexacta.trackonline.users.Tracking;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapTrackingActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;

  private String email;

  DatabaseReference mLocationsRef;

  Double lat, lng;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_tracking);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    // Ref to Firebase
    mLocationsRef = FirebaseDatabase.getInstance().getReference("Locations");

    // Get Intent
    if (getIntent() != null) {
      email = getIntent().getStringExtra("email");
      lat = getIntent().getDoubleExtra("lat", 0);
      lng = getIntent().getDoubleExtra("lng", 0);
    }

    if (!TextUtils.isEmpty(email)) {
      loadCurrentUserLocation(email);
    }
  }

  private void loadCurrentUserLocation(String email) {
    Query userLocation = mLocationsRef.orderByChild("email").equalTo(email);
    userLocation.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        LatLng friendLocation = null;
        for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
          Tracking tracking = postSnapShot.getValue(Tracking.class);

          // Add marker for friend location.
          friendLocation = new LatLng(Double.parseDouble(tracking.getLat()), Double.parseDouble(tracking.getLng()));

          Location currentUser = new Location("");
          currentUser.setLatitude(lat);
          currentUser.setLongitude(lng);

          Location friend = new Location("");
          friend.setLatitude(Double.parseDouble(tracking.getLat()));
          friend.setLongitude(Double.parseDouble(tracking.getLng()));

          // Clear all old markers
          mMap.clear();

          mMap.addMarker(new MarkerOptions()
                  .position(friendLocation)
                  .title(tracking.getEmail())
                  .snippet("Distance: " + new DecimalFormat("#.#").format((currentUser.distanceTo(friend)) / 1000) + "KM")
                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

          mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12.0f));
        }

        // Create marker for current User.
        LatLng currentUser = new LatLng(lat, lng);

        // Getting URL to get Google Directions API
        if (currentUser != null && friendLocation != null) {
          String url = getUrl(currentUser, friendLocation);

          FetchUrl FetchUrl = new FetchUrl();
          FetchUrl.execute(url);
        }

        mMap.addMarker(new MarkerOptions().position(currentUser).title(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private String getUrl(LatLng origin, LatLng dest) {

    // Origin of route
    String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

    // Destination of route
    String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


    // Sensor enabled
    String sensor = "sensor=false";

    // Mode walking
    String mode = "mode=walking";

    String key = "key=" + getString(R.string.google_api_key);

    // Building the parameters to the web service
    String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

    // Output format
    String output = "json";

    // Building the url to the web service
    String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

    return url;
  }

  /**
   * A method to download json data from url
   */
  private String downloadUrl(String strUrl) throws IOException {
    String data = "";
    InputStream iStream = null;
    HttpURLConnection urlConnection = null;
    try {
      URL url = new URL(strUrl);

      // Creating an http connection to communicate with url
      urlConnection = (HttpURLConnection) url.openConnection();

      // Connecting to url
      urlConnection.connect();

      // Reading data from url
      iStream = urlConnection.getInputStream();

      BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

      StringBuffer sb = new StringBuffer();

      String line = "";
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }

      data = sb.toString();
      Log.d("downloadUrl", data.toString());
      br.close();

    } catch (Exception e) {
      Log.d("Exception", e.toString());
    } finally {
      iStream.close();
      urlConnection.disconnect();
    }
    return data;
  }

  private double distance(Location currentUser, Location friend) {
    double theta = currentUser.getLongitude() - friend.getLongitude();
    double dist = Math.sin(deg2rad(currentUser.getLatitude()))
                * Math.sin(deg2rad(friend.getLatitude()))
                * Math.cos(deg2rad(currentUser.getLatitude()))
                * Math.cos(deg2rad(friend.getLatitude()))
                * Math.cos(deg2rad(theta));

    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    return (dist);
  }

  private double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  private double rad2deg(double rad) {
    return (rad * 180 / Math.PI);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
  }

  // Fetches data from url passed
  private class FetchUrl extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... url) {

      // For storing data from web service
      String data = "";

      try {
        // Fetching the data from web service
        data = downloadUrl(url[0]);
        Log.d("Background Task data", data.toString());
      } catch (Exception e) {
        Log.d("Background Task", e.toString());
      }
      return data;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      ParserTask parserTask = new ParserTask();

      // Invokes the thread for parsing the JSON data
      parserTask.execute(result);

    }
  }

  /**
   * A class to parse the Google Places in JSON format
   */
  private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

      JSONObject jObject;
      List<List<HashMap<String, String>>> routes = null;

      try {
        jObject = new JSONObject(jsonData[0]);
        Log.d("ParserTask",jsonData[0].toString());
        DataParser parser = new DataParser();
        Log.d("ParserTask", parser.toString());

        // Starts parsing data
        routes = parser.parse(jObject);
        Log.d("ParserTask","Executing routes");
        Log.d("ParserTask",routes.toString());

      } catch (Exception e) {
        Log.d("ParserTask",e.toString());
        e.printStackTrace();
      }
      return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
      ArrayList<LatLng> points;
      PolylineOptions lineOptions = null;

      // Traversing through all the routes
      for (int i = 0; i < result.size(); i++) {
        points = new ArrayList<>();
        lineOptions = new PolylineOptions();

        // Fetching i-th route
        List<HashMap<String, String>> path = result.get(i);

        // Fetching all the points in i-th route
        for (int j = 0; j < path.size(); j++) {
          HashMap<String, String> point = path.get(j);

          double lat = Double.parseDouble(point.get("lat"));
          double lng = Double.parseDouble(point.get("lng"));
          LatLng position = new LatLng(lat, lng);

          points.add(position);
        }

        // Adding all the points in the route to LineOptions
        lineOptions.addAll(points);
        lineOptions.width(10);
        lineOptions.color(Color.BLUE);

        List<PatternItem> pattern = Arrays.<PatternItem>asList(
                new Dot(), new Dash(30));

        lineOptions.pattern(pattern);

        Log.d("onPostExecute","onPostExecute lineoptions decoded");

      }

      // Drawing polyline in the Google Map for the i-th route
      if(lineOptions != null) {
        mMap.addPolyline(lineOptions);
      }
      else {
        Log.d("onPostExecute","without Polylines drawn");
      }
    }
  }


}
