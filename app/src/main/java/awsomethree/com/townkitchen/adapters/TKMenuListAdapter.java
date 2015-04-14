package awsomethree.com.townkitchen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.interfaces.fragmentNavigationInterface;
import awsomethree.com.townkitchen.models.DailyMenu;
import awsomethree.com.townkitchen.models.ShoppingCart;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Created by long on 3/8/15.
 */

// Taking the menu item objects and turning them into views displayed in the list (like a collection in backbonejs)
public class TKMenuListAdapter extends ArrayAdapter<DailyMenu> {

    private fragmentNavigationInterface mainParentActivity;

    public TKMenuListAdapter(Context context, List<DailyMenu> options) {//list of menu options
        super(context, R.layout.fragment_main_menu, options);// default simple_list_item_1
        mainParentActivity = (fragmentNavigationInterface) context;
    }

    // Override and setup custom template
    // Should use ViewHolder pattern for every adapter you build.
    // http://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#improving-performance-with-the-viewholder-pattern
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Get the item from the list
        final DailyMenu option = getItem(position);
        final fragmentNavigationInterface mainParentActivity = this.mainParentActivity;

        // 2. Find or inflate the template
        if(convertView == null){//need to inflate? Not in recycle views pool
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_option, parent, false);
        }
        // 3. Find the subviews (components) from the layout to fill with data in the template
        //    ivFoodImage, tvOptionName, tvDesc, tvPrice
        ImageView ivFoodImage = (ImageView) convertView.findViewById(R.id.ivFoodImage);
        TextView tvOptionName = (TextView) convertView.findViewById(R.id.tvOptionName);
        TextView tvDesc = (TextView) convertView.findViewById(R.id.tvComment);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
        final TextView tvItems = (TextView) convertView.findViewById(R.id.tvItems);

        tvItems.setText(Integer.toString(option.getQtySelected()));//default value here

        ImageView ibDown = (ImageView) convertView.findViewById(R.id.ibDown);
        ImageView ibUp = (ImageView) convertView.findViewById(R.id.ibUp);

        //on buttons down and up for menu selection
        ibUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvItems.setText( ""+(Integer.parseInt(tvItems.getText().toString())+1) );
                ShoppingCart.addToShoppingCart(option, Integer.parseInt(tvItems.getText().toString()), v.getContext(),
                    mainParentActivity);
            }
        });
        ibDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newItemQty = Integer.parseInt(tvItems.getText().toString())-1;
                if (newItemQty < 0){
                    newItemQty = 0;
                }
                tvItems.setText(Integer.toString(newItemQty));//default value here
                ShoppingCart
                        .addToShoppingCart(option, Integer.parseInt(tvItems.getText().toString()),
                                v.getContext(), mainParentActivity);
            }
        });


        // 4. Populate data into the subviews
        tvOptionName.setText(option.getFoodMenu().getName());
        tvDesc.setText(option.getFoodMenu().getDescription());
        tvPrice.setText("$"+option.getFoodMenu().getPrice());
        ivFoodImage.setImageResource(android.R.color.transparent);// clear out the old image for a recycled view
        Picasso.with(getContext()).load(option.getFoodMenu().getImageUrl()).into(ivFoodImage);



        //Create a Card
        Card card = new Card(convertView.getContext());

        //Create a CardHeader
        CardHeader header = new CardHeader(convertView.getContext());
        //Set the header title
        header.setTitle("");
        //Set visible the expand/collapse button
        header.setButtonExpandVisible(true);
        //Add Header to card
        card.addCardHeader(header);

        //Card elevation
//        card.setCardElevation(56.5f);

        //This provides a simple (and useless) expand area
        CardExpand expand = new CardExpand(convertView.getContext());
        //Set inner title in Expand Area
        expand.setTitle("Comes with Drink + Dessert.");
        card.addCardExpand(expand);

        //Set card in the cardView
        CardViewNative cardView = (CardViewNative) convertView.findViewById(R.id.carddemo);
        cardView.setCard(card);


        // 5. Return the view to be inserted into the list
        return convertView;// the final item view
    }

}
