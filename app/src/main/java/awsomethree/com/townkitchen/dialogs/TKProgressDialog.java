package awsomethree.com.townkitchen.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import awsomethree.com.townkitchen.R;

/**
 * Created by smulyono on 4/12/15.
 */
public class TKProgressDialog extends DialogFragment {
    public static TKProgressDialog newInstance(int msgId) {
        TKProgressDialog fragment = new TKProgressDialog();

        Bundle args = new Bundle();
        args.putInt("msgId", msgId);

        fragment.setArguments(args);

        return fragment;
    }

    public TKProgressDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int msgId = getArguments().getInt("msgId");
        View paymentHeaderView = getActivity().getLayoutInflater().inflate(R.layout.standard_header_dialog, null);


        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getActivity().getResources().getString(msgId));
        dialog.setProgress(ProgressDialog.STYLE_SPINNER);
        dialog.setCustomTitle(paymentHeaderView);
        return dialog;
    }
}
