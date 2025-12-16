package com.stackoverflowgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;

 // Classe représentant la balle du jeu.
 // position et vitesse (dx, dy)
 // taille d’affichage (displayWidth/displayHeight)
 // vitesses min/max (maxSpeed/minSpeed)
 // gravité et vitesse de chute initiale lues depuis Tiled

public class Ball extends Entity {

    private float dx, dy;

    private static Rectangle customHitbox;
    private static Ellipse customEllipse;

    private float maxSpeed, minSpeed;
    private float displayWidth, displayHeight;
    private final float originalX;
    private final float originalY;
    
    // valeurs physiques provenant de Tiled
    private float gravity;
    private float fallSpeed;

    public Ball(Texture texture, float x, float y, float width, float height) {
        super(texture, x, y, width, height);
        this.originalX = x;
        this.originalY = y;
        this.displayWidth = width;
        this.displayHeight = height;
    }

    // config des hitbox
    public static void setHitbox(Rectangle rect) { customHitbox = rect; }

    public static void setEllipseHitbox(Ellipse ellipse, float offsetX, float offsetY) {
        customEllipse = new Ellipse(
            ellipse.x + offsetX, ellipse.y + offsetY,
            ellipse.width, ellipse.height
        );
    }

    // lecture des propriétés depuis tiled
    public void applyTiledProperties(MapProperties props) {
        if (props.containsKey("displayWidth"))
            displayWidth = ((Number) props.get("displayWidth")).floatValue();

        if (props.containsKey("displayHeight"))
            displayHeight = ((Number) props.get("displayHeight")).floatValue();

        if (props.containsKey("maxSpeed"))
            maxSpeed = ((Number) props.get("maxSpeed")).floatValue();

        if (props.containsKey("minSpeed"))
            minSpeed = ((Number) props.get("minSpeed")).floatValue();

        if (props.containsKey("gravity"))
            gravity = ((Number) props.get("gravity")).floatValue();

        if (props.containsKey("fallSpeed"))
            fallSpeed = ((Number) props.get("fallSpeed")).floatValue();
    }

    // MAJ
    @Override
    public void update(float delta) {
        x += dx * delta;
        y += dy * delta;

        if (Math.abs(dx) > maxSpeed) dx = Math.signum(dx) * maxSpeed;
        if (Math.abs(dy) > maxSpeed) dy = Math.signum(dy) * maxSpeed;
        if (Math.abs(dx) < minSpeed) dx = Math.signum(dx) * minSpeed;
        if (Math.abs(dy) < minSpeed) dy = Math.signum(dy) * minSpeed;
    }

    // rendu
    @Override
    public void render(SpriteBatch batch) {
        float offsetX = (displayWidth - width) / 2f;
        float offsetY = (displayHeight - height) / 2f;
        batch.draw(texture, x - offsetX, y - offsetY, displayWidth, displayHeight);
    }

    // les collisions
    @Override
    public Rectangle getBounds() {
        if (customEllipse != null)
            return new Rectangle(x + customEllipse.x, y + customEllipse.y,
                                 customEllipse.width, customEllipse.height);
        if (customHitbox != null)
            return new Rectangle(x + customHitbox.x, y + customHitbox.y,
                                 customHitbox.width, customHitbox.height);
        return new Rectangle(x, y, width, height);
    }

    // les outils
    public void reverseDX() { dx = -dx; }
    public void reverseDY() { dy = -dy; }

    public void resetToOriginal() {
        x = originalX;
        y = originalY;
        dx = 0;
        dy = 0;
    }

    // les getters et les setters
    public float getDX() { return dx; }
    public float getDY() { return dy; }
    public void setDX(float dx) { this.dx = dx; }
    public void setDY(float dy) { this.dy = dy; }

    public float getGravity() { return gravity; }
    public float getFallSpeed() { return fallSpeed; }
}