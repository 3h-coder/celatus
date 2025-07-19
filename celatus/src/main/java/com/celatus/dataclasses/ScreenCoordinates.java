package com.celatus.dataclasses;

public class ScreenCoordinates {

    // region =====Variables=====

    private int x;
    private int y;

    // endregion

    // region =====Getters and Setters=====

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    // endregion

    // region =====Constructors=====

    public ScreenCoordinates() {
    }

    public ScreenCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // endregion

}
