package awsomethree.com.townkitchen.fragments;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import com.parse.ParseException;
import com.parse.ParseObject;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.models.Order;

/**
 * Created by smulyono on 3/22/15.
 */
public class TrackOrderFragment extends TKFragment implements ParseQueryCallback {
    private TextView tvEstDeliveryTime;
    private TextView tvDeliveryLocation;

    private SupportMapFragment mapFragment;
    private GoogleMap map;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

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
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(getActivity(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
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
        // retrieve order from Parse
        Order.getOrderInDelivery(this, Order.ORDER_CODE);
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

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode) {
        if (queryCode == Order.ORDER_CODE){
            List<Order> orders = (List<Order>) parseObjects;
            // do something on the orders
            if (orders.size() > 0){
                Order order = orders.get(0);
                updateInfo(order);
            }
        }
    }

    private void updateInfo(Order order){
        // update the delivery address
        tvDeliveryLocation.setText(order.getDeliveryAddressStr());
        // calculate the delivery location
    }
}
