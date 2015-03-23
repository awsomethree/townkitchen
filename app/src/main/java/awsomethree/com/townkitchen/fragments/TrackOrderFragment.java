package awsomethree.com.townkitchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;

/**
 * Created by smulyono on 3/22/15.
 */
public class TrackOrderFragment extends TKFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track_order, container, false);

        setupView(v);
        setupAdaptersAndListeners();
        return v;
    }

    private void setupView(View v){
    }

    private void setupAdaptersAndListeners() {
    }

}
