package controller;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import main.Main;
import ui.LoginPanel;
import ui.MainFrame;

public class GuiController {
    private final Main mainApp;

    public GuiController(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                LoginPanel login = new LoginPanel(mainApp, frame);

                frame.setLoginPanel(login);
                frame.showLogin();
                frame.setVisible(true);
            }
        });
    }
}

