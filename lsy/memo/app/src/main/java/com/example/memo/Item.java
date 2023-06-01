package com.example.memo;


import java.sql.DatabaseMetaData;
import java.util.Date;


/**
 * item类，作为listview容器的数据类
 * 有两个属性，content-对应项的string内容
 * checked标识对应项一起的复选框的选择状态，true为勾选状态
 * 添加style属性，作为分类
 */
public class Item {

    //类别分别
    protected static final int Default = 0;   // 不设置轻重急缓的分类
    protected static final int Important_Urgent = 1;
    protected static final int Important_NUrgent = 2;
    protected static final int Urgent_NImportant = 3;
    protected static final int NImportant_NUrgent = 4;
    private String content;
    private boolean checked;
    private Date creat_date=new Date();

    private Date modify_date=new Date();

    // 分类
    private int style;
    public Item(String content, boolean checked) {
        this.content = content;
        this.checked = checked;
    }
    public void setCreat_date(Date creat_date){
        this.creat_date=creat_date;
    }

    public void set_content(String content) {
        this.content = content;
    }
    public void setModify_date(Date creat_date){
        this.modify_date=new Date();
    }


    // style的相关方法
    public void setStyle(int style){
        this.style = style;
    }

    public int getStyle(){
        return this.style;
    }


    public String getContent() {
        return content;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked){
        this.checked = checked;
    }
    public Date getCreat_date(){return creat_date;}
    public Date getModify_date(){return modify_date;}

}
