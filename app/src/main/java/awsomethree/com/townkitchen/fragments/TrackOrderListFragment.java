package awsomethree.com.townkitchen.fragments;

import com.parse.ParseException;
import com.parse.ParseObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.adapters.TKTrackingOrderListAdapter;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.models.Order;

/**
 * Created by smulyono on 3/22/15.
 */
public class TrackOrderListFragment extends TKFragment implements ParseQueryCallback {
    protected ListView lvOrder;
    protected ArrayList<Order> options;//array of FoodMenu models
    protected TKTrackingOrderListAdapter aOrderAdapters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track_list_order, container, false);

        setupView(v);
        setupAdaptersAndListeners();

        return v;
    }

    private void setupView(View v){
        lvOrder = (ListView) v.findViewById(R.id.lvOrderList);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary data
        options = new ArrayList<>();

        // setup the adapters (Using basic)
        aOrderAdapters = new TKTrackingOrderListAdapter(getActivity(), options, this);

        // 4. Connect the adapter to the listview
        lvOrder.setAdapter(aOrderAdapters);

        // Do async query to get the order
        Order.getOrderInDelivery(this, Order.ORDER_CODE);
    }


    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode) {
        if (queryCode == Order.ORDER_CODE){
            List<Order> orders = (List<Order>) parseObjects;
            // put them back into adapters
            aOrderAdapters.clear();
            aOrderAdapters.addAll(orders);
        }
    }

}
