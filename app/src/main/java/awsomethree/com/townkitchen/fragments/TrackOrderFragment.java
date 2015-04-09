package awsomethree.com.townkitchen.fragments;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.parse.ParseGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.helpers.GeoCodingLocation;
import awsomethree.com.townkitchen.models.Order;

/**
 * Created by smulyono on 3/22/15.
 */
public class TrackOrderFragment extends TKFragment {
    private TextView tvEstDeliveryTime;
    private TextView tvDeliveryLocation;
    private Button refreshButton;
    private Button backButton;

    private SupportMapFragment mapFragment;
    private GoogleMap map;

    private Marker destinationMarker,sourceMarker;

    private ParseGeoPoint driverLocation;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private Order currentOrder;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track_order, container, false);

        setupView(v);

        return v;
    }

    private void setupView(View v){
        tvEstDeliveryTime = (TextView) v.findViewById(R.id.tvEstDeliverTime);
        tvDeliveryLocation = (TextView) v.findViewById(R.id.tvDeliveryLocation);
        refreshButton = (Button) v.findViewById(R.id.btnRefresh);
        backButton = (Button) v.findViewById(R.id.btnBack);


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveOrderLocation();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectFragmentTo(MainActivity.TRACKMYODER_DRAWER_POSITION);
            }
        });


        mapFragment = new SupportMapFragment(){
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                map = mapFragment.getMap();
                if (map != null){
                    loadMap(map);
                }
            }
        };

        getChildFragmentManager().beginTransaction()
                .add(R.id.map, mapFragment).commit();

        currentOrder = (Order) getArguments().getParcelable("order");

    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Log.d(MainActivity.APP, "Map Fragment was loaded properly!");
            map.setMyLocationEnabled(true);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.getUiSettings().setMapToolbarEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            retrieveOrderLocation();
        } else {
            Toast.makeText(getActivity(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrieveOrderLocation() {
        // clear out any marker
        if (sourceMarker != null){
            sourceMarker.remove();
        }
        if (destinationMarker != null){
            destinationMarker.remove();
        }
        // get the order
        updateInfo(currentOrder);
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                errorDialog.show();
            }

            return false;
        }
    }

    private void updateInfo(Order order){
        // update the delivery address
        tvDeliveryLocation.setText(order.getDeliveryAddressStr());
        // calculate the delivery location
        driverLocation = order.getDeliveryCurrentLocation();
        sourceMarker = putMarkerDown(driverLocation.getLatitude(), driverLocation.getLongitude(), "Our driver!");
        // point to the maps delivery address
        GeoCodingLocation.getAddressFromLocation(
                order.getDeliveryAddressStr(), getActivity().getApplicationContext(),
                new GeocoderHandler());
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            Double latitude = 0.0,
                    longitude = 0.0;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    latitude=bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                    // put the marker down for the
                    destinationMarker = putMarkerDown(latitude, longitude, locationAddress);
                    // now calculate the distance
                    LatLng start = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());
                    LatLng end = new LatLng(latitude, longitude);
                    new GeoDrivingDistance().execute(start, end);

                    break;
                default:
                    locationAddress = null;
            }
        }
    }

    private Marker putMarkerDown(Double latitude, Double longitude, String title){
        LatLng point = new LatLng(latitude, longitude);
        // Define color of marker icon
        BitmapDescriptor defaultMarker =
                BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        Marker marker = map.addMarker(new MarkerOptions()
                .position(point)
                .title(title)
                .icon(defaultMarker));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, 9);
        map.animateCamera(cameraUpdate);

        return marker;

    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d(MainActivity.APP, "Exception while downloading url");
            e.printStackTrace();
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class GeoDrivingDistance extends AsyncTask<LatLng, Void, String> {

        private String googleURL = "http://maps.googleapis.com/maps/api/directions/json?";

        @Override
        protected String doInBackground(LatLng... params) {
            // there will be 2 params
            LatLng start = params[0];
            LatLng end = params[1];

            String uri = googleURL
                    + "origin=" + start.latitude + "," + start.longitude
                    + "&destination=" + end.latitude + "," + end.longitude
                    + "&units=metric";
            Log.d(MainActivity.APP, uri);
            String data = null;
            try {
                data = downloadUrl(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jobj = new JSONObject(s);
                String durationText = getDurationFromJSONResult(jobj);
                tvEstDeliveryTime.setText(durationText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // get the routes

            Log.d(MainActivity.APP, "JSON OUTPUT : " + s);
        }
    }

    private String getDurationFromJSONResult(JSONObject jobj) throws JSONException{
        if (jobj.has("status") && jobj.getString("status").equals("OK")){
            JSONArray routes = jobj.getJSONArray("routes");
            // get the first item
            JSONObject routeObj = routes.getJSONObject(0);
            // get the legs property json array
            JSONArray legs = routeObj.getJSONArray("legs");
            // get the first item of legs
            JSONObject legsObj = legs.getJSONObject(0);
            // get duration property
            if (legsObj.has("duration") && legsObj.getJSONObject("duration").has("text")){
                return legsObj.getJSONObject("duration").getString("text");
            }
        }
        return "";
    }
}
