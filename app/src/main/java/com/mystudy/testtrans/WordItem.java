package com.mystudy.testtrans;

public class WordItem{
    private String word;
    private String trans;

    public WordItem(String word,String trans){
        this.word = word;
        this.trans = trans;
    }

    public String getTrans() {
        return trans;
    }

    public String getWord() {
        return word;
    }
}
