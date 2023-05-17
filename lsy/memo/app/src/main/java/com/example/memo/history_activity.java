package com.example.memo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class history_activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView listView = (ListView) findViewById(R.id.list_view_2);
        Listview_Adapter history_adapter=new Listview_Adapter(history_activity.this,R.layout.check_string,MainActivity.history_text_list);
        listView.setAdapter(history_adapter);
    }
}