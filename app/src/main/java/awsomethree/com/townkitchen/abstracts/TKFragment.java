package awsomethree.com.townkitchen.abstracts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.interfaces.fragmentNavigationInterface;

/**
 * Created by smulyono on 3/23/15.
 */
public abstract class TKFragment extends Fragment {

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
        final View actionB = v.findViewById(R.id.action_b);

        FloatingActionButton actionC = new FloatingActionButton(getActivity());
        actionC.setTitle("Hide/Show Action above");
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        ((FloatingActionsMenu) v.findViewById(R.id.multiple_actions)).addButton(actionC);

        final FloatingActionButton actionA = (FloatingActionButton) v.findViewById(R.id.action_a);
        actionA.setSize(FloatingActionButton.SIZE_NORMAL);
        actionA.setIcon(R.mipmap.ic_shoppingcart);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionA.setTitle("Go to shopping cart!");
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
