package com.stfalcon.call.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stfalcon.call.Calls;
import com.stfalcon.call.CustomAdapter;
import com.stfalcon.call.DBHelper;
import com.stfalcon.call.R;
import com.stfalcon.call.StaticClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    private CustomAdapter mAdapter;
    DBHelper dbHelper;
    SwipeRefreshLayout swipeLayout;
    ListView l;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        initialization(savedInstanceState);
        readFromDatabase();
        setListView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StaticClass.clearAll();
    }

    private boolean checkArray() {
        return StaticClass.arrName.isEmpty();
    }

    private void initialization(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        dbHelper = new DBHelper(this);


        l = (ListView) findViewById(R.id.list);
        mAdapter = new CustomAdapter(this);

        l.setAdapter(mAdapter);


        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                if (mAdapter.getItemViewType(position) != CustomAdapter.TYPE_SEPARATOR) {
                    Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                    intent.putExtra("id", mAdapter.getPosition(position));
                    startActivity(intent);
                }
            }

        });

        l.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = 0;
                if (l == null || l.getChildCount() == 0) {
                    topRowVerticalPosition = 0;
                } else {
                    topRowVerticalPosition = l.getChildAt(0).getTop();
                }
                swipeLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });


    }


    @Override
    public void onRefresh() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
                StaticClass.clearAll();
                mAdapter.clearData();
                mAdapter.notifyDataSetChanged();
                readFromDatabase();
                setListView();
            }
        });

    }

    private void setListView() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            for (int i = 0; i < StaticClass.arrName.size(); i++) {

                Date newData = formatter.parse(StaticClass.arrDate.get(i));
                Date oldData;
                if (i == 0) {
                    oldData = formatter.parse(StaticClass.arrDate.get(i));
                } else {
                    oldData = formatter.parse(StaticClass.arrDate.get(i - 1));
                }
                if (newData.compareTo(oldData) < 0) {
                    mAdapter.addSectionHeaderItem(new Calls(StaticClass.arrDate.get(i), "", ""));
                }
                mAdapter.addItem(new Calls(StaticClass.arrName.get(i),
                        StaticClass.arrTime.get(i),
                        StaticClass.arrState.get(i)));


            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void readFromDatabase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(DBHelper.NAME_TABLE, null, null, null, null, null, null);

        if (c.moveToLast()) {

            int phone = c.getColumnIndex("phone");
            int text = c.getColumnIndex("text");
            int time = c.getColumnIndex("time");
            int state = c.getColumnIndex("state");
            int date = c.getColumnIndex("date");
            int name = c.getColumnIndex("name");

            do {
                StaticClass.arrPhone.add(c.getString(phone));
                StaticClass.arrText.add(c.getString(text));
                StaticClass.arrTime.add(c.getString(time));
                StaticClass.arrState.add(c.getString(state));
                StaticClass.arrDate.add(c.getString(date));
                StaticClass.arrName.add(c.getString(name));
            } while (c.moveToPrevious());
        } else
            Log.d("lak", "0 rows");
        c.close();
    }

}
