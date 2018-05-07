package com.payu.payuui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.payu.india.Interfaces.PaymentRelatedDetailsListener;
import com.payu.india.Model.MerchantWebService;
import com.payu.india.Model.PaymentDefaultParams;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PayuResponse;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.PostParams.MerchantWebServicePostParams;
import com.payu.india.PostParams.PaymentPostParams;
import com.payu.india.PostParams.PayuWalletPostParams;
import com.payu.india.Tasks.GetPaymentRelatedDetailsTask;

public class PayUBaseActivity extends AppCompatActivity implements View.OnClickListener, PaymentRelatedDetailsListener {

    PayuResponse mPayuResponse;
    Intent mIntent;
    Button netBankingButton;
    Button emiButton;
    Button cashCardButton;
    Button payUMoneyButton;
    Button storedCardButton;
    Button creditDebitButton;
    Button merchantPaymentButton;
    Button verifyApiButton;
    PayuConfig payuConfig;

//    PaymentDefaultParams mPaymentDefaultParams;
    PaymentParams mPaymentParams;
    PayuHashes mPayUHashes;

    Bundle bundle;

//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        startActivity(new Intent(getApplicationContext(),com.hireboss.android.roachlabs.MainActivitydrawer));
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Todo lets set the toolbar
        /*toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        // leets register the buttons
        (netBankingButton = (Button) findViewById(R.id.button_netbanking)).setOnClickListener(this);
        (emiButton = (Button) findViewById(R.id.button_emi)).setOnClickListener(this);
        (cashCardButton = (Button) findViewById(R.id.button_cash_card)).setOnClickListener(this);
        (payUMoneyButton = (Button) findViewById(R.id.button_payumoney)).setOnClickListener(this);
        (storedCardButton = (Button) findViewById(R.id.button_stored_card)).setOnClickListener(this);
        (creditDebitButton = (Button) findViewById(R.id.button_credit_debit_card)).setOnClickListener(this);
        (merchantPaymentButton = (Button) findViewById(R.id.button_merchant_payment)).setOnClickListener(this);
        (verifyApiButton = (Button) findViewById(R.id.button_verify_api)).setOnClickListener(this);

        // lets collect the details from bundle to fetch the payment related details for a merchant
        bundle = getIntent().getExtras();

        payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
        payuConfig = null != payuConfig ? payuConfig : new PayuConfig();

        // TODO add null pointer check here
//        mPaymentDefaultParams = bundle.getParcelable(PayuConstants.PAYMENT_DEFAULT_PARAMS);
        mPaymentParams = bundle.getParcelable(PayuConstants.PAYMENT_PARAMS); // Todo change the name to PAYMENT_PARAMS
        mPayUHashes = bundle.getParcelable(PayuConstants.PAYU_HASHES);

        MerchantWebService merchantWebService = new MerchantWebService();
        merchantWebService.setKey(mPaymentParams.getKey());
        merchantWebService.setCommand(PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK);
        merchantWebService.setVar1(mPaymentParams.getUserCredentials() == null ? "default" : mPaymentParams.getUserCredentials());

        // hash we have to generate


        merchantWebService.setHash(mPayUHashes.getPaymentRelatedDetailsForMobileSdkHash());

//        PostData postData = new PostParams(merchantWebService).getPostParams();

        // Dont fetch the data if calling activity is PaymentActivity

        // fetching for the first time.
        if(null == savedInstanceState){ // dont fetch the data if its been called from payment activity.
            PostData postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();
            if(postData.getCode() == PayuErrors.NO_ERROR){
                // ok we got the post params, let make an api call to payu to fetch the payment related details
                payuConfig.setData(postData.getResult());

                // lets set the visibility of progress bar
                findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                GetPaymentRelatedDetailsTask paymentRelatedDetailsForMobileSdkTask = new GetPaymentRelatedDetailsTask(this);
                paymentRelatedDetailsForMobileSdkTask.execute(payuConfig);
            } else {
                Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
                // close the progress bar
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }else if(id == R.id.action_exit){
            // Not decided yet what to do
        }else if(id == R.id.action_demo){
            // not decided yet!
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        //// TODO: 29/6/15 try to use switch case coz switch case does not work well on library projects...!!!!.

        if(id == R.id.button_netbanking){
            mIntent = new Intent(this, PayUNetBankingActivity.class);
            mIntent.putParcelableArrayListExtra(PayuConstants.NETBANKING, mPayuResponse.getNetBanks());
            launchActivity(mIntent);
        }else if(id == R.id.button_cash_card){
            mIntent = new Intent(this, PayUCashCardActivity.class);
            mIntent.putParcelableArrayListExtra(PayuConstants.CASHCARD, mPayuResponse.getCashCard());
            launchActivity(mIntent);
        }else if(id == R.id.button_emi){
            mIntent = new Intent(this, PayUEmiActivity.class);
            mIntent.putParcelableArrayListExtra(PayuConstants.EMI, mPayuResponse.getEmi());
            launchActivity(mIntent);
        }else if(id == R.id.button_credit_debit_card){
            mIntent = new Intent(this, PayUCreditDebitCardActivity.class);
            mIntent.putParcelableArrayListExtra(PayuConstants.CREDITCARD, mPayuResponse.getCreditCard());
            mIntent.putParcelableArrayListExtra(PayuConstants.DEBITCARD, mPayuResponse.getDebitCard());
            launchActivity(mIntent);
        }else if(id == R.id.button_payumoney){
            launchPayumoney();
        }else if(id == R.id.button_stored_card){
            mIntent = new Intent(this, PayUStoredCardsActivity.class);
            mIntent.putParcelableArrayListExtra(PayuConstants.STORED_CARD, mPayuResponse.getStoredCards());
            launchActivity(mIntent);
        }else if(id == R.id.button_verify_api){
            mIntent = new Intent(this, PayUVerifyApiActivity.class);
            launchActivity(mIntent);
        }
    }

    private void launchPayumoney() {
        PostData postData;

        // lets try to get the post params
        mPaymentParams.setHash(mPayUHashes.getPaymentHash());

//        postData = new PayuWalletPostParams(mPaymentDefaultParams).getPayuWalletPostParams();
        postData = new PaymentPostParams(mPaymentParams, PayuConstants.PAYU_MONEY).getPaymentPostParams();
        if(postData.getCode() == PayuErrors.NO_ERROR){
            // launch webview
            payuConfig.setData(postData.getResult());
            Intent intent = new Intent(this, PaymentsActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        }else{
            Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
        }

    }

    private void launchActivity(Intent intent) {
        intent.putExtra(PayuConstants.PAYU_HASHES, mPayUHashes);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        payuConfig.setData(null);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);

        // salt
        if(bundle.getString(PayuConstants.SALT) != null)
            intent.putExtra(PayuConstants.SALT, bundle.getString(PayuConstants.SALT));

        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
    }

    @Override
    public void onPaymentRelatedDetailsResponse(PayuResponse payuResponse) {
        mPayuResponse = payuResponse;
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        if(payuResponse.isResponseAvailable() && payuResponse.getResponseStatus().getCode() == PayuErrors.NO_ERROR){ // ok we are good to go
            Toast.makeText(this, payuResponse.getResponseStatus().getResult(), Toast.LENGTH_LONG).show();
            if(payuResponse.isStoredCardsAvailable()){
                findViewById(R.id.linear_layout_stored_card).setVisibility(View.VISIBLE);
            }
            if(payuResponse.isNetBanksAvailable()){ // okay we have net banks now.
                findViewById(R.id.linear_layout_netbanking).setVisibility(View.VISIBLE);
            }
            if(payuResponse.isCashCardAvailable()){ // we have cash card too
                findViewById(R.id.linear_layout_cash_card).setVisibility(View.VISIBLE);
            }
            if(payuResponse.isCreditCardAvailable() || payuResponse.isDebitCardAvailable()){
                findViewById(R.id.linear_layout_credit_debit_card).setVisibility(View.VISIBLE);
            }
            if(payuResponse.isEmiAvailable()){
                findViewById(R.id.linear_layout_emi).setVisibility(View.VISIBLE);
            }
            if(payuResponse.isPaisaWalletAvailable() && payuResponse.getPaisaWallet().get(0).getBankCode().contains(PayuConstants.PAYUW)) {
                findViewById(R.id.linear_layout_payumoney).setVisibility(View.VISIBLE);
            }
        }else{
            Toast.makeText(this, "Something went wrong : " + payuResponse.getResponseStatus().getResult(), Toast.LENGTH_LONG).show();
        }

        // no mater what response i get just show this button, so that we can go further.
        findViewById(R.id.linear_layout_verify_api).setVisibility(View.VISIBLE);
    }
}
