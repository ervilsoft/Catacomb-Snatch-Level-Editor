package com.ervilsoft.csleveleditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LevelEditor extends JPanel {

    public static final int EDITOR_WIDTH = 800;
    public static final int EDITOR_HEIGHT = 600;
  
    public static final int LEVEL_WIDTH = 48;
    public static final int LEVEL_HEIGHT = 48;
    
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final Texture[][] level = new Texture[LEVEL_HEIGHT][LEVEL_WIDTH];
    
    
    private TexturesList list;
    private JScrollPane listScroll;
    private Buttons buttons;
    private Preview preview;
    private Editor editor;

    public LevelEditor() {
        setLayout(null);
        setPreferredSize(new Dimension(EDITOR_WIDTH, EDITOR_HEIGHT));
        setDoubleBuffered(true);
        setBackground(Color.BLACK);

        buttons = new Buttons(this);
        preview = new Preview();
       
        editor = new Editor(preview);
        editor.setScale(2);
        
        list = new TexturesList();
        listScroll = new JScrollPane(list);
        listScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        resized(EDITOR_WIDTH, EDITOR_HEIGHT);
        
        add(listScroll);
        add(buttons);
        add(preview);
        add(editor);
        
        // fill level
        for (int x = 0; x < LevelEditor.LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LevelEditor.LEVEL_WIDTH; y++) {
                LevelEditor.level[x][y] = new Texture(Texture.Tile.FLOOR);
            }
        }
    }
    
    public void init() {
        editor.createGraphics();
    }
    
    public final void resized(int w, int h) {
        list.setBounds(0, 0, 144, h - 300);
        listScroll.setBounds(12, 12, 144, h - 279);
        buttons.setBounds(12, h - 258, 144, 92);
        preview.setBounds(12, h - 157, 144, 144);
        editor.setBounds(169, 1, w - 170, h - 2);
    }
    
    public void newLevel() {
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {
                level[x][y] = new Texture(Texture.Tile.FLOOR);
            }
        }
        
        editor.repaint();
        preview.repaint();
    }
   
    public void loadLevel() {
        JFileChooser fc = new JFileChooser("levels");
        fc.setDialogTitle("Open Level");
        fc.setFileFilter(new FileNameExtensionFilter("BMP (*.BMP)", "bmp"));
        int retval = fc.showOpenDialog(this);

        if (retval == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage bufferedImage = ImageIO.read(fc.getSelectedFile());
                int w = bufferedImage.getWidth();
                int h = bufferedImage.getHeight();
                
                // validate size
                System.out.println(w);
                if (w > LEVEL_WIDTH || h > LEVEL_HEIGHT) {
                    JOptionPane.showMessageDialog(editor, "Level max size is 48x48", "Error...", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int[] rgbs = new int[w * h];

                bufferedImage.getRGB(0, 0, w, h, rgbs, 0, w);

                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        int col = rgbs[x + y * w] & 0xffffff;

                        Texture tile = new Texture(Texture.Tile.FLOOR);
                        if (col == 0xA8A800) {
                            //tile = new SandTile();
                        } else if (col == 0x969696) {
                            tile = new Texture(Texture.Tile.UNBREAKABLERAIL);
                        } else if (col == 0x888800) {
                            //tile = new UnpassableSandTile();
                        } else if (col == 0xFF7777) {
                            tile = new Texture(Texture.Tile.DESTROYABLEWALL);
                        } else if (col == 0x000000) {
                            tile = new Texture(Texture.Tile.HOLE);
                        } else if (col == 0xff0000) {
                            tile = new Texture(Texture.Tile.WALL);
                        } else if (col == 0xffff00) {
                            tile = new Texture(Texture.Tile.TREASURE);
                        }

                        level[x][y] = tile;
                    }
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(editor, "Error loading level: " + ex, "Error...", JOptionPane.ERROR_MESSAGE);
            }

            editor.moveToSpawn();
            editor.repaint();
            preview.repaint();
        }
    }
    
    public void saveLevel() {       
        JFileChooser fc = new JFileChooser("levels");
        fc.setSelectedFile(new File("level.bmp"));
        fc.setDialogTitle("Save Level");
        int retval = fc.showSaveDialog(this);
        
        if (retval == JFileChooser.APPROVE_OPTION) {

            BufferedImage image = new BufferedImage(LEVEL_WIDTH, LEVEL_HEIGHT, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < LEVEL_HEIGHT; x++) {
                for (int y = 0; y < LEVEL_WIDTH; y++) {
                    image.setRGB(x, y, level[x][y].tile.COLOR);
                }
            }

            try {
                ImageIO.write(image, "BMP", fc.getSelectedFile());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(editor, "Level not saved: " + ex, "Error...", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int h = getHeight();

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int i = 0; i < h; i++) {
            g2.drawImage(Art.gui, 0, i * 1, 169, i * 1 + 1, 0, 23, 169, 24, null);
        }
        
        g2.drawImage(Art.gui, 0, 0, 169, 23, 0, 0, 169, 23, null);
        g2.drawImage(Art.gui, 0, h - 23, 169, h, 0, 24, 169, 47, null);
    }

    public static void main(String[] args) {
        

        
        final LevelEditor mapEditor = new LevelEditor();
        final JFrame f = new JFrame();
        f.add(mapEditor);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(EDITOR_WIDTH, EDITOR_HEIGHT);
        f.setLocationRelativeTo(null);
        f.setTitle("Catacomb Snatch Level Editor v1.0 By irvin");
        f.pack();
        f.setVisible(true);
        
        f.addComponentListener(new java.awt.event.ComponentAdapter() {

            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                try {
                    mapEditor.resized(f.getContentPane().getWidth(), f.getContentPane().getHeight());
                    mapEditor.init();
                } catch (Exception ex) {
                }
            }
        });
        
        mapEditor.init();
    }
}