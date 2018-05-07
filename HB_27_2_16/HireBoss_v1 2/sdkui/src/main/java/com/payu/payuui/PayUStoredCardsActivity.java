package com.payu.payuui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.payu.india.Interfaces.DeleteCardApiListener;
import com.payu.india.Interfaces.GetStoredCardApiListener;
import com.payu.india.Model.MerchantWebService;
import com.payu.india.Model.PaymentDefaultParams;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PayuResponse;
import com.payu.india.Model.PostData;
import com.payu.india.Model.StoredCard;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.Payu.PayuUtils;
import com.payu.india.PostParams.MerchantWebServicePostParams;
import com.payu.india.PostParams.PaymentPostParams;
import com.payu.india.PostParams.StoredCardPostParams;
import com.payu.india.Tasks.DeleteCardTask;
import com.payu.india.Tasks.GetStoredCardTask;

import java.util.ArrayList;


public class PayUStoredCardsActivity extends AppCompatActivity implements DeleteCardApiListener, GetStoredCardApiListener {

    private ListView storedCardListView;
    private PayUStoredCardsAdapter payUStoredCardsAdapter;
    private Bundle bundle;
    private ArrayList<StoredCard> storedCardList;

    private PayuHashes payuHashes;
    private PaymentParams mPaymentParams;
    private Toolbar toolbar;
    private TextView amountTextView;
    private TextView transactionIdTextView;

    private PayuConfig payuConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cards);

        // TODO lets set the toolbar
        /*toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        storedCardListView = (ListView) findViewById(R.id.list_view_user_card);

        // lets get the required data form bundle
        bundle = getIntent().getExtras();


        if (bundle != null && bundle.getParcelableArrayList(PayuConstants.STORED_CARD) != null) {
            storedCardList = new ArrayList<StoredCard>();
            storedCardList = bundle.getParcelableArrayList(PayuConstants.STORED_CARD);
            payUStoredCardsAdapter = new PayUStoredCardsAdapter(this, storedCardList);
            storedCardListView.setAdapter(payUStoredCardsAdapter);

        } else {
            // we gotta fetch data from server
            Toast.makeText(this, "Could not get user card list from the previous activity", Toast.LENGTH_LONG).show();
        }

        payuHashes = bundle.getParcelable(PayuConstants.PAYU_HASHES);
        mPaymentParams = bundle.getParcelable(PayuConstants.PAYMENT_PARAMS);
        payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
        payuConfig = null != payuConfig ? payuConfig : new PayuConfig();

        (amountTextView = (TextView) findViewById(R.id.text_view_amount)).setText(PayuConstants.AMOUNT + ": " + mPaymentParams.getAmount());
        (transactionIdTextView = (TextView) findViewById(R.id.text_view_transaction_id)).setText(PayuConstants.TXNID + ": " + mPaymentParams.getTxnId());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_cards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteCardApiResponse(PayuResponse payuResponse) {
        if (payuResponse.isResponseAvailable()) {
            Toast.makeText(this, payuResponse.getResponseStatus().getResult(), Toast.LENGTH_LONG).show();
        }
        if (payuResponse.getResponseStatus().getCode() == PayuErrors.NO_ERROR) {
            // there is no error, lets fetch te cards list.

            MerchantWebService merchantWebService = new MerchantWebService();
            merchantWebService.setKey(mPaymentParams.getKey());
            merchantWebService.setCommand(PayuConstants.GET_USER_CARDS);
            merchantWebService.setVar1(mPaymentParams.getUserCredentials());
            merchantWebService.setHash(payuHashes.getStoredCardsHash());

            PostData postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();

            if (postData.getCode() == PayuErrors.NO_ERROR) {
                // ok we got the post params, let make an api call to payu to fetch the payment related details

                payuConfig.setData(postData.getResult());
                payuConfig.setEnvironment(payuConfig.getEnvironment());

                GetStoredCardTask getStoredCardTask = new GetStoredCardTask(this);
                getStoredCardTask.execute(payuConfig);
            } else {
                Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onGetStoredCardApiResponse(PayuResponse payuResponse) {
        Toast.makeText(this, payuResponse.getResponseStatus().getResult(), Toast.LENGTH_LONG).show();
        payUStoredCardsAdapter = null;
        payUStoredCardsAdapter = new PayUStoredCardsAdapter(this, storedCardList=payuResponse.getStoredCards());
        storedCardListView.setAdapter(payUStoredCardsAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
    }


    //Adaptor
    public class PayUStoredCardsAdapter extends BaseAdapter { // todo rename to storedcardAdapter

        private ArrayList<StoredCard> mStoredCards;
        private Context mContext;

        private PayuUtils payuUtils;

        public PayUStoredCardsAdapter(Context context, ArrayList<StoredCard> StoredCards) {
            mContext = context;
            mStoredCards = StoredCards;
            payuUtils = new PayuUtils();
        }

        private void viewHolder(ViewHolder holder, int position) {
//            holder.setPosition(position);
            String issuer = payuUtils.getIssuer(mStoredCards.get(position).getCardBin());
            switch (issuer) {
                case PayuConstants.VISA:
                    holder.cardIconImageView.setImageResource(R.mipmap.visa);
                    break;
                case PayuConstants.LASER:
                    holder.cardIconImageView.setImageResource(R.mipmap.laser);
                    break;
                case PayuConstants.DISCOVER:
                    holder.cardIconImageView.setImageResource(R.mipmap.discover);
                    break;
                case PayuConstants.MAES:
                    holder.cardIconImageView.setImageResource(R.mipmap.maestro);
                    break;
                case PayuConstants.MAST:
                    holder.cardIconImageView.setImageResource(R.mipmap.master);
                    break;
                case PayuConstants.AMEX:
                    holder.cardIconImageView.setImageResource(R.mipmap.amex);
                    break;
                case PayuConstants.DINR:
                    holder.cardIconImageView.setImageResource(R.mipmap.diner);
                    break;
                case PayuConstants.JCB:
                    holder.cardIconImageView.setImageResource(R.mipmap.jcb);
                    break;
                case PayuConstants.SMAE:
                    holder.cardIconImageView.setImageResource(R.mipmap.maestro);
                    break;
                default:
                    holder.cardIconImageView.setImageResource(R.mipmap.card);
                    break;

            }

            holder.cardNumberTextView.setText(mStoredCards.get(position).getMaskedCardNumber());
            holder.cardNameTextView.setText(mStoredCards.get(position).getCardName());
        }

        @Override
        public int getCount() {
            if(mStoredCards != null)
                return mStoredCards.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int index) {
            if(null != mStoredCards) return mStoredCards.get(index);
            else return 0;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.user_card_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.setPosition(position);
            viewHolder(holder, position);

            return convertView;
        }


        class ViewHolder implements View.OnClickListener {

            int position; //for index

            ImageView cardIconImageView;
            ImageView cardTrashImageView;
            TextView cardNumberTextView;
            TextView cardNameTextView;
            LinearLayout cvvPayNowLinearLayout;
            LinearLayout rowLinearLayout;
            Button paynNowButton;
            EditText cvvEditText;

            public void setPosition(int position) {
                this.position = position;
            }

            public ViewHolder(View itemView) {

                cardIconImageView = (ImageView) itemView.findViewById(R.id.image_view_card_icon);
                cardNumberTextView = (TextView) itemView.findViewById(R.id.text_view_card_number);
                cardTrashImageView = (ImageView) itemView.findViewById(R.id.image_view_card_trash);
                cardNameTextView = (TextView) itemView.findViewById(R.id.text_view_card_name);
                rowLinearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_row);
                cvvPayNowLinearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_cvv_paynow);
                paynNowButton = (Button) itemView.findViewById(R.id.button_pay_now);
                cvvEditText = (EditText) itemView.findViewById(R.id.edit_text_cvv);

                // lets restrict the user not from typing alpha characters.

                cardTrashImageView.setOnClickListener(this);
                cvvPayNowLinearLayout.setOnClickListener(this);
                rowLinearLayout.setOnClickListener(this);
                paynNowButton.setOnClickListener(this);

                // we need to set the length of cvv field according to the card number

                cvvEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        /// lets enable or disable the pay now button according to the cvv and card number
                        cvvEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(payuUtils.getIssuer(mStoredCards.get(position).getCardBin()).contentEquals(PayuConstants.AMEX) ? 4 : 3)});
                        if (payuUtils.validateCvv(mStoredCards.get(position).getCardBin(), s.toString())) {
                            paynNowButton.setEnabled(true);
                        } else {
                            paynNowButton.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            @Override
            public void onClick(View view) {
                if (cvvPayNowLinearLayout.getVisibility() == View.VISIBLE) {
                    cvvPayNowLinearLayout.setVisibility(View.GONE);
                } else {
                    cvvPayNowLinearLayout.setVisibility(View.VISIBLE);
                }
                if (view.getId() == R.id.image_view_card_trash) {
                    deleteCard(storedCardList.get(position));
                } else if (view.getId() == R.id.button_pay_now) {
                    makePayment(storedCardList.get(position), cvvEditText.getText().toString());
                }
            }
        }
    }

    private void makePayment(StoredCard storedCard, String cvv) {
        PostData postData = new PostData();
        // lets try to get the post params
        postData = null;
        storedCard.setCvv(cvv); // make sure that you set the cvv also
        mPaymentParams.setHash(payuHashes.getPaymentHash()); // make sure that you set payment hash
        mPaymentParams.setCardToken(storedCard.getCardToken());
        mPaymentParams.setCvv(cvv);
        mPaymentParams.setNameOnCard(storedCard.getNameOnCard());
        mPaymentParams.setCardName(storedCard.getCardName());
        mPaymentParams.setExpiryMonth(storedCard.getExpiryMonth());
        mPaymentParams.setExpiryYear(storedCard.getExpiryYear());

        postData = new PaymentPostParams(mPaymentParams, PayuConstants.CC).getPaymentPostParams();

        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(postData.getResult());
            Intent intent = new Intent(this, PaymentsActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        } else {
            Toast.makeText(this, postData.getResult(), Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteCard(StoredCard storedCard) {
        MerchantWebService merchantWebService = new MerchantWebService();
        merchantWebService.setKey(mPaymentParams.getKey());
        merchantWebService.setCommand(PayuConstants.DELETE_USER_CARD);
        merchantWebService.setVar1(mPaymentParams.getUserCredentials());
        merchantWebService.setVar2(storedCard.getCardToken());
        merchantWebService.setHash(payuHashes.getDeleteCardHash());

        PostData postData = null;
        postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();

        if (postData.getCode() == PayuErrors.NO_ERROR) {
            // ok we got the post params, let make an api call to payu to fetch
            // the payment related details
            payuConfig.setData(postData.getResult());
            payuConfig.setEnvironment(payuConfig.getEnvironment());

            DeleteCardTask deleteCardTask = new DeleteCardTask(this);
            deleteCardTask.execute(payuConfig);
        } else {
            Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
        }
    }
}


