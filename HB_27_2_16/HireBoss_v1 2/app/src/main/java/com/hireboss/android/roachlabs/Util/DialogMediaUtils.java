package com.hireboss.android.roachlabs.Util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hireboss.android.roachlabs.DialogMediaActivity;
import com.hireboss.android.roachlabs.MainActivity;
import com.hireboss.android.roachlabs.MainActivitydrawer;
import com.hireboss.android.roachlabs.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DialogMediaUtils {

	private DialogMediaActivity mDialogMediaActivity;
	private Dialog mDialog;

	private EditText mDialogComment;
	private TextView mOKButton;
	private TextView mCancelButton;
//	private ImageView mDialogImage;
	private TextView mName;
	//private EditText mComment;
	private RatingBar mRatingBar;
	SharedPreferences spac,spreq;
	String account_id,request_id;
	Context cont;

	public DialogMediaUtils(DialogMediaActivity mDialogMediaActivity) {
		this.mDialogMediaActivity = mDialogMediaActivity;
	}

	public void showDialog(Context context) {
		cont=context;
		if (mDialog == null) {
			mDialog = new Dialog(mDialogMediaActivity,
					R.style.CustomDialogTheme);
		}
		spac=context.getSharedPreferences("MyData",Context.MODE_PRIVATE);
		account_id=spac.getString("account_id", "nodata");
		spreq=context.getSharedPreferences("LOCATION", Context.MODE_APPEND);
		request_id=spreq.getString("request_id", "nodata");

		mDialog.setContentView(R.layout.dialog_media);

		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();

		mOKButton = (TextView) mDialog.findViewById(R.id.dialog_media_ok);
		//mCancelButton = (TextView) mDialog.findViewById(R.id.dialog_media_cancel);
		mName = (TextView) mDialog.findViewById(R.id.dialog_media_ok);
//		mComment = (EditText) mDialog.findViewById(R.id.dialog_media_comment);
//		mDialogImage = (ImageView) mDialog.findViewById(R.id.dialog_media_image);
		mRatingBar = (RatingBar) mDialog.findViewById(R.id.dialog_media_ratingBar);
//		mDialogComment = (EditText) mDialog.findViewById(R.id.dialog_media_comment);

//		ImageUtil.displayRoundImage(mDialogImage, "http://pengaja.com/uiapptemplate/newphotos/profileimages/0.jpg", null);

		mRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
										boolean fromUser) {
				// TODO Auto-generated method stub
				Toast.makeText(mDialogMediaActivity, Float.toString(rating), Toast.LENGTH_SHORT).show();
			}
		});

		initDialogButtons(context);
	}

	private void initDialogButtons(final Context context) {


		mOKButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Toast.makeText(mDialogMediaActivity, "Rate: " + Float.toString(mRatingBar.getRating()) , Toast.LENGTH_SHORT).show();
				WebRequest wr=new WebRequest();
				wr.execute("http://www.hireboss.co/webservices/request_rating.php?account_id=" + account_id + "&request_id=" + request_id + "&user_type=users&rating_star=" + Float.toString(mRatingBar.getRating()));

				}

		});

//		mCancelButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View view) {
//				mDialog.dismiss();
//			}
//		});
	}

	public void dismissDialog() {
		mDialog.dismiss();
	}

	public class WebRequest extends AsyncTask<String,String,String> {
		String data;
		String toast;
		String success,message,account_id;
		@Override
		protected String doInBackground(String... params) {

			HttpURLConnection connection = null;
			BufferedReader br = null;
			StringBuffer sb = null;
			data=null;
			try {
				URL url;
				url = new URL(params[0]);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				// response received in input stream

				InputStream is = connection.getInputStream();

				br = new BufferedReader(new InputStreamReader(is));
				//reading o/p line by line

				sb = new StringBuffer();

				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				data=sb.toString();

				//json parsing when array based
            /*
            JSONObject jobj=new JSONObject(data);
            JSONArray jarr=jobj.getJSONArray("");//array name
            JSONObject finaljobj=jarr.getJSONObject(0);//

            String key1=finaljobj.getString("key1")//keys retrieval
            String key2=finaljobj.getString("key2")//keys retrieval
            */
				//json non array based
				JSONObject jobj=new JSONObject(data);
				success=jobj.getString("success");
				message=jobj.getString("message");
				Log.d("message:", message);





				return data;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				if (connection != null)
					connection.disconnect();
				try {
					if (br != null)
						br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}



			return null;
		}
		private ProgressDialog pdia;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			//  textv.append("\n" + s);

			String path="/data/data/com.hireboss.android.roachlabs/shared_prefs/FINISHTASK.xml";
			File file = new File ( path );
			file.delete();
			String path2="/data/data/com.hireboss.android.roachlabs/shared_prefs/DETAILTASK.xml";
			File file2 = new File ( path2 );
			file2.delete() ;
			String path1="/data/data/com.hireboss.android.roachlabs/shared_prefs/FINISHTASKDETAILS.xml";
			File file1 = new File ( path1 );
			file1.delete();


			Intent viewIntent = new Intent(cont,MainActivitydrawer.class);
			cont.startActivity(viewIntent);






		}

	}
}
