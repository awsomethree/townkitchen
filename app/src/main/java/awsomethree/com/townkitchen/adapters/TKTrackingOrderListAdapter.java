package awsomethree.com.townkitchen.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.fragments.TrackOrderListFragment;
import awsomethree.com.townkitchen.models.Order;

/**
 * Created by smulyono on 3/31/15.
 */
public class TKTrackingOrderListAdapter extends ArrayAdapter<Order> {

    private final String DISPLAY_DAY_FORMAT = "EEEE , yyyy-MM-dd";

    private TrackOrderListFragment mMainActivity;

    class ViewHolder {
        TextView tvTotalOrder;
        TextView tvDeliveryAddress;
        TextView tvDeliveryStatus;
        ImageButton ibShowMap;
    }


    public TKTrackingOrderListAdapter(Context context, List<Order> options, TrackOrderListFragment mfragment) {
        super(context, R.layout.track_order_item_layout , options);
        mMainActivity = mfragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the items
        final Order option = getItem(position);

        final ViewHolder viewHolder;

        // Check viewholder usage
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_order_item_layout, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.tvTotalOrder = (TextView) convertView.findViewById(R.id.tvTotalOrder);
            viewHolder.tvDeliveryAddress = (TextView) convertView.findViewById(R.id.tvDeliveryLocation);
            viewHolder.ibShowMap = (ImageButton) convertView.findViewById(R.id.ibShowMap);
            viewHolder.tvDeliveryStatus = (TextView) convertView.findViewById(R.id.tvDeliveryStatus);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTotalOrder.setText(
                Html.fromHtml("<strong>" + option.getTotalOrder() + "</strong> order" + (option.getTotalOrder() > 0 ? "s" : ""))
                        .toString());
        viewHolder.tvDeliveryAddress.setText(option.getDeliveryAddressStr());

        viewHolder.tvDeliveryStatus.setText(option.getDeliveryStatus());

        if (option.getDeliveryStatus().equalsIgnoreCase(Order.OUT_FOR_DELIVERY_STATUS)){
            viewHolder.ibShowMap.setVisibility(View.VISIBLE);
            viewHolder.ibShowMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open the fragment with correct info
                    Bundle args = new Bundle();
                    args.putParcelable("order", option);
                    mMainActivity.redirectFragmentTo(MainActivity.MENU_TRACKMYODER_DETAIL_DRAWER_POSITION, args);
                }
            });


        } else {
            viewHolder.ibShowMap.setVisibility(View.GONE);
        }

        // return views
        return convertView;
    }
}
