package awsomethree.com.townkitchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;

/**
 * Created by smulyono on 3/22/15.
 */
public class OrderHistoryFragment extends TKFragment {
    protected ListView lvMenu;
    private ArrayAdapter<String> menuAdapters;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_history, container, false);

        setupView(v);
        setupAdaptersAndListeners();
        return v;
    }

    private void setupView(View v){
        lvMenu = (ListView) v.findViewById(R.id.lvShoppingCart);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary menu
        String[] menus = {"Order 1", "Order 2", "Order 3", "Order 4", "Order 5"};
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
    }

}
