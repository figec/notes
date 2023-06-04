package com.example.memo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class history_activity extends AppCompatActivity {

    protected static Toast myToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //原来的 history_text_list 不是按时间排序的，改为按时间排序
        ArrayList<Item> temp_list = new ArrayList<>(MainActivity.history_text_list);
        Collections.sort(temp_list,new MainActivity.TimeComparator());

        ArrayList<PinnedSectionBean> real_data = PinnedSectionBean.getData(temp_list);
        history_adapter myadapter = new history_adapter(real_data, this);
        PinnedSectionListView listView = (PinnedSectionListView) findViewById(R.id.PinnedSectionListView);
        listView.setAdapter(myadapter);

    }
}