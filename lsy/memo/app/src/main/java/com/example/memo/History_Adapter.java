package com.example.memo;

import com.example.memo.PinnedSectionListView.PinnedSectionListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * 分好类的适配器.加载的是分好组的list数据
 * @author max.chengdu 2015年11月29日
 *
 */
public class history_adapter extends BaseAdapter implements PinnedSectionListAdapter{
    // 时间以及相关信息
    private ArrayList<PinnedSectionBean> list; //这个是分好类别后的list,在所属activity进行数据分类
    private Context mContext;

    public ArrayList<PinnedSectionBean> getList() {
        return list;
    }

    public void setList(ArrayList<PinnedSectionBean> list) {
        if (list != null) {
            this.list = list;
        }else{
            list = new ArrayList<PinnedSectionBean>();
        }
    }
    public history_adapter(ArrayList<PinnedSectionBean> list, Context mContext) {
        super();
        this.setList(list);
        this.mContext = mContext;
    }
    final static class ViewHolder {
        TextView item_date, item_content;
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public PinnedSectionBean getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.historty_listview, null);
            viewHolder.item_date = (TextView) convertView.findViewById(R.id.item_date);
            viewHolder.item_content = (TextView) convertView.findViewById(R.id.item_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SimpleDateFormat formatter = null;
        PinnedSectionBean bean =  getItem(position);
        //当item属于标题的时候,显示对应时间段
        if (bean.type == PinnedSectionBean.SECTION) {
            viewHolder.item_date.setText(bean.item.getContent());
        }
        //当item属于内容的时候,就显示时间和内容
        else{
            Item item = bean.item;
            switch (bean.item.create_period) {
                case 1:
                    formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
                    break;
                case 2:
                    formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
                    break;
                case 3:
                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                    break;
                case 4:
                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                    break;
                default:
                    formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                    break;
            }
            String formatted_Createdate = formatter.format(item.getCreat_date());//修改创建时间格式
            viewHolder.item_date.setText(formatted_Createdate);
            int i = item.getContent().indexOf(' ');
            viewHolder.item_content.setText(item.getContent().substring(i+1));
            if (item.getChecked()) {
                //背景色设置为浅灰色
                viewHolder.item_content.setBackgroundColor(Color.LTGRAY);
                //增加删除线
                viewHolder.item_content.setPaintFlags(viewHolder.item_content.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                //背景色变回白色
                viewHolder.item_content.setBackgroundColor(Color.WHITE);
                // 去除删除线
                viewHolder.item_content.setPaintFlags(viewHolder.item_content.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
        return convertView;
    }

    //判断是否是属于标题悬浮的
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == PinnedSectionBean.SECTION;
    }

    //arraylist的数据里面有2种类型,标题和内容
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return ((PinnedSectionBean)getItem(position)).type;
    }

}
