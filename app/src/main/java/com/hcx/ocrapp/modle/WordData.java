package com.hcx.ocrapp.modle;

public class WordData {
    private int id;
    private String word;
    private String time;
    private String userId;



    public WordData(int id, String word, String time, String userId){
        this.id = id;   //id
        this.word = word;   //文本
        this.time = time;   //时间
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
