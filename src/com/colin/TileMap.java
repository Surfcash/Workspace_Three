package com.colin;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static com.colin.MainApp.*;
import static processing.core.PApplet.floor;

class TileMap {
    Piece currentPiece;
    private ArrayList<Tile> tiles;
    PVector grid = new PVector(10, 20);
    float timePassed = 0;
    private int linesFinished = 0;
    private int level = 0;
    private float tickSpeed = 60F / (level + 1);
    private PApplet p;
    private int score = 0;
    boolean running = true;
    boolean paused = false;

    TileMap(PApplet parent) {
        p = parent;
        tiles = new ArrayList<>();
        currentPiece = new Piece(tiles);
    }

    void update() {
        if(running && !paused) {
            checkLevel();
            tickSpeed = 60F / (level + 1);
            checkForTetris();
            gameTick();
        }
    }

    void render() {
        renderGrid();
        renderTiles();
        currentPiece.render();
        renderScore();
        if(paused) {
            renderPaused();
        }
        if(!running) {
            renderGameOver();
        }
    }

    private void renderScore() {
        p.rectMode(p.CENTER);
        p.fill(255);
        p.stroke(0);
        p.strokeWeight(6);
        p.rect(p.width * 0.8F, p.height * 0.5F, 400, 200);

        p.textSize(50);
        p.fill(0);
        p.textAlign(p.LEFT, p.CENTER);
        p.text("Score: " + score + "\nLevel: " + level, p.width * 0.7F, p.height * 0.5F - 5);
    }

    private void renderGameOver() {
        p.rectMode(p.CENTER);
        p.stroke(0);
        p.strokeWeight(10);
        p.fill(75);
        p.rect(p.width / 2F, p.height / 2F, grid.x * tileSize, 85);

        p.textSize(75);
        p.fill(255);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("GAME OVER", p.width / 2F, p.height / 2F - 10);
    }

    private void renderPaused() {
        p.rectMode(p.CENTER);
        p.stroke(0);
        p.strokeWeight(10);
        p.fill(75);
        p.rect(p.width / 2F, p.height / 2F, grid.x * tileSize, 85);

        p.textSize(75);
        p.fill(255);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("PAUSED", p.width / 2F, p.height / 2F - 10);
    }

    private void renderTiles() {
        for (Tile i : tiles) {
            i.render();
        }
    }

    private void renderGrid() {
        p.rectMode(p.CORNER);
        p.fill(255);
        p.stroke(0);
        p.strokeWeight(6);
        p.rect(gridCorner.x - 3, gridCorner.y - 3, grid.x * tileSize + 6, grid.y * tileSize + 6);

        for (int i = 0; i < grid.x; i++) {
            for (int j = 0; j < grid.y; j++) {
                p.rectMode(p.CORNER);
                p.noFill();
                p.stroke(50);
                p.strokeWeight(1);
                p.rect(i * tileSize + gridCorner.x, j * tileSize + gridCorner.y, tileSize, tileSize);
            }
        }
    }

    private void gameTick() {
        if(timePassed >= tickSpeed) {
            if(currentPiece.checkCollisions()) {
                addCurrentPiece();
            }
            currentPiece.move(0, 1);
            timePassed = 0;
        }
        timePassed += deltaTime;
    }

    private void addCurrentPiece() {
        tiles.addAll(currentPiece.tiles);
        currentPiece = new Piece(tiles);
    }

    private void checkLevel() {
        level = linesFinished / 8;
    }

    private void checkForTetris() {
        int tileInLine;
        int linesCleared = 0;
        boolean lineCleared;
        ArrayList<Tile> rowRemove;
        do {
            lineCleared = false;
            for (int i = floor(grid.y - 1); i > 0; i--) {
                tileInLine = 0;
                rowRemove = new ArrayList<>();
                for (Tile j : tiles) {
                    if (j.pos.y == i) {
                        tileInLine++;
                    }
                }
                if (tileInLine >= 10) {
                    for (Tile j : tiles) {
                        if (j.pos.y == i) {
                            rowRemove.add(j);
                        }
                    }
                    for (Tile j : rowRemove) {
                        tiles.remove(j);
                    }
                    for (Tile j : tiles) {
                        if (j.pos.y < i) {
                            j.pos.y++;
                        }
                    }
                    linesCleared++;
                    lineCleared = true;
                }
            }
        } while(lineCleared);

        switch(linesCleared) {
            case 1 : {
                score += 40 * (level + 1);
                break;
            }
            case 2 : {
                score += 100 * (level + 1);
                break;
            }
            case 3 : {
                score += 300 * (level + 1);
                break;
            }
            case 4: {
                score += 1200 * (level + 1);
            }
        }
        linesFinished += linesCleared;
    }

    class Piece implements Cloneable{
        PVector pos = new PVector(4, -1);
        ArrayList<Tile> mapTiles;
        ArrayList<Tile> tiles = new ArrayList<>();
        int color;

        Piece(ArrayList<Tile> mapTiles) {
            this.mapTiles = mapTiles;
            initType();
            if(checkCollisions()) {
                tilemap.running = false;
            }
        }

        void initType() {
            float rand = p.random(1);

            if(rand > 0.85) {
                color = p.color(0);
                tiles.add(new Tile(pos.x - 1, pos.y, color));
                tiles.add(new Tile(pos.x, pos.y, color));
                tiles.add(new Tile(pos.x + 1, pos.y, color));
                tiles.add(new Tile(pos.x, pos.y + 1, color));
            } else if(rand > 0.70) {
                color = p.color(32);
                tiles.add(new Tile(pos.x, pos.y, color));
                tiles.add(new Tile(pos.x + 1, pos.y, color));
                tiles.add(new Tile(pos.x, pos.y + 1, color));
                tiles.add(new Tile(pos.x + 1, pos.y + 1, color));
            } else if(rand > 0.55) {
                color = p.color(64);
                tiles.add(new Tile(pos.x, pos.y, color));
                tiles.add(new Tile(pos.x, pos.y + 1, color));
                tiles.add(new Tile(pos.x + 1, pos.y + 1, color));
                tiles.add(new Tile(pos.x + 1, pos.y + 2, color));
            } else if(rand > 0.40) {
                color = p.color(96);
                tiles.add(new Tile(pos.x, pos.y - 1, color));
                tiles.add(new Tile(pos.x, pos.y, color));
                tiles.add(new Tile(pos.x, pos.y + 1, color));
                tiles.add(new Tile(pos.x + 1, pos.y + 1, color));
            } else if(rand > 0.25) {
                color = p.color(128);
                tiles.add(new Tile(pos.x, pos.y - 1, color));
                tiles.add(new Tile(pos.x, pos.y, color));
                tiles.add(new Tile(pos.x, pos.y + 1, color));
                tiles.add(new Tile(pos.x - 1, pos.y + 1, color));
            } else if(rand > 0.10) {
                color = p.color(160);
                tiles.add(new Tile(pos.x, pos.y - 1, color));
                tiles.add(new Tile(pos.x, pos.y, color));
                tiles.add(new Tile(pos.x, pos.y + 1, color));
                tiles.add(new Tile(pos.x, pos.y + 2, color));
            } else {
                color = p.color(192);
                tiles.add(new Tile(pos.x + 1, pos.y, color));
                tiles.add(new Tile(pos.x + 1, pos.y + 1, color));
                tiles.add(new Tile(pos.x, pos.y + 1, color));
                tiles.add(new Tile(pos.x, pos.y + 2, color));
            }
        }

        void render() {
            renderHighlight();
            for(Tile i : tiles) {
                i.render();
            }
        }

        void renderHighlight() {
            float lowestPoint = grid.y - 1;
            for(Tile i : mapTiles) {
                for(Tile j : tiles) {
                    if(i.pos.x == j.pos.x) {
                        if(i.pos.y - j.pos.y - 1 < lowestPoint) lowestPoint = i.pos.y - j.pos.y - 1;
                    }
                }
            }

            for(Tile i : tiles) {
                if(i.pos.y + lowestPoint >= grid.y) {
                    lowestPoint = grid.y - 1 - i.pos.y;
                }
            }

            for(Tile i: tiles) {
                if(i.pos.y + lowestPoint >= 0 && i.pos.y + lowestPoint > i.pos.y) {
                    p.noFill();
                    p.stroke(25);
                    p.strokeWeight(5);
                    p.rectMode(p.CORNER);
                    p.rect(i.pos.x * tileSize + 1 + gridCorner.x, (i.pos.y + lowestPoint) * tileSize + 1 + gridCorner.y, tileSize - 2, tileSize - 2);
                }
            }
        }

        void quickDrop() {
            int tilesDropped = 0;
            do {
                move(0, 1);
                tilesDropped++;
            } while(!checkCollisions());
            tilemap.score += tilesDropped * 2;
            tilemap.timePassed = tickSpeed;
        }

        boolean checkCollisions() {
            for(Tile i : tiles) {
                for(Tile j : mapTiles) {
                    if(i.pos.x == j.pos.x && i.pos.y == j.pos.y - 1) {
                        return true;
                    }
                }
                if(i.pos.y == grid.y - 1) {
                    return true;
                }
            }
            return false;
        }

        boolean checkTileOverlap() {
            for(Tile i : tiles) {
                for(Tile j : mapTiles) {
                    if(i.pos.equals(j.pos)) {
                        return true;
                    }
                }
            }
            return false;
        }

        void rotate() {
            for(Tile i : tiles) {
                int deltaX = floor(i.pos.x - pos.x);
                int deltaY = floor(i.pos.y - pos.y);

                i.pos.y = pos.y + deltaX;
                i.pos.x = pos.x - deltaY;
                if(i.pos.x < 0) {
                    move(1, 0);
                }
                if(i.pos.x > grid.x - 1) {
                    move(-1, 0);
                }
                do {
                    for (Tile j : mapTiles) {
                        if(i.pos.equals(j.pos) && i.pos.x < pos.x) {
                            move(1, 0);
                        }
                        if(i.pos.equals(j.pos) && i.pos.x > pos.x) {
                            move(-1, 0);
                        }
                        if(i.pos.equals(j.pos) && i.pos.y > pos.y) {
                            move(0, -1);
                        }
                    }
                } while(checkTileOverlap());
            }
            tilemap.timePassed = 0;
        }

        void move(int x, int y) {
            boolean canMoveX = true;
            boolean canMoveY = true;
            PVector fPos;
            for(Tile i : tiles) {
                fPos = new PVector(i.pos.x + x, i.pos.y + y);
                if(fPos.x < 0 || fPos.x > grid.x - 1) {
                    canMoveX = false;
                }
                if(fPos.y > grid.y - 1) {
                    canMoveY = false;
                }
                for(Tile j : mapTiles) {
                    if(j.pos.equals(fPos)) {
                        canMoveX = false;
                        canMoveY = false;
                    }
                }
            }
            for(Tile i : tiles) {
                if(canMoveX) i.pos.x += x;
                if(canMoveY) i.pos.y += y;
            }
            if(canMoveX) pos.x += x;
            if(canMoveY) pos.y += y;
        }

        public Piece clone() throws CloneNotSupportedException{
            Piece piece = (Piece) super.clone();

            piece.tiles = new ArrayList<>(tiles);
            return piece;
        }
    }

    class Tile {
        PVector pos;
        int col;
        Tile(float x, float y, int color) {
            pos = new PVector(x, y);
            col = color;
        }

        void render() {
            if(pos.y >= 0) {
                p.fill(col);
                p.stroke(255);
                p.strokeWeight(1);
                p.rectMode(p.CORNER);
                p.rect(pos.x * tileSize + gridCorner.x, pos.y * tileSize + gridCorner.y, tileSize, tileSize);
            }
        }
    }
}
