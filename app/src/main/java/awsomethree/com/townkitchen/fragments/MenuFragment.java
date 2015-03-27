package awsomethree.com.townkitchen.fragments;

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

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
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


    public void populateMenuOptions(int startPos) {
        //have fragment override

        //but do for now mock it - shold get json from some kind of API - purse..
        /*JSONArray jsonArray = new JSONArray();
        try {
            JSONObject object=new JSONObject();
            object.put("name", "Option 1");
            object.put("description", "Some food description.");
            object.put("imageUrl", "http://static1.squarespace.com/static/54b5bb0de4b0a14bf3e854e0/54b6d4f5e4b0b6737de70fb4/551091c6e4b04012df29c309/1427149265871/Egg+Salad+Salmon+Cutting+Board.jpg?format=1500w");
            object.put("price", new Double(11.00));
            jsonArray.put(object);
            jsonArray.put(object);
            jsonArray.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<FoodMenu> options = FoodMenu.fromJSONArray(jsonArray);//return us a list of tweets
       // aMenuAdapters.clear();//clear existing list
        aMenuAdapters.addAll(options);*/
    }

    private void setupView(View v){
        lvMenu = (ListView) v.findViewById(R.id.lvMenu);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary menu
        //String[] menus = {"Menu1", "Menu2", "Menu3", "Menu4"};
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
        DailyMenu.listAllMenuByDates(new GregorianCalendar(2015,3,1).getTime(), this, DailyMenu.DAILYMENU_CODE);
    }

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode) {
        if (queryCode == DailyMenu.DAILYMENU_CODE){
            // this is food menu
            List<DailyMenu> recs = (List<DailyMenu>) parseObjects;
            Log.d(MainActivity.APP, recs.size() + " total menu");

            aMenuAdapters.clear();//clear existing list
            aMenuAdapters.addAll(recs);
        }
    }
}
