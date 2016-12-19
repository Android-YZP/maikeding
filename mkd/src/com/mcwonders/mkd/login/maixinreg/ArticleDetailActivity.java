package com.mcwonders.mkd.login.maixinreg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcwonders.mkd.R;
import com.mcwonders.mkd.config.CommonConstants;

public class ArticleDetailActivity extends Activity {
	private ImageView mIvBack;//test-jlj
	private TextView mTvTitle;//test-zj
	
	private int mArticleId;
	private WebView m_web_view;
	
	private String mActivityTitle;
	private WebView mWvArticleContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_detail);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		//文章内容
		mWvArticleContent = (WebView)findViewById(R.id.wv_article_content);
	}

	private void initData() {
		//获取文章id
		Bundle _bundle = getIntent().getExtras();
		if(_bundle!=null){
			mArticleId = _bundle.getInt("_article_id");
			mActivityTitle = _bundle.getString("_article_title");
			//改成webview
			m_web_view = mWvArticleContent;
			WebSettings _webSettings = m_web_view.getSettings();
			_webSettings.setJavaScriptEnabled(true);
			_webSettings.setDomStorageEnabled(true);
			_webSettings.setBlockNetworkImage(false);
			_webSettings.setDefaultTextEncodingName("UTF-8");
			m_web_view.loadUrl(CommonConstants.MKF_DETAIL_WAP + mArticleId);
			m_web_view.setWebViewClient(new MyWebViewClient());
		}
		//topbar标题
		mTvTitle.setText(mActivityTitle);
		
		
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ArticleDetailActivity.this.finish();
			}
		});
//
//		mWvArticleContent.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<WebView>() {
//
//			@Override
//			public void onRefresh(PullToRefreshBase<WebView> refreshView) {
//				if(mArticleId!=0){
//					m_web_view.loadUrl(CommonConstants.MKF_DETAIL_WAP + mArticleId);
//					if(mWvArticleContent!=null&&mWvArticleContent.isRefreshing()){
//						mWvArticleContent.onRefreshComplete();
//					}
//				}
//			}
//		});
		
	}

	private class MyWebViewClient extends WebViewClient{
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			m_web_view.loadData("<div style='text-align:center;'>加载失败，请检查您的网络</div>", "text/html; charset=UTF-8", null);
		}
	}
}
