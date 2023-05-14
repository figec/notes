package com.example.memo;


import java.sql.DatabaseMetaData;
import java.util.Date;


/**
 * item类，作为listview容器的数据类
 * 有两个属性，content-对应项的string内容
 * checked标识对应项一起的复选框的选择状态，true为勾选状态
 */
public class Item {
    private String content;
    private boolean checked;
    private Date creat_date=new Date();

    private Date modify_date=new Date();
    public Item(String content, boolean checked) {
        this.content = content;
        this.checked = checked;
    }
    public void setCreat_date(Date creat_date){
        this.creat_date=creat_date;
    }
    public void setModify_date(Date creat_date){
        this.creat_date=creat_date;
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
