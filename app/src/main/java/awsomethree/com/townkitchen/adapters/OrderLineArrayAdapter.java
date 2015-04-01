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
import awsomethree.com.townkitchen.models.Order;
import awsomethree.com.townkitchen.models.OrderLineItem;

/**
 * Created by ktruong on 3/29/15.
 */
public class OrderLineArrayAdapter extends ArrayAdapter<OrderLineItem> {
    private static class ViewHolder {
        public ImageView imageUrl;
        public TextView shippingDay;
        public TextView optionName;
        public TextView optionDesc;
        public TextView price;
    }

    public OrderLineArrayAdapter(Context context, List<OrderLineItem> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
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
        Date shipDate = order.getShipDate();

        if (shipDate != null){
            CharSequence relativeTimeSpanString = DateUtils.getRelativeTimeSpanString(shipDate.getTime(), System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_WEEKDAY);
            viewHolder.shippingDay.setText(relativeTimeSpanString);
        }
        viewHolder.price.setText("$"+orderLineItem.getPrice());
        viewHolder.optionName.setText(orderLineItem.getMenu().getFoodMenu().getName());
        viewHolder.optionDesc.setText(Html.fromHtml(orderLineItem.getMenu().getFoodMenu().getDescription()));
//        viewHolder.orderHistoryFooter.setText(Html.fromHtml("Want To Change Order ? Call <br> <b>1-800-town-app</b>"));

        return convertView;
    }
}
