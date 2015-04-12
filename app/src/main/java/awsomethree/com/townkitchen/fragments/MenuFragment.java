package awsomethree.com.townkitchen.fragments;

import com.getbase.floatingactionbutton.FloatingActionButton;
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
import awsomethree.com.townkitchen.interfaces.fragmentNavigationInterface;
import awsomethree.com.townkitchen.models.DailyMenu;
import awsomethree.com.townkitchen.models.OrderLineItem;
import awsomethree.com.townkitchen.models.ShoppingCart;

/**
 * Created by smulyono on 3/22/15.
 * coauthor long huynh
 */
public class MenuFragment extends TKFragment implements ParseQueryCallback {
    protected final int TOTAL_ASYNC_QUERY = 2;

    protected ListView lvMenu;
    protected ImageView ibDown;
    protected ImageView ibUp;

    protected ArrayList<DailyMenu> options;//array of FoodMenu models
    //private ArrayAdapter<String> menuAdapters;
    protected TKMenuListAdapter aMenuAdapters;


    protected List<DailyMenu> lrecs;
    protected List<OrderLineItem> cartItems;
    protected int asyncQueryCount;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        setupView(v);
        setupAdaptersAndListeners();
        setupFloatButtons(v);

        return v;
    }


    private void setupView(View v){
        lvMenu = (ListView) v.findViewById(R.id.lvMenu);


        //CLEAR CART
        final FloatingActionButton actionCLear = (FloatingActionButton) v.findViewById(R.id.action_clear);
        if(actionCLear != null) {
            actionCLear.setSize(FloatingActionButton.SIZE_NORMAL);
            actionCLear.setIcon(R.mipmap.ic_clear_cart);
            actionCLear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionCLear.setTitle("Clearing cart...");
                    // Clearout the shopping cart
                    ShoppingCart.clearShoppingCart(getActivity().getApplicationContext(),(fragmentNavigationInterface) getActivity());
                    redirectFragmentTo(MainActivity.HOME_DRAWER_POSITION);
                }
            });
        }


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
                //Toast.makeText(getActivity(), "clicked!", Toast.LENGTH_SHORT).show();
            }
        });



        // Example for retrieving Parse Object
        // get the menu for 2015-04-01, month starts from 0

        // get the Dates from arguments or default to today
        Long dateArgs = new Date().getTime();
        if (getArguments() != null){
            dateArgs = getArguments().getLong("menuDate");
        }

        lrecs = new ArrayList<>();
        cartItems = new ArrayList<>();
        asyncQueryCount = 0;
        // Async Query to get the menu
        DailyMenu.listAllMenuByDates(new Date(dateArgs), this, DailyMenu.DAILYMENU_CODE);
        // Async query to pick up the shopping cart relevant data
        ShoppingCart.getShoppingCart(this, ShoppingCart.SHOPPING_CART_CODE, getActivity().getApplicationContext());
    }

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode) {
        if (queryCode == DailyMenu.DAILYMENU_CODE){
            // this is food menu
            lrecs = (List<DailyMenu>) parseObjects;
            int size = (lrecs != null) ? lrecs.size() : 0;
            Log.d(MainActivity.APP, size + " total menu");

            // TODO... make nice dialog or preventing them before hand
            if (size == 0){
                Toast.makeText(getActivity().getApplicationContext(), "No menu for today!", Toast.LENGTH_SHORT).show();
            } else {
                asyncQueryCount++;
                // draw the updated if shopping cart information are ready
                if (asyncQueryCount == TOTAL_ASYNC_QUERY){
                    updateMenuBasedOnShoppingCart();
                }
            }
       } else if (queryCode == ShoppingCart.SHOPPING_CART_CODE){
            // get the shopping cart details
            cartItems = (List<OrderLineItem>) parseObjects;
            asyncQueryCount++;
            if (asyncQueryCount == TOTAL_ASYNC_QUERY){
                updateMenuBasedOnShoppingCart();
            }
        }
    }

    public void updateMenuBasedOnShoppingCart(){
        // update count based on shopping cart item
        if (cartItems != null && cartItems.size() > 0){
            for (DailyMenu rec : lrecs){
                for (OrderLineItem cartItem : cartItems){
                    if (cartItem.getMenu().getObjectId().equals(rec.getObjectId())){
                        // update the item
                        rec.setQtySelected(cartItem.getQty());
                    }
                }
            }
        } else {
            for (DailyMenu rec : lrecs){
                rec.setQtySelected(0);
            }
        }

        aMenuAdapters.clear();//clear existing list
        aMenuAdapters.addAll(lrecs);
    }
}
