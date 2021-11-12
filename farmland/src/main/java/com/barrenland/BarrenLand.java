package com.barrenland;


public class BarrenLand {

    private int xCoord1, xCoord2, yCoord1, yCoord2;

    public BarrenLand(int x1, int y1, int x2, int y2){
        xCoord1 = x1;
        xCoord2 = x2;
        yCoord1 = y1;
        yCoord2 = y2;
    }

    public int getX1(){
        return xCoord1;
    }

    public int getY1(){
        return yCoord1;
    }

    public int getX2(){
        return xCoord2;
    }

    public int getY2(){
        return yCoord2;
    }
}