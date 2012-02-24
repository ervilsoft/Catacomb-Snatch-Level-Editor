package com.ervilsoft.csleveleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TexturesList extends JList {

    public TexturesList() {
        setBackground(new Color(117,64,31));
        setDoubleBuffered(true);
        setCellRenderer(new CustomCellRenderer());
        
        addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    switch (getSelectedIndex()) {
                        case 0:
                            Editor.pencil = Texture.Tile.FLOOR;
                            break;
                        case 1:
                            Editor.pencil = Texture.Tile.HOLE;
                            break;
                        case 2:
                            Editor.pencil = Texture.Tile.WALL;
                            break;
                        case 3:
                            Editor.pencil = Texture.Tile.DESTROYABLEWALL;
                            break;
                        case 4:
                            Editor.pencil = Texture.Tile.TREASURE;
                            break;
                        case 5:
                            Editor.pencil = Texture.Tile.UNBREAKABLERAIL;
                            break;
                    }
                }
            }
        });

        JPanel[] lines = new JPanel[6];
        lines[0] = createItem("Floor", Art.floorTiles[0][0]);
        lines[1] = createItem("Hole", Art.floorTiles[4][0]);
        lines[2] = createItem("Wall", Art.wallTiles[0][0]);
        lines[3] = createItem("Destroyable Wall", Art.treasureTiles[4][0]);
        lines[4] = createItem("Treasure", Art.treasureTiles[0][0]);
        lines[5] = createItem("Rails", Art.railsTiles[0][0]);
        
        
//        int w = Art.testiles.length;
//        int h = Art.testiles[0].length;
//        
//        JPanel[] lines = new JPanel[w * h];
//        
//        System.out.println(Art.testiles.length  + "," +  Art.testiles[0].length);
//        
//        for(int i = 0; i < h; i++){
//            for(int j = 0; j < w; j++){
//               lines[j + (i * w)] = createItem("Tile #" + (j + (i * w)), Art.testiles[j][i]);
//            }
//        }
        
        setListData(lines);
    }
    
    private JPanel createItem(String text, BufferedImage image) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text);
        label.setIcon(new ImageIcon(image));
        label.setForeground(Color.WHITE);
        panel.add(label);

        return panel;
    }

    class CustomCellRenderer implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            Component component = (Component) value;
            component.setBackground(isSelected ? new Color(161,89,42) : new Color(117,64,31));
            return component;
        }
    }
}