package awsomethree.com.townkitchen.adapters;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.models.Daily;

/**
 * Created by smulyono on 3/31/15.
 */
public class TKHomeListAdapter extends ArrayAdapter<Daily> {

    private final String DISPLAY_DAY_FORMAT = "EEEE , yyyy-MM-dd";

    class ViewHolder {
        ImageView mImageView;
        TextView tvOptionName;
    }


    public TKHomeListAdapter(Context context, List<Daily> options) {
        super(context, R.layout.home_item , options);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the items
        Daily option = getItem(position);

        final ViewHolder viewHolder;

        // Check viewholder usage
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.ivFoodImage);
            viewHolder.tvOptionName = (TextView) convertView.findViewById(R.id.tvDaytext);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the data based on data
        // shows the date format
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DAY_FORMAT);
        viewHolder.tvOptionName.setText(sdf.format(option.getMenuDate()));

        viewHolder.mImageView.setImageResource(android.R.color.transparent);// clear out the old image for a recycled view
        Picasso.with(getContext())
                .load(option.getImageUrl())
                .resize(0,100)
                .into(viewHolder.mImageView);

        // return views
        return convertView;
    }
}
