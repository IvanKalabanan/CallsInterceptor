package com.stfalcon.call.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.TextView;

import com.stfalcon.call.R;
import com.stfalcon.call.StaticClass;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class InfoActivity extends Activity {

    public int id;
    TextView textViewName, textViewTime, textViewState, textViewText;
    ImageView userPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initialization(savedInstanceState);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);

        setTextToView();
    }

    public void initialization(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewState = (TextView) findViewById(R.id.textViewState);
        textViewText = (TextView) findViewById(R.id.textViewText);
        userPhoto = (ImageView) findViewById(R.id.imageView);

    }

    public void setTextToView() {
        textViewName.setText(StaticClass.arrName.get(id));
        textViewTime.setText(StaticClass.arrTime.get(id));
        textViewState.setText(StaticClass.arrState.get(id));
        textViewText.setText(StaticClass.arrText.get(id));
        userPhoto.setImageURI(getUserImageUri());
    }

    public Uri getUserImageUri() {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(StaticClass.arrPhone.get(id)));
        Uri uriImage = null;
        Cursor cursor = this.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI}, StaticClass.arrPhone.get(id), null, null);
        if (cursor.moveToFirst()) {
            try {
                uriImage = Uri.parse(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI)));
            } catch (NullPointerException e) {
                uriImage = Uri.parse("android.resource://com.stfalcon.call/drawable/noavatar");
                try {
                    InputStream stream = getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException ee) {
                    ee.printStackTrace();
                }
            }

        }
        cursor.close();
        return uriImage;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
