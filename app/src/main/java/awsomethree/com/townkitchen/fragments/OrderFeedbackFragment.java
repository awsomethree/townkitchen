package awsomethree.com.townkitchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.adapters.TKFeedListAdapter;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.models.Feedback;

/**
 * Created by smulyono on 3/22/15.
 */
public class OrderFeedbackFragment extends TKFragment  implements ParseQueryCallback {
    protected ListView lvMenu;
    protected ArrayList<Feedback> feeds;//array of Feedback models
    //private ArrayAdapter<String> menuAdapters;
    protected TKFeedListAdapter aFeedAdapters;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedbacks, container, false);

        setupView(v);
        setupAdaptersAndListeners();
        return v;
    }

    private void setupView(View v){
        lvMenu = (ListView) v.findViewById(R.id.lvShoppingCart);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary menu
        //String[] menus = {"Order 1 - Pending Review", "Order 2 - Pending Review", "Order 3 - Pending Review", "Order 4 - Pending Review"};
        feeds = new ArrayList<>();

        // setup the adapters (Using basic)
        //aMenuAdapters = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menus);
        aFeedAdapters = new TKFeedListAdapter(getActivity(), feeds);


        lvMenu.setAdapter(aFeedAdapters);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // show dialogs for review
                Toast.makeText(getActivity(), "clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // get the menu for 2015-04-01, month starts from 0
        Feedback.listAllFeedsByDates(new GregorianCalendar(2015, 3, 1).getTime(), this, Feedback.FEED_CODE);
    }

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
                               int queryCode) {
        if (queryCode == Feedback.FEED_CODE){
            // this is food menu
            List<Feedback> recs = (List<Feedback>) parseObjects;
            Log.d(MainActivity.APP, recs.size() + " total menu");

            aFeedAdapters.clear();//clear existing list
            aFeedAdapters.addAll(recs);
        }
    }
}
