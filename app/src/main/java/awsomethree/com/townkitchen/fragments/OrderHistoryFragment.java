package awsomethree.com.townkitchen.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.models.FoodMenu;
import awsomethree.com.townkitchen.models.Order;
import awsomethree.com.townkitchen.models.OrderLineItem;

/**
 * Created by smulyono on 3/22/15.
 */
public class OrderHistoryFragment extends TKFragment {
    protected ListView lvMenu;
//    private ArrayAdapter<String> menuAdapters;
    private OrderLineArrayAdapter orderLineArrayAdapter;
    private List<OrderLineItem> orderLines;
    private TextView lvOrderHistoryFooter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_history, container, false);

        setupView(v);
        setupAdaptersAndListeners();
        return v;
    }

    private void setupView(View v){
        lvMenu = (ListView) v.findViewById(R.id.lvOrderHistory);
        lvOrderHistoryFooter = (TextView)v.findViewById(R.id.tvOrderHistoryFooter);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary menu
//        String[] menus = {"Order 1", "Order 2", "Order 3", "Order 4", "Order 5"};
        // setup the adapters (Using basic)
//        menuAdapters = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menus);

        orderLines = getStuffOrder();
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
        lvOrderHistoryFooter.setText(Html.fromHtml("Want To Change Order ? Call <br> <b>1-800-town-app</b>"));
    }

    /**
     *         viewHolder.shippingDay.setText(orderLineItem.getOrder().getShipDate().toString()); // for now to string todo format
     viewHolder.optionName.setText(orderLineItem.getMenu().getName());
     viewHolder.optionDesc.setText(Html.fromHtml(orderLineItem.getMenu().getDescription()));
]
     * @return
     */
    public List<OrderLineItem> getStuffOrder() {
        Order order = new Order();
        DateTime shipDate = DateTime.now().plusDays(2);
        order.setShipDate(shipDate.toDate());

        List items = new LinkedList<>();
        OrderLineItem item1 = new OrderLineItem();

        String imgUrl = "http://static1.squarespace.com/static/54b5bb0de4b0a14bf3e854e0/54b6d4f5e4b0b6737de70fb4/5510937de4b084049ce0f5c3/1427149706700/Egg+Salad+Salmon+Cutting+Board.jpg?format=500w";
        String optionName = "Option #1";
        String optionDesc = "Buffalo Chicken BLT w/ Spiced Macaroni Salad";
        double price = Double.parseDouble("13.00");
        item1.setMenu(newFoodMenu(imgUrl, optionName, optionDesc, price));
        item1.setOrder(order);
        items.add(item1);

        OrderLineItem item2 = new OrderLineItem();
        item2.setOrder(order);
        item2.setMenu(newFoodMenu(imgUrl, "Option #2", "Smoked Trout Farmers' Salad w/ Asparagus, Roasted Potatoes, Tomato, Cucumber & Meyer Lemon Yogurt Dressing", 13.00d));
        items.add(item2);

        OrderLineItem item3 = new OrderLineItem();
        item3.setOrder(order);
        item3.setMenu(newFoodMenu(imgUrl, "Option #3", "Roasted Mushroom Goat Cheese Flatbread w/ Romesco, Sprouts, Cucumber & Baby Arugula Salad in Honey Balsamic", 13.00d));
        items.add(item3);

        OrderLineItem item4 = new OrderLineItem();
        item4.setOrder(order);
        item4.setMenu(newFoodMenu(imgUrl, "Option #4", "Smoked Trout Farmers' Salad w/ Asparagus, Roasted Potatoes, Tomato, Cucumber & Meyer Lemon Yogurt Dressing", 13.00d));
        items.add(item4);

        return items;
    }

    private FoodMenu newFoodMenu(String imgrUrl, String optionName, String optionDesc, double price) {
        FoodMenu foodMenu = new FoodMenu();
        foodMenu.setImageUrl(imgrUrl);
        foodMenu.setName(optionName);
        foodMenu.setDescription(optionDesc);
        foodMenu.setPrice(price);

        return foodMenu;
    }
}
