package com.example.memo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 把初始Arraylist里面的数据,转化成带有分组并且标识悬浮类别(SECTION)和内容(ITEM)的list
 * @author max.chengdu 2015年11月29日
 *
 */

public class PinnedSectionBean {
    //类型--内容
    public static final int ITEM = 0;
    //类型--顶部悬浮的标题
    public static final int SECTION = 1;

    public final int type; //所属于的类型
    public final Item item; //listview显示的item的数据实体类,可根据自己的项目来设置

    public int sectionPosition; //顶部悬浮的标题的位置
    public int listPosition; //内容的位置

    public int getSectionPosition() {
        return sectionPosition;
    }

    public void setSectionPosition(int sectionPosition) {
        this.sectionPosition = sectionPosition;
    }

    public Item getDetail() {
        return item;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public PinnedSectionBean(int type, Item item) {
        super();
        this.type = type;
        this.item = item;
    }

    public PinnedSectionBean(int type, Item item, int sectionPosition,
                             int listPosition) {
        super();
        this.type = type;
        this.item = item;
        this.sectionPosition = sectionPosition;
        this.listPosition = listPosition;
    }

    /**
     * 通过HashMap键值对的特性，将ArrayList的数据进行分组，返回带有分组Header的ArrayList。
     *
     * @param history_text 从后台接受到的ArrayList的数据，其中日期格式为：yyyy-MM-dd HH:mm:ss
     * @return list  返回的list是分类后的包含header（yyyy-MM-dd）和item（HH:mm:ss）的ArrayList
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static ArrayList<PinnedSectionBean> getData(List<Item> history_text){
        //最后我们要返回带有分组的list,初始化
        ArrayList<PinnedSectionBean> list = new ArrayList<PinnedSectionBean>();

        //共设置四个划分时间段，分别为三天内、一周内（三天内之外）、本月内（前两者之外，一个月定 30 天）与更久之前
        Map<Item, List<Item>> map = new HashMap<>();
        Item item1 = new Item("三天内", false), item2 = new Item("一周内", false),item3 = new Item("一个月内", false),item4 = new Item("更久之前", false);
        map.put(item1,new ArrayList<Item>());
        map.put(item2,new ArrayList<Item>());
        map.put(item3,new ArrayList<Item>());
        map.put(item4,new ArrayList<Item>());
        LocalDate localDate =LocalDate.now();
        //按照item里面的时间进行分类
        for (int i = 0; i < history_text.size(); i++) {
            try {
                LocalDate startLocalDate = history_text.get(i).getCreat_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                long daysCount = ChronoUnit.DAYS.between(startLocalDate, localDate);
                if (daysCount <= 3) {
                    history_text.get(i).set_create_period(1);
                    map.get(item1).add(history_text.get(i));
                } else if (daysCount <= 7) {
                    history_text.get(i).set_create_period(2);
                    map.get(item2).add(history_text.get(i));
                } else if (daysCount <= 30) {
                    history_text.get(i).set_create_period(3);
                    map.get(item3).add(history_text.get(i));
                } else {
                    history_text.get(i).set_create_period(4);
                    map.get(item4).add(history_text.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //依次添加进list
        list.add(new PinnedSectionBean(SECTION, item1));//三天内的item
        List<Item> li = (List<Item>) map.get(item1);
        for (Item item : li) {
            list.add(new PinnedSectionBean(ITEM, item));
        }
        list.add(new PinnedSectionBean(SECTION, item2));//一周内的item
        li = (List<Item>) map.get(item2);
        for (Item item : li) {
            list.add(new PinnedSectionBean(ITEM, item));
        }
        list.add(new PinnedSectionBean(SECTION, item3));//一个月内的item
        li = (List<Item>) map.get(item3);
        for (Item item : li) {
            list.add(new PinnedSectionBean(ITEM, item));
        }
        list.add(new PinnedSectionBean(SECTION, item4));//更久之前的item
        li = (List<Item>) map.get(item4);
        for (Item item : li) {
            list.add(new PinnedSectionBean(ITEM, item));
        }
        //把分好类的hashmap添加到list里面便于显示
        return list;
    }

}
