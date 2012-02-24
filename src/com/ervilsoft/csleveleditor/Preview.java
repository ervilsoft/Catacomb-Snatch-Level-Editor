package com.ervilsoft.csleveleditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class Preview extends JPanel {

    private final int scale = (int) (32 * 0.1);

    public Preview() {
        setDoubleBuffered(true);
        setLayout(null);
        setBackground(Color.WHITE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int x = 0; x < LevelEditor.LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LevelEditor.LEVEL_WIDTH; y++) {

                g2.setColor(Color.decode(LevelEditor.level[x][y].tile.COLOR + ""));
                g2.fillRect(x * scale, y * scale, scale, scale);
            }
        }
    }
}