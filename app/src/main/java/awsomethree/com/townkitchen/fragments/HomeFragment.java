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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.EndlessScrollListener;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.adapters.TKHomeListAdapter;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.models.Daily;

/**
 * Created by smulyono on 3/22/15.
 * coauthor long huynh
 */
public class HomeFragment extends TKFragment implements ParseQueryCallback {
    protected ListView lvMenu;

    protected ArrayList<Daily> options;//array of FoodMenu models
    protected TKHomeListAdapter aMenuAdapters;

    protected int pageScroll;
    protected SwipeRefreshLayout swipeContainer;
    protected ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        setupView(v);
        setupAdaptersAndListeners();
        setupFloatButtons(v);

        return v;
    }

    private void setupView(View v){
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        lvMenu = (ListView) v.findViewById(R.id.lvHomeMenu);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary data
        options = new ArrayList<>();

        // setup the adapters (Using basic)
        aMenuAdapters = new TKHomeListAdapter(getActivity(), options);

        // 4. Connect the adapter to the listview
        lvMenu.setAdapter(aMenuAdapters);

        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the selected day and go to Menu
                Log.d(MainActivity.APP, "id : " + id);
                Daily itemRec = aMenuAdapters.getItem(position);
                if (itemRec != null){
                    // passing this to menu fragment as the date to pass
                    Bundle args = new Bundle();
                    args.putLong("menuDate", itemRec.getMenuDate().getTime());
                    //redirectFragmentTo(MainActivity.SHOPPINGCART_DRAWER_POSITION, args);
                    redirectFragmentTo(MainActivity.MENU_DRAWER_POSITION, args);
                }
            }
        });
        lvMenu.setOnScrollListener(new EndlessScrollListener() {
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
            aMenuAdapters.clear();
        }
        // Do async query to get the daily data
        Daily.listAllAvailableDays(pageScroll, this, Daily.DAILY_CODE);
    }

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode) {
        if (queryCode == Daily.DAILY_CODE){
            List<Daily> recs = (List<Daily>) parseObjects;
            aMenuAdapters.addAll(recs);

            swipeContainer.setRefreshing(false);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }
}
