package awsomethree.com.townkitchen.activities;

import com.parse.ui.ParseLoginDispatchActivity;

/**
 * Created by smulyono on 4/8/15.
 */
public class MainDispatchActivity extends ParseLoginDispatchActivity{

    @Override
    protected Class<?> getTargetClass() {
        return MainActivity.class;
    }
}
