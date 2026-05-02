package miumg.edu.gt.controlinventariopeps;

import miumg.edu.gt.controlinventariopeps.vista.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}