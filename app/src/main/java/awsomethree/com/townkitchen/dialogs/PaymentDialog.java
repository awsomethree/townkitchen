package awsomethree.com.townkitchen.dialogs;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.interfaces.dialogInterfaceListener;
import awsomethree.com.townkitchen.interfaces.fragmentNavigationInterface;
import awsomethree.com.townkitchen.models.ShoppingCart;

/**
 * Created by smulyono on 3/22/15.
 */
public class PaymentDialog extends DialogFragment implements ParseQueryCallback {
    private dialogInterfaceListener mListener;
    private ShoppingCart mShoppingCart;

    protected TextView tvTotal;
    protected TextView tvShippingLabel;
    protected TextView tvCCMask;

    protected Button addNewCard;
    protected RelativeLayout newCardPanel;
    protected boolean addNewCardOpen;

    protected EditText etCC;
    protected EditText etCCV;
    protected Spinner etCCMonth;
    protected Spinner etCCYear;
    protected ProgressBar progressBar;
    protected TextView progressText;

    private Map<String, Object> chargeParams;

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

        View paymentHeaderView = inflater.inflate(R.layout.payment_header_dialog, null);
        View bodyView = inflater.inflate(R.layout.dialog_payment, null);

        newCardPanel = (RelativeLayout) bodyView.findViewById(R.id.addNewPanel);
        addNewCard = (Button)bodyView.findViewById(R.id.btnAddCard);
        tvTotal = (TextView) bodyView.findViewById(R.id.tvTotalAmount);
        tvShippingLabel = (TextView) bodyView.findViewById(R.id.tvShippingAddress);
        tvCCMask = (TextView) bodyView.findViewById(R.id.tvCardMask);
        etCC = (EditText) bodyView.findViewById(R.id.etCC);
        etCCV = (EditText) bodyView.findViewById(R.id.etCCV);
        etCCMonth = (Spinner) bodyView.findViewById(R.id.etCCMonth);
        etCCYear = (Spinner) bodyView.findViewById(R.id.etCCyear);
        progressBar = (ProgressBar) bodyView.findViewById(R.id.progressBar);
        progressText = (TextView) bodyView.findViewById(R.id.tvProgressText);

        StringBuilder paymentTitle = new StringBuilder();
        paymentTitle.append(getString(R.string.payment_ok));
        paymentTitle.append( " " + mShoppingCart.getTotalString() );

        tvTotal.setText(mShoppingCart.getTotalString());
        tvShippingLabel.setText(Html.fromHtml(mShoppingCart.getShippingAddress()));

        // check if there is saved customer id
        String customerId = getSavedPaymentOptions();
        // hides the panels
        newCardPanel.setVisibility(View.INVISIBLE);
        addNewCardOpen = false;
        if (customerId.equals("-")){
            newCardPanel.setVisibility(View.VISIBLE);
            addNewCardOpen = true;
        } else {
            String ccnumber = getSavedCCnumber();
            tvCCMask.setText("XXXXXXXXXXXX-" + ccnumber);
        }

        builder
                .setCustomTitle(paymentHeaderView)
                .setView(bodyView)
                .setPositiveButton(paymentTitle.toString(), new DialogInterface.OnClickListener() {
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

        hideProgress();

        // add new card button
        addNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addNewCardOpen){
                    showNewCardPanelWithAnimation();
                } else {
                    hideNewCardPanelWithAnimation();
                }
            }
        });
        return builder.create();
    }

    private void showNewCardPanelWithAnimation(){
        newCardPanel.setVisibility(View.VISIBLE);
        addNewCardOpen = true;
        addNewCard.setText(R.string.cancel);
        // simple animation
        AnimatorSet animatorSet = new AnimatorSet();
        // alpha
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(newCardPanel, "alpha", 0.2f, 1.0f);
        // skew
        ObjectAnimator skewAnimation = ObjectAnimator.ofFloat(newCardPanel, "scaleX", 0.2f, 1.0f);

        animatorSet.setDuration(1000);
        animatorSet.playTogether(alphaAnimation, skewAnimation);
        animatorSet.start();
    }

    private void hideNewCardPanelWithAnimation(){
        addNewCardOpen = false;
        addNewCard.setText(R.string.change_card_text);
        AnimatorSet animatorSet = new AnimatorSet();
        // alpha
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(newCardPanel, "alpha", 1.0f, 0.2f);

        // skew
        ObjectAnimator skewAnimation = ObjectAnimator.ofFloat(newCardPanel, "scaleX", 1.0f, 0.2f);

        animatorSet.setDuration(1000);
        animatorSet.playTogether(alphaAnimation,skewAnimation);
        animatorSet.start();
        // animator callback
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                newCardPanel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    private String getSavedPaymentOptions() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        String objectId = pref.getString("paymentOptions", "a");

        return objectId;
    }

    private String getSavedCCnumber(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        String objectId = pref.getString("ccnumber", "1234");

        return objectId;
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog d = (AlertDialog) getDialog();

        if (d != null){
            Button positiveButton =  (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setBackgroundResource(R.drawable.tk_dialog_button_states);
            positiveButton.setTextColor(getResources().getColor(R.color.tk_white_color));
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean allValid = true;
                    showProgress();
                    if (etCC.getText().toString().isEmpty()) {
                        etCC.setError("Credit card cannot be empty");
                        allValid = false;
                    }
                    if (etCCV.getText().toString().isEmpty()) {
                        etCCV.setError("Please fill in the CCV number");
                        allValid = false;
                    }
                    if (etCCMonth.getSelectedItem().toString().equalsIgnoreCase("month") ) {
                        // must set some errors
                        TextView errorText = (TextView) etCCMonth.getSelectedView();
                        errorText.setError("");
                        allValid = false;
                    }
                    if (etCCYear.getSelectedItem().toString().equalsIgnoreCase("year") ) {
                        // must set some errors
                        TextView errorText = (TextView) etCCYear.getSelectedView();
                        errorText.setError("");
                        allValid = false;
                    }

                    if (mListener != null && allValid) {
                        // move on with the Stripe validation before moving forward
                        Card card = new Card(
                                etCC.getText().toString(),
                                Integer.parseInt(etCCMonth.getSelectedItem().toString()),
                                Integer.parseInt(etCCYear.getSelectedItem().toString()),
                                etCCV.getText().toString()
                        );
                        boolean validation = card.validateCard();
                        if (validation){
                            // create Token before it can be charged
                            makePayment(card);
                        } else if (!card.validateExpMonth()) {
                            TextView errorText = (TextView) etCCMonth.getSelectedView();
                            errorText.setError("");allValid = false;
                        } else if (!card.validateExpYear()) {
                            TextView errorText = (TextView) etCCYear.getSelectedView();
                            errorText.setError("");allValid = false;
                        } else if (!card.validateCVC()){
                            etCCV.setError("CCV/CVC code not valid!");allValid = false;
                        } else {
                            etCC.setError("Credit card information not valid");allValid = false;
                        }
                        if (!allValid){
                            hideProgress();
                        }
                    } else {
                        hideProgress();
                    }
                }
            });
        }
    }

    private void makePayment(Card card){
        new Stripe().createToken(
                card,
                getString(R.string.stripe_publishable_key),
                new TokenCallback() {
                    @Override
                    public void onError(Exception e) {
                        Log.d(MainActivity.APP, e.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(Token token) {
                        Log.d(MainActivity.APP, token.toString());
                        chargeParams = new HashMap<>();
                        chargeParams.put("currency", "usd");
                        chargeParams.put("card", token.getId());
                        chargeParams.put("amount", mShoppingCart.getTotal());
                        chargeParams.put("description", "TK Payment");
                        new stripeCardPayment().execute();
                    }
                }
        );
    }

    public void flushShoppingCart(Card card){
        ShoppingCart.flushShoppingCart(this, ShoppingCart.SHOPPING_CART_CODE, mShoppingCart, getDialog().getContext());
    }

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode) {
        if (queryCode == ShoppingCart.SHOPPING_CART_CODE){
            ShoppingCart.clearShoppingCart(getActivity().getApplicationContext());
            // Clearout the shopping cart
            ShoppingCart.updateCartTotal(getActivity().getApplicationContext(),
                    (fragmentNavigationInterface) getActivity());
            mListener.onSuccessDialog();
            hideProgress();
            getDialog().dismiss();
        }
    }

    class stripeCardPayment extends AsyncTask<Void, Void, Void>{
        Charge chargePayment;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                chargePayment = Charge.create(chargeParams,getString(R.string.stripe_secret_key));
            } catch (AuthenticationException e) {
                e.printStackTrace();
            } catch (InvalidRequestException e) {
                e.printStackTrace();
            } catch (APIConnectionException e) {
                e.printStackTrace();
            } catch (CardException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgress();
        }
    }


    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);
    }


}
