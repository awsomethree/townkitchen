package awsomethree.com.townkitchen.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.adapters.OrderLineArrayAdapter;
import awsomethree.com.townkitchen.dialogs.GiveFeedbackDialog;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.interfaces.dialogInterfaceListener;
import awsomethree.com.townkitchen.models.Feedback;
import awsomethree.com.townkitchen.models.OrderLineItem;

/**
 * Created by smulyono on 3/22/15.
 * coauthor long huynh
 */
public class OrderHistoryFragment extends TKFragment implements dialogInterfaceListener, ParseQueryCallback {
    protected ListView lvMenu;
//    private ArrayAdapter<String> menuAdapters;
    private OrderLineArrayAdapter orderLineArrayAdapter;
    private List<OrderLineItem> orderLines;
    private TextView orderHistoryFooter;
    public static Map orderLineItemMap = new HashMap<String, Integer>();
    public static ArrayList colorArray = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_history, container, false);

        setupView(v, savedInstanceState);
        setupAdaptersAndListeners();
        return v;
    }

    private void setupView(View v, Bundle savedInstanceState){
        //todo viewholder for footer
        orderHistoryFooter = (TextView) v.findViewById(R.id.tvOrderHistoryFooter);
        lvMenu = (ListView) v.findViewById(R.id.lvOrderHistory);

        colorArray.add(Color.parseColor("#DCFFEA"));
        colorArray.add(Color.parseColor("#eef7ff"));
        colorArray.add(Color.parseColor("#dde6e8"));
        colorArray.add(Color.parseColor("#ff7373"));
        colorArray.add(Color.parseColor("#d3ffce"));
        colorArray.add(Color.parseColor("#DCFFEA"));
        colorArray.add(Color.parseColor("#46c6e2"));
    }

    private void setupAdaptersAndListeners() {
        // arbitrary menu
        //String[] menus = {"Order 1 - Pending Review", "Order 2 - Pending Review", "Order 3 - Pending Review", "Order 4 - Pending Review"};
        orderLines = new ArrayList<>();

        // setup the adapters (Using basic)
        //aMenuAdapters = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menus);
        orderLineArrayAdapter = new OrderLineArrayAdapter(getActivity(), orderLines);


        lvMenu.setAdapter(orderLineArrayAdapter);

        //setup for feedback dialog onclick on Item
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderLineItem orderData = orderLineArrayAdapter.getItem(position);
                Feedback feedbackModel = new Feedback();
                feedbackModel.setOrderLIid(orderData.getObjectId());
                // show dialogs for review
                //Toast.makeText(getActivity(), "clicked!", Toast.LENGTH_SHORT).show();
                // open up new dialogs for paying
                GiveFeedbackDialog feedbackDialog = GiveFeedbackDialog.newInstance(getCurrentFragment(), feedbackModel);
                feedbackDialog.show(getFragmentManager(), "Pay");
            }
        });

        orderHistoryFooter.setText(Html.fromHtml("Want To Change Order ? Call <br> <b>1-800-town-app</b>"));
        // get the menu for 2015-04-01, month starts from 0
        OrderLineItem.listAllOrderLineItemsByDates(new GregorianCalendar(2015, 3, 1).getTime(), this, OrderLineItem.ORDERLINEITEM_CODE);



    }

    @Override
    public void onSuccessDialog() {
        Toast.makeText(getActivity(), "Ready for your feedback!", Toast.LENGTH_SHORT).show();

        // redirect to order history
        redirectFragmentTo(MainActivity.ORDERHISTORY_DRAWER_POSITION);
    }

    @Override
    public void onFailDialog() {
        Toast.makeText(getActivity(), "Seriously? No feedback?", Toast.LENGTH_SHORT).show();
    }

    /**
     * TODO call parse to get order in the db
     * @return
     */
    //public List<OrderLineItem> getOrder() {
    //    return OrderUtils.getStuffOrder();
    //}

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
                               int queryCode) {
        if (queryCode == OrderLineItem.ORDERLINEITEM_CODE){
            // this is food menu
            List<OrderLineItem> recs = (List<OrderLineItem>) parseObjects;
            Log.d(MainActivity.APP, recs.size() + " total menu");

            orderLineArrayAdapter.clear();//clear existing list
            orderLineArrayAdapter.addAll(recs);

        }
    }
}
