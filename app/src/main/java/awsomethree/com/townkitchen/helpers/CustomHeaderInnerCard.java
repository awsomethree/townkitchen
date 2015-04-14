package awsomethree.com.townkitchen.helpers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import awsomethree.com.townkitchen.R;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by long on 4/14/15.
 */
public class CustomHeaderInnerCard extends CardHeader {

    public CustomHeaderInnerCard(Context context) {
        super(context, R.layout.carddemo_example_inner_header);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view!=null){
            TextView t1 = (TextView) view.findViewById(R.id.text_example1);
            if (t1!=null)
                t1.setText(getContext().getString(R.string.demo_header_exampletitle1));

            /*TextView t2 = (TextView) view.findViewById(R.id.text_example2);
            if (t2!=null)
                t2.setText(getContext().getString(R.string.demo_header_exampletitle2));*/
        }
    }
}