package com.ervilsoft.csleveleditor;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Buttons extends JPanel {
    
    private LevelEditor levelEditor;

    public Buttons(LevelEditor le) {
        levelEditor = le;
        setOpaque(false);

        JButton buttonNew = createButton(Art.buttons[0][0], Art.buttons[1][0]);
        JButton buttonOpen = createButton(Art.buttons[0][1], Art.buttons[1][1]);
        JButton buttonSave = createButton(Art.buttons[0][2], Art.buttons[1][2]);

        add(buttonNew);
        add(buttonOpen);
        add(buttonSave);
        
        buttonNew.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelEditor.newLevel();
            }
        });

        buttonOpen.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelEditor.loadLevel();
            }
        });
        
        buttonSave.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelEditor.saveLevel();
            }
        });
        
    }

    private JButton createButton(BufferedImage image, BufferedImage imageRollover) {
        JButton button = new JButton(new ImageIcon(image));
        button.setRolloverIcon(new ImageIcon(imageRollover));
        button.setContentAreaFilled(false);
        button.setBorder(null);

        return button;
    }
}