package awsomethree.com.townkitchen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.models.NavDrawerItem;

/**
 * Created by smulyono on 3/22/15.
 */
public class TKDrawerListAdapter extends BaseAdapter{
    private class ViewHolder {
        ImageView imgIcon;
        TextView txtTitle;
    }


    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public TKDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO... viewHolder pattern needs to be implemented
        ViewHolder v;
        // how to draw each of the menu
        // use drawer_nav_item as layout
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.drawer_nav_item, null);

            v = new ViewHolder();
            v.imgIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            v.txtTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
        v.txtTitle.setText(navDrawerItems.get(position).getTitle());

        return convertView;
    }
}
