package com.mzs.market.activity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.mzs.market.R;
import com.mzs.market.bean.FeedbackBean;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.net.GsonRequest;
import com.mzs.market.net.MzsVolley;
import com.mzs.market.net.ParamsList;
import com.mzs.market.net.RequestParams;
import com.mzs.market.net.UrlUtils;
import com.mzs.market.utils.UIUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class FeedbackActivity extends BaseActivity{
	private EditText 	mContent 	 = null;
	private EditText	mQQNumber 	 = null;
	private EditText 	mPhoneNumber = null;
	private Spinner		mSpinnerCate = null;
	private Button		mBtnClear 	 = null;
	private Button		mBtnSubmit   = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		
		mContent = (EditText) findViewById(R.id.content);
		mQQNumber = (EditText) findViewById(R.id.qqNumber);
		mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
		mSpinnerCate = (Spinner) findViewById(R.id.category);
		mBtnClear	= (Button) findViewById(R.id.clear);
		mBtnSubmit 	= (Button) findViewById(R.id.submit);
	
		mBtnClear.setOnClickListener(BtnListener);
		mBtnSubmit.setOnClickListener(BtnListener);
	}
	
	
	private View.OnClickListener BtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.clear:
				mContent.setText("");
				mQQNumber.setText("");
				mPhoneNumber.setText("");
				mSpinnerCate.setSelection(0);
				break;
			case R.id.submit:
				String strContent = mContent.getText().toString();
				String strQQNumber = mQQNumber.getText().toString();
				String strPhoneNumber = mPhoneNumber.getText().toString();
				String strCate = mSpinnerCate.getSelectedItem().toString();
				
				
				if(strContent.isEmpty() && strQQNumber.isEmpty() && strPhoneNumber.isEmpty()){
					Toast.makeText(getApplicationContext(), "你输入的信息过少，请再输入的详细些", Toast.LENGTH_SHORT).show(); 
				}else {
			        ParamsList params = new ParamsList();
			        params.add(new RequestParams("type", "1"));
			        params.add(new RequestParams("content",strContent));
			        params.add(new RequestParams("qq", strQQNumber));
			        params.add(new RequestParams("phone", strPhoneNumber));
			        params.add(new RequestParams("cate", strCate));
			        final String url = UrlUtils.encodeUrl(MzsConstant.FD_FEEDBACK, params);
			        Log.d("HttpClient", "url="+url);
			        Response.Listener<FeedbackBean> responseListener = new Response.Listener<FeedbackBean>() {
			            @Override
			            public void onResponse(FeedbackBean response) {
			            	if(response!=null){
			            		if(response.errorCode==0){
			            			UIUtils.showShortToast("感谢反馈");
			            		}else{
			            			UIUtils.showShortToast("发送失败，请重试");
			            		}
			            	}
			            }
			        };
			        Response.ErrorListener errorListener = new Response.ErrorListener() {
			            @Override
			            public void onErrorResponse(VolleyError error) {
			                Log.e("wh", "get choice list error : " + error.toString());
			            }
			        };
			        GsonRequest<FeedbackBean> request = new GsonRequest<FeedbackBean>(Method.GET, url,
			        		FeedbackBean.class, responseListener, errorListener);
			        MzsVolley.getRequestQueue().add(request);
				}
						
				break;
			}
		}
	};
}


