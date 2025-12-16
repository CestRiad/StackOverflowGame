package com.stackoverflowgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;

// Représente le bumper
// Contrôle via les flèches gauche/droite, propriétés définies dans Tiled.

public class Bumper extends Entity {

    private float speed;
    private float displayWidth, displayHeight;

    public Bumper(Texture texture, float x, float y, float width, float height) {
        super(texture, x, y, width, height);
        this.displayWidth = width;
        this.displayHeight = height;
    }

    // Applique les propriétés Tiled : taille visuelle et vitesse
    public void applyTiledProperties(MapProperties props) {
        if (props.containsKey("displayWidth"))
            displayWidth = ((Number) props.get("displayWidth")).floatValue();
        if (props.containsKey("displayHeight"))
            displayHeight = ((Number) props.get("displayHeight")).floatValue();
        if (props.containsKey("speed"))
            speed = ((Number) props.get("speed")).floatValue();
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  x -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) x += speed * delta;

        float screenW = Gdx.graphics.getWidth(), overflow = displayWidth / 4f;
        if (x < -overflow) x = -overflow;
        if (x + width > screenW + overflow) x = screenW + overflow - width;
    }

    @Override
    public void render(SpriteBatch batch) {
        float offsetX = (displayWidth - width) / 2f;
        float offsetY = (displayHeight - height) / 2f;
        batch.draw(texture, x - offsetX, y - offsetY, displayWidth, displayHeight);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Réinitialiser la position du bumper
    @Override
    public void reset(float startX, float startY) {
        this.x = startX;
        this.y = startY;
    }

    public float getDisplayWidth()  { return displayWidth; }
    public float getDisplayHeight() { return displayHeight; }
    public float getSpeed()         { return speed; }

 
    // DEBUG : afficher la hitbox
    // import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
    // import com.badlogic.gdx.graphics.Color;
    // public void debugRender(ShapeRenderer s) {
    //     s.setColor(Color.GREEN);
    //     Rectangle r = getBounds();
    //     s.rect(r.x, r.y, r.width, r.height);
    // }

}