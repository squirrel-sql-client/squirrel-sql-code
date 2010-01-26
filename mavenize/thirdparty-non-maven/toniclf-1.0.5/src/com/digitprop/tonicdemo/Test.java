package com.digitprop.tonicdemo;


import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

import com.digitprop.tonic.TonicLookAndFeel;

public class Test {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(TonicLookAndFeel.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame();
        frame.setTitle("Frame");

        // This order works
        JCheckBox checkBox = new JCheckBox("checkBox");
        JRadioButton radio = new JRadioButton("radioButton");

        // This order results in the radio button being painted as if it
        // were a checkbox
        //JRadioButton radio = new JRadioButton("radioButton");
        //JCheckBox checkBox = new JCheckBox("checkBox");

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(radio);
        panel.add(checkBox);

        frame.getContentPane().add(panel);

        frame.pack();
        frame.show();
    }
}