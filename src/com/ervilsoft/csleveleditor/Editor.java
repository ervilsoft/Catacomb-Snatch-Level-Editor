package com.ervilsoft.csleveleditor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Editor extends Canvas implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private Image offscreen;
    private Graphics screen;

    private double scale;
    
    private int lvlW;
    private int lvlH;
    private int lvlX;
    private int lvlY;
    
    private Point mapLastPos = new Point();
    private Point clickPos = new Point();
    
    private boolean dragging;
    private boolean painting;
    private boolean erasing;
    
    public static Texture.Tile pencil = Texture.Tile.FLOOR;
    
    private Preview preview;
    
    public Editor(Preview pre) {
        preview = pre;
        
        setBackground(Color.black);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    public void setScale(double s) {
        // don't cross negative threshold. also, setting scale to 0 has bad effects
        scale = Math.max(0.1, scale + s);

        lvlW = (LevelEditor.LEVEL_WIDTH - 1) * scaleValue(LevelEditor.TILE_WIDTH);
        lvlH = LevelEditor.LEVEL_HEIGHT * scaleValue(LevelEditor.TILE_HEIGHT);
    }
    
    public int scaleValue(int value) {
        return (int) (value * scale);
    }

    public void moveToSpawn() {
        lvlX = -((lvlW / 2) - (getWidth() / 2));

        if (lvlH > getHeight()) {
            lvlY = -(lvlH - getHeight()); // move to the bottom
        } else {
            lvlY = -(lvlH / 2 - (getHeight() / 2)); // move to the center
        }
    }

    public void createGraphics() {
        offscreen = createImage(getWidth(), getHeight());
        screen = offscreen.getGraphics();
    }
    
    public void draw(int x, int y) {
        draw(x, y, pencil);
    }
    
    public void draw(int x, int y, Texture.Tile tile) {
        // convert current pos to map pos
        x -= lvlX;
        y -= lvlY;
        
        int scaleSize = (int) (32 * scale);      
        
        int dX = x / scaleSize;
        int dY = y / scaleSize;
        
        // min and max 
        if(dX < 0 || dX > LevelEditor.LEVEL_WIDTH - 1) return;
        if(dY < 0 || dY > LevelEditor.LEVEL_HEIGHT -1) return;
        
        // same texture
        if (LevelEditor.level[dX][dY].tile == tile) return;

        LevelEditor.level[dX][dY] = new Texture(tile);
        
        preview.repaint();
    }
    
    @Override
    public void paint(Graphics g) {       
        screen.clearRect(0, 0, getWidth(), getHeight());
        
        int scaleSize = (int) (32 * scale);

        for (int x = 0; x < LevelEditor.LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LevelEditor.LEVEL_WIDTH; y++) {

                if (LevelEditor.level[x][y].tile == Texture.Tile.FLOOR) {
                    screen.drawImage(Art.floorTiles[LevelEditor.level[x][y].image][0], scaleSize * x + lvlX, scaleSize * y + lvlY, scaleSize, scaleSize, null);
                } else if (LevelEditor.level[x][y].tile == Texture.Tile.WALL) {
                    int scaleX = (int) (Art.wallTiles[0][0].getWidth() * scale);
                    int scaleY = (int) (Art.wallTiles[0][0].getHeight() * scale);
                    int halfpos = (int)((32 - (64 - Art.wallTiles[0][0].getHeight())) * scale);
                    
                    screen.setColor(new Color(255, 0, 255));
                    screen.drawImage(Art.wallTiles[LevelEditor.level[x][y].image][0], scaleSize * x + lvlX, (scaleSize * y + lvlY) - halfpos, scaleX, scaleY, null);
                } else if (LevelEditor.level[x][y].tile == Texture.Tile.DESTROYABLEWALL) {
                    int scaleX = (int) (Art.treasureTiles[4][0].getWidth() * scale);
                    int scaleY = (int) (Art.treasureTiles[4][0].getHeight() * scale);
                    int halfpos = (int)((32 - (64 - Art.treasureTiles[4][0].getHeight())) * scale);
                    
                    screen.setColor(new Color(255, 0, 255));
                    screen.drawImage(Art.treasureTiles[4][0], scaleSize * x + lvlX, (scaleSize * y + lvlY) - halfpos, scaleX, scaleY, null);
                } else if (LevelEditor.level[x][y].tile == Texture.Tile.TREASURE) {
                    int scaleX = (int) (Art.treasureTiles[0][0].getWidth() * scale);
                    int scaleY = (int) (Art.treasureTiles[0][0].getHeight() * scale);
                    int halfpos = (int)((32 - (64 - Art.treasureTiles[0][0].getHeight())) * scale);
                    
                    screen.setColor(new Color(255, 0, 255));
                    screen.drawImage(Art.treasureTiles[0][0], scaleSize * x + lvlX, (scaleSize * y + lvlY) - halfpos, scaleX, scaleY, null);
                }else if (LevelEditor.level[x][y].tile == Texture.Tile.HOLE) {
                    if (y > 0 && !(LevelEditor.level[x][y - 1].tile == Texture.Tile.HOLE)) {
                        screen.drawImage(Art.floorTiles[4][0], scaleSize * x + lvlX, scaleSize * y + lvlY, scaleSize, scaleSize, null);
                    }else{
                        screen.setColor(Color.BLACK);
                        screen.fillRect(scaleSize * x + lvlX, scaleSize * y + lvlY, scaleSize, scaleSize);
                    }
                }else if (LevelEditor.level[x][y].tile == Texture.Tile.UNBREAKABLERAIL) {
                    boolean n = y > 0 && LevelEditor.level[x][y-1].tile == Texture.Tile.UNBREAKABLERAIL;
                    boolean s = y < 47 && LevelEditor.level[x][y+1].tile == Texture.Tile.UNBREAKABLERAIL;
                    boolean w = x > 0 && LevelEditor.level[x-1][y].tile == Texture.Tile.UNBREAKABLERAIL;
                    boolean e = x < 47 && LevelEditor.level[x+1][y].tile == Texture.Tile.UNBREAKABLERAIL;

                    int c = (n ? 1 : 0) + (s ? 1 : 0) + (w ? 1 : 0) + (e ? 1 : 0);
                    int img;
                    
                    if (c <= 1) {
                        img = (n || s) ? 1 : 0;     // default is horizontal
                    } else if (c == 2) { 
                        if (n && s) img = 1;        // vertical
                        else if (w && e) img = 0;   // horizontal
                        else {
                            img = n ? 4 : 2;        // north turn
                            img += e ? 0 : 1;       // south turn
                        }
                    } else {                        // 3 or more turning disk
                        img = 6;
                    }
                    
                    screen.drawImage(Art.floorTiles[0][0], scaleSize * x + lvlX, scaleSize * y + lvlY, scaleSize, scaleSize, null);
                    screen.drawImage(Art.railsTiles[img][0], scaleSize * x + lvlX, (scaleSize * y + lvlY) - (int)(6*scale), scaleSize, (int)(Art.railsTiles[img][0].getHeight() * scale), null);
                } else {
                    screen.setColor(new Color(255, 0, 255));
                    screen.fillRect(scaleSize * x + lvlX, scaleSize * y + lvlY, scaleSize, scaleSize);
                }

            }
        }

        g.drawImage(offscreen, 0, 0, this);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 1) {
            draw(e.getPoint().x, e.getPoint().y);
        }else if (e.getButton() == 2) {
            draw(e.getPoint().x, e.getPoint().y, Texture.Tile.FLOOR);
        }
        
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
            painting = true;
        } else if (e.getButton() == 2) {
            erasing = true;
        } else if (e.getButton() == 3) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            clickPos = e.getPoint();

            mapLastPos.x = lvlX;
            mapLastPos.y = lvlY;

            dragging = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        
        dragging = false;
        painting = false;
        erasing = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (dragging) {
            int movedX = (mapLastPos.x + e.getX()) - (mapLastPos.x + clickPos.x);
            int movedY = (mapLastPos.y + e.getY()) - (mapLastPos.y + clickPos.y);

            lvlX = mapLastPos.x + movedX;
            lvlY = mapLastPos.y + movedY;
        }
        
        if (painting) {
            draw(e.getPoint().x, e.getPoint().y);
        }
        
        if (erasing) {
            draw(e.getPoint().x, e.getPoint().y, Texture.Tile.FLOOR);
        }

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {        
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

            // make it a reasonable amount of zoom .1 gives a nice slow transition
            double s = (0.1 * (e.getWheelRotation() * -1));

            setScale(s);
            repaint();
        }
    }
}