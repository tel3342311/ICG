package com.liteon.icampusguardian.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by trdcmacpro on 2017/11/23.
 */

public class Utils {

    private final static String TAG = Utils.class.getSimpleName();
    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    public boolean isURLReachable(String Url) {
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL(Url);   // Change to "http://google.com" for www  test.
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(1000);          // 1 s.
                urlc.connect();
                int responseCode = urlc.getResponseCode();
                if (responseCode == 200 || responseCode == 404) {        // 200 = "OK" code (http connection is fine).
                    Log.i(TAG, "Connect to "+ Url +" Success !");
                    return true;
                } else {
                    Log.i(TAG, "Connect to " + Url + " Fail ! Response code is " + responseCode);
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static void showErrorDialog(String message) {

        final CustomDialog dialog = new CustomDialog();
        dialog.setTitle(message);
        dialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        dialog.setBtnText(App.getContext().getString(R.string.login_ok));
        dialog.setBtnConfirm(v -> dialog.dismiss());
        dialog.show(((AppCompatActivity)App.getCurrentActivity()).getSupportFragmentManager(), "dialog_fragment");

    }

    public static void showErrorDialog(String message, View.OnClickListener listener) {

        final CustomDialog dialog = new CustomDialog();
        dialog.setTitle(message);
        dialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        dialog.setBtnText(App.getContext().getString(R.string.login_ok));
        dialog.setBtnConfirm(listener);
        dialog.show(((AppCompatActivity)App.getCurrentActivity()).getSupportFragmentManager(), "dialog_fragment");

    }
}
