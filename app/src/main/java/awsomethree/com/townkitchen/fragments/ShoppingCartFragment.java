package awsomethree.com.townkitchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.dialogs.PaymentDialog;
import awsomethree.com.townkitchen.interfaces.dialogInterfaceListener;

/**
 * Created by smulyono on 3/22/15.
 */
public class ShoppingCartFragment extends TKFragment implements dialogInterfaceListener{
    protected ListView lvMenu;

    private ArrayAdapter<String> menuAdapters;
    private Button btnCheckout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        setupView(v);
        setupAdaptersAndListeners();
        return v;
    }

    private void setupView(View v){
        lvMenu = (ListView) v.findViewById(R.id.lvShoppingCart);
        btnCheckout = (Button) v.findViewById(R.id.btnCheckout);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary menu
        String[] menus = {"Selection 1", "Selection 2", "Selection 3", "Selection 4", "Selection 5"};
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


        // on click listeners for btnCheckout
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open up new dialogs for paying
                PaymentDialog payDialog = PaymentDialog.newInstance(getCurrentFragment());
                payDialog.show(getFragmentManager(), "Pay");
            }
        });
    }


    @Override
    public void onSuccessDialog() {
        Toast.makeText(getActivity(), "You make payment and redirected to Order History", Toast.LENGTH_SHORT).show();
        // redirect to order history
        redirectFragmentTo(MainActivity.ORDERHISTORY_DRAWER_POSITION);
    }

    @Override
    public void onFailDialog() {
        Toast.makeText(getActivity(), "You have NOT make payment!!!!", Toast.LENGTH_SHORT).show();
    }
}
