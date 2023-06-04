package com.example.memo;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {



    protected static ArrayList<Item> text_edit_list=new ArrayList<Item>();

    protected static ArrayList<Item> current_list;  // 存放筛选后的结果

    protected static Listview_Adapter myadapter;

    protected static ArrayList<Item> history_text_list = new ArrayList<Item>();

    protected static int type = 0;// 当前显示的类型

    protected static int isInit = 0;//是否为第一次打开进行操作，当为0时，重量数据不需要进行读取操作。当为1时，才需要进行读取操作


    protected static String pre;// 为了将保存作为静态方法的路径前缀
    //轻量级的数据存取方式,并设置为仅能在改程序下进行读取操作
    SharedPreferences sharedPreferences; //= getSharedPreferences("light_data", MODE_PRIVATE);
    //SharedPreferences sharedPreferences = getPreferences( MODE_PRIVATE);

    //编辑器
    private SharedPreferences.Editor editor; //= sharedPreferences.edit();

    // Comparator 接口是一个函数式接口，用于定义比较器，可以用于对对象进行排序。
    // 它包含一个抽象方法 compare(T o1, T o2)，用于比较两个对象的顺序。如果 o1 应该排在 o2 前面，
    // 则返回一个负整数；如果 o1 和 o2 相等，则返回 0；如果 o1 应该排在 o2 后面，则返回一个正整数。
    public static class TimeComparator implements Comparator<Item> {
        @Override
        public int compare(Item item1, Item item2) {
            // 按照创建时间从后到先排序
            // 使用 compareTo 方法比较两个 Date 对象时，如果第一个对象早于第二个对象，则返回负数；
            // 如果两个对象相等，则返回 0；如果第一个对象晚于第二个对象，则返回正数。
            return item2.getCreat_date().compareTo(item1.getCreat_date());
        }
    }

    public static class HistoryComparator implements Comparator<Item> {
        @Override
        public int compare(Item item1, Item item2) {
            //按照先 checked / deleted 再日期的排序方法
            // 保证删除/完成的在末尾，其他的在前面正常排序
            if ((item1.getStatus() && !item2.getStatus())) {
                return 1;
            } else if ((item1.getStatus() && item2.getStatus()) || (!item1.getStatus() && !item2.getStatus())) {
                return item2.getCreat_date().compareTo(item1.getCreat_date());
            } else {
                return -1;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pre = getFilesDir().getAbsolutePath(); // 保存路径前缀
        // 数据初始化
        //读写器初始化
        sharedPreferences = getSharedPreferences("light_data", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // 读取轻量级数据
        type = sharedPreferences.getInt("type",0);
//        removed_cnt = sharedPreferences.getInt("removed_cnt",0);
        isInit = sharedPreferences.getInt("isInit",0);
        //Toast.makeText(getApplicationContext(),"读取的type值为："+Integer.toString(type),Toast.LENGTH_SHORT).show();
        
        if(isInit==0){
            //
            ObjectOutputStream objectOutputStream = null;   //序列化写入文件
            File file = new File(getFilesDir().getAbsolutePath(),"text_edit_list.txt");
            File history_file = new File(getFilesDir().getAbsolutePath(),"history_text_list.txt");

            if(!file.exists()){
                // 文件夹不存在，则去创建对应的文件夹
                File ff = new File(file.getParent());
                ff.mkdirs();
                try {
                    file.createNewFile(); // 创建文件
                    history_file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
                objectOutputStream.writeObject(text_edit_list);
                objectOutputStream.close();

                objectOutputStream = new ObjectOutputStream(new FileOutputStream(history_file));
                objectOutputStream.writeObject(history_text_list);
                objectOutputStream.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            isInit = 1;
            editor.putInt("isInit",isInit);
            editor.commit();

        }
        else{
            // 进行反序列化
            get_text_list();
            get_history_list();

        }

        // 使用初始化数据
        ListView listView=(ListView) findViewById(R.id.list_view);
        current_list = (ArrayList<Item>)text_edit_list.clone(); // 浅拷贝
        if(type!=Item.Default){
            current_list.removeIf(e -> e.getStyle()!=type); // 进行筛选
        }
        myadapter=new Listview_Adapter(MainActivity.this, R.layout.check_string,current_list);
        listView.setAdapter(myadapter);





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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String input_data= current_list.get(i).getContent();
                Intent intent_list=new Intent(MainActivity.this,text_edit_activity.class);
                intent_list.putExtra("extra_data",input_data);
                intent_list.putExtra("extra_boolean",current_list.get(i).getChecked());
                intent_list.putExtra("CreatDate",current_list.get(i).getCreat_date().getTime());//传入该项目的创建时间
                intent_list.putExtra("extra_style",current_list.get(i).getStyle());
                int place = text_edit_list.indexOf(current_list.get(i)); // 转换后的位置
                text_edit_list.remove(place);//
                save_text_list();
                //Toast.makeText(view.getContext(),"i:"+Integer.toString(text_edit_list.indexOf(current_list.get(i))),Toast.LENGTH_SHORT).show();
                history_text_list.remove(place);
                save_history_list();

                startActivityForResult(intent_list,2);//打开下一个界面并传入唯一标识符2
            }
        });
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                menu.setHeaderTitle("长按菜单-ContextMenu");
                menu.add(0, 0, 0, "删除");//组，id,顺序，内容
                menu.add(0, 1, 0, "其他功能");
            }
        });


        //锁屏快捷键功能
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "imservice";
            String channelName = "锁屏快捷键";
            String description = "锁屏通知";
            int importance = NotificationManager.IMPORTANCE_HIGH; //设置重要等级
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setSound((Uri) null, (AudioAttributes) null);
            channel.setDescription(description);
            channel.enableVibration(false); //设置振动
            notificationManager.createNotificationChannel(channel);

            String input_data=null;
            Intent intent=new Intent(this,MainActivity.class);
            // intent.putExtra("extra_data",input_data);
            // intent.putExtra("extra_boolean",false);
            // intent.putExtra("extra_style",Item.Default);
            // startActivityForResult(intent,1);//打开下一个界面并传入唯一标识符1

            PendingIntent pi = PendingIntent.getActivity(this,0,intent ,0);
            int notifyID = 1;
            Notification notification = new Notification.Builder(this, channelId)
                    .setContentTitle("一键打开随记")
                    .setContentText("又有新idea了？快记录下来吧！")
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), android.R.drawable.ic_menu_edit)) //设置大图标
                    .setSmallIcon(android.R.drawable.ic_menu_edit) //设置小图标
                    .setContentIntent(pi)
                    .setStyle(new Notification.MediaStyle())
                    .setAutoCancel(false) ////设置弹窗在点击后不消失
                    .build();
            notificationManager.notify(notifyID, notification);
        }

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
                        type = Item.Default;
                        current_list = (ArrayList<Item>)text_edit_list.clone();
                        //修改数据存储
                        editor.putInt("type",type);
                        editor.commit();
                        //current_list = new ArrayList<>(text_edit_list); // 浅拷贝
                        //Toast.makeText(view.getContext(),"1",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.iu:
                        bn.setText("重要且紧急 ▼");
                        type = Item.Important_Urgent;
                        current_list = (ArrayList<Item>)text_edit_list.clone(); // 浅拷贝
                        current_list.removeIf(e -> e.getStyle()!=type); // 进行筛选

                        editor.putInt("type",type);
                        editor.commit();
                        break;
                    case R.id.inu:
                        bn.setText("重要非紧急 ▼");
                        type = Item.Important_NUrgent;
                        current_list = (ArrayList<Item>)text_edit_list.clone(); // 浅拷贝
                        current_list.removeIf(e -> e.getStyle()!=type); // 进行筛选

                        editor.putInt("type",type);
                        editor.commit();
                        break;
                    case R.id.niu:
                        bn.setText("紧急非重要 ▼");
                        type = Item.Urgent_NImportant;
                        current_list = (ArrayList<Item>)text_edit_list.clone(); // 浅拷贝
                        current_list.removeIf(e -> e.getStyle()!=type); // 进行筛选

                        editor.putInt("type",type);
                        editor.commit();
                        break;
                    case R.id.ninu:
                        bn.setText("非重要非紧急 ▼");
                        type = Item.NImportant_NUrgent;
                        current_list = (ArrayList<Item>)text_edit_list.clone(); // 浅拷贝
                        current_list.removeIf(e -> e.getStyle()!=type); // 进行筛选

                        editor.putInt("type",type);
                        editor.commit();
                        break;
                }
                // 修改listview的显示
                ListView listView=(ListView) findViewById(R.id.list_view);
                myadapter=new Listview_Adapter(MainActivity.this, R.layout.check_string,current_list);
                listView.setAdapter(myadapter);
                myadapter.notifyDataSetChanged();

                return false;
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
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String formatted_Creatdate = formatter.format(creat_date);//修改创建时间格式
                    returndata=formatted_Creatdate+" "+returndata;//给回传文本加上时间内容
                    int style = data.getIntExtra("data_return_style",Item.Default); //获取单选框编号
                    Item item = new Item(returndata,false); //这里是新建后的返回，直接用false
                    item.setCreat_date(creat_date);//传入创建时间
                    item.setStyle(style);
                    text_edit_list.add(item);
                    save_text_list();
                    history_text_list.add(item);
                    save_history_list();
                    Collections.sort(text_edit_list, new TimeComparator());
                    // history 按方法 HistoryComparator 规则排序
                    Collections.sort(history_text_list,new HistoryComparator());
                    ListView listView=(ListView) findViewById(R.id.list_view);
                    current_list = (ArrayList<Item>)text_edit_list.clone(); // 浅拷贝
                    if(type!=Item.Default){
                        current_list.removeIf(e -> e.getStyle()!=type); // 进行筛选
                    }
                    myadapter=new Listview_Adapter(MainActivity.this, R.layout.check_string,current_list);
                    listView.setAdapter(myadapter);
                    myadapter.notifyDataSetChanged();
                    //监听点击事件
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String input_data= current_list.get(i).getContent();
                            Intent intent_list=new Intent(MainActivity.this,text_edit_activity.class);
                            intent_list.putExtra("extra_data",input_data);
                            intent_list.putExtra("extra_boolean",current_list.get(i).getChecked());
                            intent_list.putExtra("CreatDate",current_list.get(i).getCreat_date().getTime());//传入该项目的创建时间
                            intent_list.putExtra("extra_style",current_list.get(i).getStyle());
                            int place = text_edit_list.indexOf(current_list.get(i)); // 转换后的位置
                            text_edit_list.remove(place);//
                            //Toast.makeText(view.getContext(),"i:"+Integer.toString(text_edit_list.indexOf(current_list.get(i))),Toast.LENGTH_SHORT).show();
                            history_text_list.remove(place);
                            save_history_list();

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
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String formatted_ModifyDate = formatter.format(modify_date);
                    returndata=formatted_ModifyDate+" "+returndata;
                    int style = data.getIntExtra("data_return_style",Item.Default); //获取单选框编号
                    Item item = new Item(returndata,checked);
                    item.setStyle(style);
                    item.setModify_date(modify_date);//设置最新修改时间
                    item.setCreat_date(creat_date);//新条目原来未修改条目的创建时间
                    text_edit_list.add(item);
                    save_text_list();
                    history_text_list.add(item);
                    save_history_list();
                    Collections.sort(text_edit_list, new TimeComparator());//根据创建时间排序
                    Collections.sort(history_text_list,new HistoryComparator());// history 按方法 HistoryComparator 规则排序
                    ListView listView=(ListView) findViewById(R.id.list_view);
                    current_list = (ArrayList<Item>)text_edit_list.clone(); // 浅拷贝
                    if(type!=Item.Default){
                        current_list.removeIf(e -> e.getStyle()!=type); // 进行筛选
                    }
                    myadapter=new Listview_Adapter(MainActivity.this, R.layout.check_string,current_list);
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
            case 0://listview 长按
                if (item_id==0) { //长按删除
                    int place = current_list.indexOf(text_edit_list.get(menuInfo.position));
                    current_list.remove(place);
                    text_edit_list.remove(menuInfo.position);
                    save_text_list();
                    history_text_list.get(menuInfo.position).setDeleted(true);//表示该记录已经删除
                    Collections.sort(history_text_list,new HistoryComparator());//根据是否删除/完成+创建时间排序
//                    editor.putInt("removed_cnt",removed_cnt);
//                    editor.commit();
                    myadapter.notifyDataSetChanged();
                    return true;
                }
                break;
            case 1://悬浮窗长按
                if (item_id==0) {//添加笔记
                    findViewById(R.id.fab).performClick();//调用悬浮窗的点击函数
                } else if (item_id==1) {//历史笔记
                    Intent intent=new Intent(MainActivity.this, history_activity.class);
                    startActivity(intent);
                    return true;
                }
                break;
            default:
        }
        return super.onContextItemSelected(item);
    }

    // 保存对应的ArrayList
    public static void save_text_list(){
        ObjectOutputStream objectOutputStream = null;   //序列化写入文件
        File file = new File(pre,"text_edit_list.txt");
        //File file = new File(getFilesDir().getAbsolutePath(),"text_edit_list.txt");
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(text_edit_list);
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save_history_list(){
        ObjectOutputStream objectOutputStream = null;   //序列化写入文件
        File file = new File(pre,"history_text_list.txt");
        //File file = new File(getFilesDir().getAbsolutePath(),"history_text_list.txt");
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(history_text_list);
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 读取对应的ArrayList
    private void get_text_list(){
        File file = new File(getFilesDir().getAbsolutePath(),"text_edit_list.txt");
        ObjectInputStream objectInputStream = null; //反序列化 从文件中读出来
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(file));
            text_edit_list = (ArrayList<Item>)objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private void get_history_list(){
        File file = new File(getFilesDir().getAbsolutePath(),"history_text_list.txt");
        ObjectInputStream objectInputStream = null; //反序列化 从文件中读出来
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(file));
            history_text_list = (ArrayList<Item>)objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



}
