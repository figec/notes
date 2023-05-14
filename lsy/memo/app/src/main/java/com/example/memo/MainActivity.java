package com.example.memo;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<Item> text_edit_list=new ArrayList<Item>();
    private Listview_Adapter myadapter;

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
    }


    @SuppressLint("MissingSuperCall")
    @Override//重写onActivityResult方法接收保存文本框的返回值
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    String returndata=data.getStringExtra("data_return");//获得用户输入的数据
                    Item item = new Item(returndata,false); //这里是新建后的返回，直接用false
                    text_edit_list.add(item);
                    ListView listView=(ListView) findViewById(R.id.list_view);
                    myadapter=new Listview_Adapter(MainActivity.this, R.layout.check_string,text_edit_list);
                    listView.setAdapter(myadapter);
                    //监听点击事件
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String input_data= text_edit_list.get(i).getContent();
                            Intent intent_list=new Intent(MainActivity.this,text_edit_activity.class);
                            intent_list.putExtra("extra_data",input_data);
                            intent_list.putExtra("extra_boolean",text_edit_list.get(i).getChecked());
                            text_edit_list.remove(i);
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
                    Item item = new Item(returndata,checked);
                    text_edit_list.add(item);
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
                    myadapter.notifyDataSetChanged();
                    return true;
                }
                break;
            case 1:
                if (item_id==0) {
                    String input_data=null;
                    Intent intent_fab=new Intent(MainActivity.this,text_edit_activity.class);
                    intent_fab.putExtra("extra_data",input_data);
                    intent_fab.putExtra("extra_boolean",false);
                    startActivityForResult(intent_fab,1);//打开下一个界面并传入唯一标识符1
                } else if (item_id==1) {
                    return true;
                }
                break;
            default:
        }
        return super.onContextItemSelected(item);
    }
}
