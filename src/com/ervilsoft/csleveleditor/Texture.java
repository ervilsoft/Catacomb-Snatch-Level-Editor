package com.ervilsoft.csleveleditor;

import java.util.Random;

public class Texture {

    public Tile tile;
    public int image = 0;

    public Texture(Tile t) {
        tile = t;

        if (tile.IMAGES > 1) {
            Random r = new Random();
            image = r.nextInt(tile.IMAGES);
        }
    }

    public enum Tile {

        FLOOR(0xffffff, 1),
        SAND(0xA8A800, 1),
        UNBREAKABLERAIL(0x969696, 1),
        UNPASSABLESAND(0x888800, 1),
        DESTROYABLEWALL(0xFF7777, 1),
        HOLE(0x000000, 1),
        WALL(0xff0000, 4),
        TREASURE(0xffff00, 1);
        
        public int COLOR;
        public int IMAGES;

        Tile(int c, int i) {
            COLOR = c;
            IMAGES = i;
        }
    }
}