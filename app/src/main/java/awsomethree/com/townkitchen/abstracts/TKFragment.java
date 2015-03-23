package awsomethree.com.townkitchen.abstracts;

import android.support.v4.app.Fragment;

import java.util.List;

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

    public void redirectFragmentTo(int posId){
        // redirect to order history
        fragmentNavigationInterface listener = (fragmentNavigationInterface) getActivity();
        listener.changeFragmentTo(posId);
    }
}
