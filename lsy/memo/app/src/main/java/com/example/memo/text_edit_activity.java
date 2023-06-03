package com.example.memo;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class text_edit_activity extends AppCompatActivity {
    private EditText editText;
    private boolean checked;

    private int style;



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
        style = intent_input.getIntExtra("extra_style",Item.Default);
        if (input_data!=null){//设置文本框初始值
            int i=input_data.indexOf(" ");
            input_data=input_data.substring(i+1);//扔掉时间变量
            editText.setText(input_data.toCharArray(),0,input_data.length());
        }

        //Toast.makeText(getApplicationContext(),"style值为："+String.valueOf(style),Toast.LENGTH_SHORT).show();


        // 设置菜单按钮初始值
        Button radio_bn = (Button)findViewById(R.id.radio_button);
        switch (style){
            case Item.Important_Urgent:
                radio_bn.setText("重要且紧急 ▼");
                break;
            case Item.Important_NUrgent:
                radio_bn.setText("重要非紧急 ▼");
                break;
            case Item.Urgent_NImportant:
                radio_bn.setText("紧急非重要 ▼");
                break;
            case Item.NImportant_NUrgent:
                radio_bn.setText("非重要非紧急 ▼");
                break;
            default:

                break;
        }




        // 索引按钮响应
        Button index=(Button) findViewById(R.id.radio_button);
        index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(index);

            }
        });



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
                    intent_output.putExtra("data_return_style",style);
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formatted_Creatdate = formatter.format(create_Time);//修改创建时间格式
        data=formatted_Creatdate+" "+ data;
        Item item=new Item(data,false);
        item.setCreat_date(create_Time);
        MainActivity.text_edit_list.add(item);
        MainActivity.myadapter.notifyDataSetChanged();
    }

    //弹出菜单的按钮框
    private void showPopupMenu(final View view) {
        Button bn = (Button)findViewById(R.id.radio_button);
        final PopupMenu popupMenu = new PopupMenu(this,view);
        //menu 布局
        popupMenu.getMenuInflater().inflate(R.menu.radio_select_menu,popupMenu.getMenu());
        //点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.radio_all:
                        bn.setText("默认 ▼");
                        style = Item.Default;
                        break;
                    case R.id.radio_iu:
                        bn.setText("重要且紧急 ▼");
                        style = Item.Important_Urgent;
                        break;
                    case R.id.radio_inu:
                        bn.setText("重要非紧急 ▼");
                        style = Item.Important_NUrgent;
                        break;
                    case R.id.radio_niu:
                        bn.setText("紧急非重要 ▼");
                        style = Item.Urgent_NImportant;
                        break;
                    case R.id.radio_ninu:
                        bn.setText("非重要非紧急 ▼");
                        style = Item.NImportant_NUrgent;

                        break;
                }
                /*
                // 修改listview的显示
                ListView listView=(ListView) findViewById(R.id.list_view);
                myadapter=new Listview_Adapter(MainActivity.this, R.layout.check_string,current_list);
                listView.setAdapter(myadapter);
                myadapter.notifyDataSetChanged();

                 */

                return false;
            }
        });

        //显示菜单，不要少了这一步
        popupMenu.show();
    }
}
