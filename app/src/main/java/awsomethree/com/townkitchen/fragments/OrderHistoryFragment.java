package awsomethree.com.townkitchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.adapters.OrderLineArrayAdapter;
import awsomethree.com.townkitchen.models.OrderLineItem;
import awsomethree.com.townkitchen.models.OrderUtils;

/**
 * Created by smulyono on 3/22/15.
 */
public class OrderHistoryFragment extends TKFragment {
    protected ListView lvMenu;
//    private ArrayAdapter<String> menuAdapters;
    private OrderLineArrayAdapter orderLineArrayAdapter;
    private List<OrderLineItem> orderLines;
    private TextView orderHistoryFooter;
    private View orderHistoryFooterView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_history, container, false);

        //todo viewholder for footer
        orderHistoryFooterView = getLayoutInflater(savedInstanceState).inflate(R.layout.order_history_footer_layout, null);
        orderHistoryFooter = (TextView) orderHistoryFooterView.findViewById(R.id.tvOrderHistoryFooter);

        setupView(v);
        setupAdaptersAndListeners();
        return v;
    }

    private void setupView(View v){
        lvMenu = (ListView) v.findViewById(R.id.lvOrderHistory);
        lvMenu.addFooterView(orderHistoryFooterView);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary menu
//        String[] menus = {"Order 1", "Order 2", "Order 3", "Order 4", "Order 5"};
        // setup the adapters (Using basic)
//        menuAdapters = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menus);

        orderLines = getOrder();
        orderLineArrayAdapter = new OrderLineArrayAdapter(getActivity(), orderLines);

        lvMenu.setAdapter(orderLineArrayAdapter);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // move to cart or something
                Toast.makeText(getActivity(), "clicked!", Toast.LENGTH_SHORT).show();

//                    Intent intent = new Intent(OrderHistoryFragment.this, OrderHistoryDetailActivity.class);
//                    intent.putExtra("order", orders.get(position));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                    startActivity(intent);
            }
        });
        orderHistoryFooter.setText(Html.fromHtml("Want To Change Order ? Call <br> <b>1-800-town-app</b>"));
    }

    /**
     * TODO call parse to get order in the db
     * @return
     */
    public List<OrderLineItem> getOrder() {
        return OrderUtils.getStuffOrder();
    }
}
