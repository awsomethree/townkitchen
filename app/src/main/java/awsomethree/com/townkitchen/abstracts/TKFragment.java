package awsomethree.com.townkitchen.abstracts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.interfaces.fragmentNavigationInterface;
import awsomethree.com.townkitchen.layouts.TKNavigationDrawer;

/**
 * Created by smulyono on 3/23/15.
 * coauthor long huynh
 */
public abstract class TKFragment extends Fragment {
    protected TKNavigationDrawer tkDrawer;

    public Fragment getCurrentFragment(){
        List<Fragment> fragmentList
                = getFragmentManager().getFragments();
        if (fragmentList != null && fragmentList.size() > 0){
            // always get the most top
            return fragmentList.get(0);
        }
        return null;
    }


    protected void setupFloatButtons(View v) {
        //set floating button settings
        /*final View actionB = v.findViewById(R.id.action_b);
        FloatingActionButton actionC = new FloatingActionButton(getActivity());
        actionC.setTitle("Hide/Show Action above");
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        ((FloatingActionsMenu) v.findViewById(R.id.multiple_actions)).addButton(actionC);*/


        //SHOPPING CART
        final FloatingActionButton actionA = (FloatingActionButton) v.findViewById(R.id.action_a);
        if(actionA != null) {
                actionA.setSize(FloatingActionButton.SIZE_NORMAL);
                actionA.setIcon(R.mipmap.ic_shoppingcart);
                actionA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionA.setTitle("Go to shopping cart...");
                        redirectFragmentTo(MainActivity.SHOPPINGCART_DRAWER_POSITION);
                }
            });
        }


        //HOME
        final FloatingActionButton actionB = (FloatingActionButton) v.findViewById(R.id.action_b);
        if(actionB != null) {
            actionB.setSize(FloatingActionButton.SIZE_NORMAL);
            actionB.setIcon(R.mipmap.ic_home);
            actionB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionA.setTitle("Going Home...");
                    redirectFragmentTo(MainActivity.HOME_DRAWER_POSITION);
                }
            });
        }


        //getEmptyShoppingCart

        v.findViewById(R.id.pink_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Clicked pink Floating Action Button", Toast.LENGTH_SHORT).show();
                redirectFragmentTo(MainActivity.SHOPPINGCART_DRAWER_POSITION);
            }
        });
    }

    public void redirectFragmentTo(int posId){
        redirectFragmentTo(posId, null);
    }
    public void redirectFragmentTo(int posId, Bundle args){
        // redirect to order history
        fragmentNavigationInterface listener = (fragmentNavigationInterface) getActivity();
        listener.changeFragmentTo(posId, args);
    }
}
