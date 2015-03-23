package awsomethree.com.townkitchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import awsomethree.com.townkitchen.R;

/**
 * Created by smulyono on 3/22/15.
 */
public class OrderFeedbackFragment extends Fragment {
    protected ListView lvMenu;
    private ArrayAdapter<String> menuAdapters;

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
        String[] menus = {"Order 1 - Pending Review", "Order 2 - Pending Review", "Order 3 - Pending Review", "Order 4 - Pending Review"};
        // setup the adapters (Using basic)
        menuAdapters = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menus);

        lvMenu.setAdapter(menuAdapters);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // show dialogs for review
            }
        });
    }

}
