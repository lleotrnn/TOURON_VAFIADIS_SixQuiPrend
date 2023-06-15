package com.isep.sixquiprend.GUI;

import com.isep.sixquiprend.Core.Game;
import javafx.application.Application;

public class StartController {
    private GameApplication application;

    public StartController(GameApplication application) {
        this.application = application;
    }

    public void start() {
        application.startProfileCreation();
    }
}
