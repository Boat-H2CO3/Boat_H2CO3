package org.koishi.launcher.h2co3.resources.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.zhanghai.android.fastscroll.FastScrollNestedScrollView;

public class H2CO3MarkdownView extends FastScrollNestedScrollView {
    private static final String EMPTY_STRING = "";
    private static final String ERROR_MESSAGE_INVALID_FILE = "Markdown文件不存在或无效";
    private static final String ERROR_MESSAGE_READ_FILE = "无法读取Markdown文件";

    private WebView webView;

    public H2CO3MarkdownView(@NonNull Context context) {
        super(context);
        init();
    }

    public H2CO3MarkdownView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public H2CO3MarkdownView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        webView = new WebView(getContext());
        webView.setLayoutParams(layoutParams);
        webView.setClickable(true);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            webSettings.setForceDark(WebSettings.FORCE_DARK_AUTO);
        }
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        linearLayout.addView(webView);
        addView(linearLayout);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void setMarkdownFilePath(String filePath) {
        loadMarkdownFile(filePath);
    }

    private void loadMarkdownFile(String filePath) {
        if (!isValidFilePath(filePath)) {
            webView.loadData(ERROR_MESSAGE_INVALID_FILE, "text/html", "UTF-8");
            return;
        }

        String markdownContent = readMarkdownFile(filePath);
        if (markdownContent != null) {
            Spanned spanned = convertMarkdownToSpanned(markdownContent);
            if (isDarkModeEnabled()) {
                setWebViewDarkMode();
            } else {
                setWebViewLightMode();
            }
            webView.loadDataWithBaseURL(null, String.valueOf(spanned), "text/html", "UTF-8", null);
        } else {
            webView.loadData(ERROR_MESSAGE_READ_FILE, "text/html", "UTF-8");
        }
    }

    private boolean isValidFilePath(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    private String readMarkdownFile(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = new FileInputStream(filePath);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Spanned convertMarkdownToSpanned(String markdownContent) {
        if (markdownContent == null) {
            return new SpannableString(EMPTY_STRING);
        }

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownContent);

        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String htmlContent = renderer.render(document);

        SpannableString spannableString = new SpannableString(htmlContent);
        makeLinksClickable(spannableString);
        return spannableString;
    }

    private void makeLinksClickable(Spannable spannable) {
        Pattern pattern = Pattern.compile("\\[([^]]+)]\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(spannable);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String linkUrl = matcher.group(2);
            if (linkUrl != null) {
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        openLink(linkUrl);
                    }
                };
                spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private void openLink(String url) {
        if (url == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        getContext().startActivity(intent);
    }

    private void setWebViewDarkMode() {
        webView.setBackgroundColor(Color.BLACK);
        webView.setWebViewClient(new DarkModeWebViewClient());
    }

    private void setWebViewLightMode() {
        webView.setBackgroundColor(Color.WHITE);
        webView.setWebViewClient(new WebViewClient());
    }

    private boolean isDarkModeEnabled() {
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private static class WebViewClient extends androidx.webkit.WebViewClientCompat {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }
    }

    private class DarkModeWebViewClient extends androidx.webkit.WebViewClientCompat {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (isDarkModeEnabled()) {
                view.evaluateJavascript("document.body.style.backgroundColor = 'black';", null);
                view.evaluateJavascript("document.body.style.color = 'white';", null);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }
    }
}