package awsomethree.com.townkitchen.fragments;

import com.parse.ParseException;
import com.parse.ParseObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.adapters.TKMenuListAdapter;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.models.DailyMenu;

/**
 * Created by smulyono on 3/22/15.
 */
public class MenuFragment extends TKFragment implements ParseQueryCallback {
    protected ListView lvMenu;
    protected ImageView ibDown;
    protected ImageView ibUp;

    protected ArrayList<DailyMenu> options;//array of FoodMenu models
    //private ArrayAdapter<String> menuAdapters;
    protected TKMenuListAdapter aMenuAdapters;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        setupView(v);
        setupAdaptersAndListeners();
        return v;
    }


    private void setupView(View v){
        lvMenu = (ListView) v.findViewById(R.id.lvMenu);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary menu
        options = new ArrayList<>();

        // setup the adapters (Using basic)
        //menuAdapters = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menus);
        aMenuAdapters = new TKMenuListAdapter(getActivity(), options);

        // 4. Connect the adapter to the listview
        lvMenu.setAdapter(aMenuAdapters);

        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // move to cart or something
                Toast.makeText(getActivity(), "clicked!", Toast.LENGTH_SHORT).show();
            }
        });


        //populateMenuOptions(0);

        // Example for retrieving Parse Object
        // get the menu for 2015-04-01, month starts from 0

        // get the Dates from arguments or default to today
        Long dateArgs = getArguments().getLong("menuDate");
        if (dateArgs == null){
            // get today or current time
            dateArgs = new Date().getTime();
        }
        DailyMenu.listAllMenuByDates(new Date(dateArgs), this, DailyMenu.DAILYMENU_CODE);
    }

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode) {
        if (queryCode == DailyMenu.DAILYMENU_CODE){
            // this is food menu
            List<DailyMenu> recs = (List<DailyMenu>) parseObjects;
            int size = (recs != null) ? recs.size() : 0;
            Log.d(MainActivity.APP, size + " total menu");

            aMenuAdapters.clear();//clear existing list
            aMenuAdapters.addAll(recs);

            // TODO... make nice dialog or preventing them before hand
            if (size == 0){
                Toast.makeText(getActivity().getApplicationContext(), "No menu for today!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
