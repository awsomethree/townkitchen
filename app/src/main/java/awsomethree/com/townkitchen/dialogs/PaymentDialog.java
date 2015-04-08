package awsomethree.com.townkitchen.dialogs;

import com.parse.ParseException;
import com.parse.ParseObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.interfaces.dialogInterfaceListener;
import awsomethree.com.townkitchen.models.ShoppingCart;

/**
 * Created by smulyono on 3/22/15.
 */
public class PaymentDialog extends DialogFragment implements ParseQueryCallback {
    private dialogInterfaceListener mListener;
    private ShoppingCart mShoppingCart;

    protected EditText etCC;
    protected EditText etCCV;
    protected EditText etCCDate;

    public static PaymentDialog newInstance(Fragment targetFragment, ShoppingCart shoppingCart){
        PaymentDialog newDialog = new PaymentDialog();
        newDialog.setDialogInterfaceListener((dialogInterfaceListener) targetFragment);
        newDialog.setShoppingCartModel(shoppingCart);
        return newDialog;
    }

    public void setDialogInterfaceListener(dialogInterfaceListener listener){
        this.mListener = listener;
    }

    public void setShoppingCartModel(ShoppingCart shoppingCart){
        this.mShoppingCart = shoppingCart;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.TKDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View bodyView = inflater.inflate(R.layout.dialog_payment, null);

        etCC = (EditText) bodyView.findViewById(R.id.etCC);
        etCCDate = (EditText) bodyView.findViewById(R.id.etCCdate);
        etCCV = (EditText) bodyView.findViewById(R.id.etCCV);

        builder
//                uncomment this for custom title
//                .setCustomTitle(titleView)
                .setView(bodyView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Look at onStart method which will override this
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onFailDialog();
                        }
                        getDialog().cancel();
                    }
                })
        ;

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog d = (AlertDialog) getDialog();

        if (d != null){
            Button positiveButton =  (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean allValid = true;
                    if (etCC.getText().toString().isEmpty()) {
                        etCC.setError("Credit card cannot be empty");
                        allValid = false;
                    }
                    if (etCCV.getText().toString().isEmpty()) {
                        etCCV.setError("Please fill in the CCV number");
                        allValid = false;
                    }
                    if (etCCDate.getText().toString().isEmpty()) {
                        etCCDate.setError("Please specify the expiration date");
                        allValid = false;
                    }

                    if (mListener != null && allValid) {
                        flushShoppingCart();
                    }
                }
            });
        }
    }

    public void flushShoppingCart(){
        ShoppingCart.flushShoppingCart(this, ShoppingCart.SHOPPING_CART_CODE, mShoppingCart, getDialog().getContext());
    }

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode) {
        if (queryCode == ShoppingCart.SHOPPING_CART_CODE){
            ShoppingCart.clearShoppingCart(getActivity().getApplicationContext());
            mListener.onSuccessDialog();
            getDialog().dismiss();
        }
    }
}
