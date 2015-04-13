package awsomethree.com.townkitchen.fragments;

import com.parse.ParseException;
import com.parse.ParseObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.EndlessScrollListener;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.activities.MainActivity;
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

    protected int pageScroll;
    protected SwipeRefreshLayout swipeContainer;
    protected ProgressBar progressBar;

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
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        lvOrder = (ListView) v.findViewById(R.id.lvOrderList);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary data
        options = new ArrayList<>();

        // setup the adapters (Using basic)
        aOrderAdapters = new TKTrackingOrderListAdapter(getActivity(), options, this);

        // 4. Connect the adapter to the listview
        lvOrder.setAdapter(aOrderAdapters);

        lvOrder.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                int nextSkip = (page-1) * MainActivity.PAGE_SIZE;
                Log.d(MainActivity.APP, "scrolling for " + nextSkip);
                loadData(nextSkip);
            }
        });
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(0);
            }
        });


        loadData(0);
    }

    private void loadData(int skipSize){
        progressBar.setVisibility(View.VISIBLE);
        pageScroll = skipSize;
        if (skipSize == 0){
            aOrderAdapters.clear();
        }
        // Do async query to get the order
        Order.getOrderInDelivery(pageScroll, this, Order.ORDER_CODE);
    }

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode) {
        if (queryCode == Order.ORDER_CODE){
            List<Order> orders = (List<Order>) parseObjects;
            // put them back into adapters

            aOrderAdapters.addAll(orders);

            swipeContainer.setRefreshing(false);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

}
