package com.example.memo;


/**
 * item类，作为listview容器的数据类
 * 有两个属性，content-对应项的string内容
 * checked标识对应项一起的复选框的选择状态，true为勾选状态
 */
public class Item {
    private String content;
    private boolean checked;

    public Item(String content, boolean checked) {
        this.content = content;
        this.checked = checked;
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

}
