package com.example.memo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;
import java.io.Serializable;
import java.sql.SQLOutput;
import java.util.Calendar;

public class input_activity extends AppCompatActivity {
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

        editText=(EditText) findViewById(R.id.edit_text);
        Intent intent1=getIntent();
        String data=intent1.getStringExtra("extra_data");

        if (data!=null){//设置文本框初始值
            int i=data.indexOf(" ");
            data=data.substring(i+1);//扔掉时间变量
            editText.setText(data.toCharArray(),0,data.length());
        }

        Calendar calendar = Calendar.getInstance();
//获取系统的日期
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String time=year+"年"+month+"月"+day+"日"+hour+":"+minute+":"+second;
        //点击悬浮窗后做的响应
        Button save=(Button) findViewById(R.id.title_save);
        //System.out.println(inputText);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                if(!editText.getText().toString().equals("")){//判断现在文本框里是否有文字
                    String returndata=time+" "+editText.getText().toString();
                    intent.putExtra("data_return",returndata);
                    setResult(RESULT_OK,intent);
                }
                finish();
            }
        });
    }
}