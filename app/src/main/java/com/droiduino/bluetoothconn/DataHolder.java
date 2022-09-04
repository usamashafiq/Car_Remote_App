package com.droiduino.bluetoothconn;

public class DataHolder {
    private static final DataHolder instance = new DataHolder();

    public static DataHolder getInstance() {
        return instance;

    }

    private String guagedata,acdata,windowdataup,windowdatadown,musicdata;
    private  int num;

    public String getGuagedataData() {
        return guagedata;
    }
    public void setGuagedataData(String data) {
        this.guagedata = data;
    }

    public String getAcData() {
        return acdata;
    }
    public void setAcData(String data) {
        this.acdata = data;
    }

    public String getWindowdataupData() {
        return windowdataup;
    }
    public void setWindowdataupData(String data) {
        this.windowdataup = data;
    }

    public String getWindowdatadownData() {
        return windowdatadown;
    }
    public void setWindowdatadownData(String data) {
        this.windowdatadown = data;
    }

    public String getMusicdataData() {
        return musicdata;
    }
    public void setMusicdataData(String data) {
        this.musicdata = data;
    }


    public int getNum() {
        return num;
    }
    public void setnum(int num){
        this.num = num;
    }
}
