package com.stackoverflowgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

// Classe de base abstraite pour toutes les entités du jeu.
// Fournit la position, la taille, la texture et les méthodes de base.

public abstract class Entity {

    protected Texture texture;
    protected float x, y;
    protected float width, height;

    public Entity(Texture texture, float x, float y, float width, float height) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Mise à jour logique propre à chaque entité
    public abstract void update(float delta);

    // Affiche l'entité à l'écran
    public void render(SpriteBatch batch) {
        if (texture != null)
            batch.draw(texture, x, y, width, height);
    }

    // Retourne le rectangle de collision
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters / Setters
    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Texture getTexture() { return texture; }

    // Replace l'entité (utile lors d’un reset)
    public void reset(float newX, float newY) {
        this.x = newX;
        this.y = newY;
    }

    // Libère la texture associée
    public void dispose() {
        if (texture != null) texture.dispose();
    }
}