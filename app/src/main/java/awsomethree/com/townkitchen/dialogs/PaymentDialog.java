package awsomethree.com.townkitchen.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.interfaces.dialogInterfaceListener;

/**
 * Created by smulyono on 3/22/15.
 */
public class PaymentDialog extends DialogFragment {
    private dialogInterfaceListener mListener;


    public static PaymentDialog newInstance(Fragment targetFragment){
        PaymentDialog newDialog = new PaymentDialog();
        newDialog.setDialogInterfaceListener((dialogInterfaceListener) targetFragment);
        return newDialog;
    }

    public void setDialogInterfaceListener(dialogInterfaceListener listener){
        this.mListener = listener;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View bodyView = inflater.inflate(R.layout.dialog_payment, null);

        builder
//                uncomment this for custom title
//                .setCustomTitle(titleView)
                .setView(bodyView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null){
                            mListener.onSuccessDialog();
                        }
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null){
                            mListener.onFailDialog();
                        }
                        getDialog().cancel();
                    }
                })
        ;

        return builder.create();
    }
}
