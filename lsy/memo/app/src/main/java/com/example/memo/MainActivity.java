package com.example.memo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {



    protected static ArrayList<Item> text_edit_list=new ArrayList<Item>();

    protected static Listview_Adapter myadapter;

    protected static ArrayList<Item> history_text_list = new ArrayList<Item>();

    protected static int removed_cnt = 0;

    public static class ItemComparator implements Comparator<Item> {


        @Override
        public int compare(Item item1, Item item2) {
            // 按照创建时间从后到先排序
            return item2.getCreat_date().compareTo(item1.getCreat_date());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //悬浮窗按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//点击悬浮窗按钮进入下一个页面
                String input_data=null;
                Intent intent_fab=new Intent(MainActivity.this,text_edit_activity.class);
                intent_fab.putExtra("extra_data",input_data);
                intent_fab.putExtra("extra_boolean",false);
                intent_fab.putExtra("extra_style",Item.Default);
                startActivityForResult(intent_fab,1);//打开下一个界面并传入唯一标识符1
            }
        });
        fab.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            //设置悬浮窗长按监听函数
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(1,0,0,"添加笔记");//与单击时相同
                contextMenu.add(1,1,0,"历史笔记");//进入隐式文档
            }
        });


        // 索引按钮响应
        Button index=(Button) findViewById(R.id.select_button);
        index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(index);

            }
        });


    }

    //弹出菜单的按钮框
    private void showPopupMenu(final View view) {
        Button bn = (Button)findViewById(R.id.select_button);
        final PopupMenu popupMenu = new PopupMenu(this,view);
        //menu 布局
        popupMenu.getMenuInflater().inflate(R.menu.select_menu,popupMenu.getMenu());
        //点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.all:
                        bn.setText("全部 ▼");
                        Toast.makeText(view.getContext(),"1",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.iu:
                        bn.setText("重要且紧急 ▼");
                        Toast.makeText(view.getContext(),"2",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.inu:
                        bn.setText("重要非紧急 ▼");
                        Toast.makeText(view.getContext(),"3",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.niu:
                        bn.setText("紧急非重要 ▼");
                        Toast.makeText(view.getContext(),"4",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.ninu:
                        bn.setText("非重要非紧急 ▼");
                        popupMenu.dismiss();
                        break;
                }
                return false;
            }
        });
        //关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(view.getContext(),"close",Toast.LENGTH_SHORT).show();
            }
        });
        //显示菜单，不要少了这一步
        popupMenu.show();
    }


    @SuppressLint("MissingSuperCall")
    @Override//重写onActivityResult方法接收保存文本框的返回值
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    String returndata=data.getStringExtra("data_return");//获得用户输入的数据
                    Date creat_date=new Date();//获取创建时间
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                    String formatted_Creatdate = formatter.format(creat_date);//修改创建时间格式
                    returndata=formatted_Creatdate+" "+returndata;//给回传文本加上时间内容
                    int style = data.getIntExtra("data_return_style",Item.Default); //获取单选框编号
                    Item item = new Item(returndata,false); //这里是新建后的返回，直接用false
                    item.setCreat_date(creat_date);//传入创建时间
                    item.setStyle(style);
                    text_edit_list.add(item);
                    history_text_list.add(item);
                    Collections.sort(text_edit_list, new ItemComparator());
                    Collections.sort(history_text_list,new ItemComparator());
                    ListView listView=(ListView) findViewById(R.id.list_view);
                    myadapter=new Listview_Adapter(MainActivity.this, R.layout.check_string,text_edit_list);
                    listView.setAdapter(myadapter);
                    myadapter.notifyDataSetChanged();
                    //监听点击事件
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String input_data= text_edit_list.get(i).getContent();
                            Intent intent_list=new Intent(MainActivity.this,text_edit_activity.class);
                            intent_list.putExtra("extra_data",input_data);
                            intent_list.putExtra("extra_boolean",text_edit_list.get(i).getChecked());
                            intent_list.putExtra("CreatDate",text_edit_list.get(i).getCreat_date().getTime());//传入该项目的创建时间
                            intent_list.putExtra("extra_style",text_edit_list.get(i).getStyle());
                            text_edit_list.remove(i);
                            history_text_list.remove(i+removed_cnt);
                            startActivityForResult(intent_list,2);//打开下一个界面并传入唯一标识符2
                        }
                    });
                    //长安点击事件
                    listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                            menu.setHeaderTitle("长按菜单-ContextMenu");
                            menu.add(0, 0, 0, "删除");//组，id,顺序，内容
                            menu.add(0, 1, 0, "其他功能");
                        }
                    });

                }
                break;
            case 2:
                if(resultCode==RESULT_OK){
                    // 这里是有值的返回，所以需要同时去确定一下返回值。具体逻辑先不管先
                    String returndata=data.getStringExtra("data_return");
                    boolean checked = data.getBooleanExtra("data_return_boolean",false);
                    long l_Creat_Date = data.getLongExtra("Return_CreatDate", 0);
                    Date creat_date = new Date(l_Creat_Date);//接受原来条目的创建时间
                    Date modify_date=new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                    String formatted_ModifyDate = formatter.format(modify_date);
                    returndata=formatted_ModifyDate+" "+returndata;
                    int style = data.getIntExtra("data_return_style",Item.Default); //获取单选框编号
                    Item item = new Item(returndata,checked);
                    item.setStyle(style);
                    item.setModify_date(modify_date);//设置最新修改时间
                    item.setCreat_date(creat_date);//新条目原来未修改条目的创建时间
                    text_edit_list.add(item);
                    history_text_list.add(item);
                    Collections.sort(text_edit_list, new ItemComparator());//根据创建时间排序
                    Collections.sort(history_text_list,new ItemComparator());//根据创建时间排序
                    ListView listView=(ListView) findViewById(R.id.list_view);
                    myadapter=new Listview_Adapter(MainActivity.this, R.layout.check_string,text_edit_list);
                    listView.setAdapter(myadapter);
//                    }
                }
            default:
        }
    }
    //长按菜单响应函数
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int item_id=item.getItemId(), item_group=item.getGroupId();
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item_group) {
            case 0:
                if (item_id==0) {
                    text_edit_list.remove(menuInfo.position);
                    removed_cnt++;
                    myadapter.notifyDataSetChanged();
                    return true;
                }
                break;
            case 1:
                if (item_id==0) {
                    findViewById(R.id.fab).performClick();//调用悬浮窗的点击函数
                } else if (item_id==1) {
                    Intent intent=new Intent(MainActivity.this, history_activity.class);
                    startActivity(intent);
                    return true;
                }
                break;
            default:
        }
        return super.onContextItemSelected(item);
    }
}
