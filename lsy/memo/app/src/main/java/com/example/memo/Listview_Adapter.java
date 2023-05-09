package com.example.memo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;



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
        content.setText(item.getContent());
        CheckBox check = (CheckBox) view.findViewById(R.id.ck);
        check.setChecked(item.getChecked());
        // 对checked下的基础样式设置
        if(item.getChecked()==true){
            content.setBackgroundColor(Color.LTGRAY);
            content.setPaintFlags(content.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
        }


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
                    content.setBackgroundColor(Color.LTGRAY);
                    //增加删除线
                    content.setPaintFlags(content.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);



                }
                else{
                    // 反之，执行删除取消命令
                    //背景色变回白色
                    content.setBackgroundColor(Color.WHITE);
                    // 去除删除线
                    content.setPaintFlags(content.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                }
            }
        });




        return view;


    }




}