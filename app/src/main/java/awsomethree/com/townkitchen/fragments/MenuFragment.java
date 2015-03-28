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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.GregorianCalendar;
import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.models.DailyMenu;

/**
 * Created by smulyono on 3/22/15.
 */
public class MenuFragment extends TKFragment implements ParseQueryCallback {
    protected ListView lvMenu;
    private ArrayAdapter<String> menuAdapters;

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
        String[] menus = {"Menu1", "Menu2", "Menu3", "Menu4"};
        // setup the adapters (Using basic)
        menuAdapters = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menus);

        lvMenu.setAdapter(menuAdapters);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // move to cart or something
                Toast.makeText(getActivity(), "clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // Example for retrieving Parse Object
        // get the menu for 2015-04-01, month starts from 0
        DailyMenu.listAllMenuByDates(new GregorianCalendar(2015,3,1).getTime(), this, DailyMenu.DAILYMENU_CODE);
    }

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode) {
        if (queryCode == DailyMenu.DAILYMENU_CODE){
            // this is food menu
            List<DailyMenu> recs = (List<DailyMenu>) parseObjects;
            int size = (recs != null) ? recs.size() : 0;
            Log.d(MainActivity.APP, size + " total menu");
        }
    }
}
