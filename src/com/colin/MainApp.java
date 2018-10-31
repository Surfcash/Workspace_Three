package com.colin;

import processing.core.PApplet;
import processing.core.PVector;

public class MainApp extends PApplet{

    static boolean MOUSE_LEFT, IN_LEFT, IN_RIGHT, IN_UP, IN_DOWN, IN_ESCAPE, IN_ENTER = false;
    static float tileSize;
    static TileMap tilemap;
    private float previousMil;
    static float deltaTime;
    static PVector gridCorner;



    public static void main(String[] args) {
        String[] PApp = {"com.colin.MainApp"};
        PApplet.main(PApp);
    }

    public void setup() {
        surface.setTitle("Colin's Tetris");
        surface.setResizable(false);
        surface.setLocation(-3, -3);
        previousMil = millis();

        tileSize = 50;
        tilemap = new TileMap(this);
        gridCorner = new PVector(width / 2F - ((tilemap.grid.x * tileSize) / 2F), height / 2F - ((tilemap.grid.y * tileSize) / 2F));
    }

    public void settings() {
        size(displayWidth, displayHeight - 61, P2D);
        //PJOGL.setIcon("assets/icon.png");
    }

    public void draw() {
        updateDeltaTime();

        background(128);
        tilemap.update();
        tilemap.render();
    }

    private void updateDeltaTime() {
        deltaTime = (previousMil / millis()) * (60 / frameRate);
        previousMil = millis();
    }

    public void keyPressed() {
        if(key == ESC) {
            key = 0;
        }

        if(tilemap.running) {
            if(!tilemap.paused) {
                if (keyCode == 32) {
                    tilemap.currentPiece.quickDrop();
                }
                if (keyCode == 37) {
                    tilemap.currentPiece.move(-1, 0);
                    tilemap.timePassed = 0;
                }
                if (keyCode == 38) {
                    tilemap.currentPiece.rotate();
                    tilemap.timePassed = 0;
                }
                if (keyCode == 39) {
                    tilemap.currentPiece.move(1, 0);
                    tilemap.timePassed = 0;
                }
                if (keyCode == 40) {
                    tilemap.currentPiece.move(0, 1);
                }
            }
            if(keyCode == 27) {
                tilemap.paused = !tilemap.paused;
            }
        } else {
            if(keyCode == 10) {
                tilemap = new TileMap(this);
            }
        }
    }
}
