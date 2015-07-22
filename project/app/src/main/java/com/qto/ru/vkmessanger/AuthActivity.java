package com.qto.ru.vkmessanger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qto.ru.vkmessanger.vk.VkUtil;

import java.net.URLEncoder;

/**
 * Используется для отображения авторизации
 */
public class AuthActivity extends Activity {
    /** id приложения зарегистрированного в VK */
    private static final String VK_APP_ID = "4999105";
    /** Ссылка для перенаправления */
    private static final String VK_REDIRECT_URL = "https://oauth.vk.com/blank.html";
    /** Окно браузера для авторизации */
    private WebView mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = (WebView)findViewById(R.id.auth);

        mAuth.getSettings().setJavaScriptEnabled(true);
        mAuth.setVerticalScrollBarEnabled(false);
        mAuth.setHorizontalScrollBarEnabled(false);

        mAuth.setWebViewClient(new VkWebViewClient());

        String url = "http://oauth.vk.com/authorize?client_id=" +
                VK_APP_ID + "&scope=friends,messages,offline&redirect_uri=" +
                URLEncoder.encode(VK_REDIRECT_URL) + "&display=mobile"
                + "&v=5.34" + "&response_type=token";
        mAuth.loadUrl(url);
        mAuth.setVisibility(View.VISIBLE);
    }

    /**
     * Используется для обработки событий браузера
     */
    class VkWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            parseUrl(url);
        }
    }

    /**
     * Получает данные из url и сохраняет их в результат
     * @param url
     * Url ссылка
     */
    private void parseUrl(String url) {
        try {
            if( url == null ) {
                return;
            }
            if( url.startsWith(VK_REDIRECT_URL) ) {
                if( !url.contains("error") ) {
                    String[] auth = VkUtil.parseRedirectUrl(url);
                    mAuth.setVisibility(View.GONE);

                    Intent intent = new Intent();
                    intent.putExtra("token", auth[0]);
                    intent.putExtra("uid", auth[1]);

                    Log.d("XX", "token: " + auth[0]);
                    Log.d("XX", "uid: " + auth[1]);

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            } else if( url.contains("error?err") ) {
                setResult(RESULT_CANCELED);
                finish();
            }
        } catch(Exception e) {
            e.printStackTrace();
            setResult(RESULT_CANCELED);
            finish();
        }
    }

}
