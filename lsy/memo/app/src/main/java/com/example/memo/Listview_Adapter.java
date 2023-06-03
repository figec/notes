package com.example.memo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * 自己写的适配器
 * 来描述listview中每一个项的布局和复选框相应的响应
 */

public class Listview_Adapter extends ArrayAdapter<Item> {
    private int resourceId;
    public Listview_Adapter(Context context, int textViewResourceId, List<Item> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Item item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView content = (TextView) view.findViewById(R.id.item_string);
        String text_time=item.getContent();
        int i=text_time.indexOf(" ");
        String text=text_time.substring(i+1);//扔掉时间变量
        String time=text_time.substring(0,i);
        content.setText(text);
        TextView time_view = (TextView) view.findViewById(R.id.item_time);
        time_view.setText(time);
        CheckBox check = (CheckBox) view.findViewById(R.id.ck);
        check.setChecked(item.getChecked());

        //根据笔记类型设置背景颜色等
        switch (item.getStyle()) {
            case Item.Important_Urgent:
                view.setBackgroundColor(Color.parseColor("#f0d9e1"));
                break;
            case Item.Important_NUrgent:
                view.setBackgroundColor(Color.parseColor("#cef9f0"));
                break;
            case Item.Urgent_NImportant:
                view.setBackgroundColor(Color.parseColor("#fff5ca"));
                break;
            case Item.NImportant_NUrgent:
                view.setBackgroundColor(Color.parseColor("#e6f7ff"));
                break;
            default:
                break;
        }


        // 对checked下的基础样式设置
        if(item.getChecked()==true){
            //content.setBackgroundColor(Color.LTGRAY);
            content.setPaintFlags(content.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
        }


        // 定时对象
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //要做的事情——一定时间后将该元素从数据中删除
                //定时5s
                int place = MainActivity.current_list.indexOf(MainActivity.text_edit_list.get(position));
                MainActivity.current_list.remove(place);
                MainActivity.text_edit_list.remove(position);
                MainActivity.save_text_list();


                //更新listview内容-这里实际上使用current_list做处理的，所以这里得同时处理current_list
                notifyDataSetChanged();
                //handler.postDelayed(this, 10000);
            }
        };


        // 创建复选框点击变化的监听器
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // 修改对应的值-修改对应check值
                item.setChecked(check.isChecked());
                MainActivity.text_edit_list.set(position,item);
                if(check.isChecked()){
                    // 如果从false到true 执行迟缓删除操作
                    //样式转换
                    //背景色设置为浅灰色
                    //content.setBackgroundColor(Color.LTGRAY);
                    //增加删除线
                    content.setPaintFlags(content.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
                    //执行迟缓删除操作
                    handler.postDelayed(runnable, 5000);
                }
                else{
                    // 反之，执行删除取消命令
                    //背景色变回白色
                    //content.setBackgroundColor(Color.WHITE);
                    // 去除删除线
                    content.setPaintFlags(content.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    // 如果在时间内取消了，则去除迟缓删除操作
                    handler.removeCallbacks(runnable);
                }
            }
        });
        return view;
    }
}
