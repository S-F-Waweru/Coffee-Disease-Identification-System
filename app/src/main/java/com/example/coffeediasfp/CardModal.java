package com.example.coffeediasfp;

public class CardModal {
    private String card_name;
    private int imgid;
    private String button_name;

    // for the target activity for each card
    private Class <?> targetActivity ;

    //intent


    public  CardModal(String card_name, int imgid, String button_name, Class<?> targetActivity ){
        this.card_name = card_name;
        this.imgid = imgid;
        this.button_name = button_name;
        this.targetActivity = targetActivity;
    }

    public String getCard_name(){
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public int getImgid() {
        return imgid;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }

    public String getButton_name(){
        return button_name;
    }
    public void  setButton_name(String button_name){
        this.button_name = button_name;
    }

    public Class<?> getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(Class<?> targetActivity) {
        this.targetActivity = targetActivity;
    }
}
