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
import com.stripe.model.Customer;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;
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
    protected final String STRIPE_CUSTOMER_ID = "stripeCustomerId";
    protected final String STRIPE_CUSTOMER_CCNUMBER = "ccnumber";

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
    protected ImageView ivProgressBG;
    protected Switch switchSaveCard;
    protected TextView tvPaidLabel;

    protected Button positiveButton;
    protected Button negativeButton;

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
        switchSaveCard = (Switch) bodyView.findViewById(R.id.switchSaveCard);
        progressBar = (ProgressBar) bodyView.findViewById(R.id.progressBar);
        progressText = (TextView) bodyView.findViewById(R.id.tvProgressText);
        ivProgressBG = (ImageView) bodyView.findViewById(R.id.ivProgressBg);
        tvPaidLabel = (TextView) bodyView.findViewById(R.id.tvPaidLabel);

        StringBuilder paymentTitle = new StringBuilder();
        paymentTitle.append(getString(R.string.payment_ok));
        paymentTitle.append( " " + mShoppingCart.getTotalString() );

        tvTotal.setText(mShoppingCart.getTotalString());
        tvShippingLabel.setText(Html.fromHtml(mShoppingCart.getShippingAddress()));

        // check if there is saved customer id
        String customerId = getSavedPaymentOptions();
        // hides the panels
        newCardPanel.setVisibility(View.GONE);
        addNewCard.setVisibility(View.VISIBLE);
        ivProgressBG.setVisibility(View.VISIBLE);
        addNewCardOpen = false;
        if (customerId.equals("-")){
            // nothing is stored in preferences yet, so we need to force user to either
            // input credit card and then store them (if they wanted to)
            newCardPanel.setVisibility(View.VISIBLE);
            addNewCard.setVisibility(View.GONE);
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

    private void showPaidLabelAnimation(){
        tvPaidLabel.setVisibility(View.VISIBLE);
        tvPaidLabel.setAlpha(0.0f);
        tvPaidLabel.setRotation(-45.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        AnimatorSet allAnimatorSet = new AnimatorSet();

        // alpha
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(tvPaidLabel, "alpha", 0f, 1.0f);
        alphaAnimation.setDuration(1000);

        ObjectAnimator scaleAnimationX = ObjectAnimator.ofFloat(tvPaidLabel, "scaleX", 2.0f, 1.0f);
        scaleAnimationX.setDuration(1000);

        ObjectAnimator scaleAnimationY = ObjectAnimator.ofFloat(tvPaidLabel, "scaleY", 2.0f, 1.0f);
        scaleAnimationY.setDuration(1000);

        // hold
        ObjectAnimator holdAnimation = ObjectAnimator.ofFloat(tvPaidLabel, "alpha", 1.0f, 1.0f);
        holdAnimation.setDuration(2000);

        animatorSet.setDuration(1000);
        animatorSet
                .playTogether(alphaAnimation, scaleAnimationX, scaleAnimationY);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                progressText.setText(R.string.progress_payment_finalized_text);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        allAnimatorSet.playSequentially(animatorSet, holdAnimation);
        allAnimatorSet.start();

        allAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // when it is ended, make payment to parse for finishing
                flushShoppingCart();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void showNewCardPanelWithAnimation(){
        newCardPanel.setVisibility(View.VISIBLE);
        newCardPanel.setAlpha(0);

        addNewCardOpen = true;
        addNewCard.setText(R.string.cancel);

        // simple animation
        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet flipPanelBG = new AnimatorSet();
        AnimatorSet flipPanel= new AnimatorSet();

        // skew progressBG
        ObjectAnimator skewAnimationBG = ObjectAnimator.ofFloat(ivProgressBG, "rotationY", 0f, 180.0f);
        skewAnimationBG.setDuration(1000);
        // alpha
        ObjectAnimator alphaAnimationBG = ObjectAnimator.ofFloat(ivProgressBG, "alpha", 1.0f, 0f);
        alphaAnimationBG.setDuration(500);

        flipPanelBG.setDuration(1000);
        flipPanelBG.playTogether(skewAnimationBG, alphaAnimationBG);

        // alpha
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(newCardPanel, "alpha", -1.0f, 1.0f);
        alphaAnimation.setDuration(1000);
        // skew
        ObjectAnimator skewAnimation = ObjectAnimator.ofFloat(newCardPanel, "rotationY", -180.0f, 0f);
        skewAnimation.setDuration(1000);

        flipPanel.setDuration(1000);
        flipPanel
                .playTogether(alphaAnimation, skewAnimation);

        animatorSet.setDuration(1000);
        animatorSet
                .playTogether(flipPanelBG, flipPanel);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ivProgressBG.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void hideNewCardPanelWithAnimation(){
        ivProgressBG.setVisibility(View.VISIBLE);
        ivProgressBG.setAlpha(-1.0f);
        addNewCardOpen = false;
        addNewCard.setText(R.string.change_card_text);

        // simple animation
        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet flipPanelBG = new AnimatorSet();
        AnimatorSet flipPanel= new AnimatorSet();

        // skew progressBG
        ObjectAnimator skewAnimationBG = ObjectAnimator.ofFloat(ivProgressBG, "rotationY", 180.0f, 0f);
        skewAnimationBG.setDuration(1000);
        // alpha
        ObjectAnimator alphaAnimationBG = ObjectAnimator.ofFloat(ivProgressBG, "alpha", -1.0f, 1.0f);
        alphaAnimationBG.setDuration(1000);

        flipPanelBG.setDuration(1000);
        flipPanelBG.playTogether(skewAnimationBG, alphaAnimationBG);

        // alpha
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(newCardPanel, "alpha", 1.0f, 0f);
        alphaAnimation.setDuration(500);
        // skew
        ObjectAnimator skewAnimation = ObjectAnimator.ofFloat(newCardPanel, "rotationY", 0f, -180.0f);
        skewAnimation.setDuration(1000);

        flipPanel.setDuration(1000);
        flipPanel
                .playTogether(alphaAnimation, skewAnimation);

        animatorSet.setDuration(1000);
        animatorSet
                .playTogether(flipPanel, flipPanelBG);
        animatorSet.start();

        // animator callback
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                newCardPanel.setVisibility(View.GONE);
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
        String objectId = pref.getString(STRIPE_CUSTOMER_ID, "-");

        return objectId;
    }

    private String getSavedCCnumber(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        String objectId = pref.getString(STRIPE_CUSTOMER_CCNUMBER, "1234");

        return objectId;
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog d = (AlertDialog) getDialog();

        if (d != null){
            negativeButton = (Button) d.getButton(Dialog.BUTTON_NEGATIVE);
            positiveButton =  (Button) d.getButton(Dialog.BUTTON_POSITIVE);

            hideProgress();

            positiveButton.setBackgroundResource(R.drawable.tk_dialog_button_states);
            positiveButton.setTextColor(getResources().getColor(R.color.tk_white_color));
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean allValid = true;
                    showProgress();
                    if (addNewCardOpen){
                        // only validate when add new card is open
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
                    }

                    if (mListener != null && allValid) {
                        // check where should we get the card or customer information
                        Card card;
                        if (addNewCardOpen){
                            card = new Card(
                                    etCC.getText().toString(),
                                    Integer.parseInt(etCCMonth.getSelectedItem().toString()),
                                    Integer.parseInt(etCCYear.getSelectedItem().toString()),
                                    etCCV.getText().toString()
                            );
                            // get the card information from add newcard
                            if (validateCardInformation(card)){
                                // see if this needs persistence
                                if (switchSaveCard.isChecked()){
                                    // persist the card information as customer, and put them into
                                    // saved preferences
                                    makePayment(card, true);
                                } else {
                                    // make one time payment with the card
                                    makePayment(card);
                                }
                            } else {
                                allValid = false;
                            }
                        } else {
                            // get the customer information from saved preferences
                            if (validateCustomerInformation()){
                                makePaymentWithCustomerInformation();
                            } else {
                                allValid = false;
                            }
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

    /**
     * From the saved preferences, retrieve the customer information and
     * validate to stripe before making any future process.
     * @return
     */
    private boolean validateCustomerInformation(){
        return true;
    }

    private boolean validateCardInformation(Card card){
        boolean allValid = true;
        // move on with the Stripe validation before moving forward

        boolean validation = card.validateCard();
        if (validation){
            allValid = true;
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
        return allValid;
    }

    private void makePayment(Card card){
        makePayment(card, false);
    }

    private void makePayment(final Card card, final boolean isSaved){
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
                        if (isSaved){
                            // this means that we need to create customer information first and then
                            // charge the customer instead of actual card
                            // Immediately charged the card
                            chargeParams = new HashMap<>();
                            chargeParams.put("description", "new credit card info");
                            chargeParams.put("card", token.getId());
                            new stripeCustomerCreate().execute(card.getLast4());
                        } else {
                            // Immediately charged the card
                            chargeParams = new HashMap<>();
                            chargeParams.put("currency", "usd");
                            chargeParams.put("card", token.getId());
                            chargeParams.put("amount", getTotalAmountsInCents(mShoppingCart.getTotal()));
                            chargeParams.put("description", "TK Payment");
                            new stripeCardPayment().execute();
                        }
                    }
                }
        );
    }

    private void makePaymentWithCustomerInformation() {
        // Retrieve customer information from saved preferences
        String customeriId = getSavedPaymentOptions();
        chargeParams = new HashMap<>();
        chargeParams.put("currency", "usd");
        chargeParams.put("customer", customeriId);
        chargeParams.put("amount", getTotalAmountsInCents(mShoppingCart.getTotal()));
        chargeParams.put("description", "TK Payment");
        new stripeCardPayment().execute();

    }

    public void flushShoppingCart(){
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
            // All payments will ended here
            finishSuccessfullPayment();
        }
    }

    class stripeCustomerCreate extends AsyncTask<String, Void, Void>{
        Customer mCustomer;
        @Override
        protected Void doInBackground(String... params) {
            try {
                mCustomer= Customer.create(chargeParams, getString(R.string.stripe_secret_key));
                // saved the customer information in saved preferneces
                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor edit = pref.edit();
                edit.putString(STRIPE_CUSTOMER_ID, mCustomer.getId());
                String last4CardDigit = params[0];
                edit.putString(STRIPE_CUSTOMER_CCNUMBER, last4CardDigit);
                edit.commit();

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
            //
            makePaymentWithCustomerInformation();
        }
    }

    private void finishSuccessfullPayment(){
        // show the paid animation and then shopping cart can be flushed
        showPaidLabelAnimation();
    }

    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        positiveButton.setEnabled(false);
        negativeButton.setEnabled(false);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);
        positiveButton.setEnabled(true);
        negativeButton.setEnabled(true);
    }

    /**
     * For stripe, it needs the amounts in cents
     * @param df
     * @return
     */
    private Integer getTotalAmountsInCents(Double df){
        // make sure that there are no decimal points
        DecimalFormat dformat = new DecimalFormat("#");
        return Integer.valueOf(dformat.format(df * 100));
    }
}
