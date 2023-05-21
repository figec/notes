//package com.example.memo;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import java.util.List;
//import java.util.Date;
//import java.text.SimpleDateFormat;
//
//public class History_Adapter extends ArrayAdapter<Item> {
//
//    private int resourceId;
//
//    public History_Adapter(Context context, int textViewResourceId, List<Item> objects){
//        super(context,textViewResourceId,objects);
//        resourceId = textViewResourceId;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        Item item = getItem(position);
//        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
//        TextView textView_content = (TextView) view.findViewById(R.id.history_item_content);
//        TextView textView_time = (TextView) view.findViewById(R.id.history_item_time);
//        String temp = item.getContent();
//        int i=temp.indexOf(" ");//空格的下标
//        textView_content.setText(temp.toCharArray(),i+1,temp.length()-i-1);//设置内容文本框内容
//        if (item.getChecked()) {//若已完成，设置样式
//            //背景色设置为浅灰色
//            textView_content.setBackgroundColor(Color.LTGRAY);
//            //增加删除线
//            textView_content.setPaintFlags(textView_content.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        }
//        Date date = item.getCreat_date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
//        String formattedDate = formatter.format(date);
//        textView_time.setText(formattedDate.toCharArray(),0,formattedDate.length());//设置时间文本框内容
//        return view;
//    }
//}
