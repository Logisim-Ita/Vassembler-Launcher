package org.altervista.logisim.Vassembler_Launcher;

import javax.swing.JOptionPane;

public class Logger {
    
    public static void log(String text){
        JOptionPane.showMessageDialog(null, text,
                                   "Error", JOptionPane.ERROR_MESSAGE);
    }
}
