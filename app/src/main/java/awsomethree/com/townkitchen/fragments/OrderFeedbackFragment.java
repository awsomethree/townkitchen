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
import awsomethree.com.townkitchen.adapters.TKFeedListAdapter;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.models.Feedback;

/**
 * Created by smulyono on 3/22/15.
 * coauthor long huynh
 */
public class OrderFeedbackFragment extends TKFragment  implements ParseQueryCallback {
    protected ListView lvMenu;
    protected ArrayList<Feedback> feeds;//array of Feedback models
    //private ArrayAdapter<String> menuAdapters;
    protected TKFeedListAdapter aFeedAdapters;

    protected int pageScroll;
    protected SwipeRefreshLayout swipeContainer;
    protected ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedbacks, container, false);

        setupView(v);
        setupAdaptersAndListeners();
        return v;
    }

    private void setupView(View v){
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        lvMenu = (ListView) v.findViewById(R.id.lvShoppingCart);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary menu
        feeds = new ArrayList<>();

        // setup the adapters (Using basic)
        //aMenuAdapters = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menus);
        aFeedAdapters = new TKFeedListAdapter(getActivity(), feeds);


        lvMenu.setAdapter(aFeedAdapters);

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
            aFeedAdapters.clear();
        }
        // Do async query to get the daily data
        Feedback.listAllFeedsByDates(pageScroll, this, Feedback.FEED_CODE);
    }

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
                               int queryCode) {
        if (queryCode == Feedback.FEED_CODE){
            // this is food menu
            List<Feedback> recs = (List<Feedback>) parseObjects;
            Log.d(MainActivity.APP, recs.size() + " total menu");

            aFeedAdapters.addAll(recs);

            swipeContainer.setRefreshing(false);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }
}
