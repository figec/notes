package com.example.memo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class history_activity extends AppCompatActivity {

    protected static Toast myToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView listView = (ListView) findViewById(R.id.list_view_2);
        History_Adapter history_adapter = new History_Adapter(history_activity.this,R.layout.historty_listview,MainActivity.history_text_list);
        listView.setAdapter(history_adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当 ListView 的滚动状态发生变化时被调用
                // 可以根据滚动状态进行相应的处理
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount == 0) {
                    return;
                }
                int temp = firstVisibleItem + visibleItemCount - 1;//当前滑到的记录位置
                Item item = MainActivity.history_text_list.get(temp);
                Date date = item.getCreat_date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                String formattedDate = formatter.format(date);
                if (myToast!=null) {
                    myToast.cancel();
                    myToast = Toast.makeText(history_activity.this,"当前记录时间为"+formattedDate,Toast.LENGTH_SHORT);
                } else {
                    myToast = Toast.makeText(history_activity.this,"当前记录时间为"+formattedDate,Toast.LENGTH_SHORT);
                }
                myToast.setDuration(1000);
                myToast.show();
            }
        });

    }
}