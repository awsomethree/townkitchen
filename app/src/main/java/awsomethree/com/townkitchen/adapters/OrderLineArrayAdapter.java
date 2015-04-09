package awsomethree.com.townkitchen.adapters;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.fragments.OrderHistoryFragment;
import awsomethree.com.townkitchen.models.Order;
import awsomethree.com.townkitchen.models.OrderLineItem;

/**
 * Created by ktruong on 3/29/15.
 * coauthor long huynh
 */
public class OrderLineArrayAdapter extends ArrayAdapter<OrderLineItem> {
    private static class ViewHolder {
        public ImageView imageUrl;
        public TextView shippingDay;
        public TextView optionName;
        public TextView optionDesc;
        public TextView price;
        //public TextView more;
    }

    public OrderLineArrayAdapter(Context context, List<OrderLineItem> tweets) {
        super(context, R.layout.fragment_order_history, tweets);
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OrderLineItem orderLineItem = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_history_item_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imageUrl = (ImageView)convertView.findViewById(R.id.ivFoodImageUrl);
            viewHolder.shippingDay = (TextView)convertView.findViewById(R.id.tvShippingDay);
            viewHolder.optionName = (TextView)convertView.findViewById(R.id.tvOptionName);
            viewHolder.optionDesc = (TextView)convertView.findViewById(R.id.tvOptionDesc);
            viewHolder.price = (TextView)convertView.findViewById(R.id.tvPrice);
           // viewHolder.more = (TextView)convertView.findViewById(R.id.tvMore);

//            viewHolder.orderHistoryFooter = (TextView)convertView.findViewById(R.id.tvOrderHistoryFooter);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.imageUrl.setImageResource(0);


//        Transformation transformation = new RoundedTransformationBuilder()
//                .borderColor(Color.TRANSPARENT).scaleType(ImageView.ScaleType.FIT_CENTER)
//                .borderWidthDp(3)
//                .cornerRadiusDp(30)
//                .build();
        Picasso.with(getContext()).load(orderLineItem.getMenu().getFoodMenu().getImageUrl()).fit().into(viewHolder.imageUrl);

        Order order = orderLineItem.getOrder();

        if (order != null){
            Date shipDate = order.getShipDate();
            if (shipDate != null){
                CharSequence relativeTimeSpanString = DateUtils.getRelativeTimeSpanString(shipDate.getTime(), System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_WEEKDAY);
                viewHolder.shippingDay.setText(relativeTimeSpanString);
            }
        }

       // viewHolder.more.setText("more...");
        viewHolder.price.setText("$"+orderLineItem.getPrice());
        viewHolder.optionName.setText(orderLineItem.getMenu().getFoodMenu().getName());
        viewHolder.optionDesc.setText(Html.fromHtml(orderLineItem.getMenu().getFoodMenu().getDescription()));

        //group the line items by their order's id
        if(OrderHistoryFragment.orderLineItemMap.containsKey(orderLineItem.getOrder().getObjectId())) {
            convertView.setBackgroundColor( Integer.parseInt(OrderHistoryFragment.orderLineItemMap.get(orderLineItem.getOrder().getObjectId()).toString()) );
        }
        else{
            Integer index = OrderHistoryFragment.orderLineItemMap.size()%OrderHistoryFragment.colorArray.size();
            OrderHistoryFragment.orderLineItemMap.put(orderLineItem.getOrder().getObjectId(), OrderHistoryFragment.colorArray.get(index));
            convertView.setBackgroundColor( Integer.parseInt(OrderHistoryFragment.orderLineItemMap.get(orderLineItem.getOrder().getObjectId()).toString()) );

        }
        return convertView;
    }
}
