package com.stfalcon.call;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by root on 05.06.15.
 */
public class CallReceiver extends BroadcastReceiver {
    private static WindowManager windowManager;
    private static ViewGroup windowLayout;
    private static String phoneNumber, phoneName;
    private static String time;
    private static String date;
    private String DATE_TEMPLATE = "%s/%s/%s";
    private static boolean out;
    DBHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            phoneNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            getPhoneName(context, phoneNumber);
            Log.v("Debugg", "Набирає  " + phoneNumber);
            out = true;
            setTime();

        } else if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                getPhoneName(context, phoneNumber);
                Log.v("Debugg", "Звонить  " + phoneNumber);
                out = false;
                setTime();

            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                if (out) {
                    showWindow(context, "Вихідний");
                } else {
                    showWindow(context, "Вхідний");
                }
            }
        }
    }

    private void showWindow(Context context, final String state) {
        initializationWindow(context);
        initializationView(context, state);

    }

    private void closeWindow() {
        if (windowLayout != null) {
            windowManager.removeView(windowLayout);
            windowLayout = null;
        }
    }

    private void getPhoneName(Context context, String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, phoneNumber, null, null);
        if (cursor.moveToFirst()) {
            phoneName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        cursor.close();


    }

    private void setDataToDatabase(Context context, String state, String text) {
        dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        cv.put("phone", phoneNumber);
        cv.put("name", phoneName);
        cv.put("time", time);
        cv.put("state", state);
        cv.put("text", text);
        cv.put("date", date);
        db.insert(DBHelper.NAME_TABLE, null, cv);
    }

    private void setTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
        Calendar rightNow = Calendar.getInstance();
        time = sdf.format(rightNow.getTime());
        date = String.format(DATE_TEMPLATE,
                rightNow.get(Calendar.DAY_OF_MONTH),
                rightNow.get(Calendar.MONTH) + 1,
                rightNow.get(Calendar.YEAR));
    }

    private void initializationWindow(Context context) {

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;

        windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info_window, null);

        windowManager.addView(windowLayout, params);
    }

    private void initializationView(final Context context, final String state) {

        final ViewHolder holder = new ViewHolder();
        holder.textViewState = (TextView) windowLayout.findViewById(R.id.textViewState);
        holder.textViewName = (TextView) windowLayout.findViewById(R.id.textViewName);
        holder.textViewTime = (TextView) windowLayout.findViewById(R.id.textViewTime);
        holder.editText = (EditText) windowLayout.findViewById(R.id.editText);
        holder.buttonClose = (Button) windowLayout.findViewById(R.id.buttonClose);
        holder.buttonOk = (Button) windowLayout.findViewById(R.id.buttonOk);
        holder.textViewState.setText(state);
        holder.textViewName.setText(phoneName);
        holder.textViewTime.setText(time);
        holder.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWindow();
            }
        });
        holder.buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataToDatabase(context, state, holder.editText.getText().toString());
                closeWindow();
            }
        });

    }

    public static class ViewHolder {
        public TextView textViewState;
        public TextView textViewName;
        public TextView textViewTime;
        public EditText editText;
        public Button buttonClose;
        public Button buttonOk;
    }
}