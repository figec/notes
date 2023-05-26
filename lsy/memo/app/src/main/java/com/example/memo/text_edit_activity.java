package com.example.memo;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class text_edit_activity extends AppCompatActivity {
    private EditText editText;
    private boolean checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        editText=(EditText) findViewById(R.id.edit_text);
        
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        //设置接收类型为文本
        if (Intent.ACTION_SEND.equals(action) && type != null){
            if ("text/plain".equals(type)) {
                handlerText(intent);
            }
        }


        Intent intent_input=getIntent();
        String input_data=intent_input.getStringExtra("extra_data");
        checked = intent_input.getBooleanExtra("extra_boolean",false);
        long l_Creat_Date = getIntent().getLongExtra("CreatDate", 0);
        Date Creat_date = new Date(l_Creat_Date);//接受该项目的传入时间
        if (input_data!=null){//设置文本框初始值
            int i=input_data.indexOf(" ");
            input_data=input_data.substring(i+1);//扔掉时间变量
            editText.setText(input_data.toCharArray(),0,input_data.length());
        }
        //点击按钮后做的响应
        Button save=(Button) findViewById(R.id.title_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_output=new Intent();
                if(!editText.getText().toString().equals("")){//判断现在文本框里是否有文字
                    //String returnStr = time+" "+editText.getText().toString();
                    //boolean returnB =false; //这个不用的只是为了组成返回值
                    //Item returndata = new Item(returnStr,returnB);
                    String returndata=editText.getText().toString();
                    intent_output.putExtra("data_return",returndata);
                    intent_output.putExtra("data_return_boolean",checked);
                    intent_output.putExtra("Return_CreatDate",Creat_date.getTime());
                    setResult(RESULT_OK,intent_output);
                }
                finish();
            }
        });
    }
    private void handlerText(Intent intent) {
        String data = intent.getStringExtra(Intent.EXTRA_TEXT);
        Intent share_data=new Intent();
        editText.setText(data);
        Date create_Time=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String formatted_Creatdate = formatter.format(create_Time);//修改创建时间格式
        data=formatted_Creatdate+" "+ data;
        Item item=new Item(data,false);
        item.setCreat_date(create_Time);
        MainActivity.text_edit_list.add(item);
        MainActivity.myadapter.notifyDataSetChanged();
    }
}
