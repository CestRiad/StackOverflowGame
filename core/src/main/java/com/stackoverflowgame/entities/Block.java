package com.stackoverflowgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;

// Représente un bloc destructible (statique).
// hitbox = taille Tiled
// affichage = propriétés displayWidth/Height sur Tiled

public class Block extends Entity {

    private boolean destroyed = false;
    private float displayWidth, displayHeight;
    private static Rectangle customHitbox;
    private final float originalX, originalY; // position initiale

    public Block(Texture texture, float x, float y, float width, float height) {
        super(texture, x, y, width, height);
        this.originalX = x;
        this.originalY = y;
        this.displayWidth = width;
        this.displayHeight = height;
    }

    // Définit une hitbox spécifique depuis Tiled
    public static void setHitbox(Rectangle rect) {
        customHitbox = rect;
    }

    // Applique les propriétés custom Tiled (ex: displayWidth/Height)
    public void applyTiledProperties(MapProperties props) {
        if (props.containsKey("displayWidth"))
            displayWidth = ((Number) props.get("displayWidth")).floatValue();
        if (props.containsKey("displayHeight"))
            displayHeight = ((Number) props.get("displayHeight")).floatValue();
    }

    public void destroy() { destroyed = true; }

    // Réinitialise le bloc à sa position et état d’origine
    public void reset() {
        destroyed = false;
        x = originalX;
        y = originalY;
    }

    public boolean isDestroyed() { return destroyed; }

    @Override
    public void update(float delta) {
        // bloc statique
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!destroyed) {
            float offsetX = (displayWidth - width) / 2f;
            float offsetY = (displayHeight - height) / 2f;
            batch.draw(texture, x - offsetX, y - offsetY, displayWidth, displayHeight);
        }
    }

    @Override
    public Rectangle getBounds() {
        if (customHitbox != null)
            return new Rectangle(x + customHitbox.x, y + customHitbox.y,
                                 customHitbox.width, customHitbox.height);
        return new Rectangle(x, y, width, height);
    }

    public float getDisplayWidth()  { return displayWidth; }
    public float getDisplayHeight() { return displayHeight; }

    // DEBUG : afficher la hitbox
    // import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
    // import com.badlogic.gdx.graphics.Color;
    // public void debugRender(ShapeRenderer s) {
    //     if (!destroyed) {
    //         s.setColor(Color.YELLOW);
    //         Rectangle r = getBounds();
    //         s.rect(r.x, r.y, r.width, r.height);
    //     }
    // }
    
}