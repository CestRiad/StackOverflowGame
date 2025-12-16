package com.stackoverflowgame.utils;

import com.badlogic.gdx.math.Rectangle;
import com.stackoverflowgame.entities.Ball;

// Utilitaires de détection et de gestion des collisions.

public class CollisionUtils {

    // Vérifie si deux rectangles se chevauchent
    public static boolean checkCollision(Rectangle a, Rectangle b) {
        return a.overlaps(b);
    }

    // Gère le rebond de la balle sur un obstacle (mur, bloc, bumper...) 
    public static void handleBounce(Ball ball, Rectangle obstacle) {
        Rectangle ballRect = ball.getBounds();
        if (!ballRect.overlaps(obstacle)) return;

        // chevauchement sur les deux axes
        float overlapX = Math.min(ballRect.x + ballRect.width, obstacle.x + obstacle.width)
                       - Math.max(ballRect.x, obstacle.x);
        float overlapY = Math.min(ballRect.y + ballRect.height, obstacle.y + obstacle.height)
                       - Math.max(ballRect.y, obstacle.y);

        // collision horizontale
        if (overlapX < overlapY) {
            if (ball.getX() < obstacle.x)
                ball.setX(obstacle.x - ball.getWidth());
            else
                ball.setX(obstacle.x + obstacle.width);
            ball.reverseDX();
        }
        // collision verticale
        else {
            if (ball.getY() > obstacle.y)
                ball.setY(obstacle.y + obstacle.height);
            else
                ball.setY(obstacle.y - ball.getHeight());
            ball.reverseDY();
        }
    }

    
    // DEBUG : affiche les zones de collision (à activer si besoin)
    // import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
    // import com.badlogic.gdx.graphics.Color;
    // public static void debugCollision(ShapeRenderer s, Rectangle a, Rectangle b) {
    //     s.setColor(Color.RED);
    //     s.rect(a.x, a.y, a.width, a.height);
    //     s.setColor(Color.BLUE);
    //     s.rect(b.x, b.y, b.width, b.height);
    // }
    
}