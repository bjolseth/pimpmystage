package com.cisco.telepresence.sandbox.stage.model;

import java.util.ArrayList;
import java.util.List;

public class Stage {

    private List<Screen> screens = new ArrayList<Screen>();

    public List<Screen> getScreens() {
        return screens;
    }
    public void setScreens(List<Screen> screens) {
        this.screens = screens;
    }

}
