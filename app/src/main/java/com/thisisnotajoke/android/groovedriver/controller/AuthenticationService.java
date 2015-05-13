package com.thisisnotajoke.android.groovedriver.controller;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.thisisnotajoke.android.groovedriver.R;
import com.thisisnotajoke.android.groovedriver.model.FacebookOAuthApi;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationService {
    public static final String REDIRECT_URI	= "https://www.facebook.com/connect/login_success.html";
    private static final String TAG = "AuthenticationService";
    private OAuthService mService;
    private TokenCallback mCallback;

    public interface TokenCallback {
        void token(String token);
    }

    public AuthenticationService(Context context) {
        mService = new ServiceBuilder()
                .provider(FacebookOAuthApi.class)
                .apiKey(context.getString(R.string.facebook_app_id))
                .apiSecret(context.getString(R.string.facebook_app_id))
                .signatureType(SignatureType.QueryString)
                .callback(REDIRECT_URI)
                .build();
    }

    public void getToken(TokenCallback cb, WebView webView) {
        mCallback = cb;
        driveWebview(webView);
    }

    public void driveWebview(final WebView webView) {
        String authUrl = mService.getAuthorizationUrl(null);
        Log.d(TAG, authUrl);
        webView.loadUrl(authUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "url is: " + url);
                if (url.startsWith(AuthenticationService.REDIRECT_URI)) {
                    Log.d(TAG, "Overriding loading");
                    // extract OAuth2 access_token appended in url
                    String fragment = Uri.parse(url).getFragment();
                    String[] args = fragment.split("&");
                    for(String arg : args) {
                        String[] split = arg.split("=", 2);
                        if(split[0].equals("access_token")) {
                            Log.d(TAG, "Found token " + split[1]);
                            mCallback.token(split[1]);
                        }
                    }
                    // don't go to redirectUri
                    return false;
                }

                // load the webpage from url (login and grant access)
                return super.shouldOverrideUrlLoading(view, url); // return false;
            }
        });
    }
}
