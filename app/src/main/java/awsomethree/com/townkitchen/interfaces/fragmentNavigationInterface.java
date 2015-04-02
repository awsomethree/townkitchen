package awsomethree.com.townkitchen.interfaces;

import android.os.Bundle;

/**
 * Created by smulyono on 3/22/15.
 */
public interface fragmentNavigationInterface {
    public void changeFragmentTo(int fragmentMenuOption);
    public void changeFragmentTo(int fragmentMenuOption, Bundle args);
    // other non-navigation preference
    public void updateCartBadge(String countText);
}
