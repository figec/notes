package com.example.memo;

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
