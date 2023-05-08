package com.example.memo;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import java.util.Calendar;

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


        Intent intent_input=getIntent();
        String input_data=intent_input.getStringExtra("extra_data");
        checked = intent_input.getBooleanExtra("extra_boolean",false);
        if (input_data!=null){//设置文本框初始值
            int i=input_data.indexOf(" ");
            input_data=input_data.substring(i+1);//扔掉时间变量
            editText.setText(input_data.toCharArray(),0,input_data.length());
        }
        //点击悬浮窗后做的响应
        Button save=(Button) findViewById(R.id.title_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                //获取系统的日期和时间
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH)+1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                String time=year+"年"+month+"月"+day+"日"+hour+":"+minute+":"+second;
                Intent intent_output=new Intent();
                if(!editText.getText().toString().equals("")){//判断现在文本框里是否有文字
                    //String returnStr = time+" "+editText.getText().toString();
                    //boolean returnB =false; //这个不用的只是为了组成返回值
                    //Item returndata = new Item(returnStr,returnB);
                    String returndata=time+" "+editText.getText().toString();
                    intent_output.putExtra("data_return",returndata);
                    intent_output.putExtra("data_return_boolean",checked);
                    setResult(RESULT_OK,intent_output);
                }
                finish();
            }
        });
    }
}
