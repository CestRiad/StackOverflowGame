package com.stackoverflowgame.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.stackoverflowgame.entities.Ball;
import com.stackoverflowgame.entities.Block;
import com.stackoverflowgame.entities.Bumper;
import com.stackoverflowgame.entities.Entity;

// Fabrique des entités, on les charges depuis Tiled et on applique les propriétées (taille, vitesse, couleur, score...)

public class EntityFactory {

    public static float initialBumperX = 0, initialBumperY = 0;
    public static float initialBallX = 0, initialBallY = 0;

    private static final Random rand = new Random();

    public static List<Entity> loadEntitiesFromMap(TiledMap map) {
        List<Entity> entities = new ArrayList<>();

        System.out.println("\n===============================");
        System.out.println("CHARGEMENT DES ENTITÉS TILED");
        System.out.println("===============================");

        for (MapLayer layer : map.getLayers()) {
            for (MapObject obj : layer.getObjects()) {

                String name = (obj.getName() != null) ? obj.getName().trim().toLowerCase() : "";

                // Tentative de déduction du type si pas de nom
                if (name.isEmpty()) {
                    if (obj instanceof TiledMapTileMapObject tileObj) {
                        TiledMapTile tile = tileObj.getTile();
                        if (tile != null) {
                            if (layer.getName().toLowerCase().contains("block") ||
                                tile.getProperties().toString().toLowerCase().contains("block")) {
                                name = "block";
                            }
                        }
                    } else if (layer.getName().toLowerCase().contains("block")) {
                        name = "block";
                    }
                }

                if (name.isEmpty()) continue;

                float x = 0, y = 0, width = 0, height = 0;

                // Rectangle
                if (obj instanceof RectangleMapObject rectObj) {
                    x = rectObj.getRectangle().x;
                    y = rectObj.getRectangle().y;
                    width = rectObj.getRectangle().width;
                    height = rectObj.getRectangle().height;
                }

                // Objet issu d’un tileset
                else if (obj instanceof TiledMapTileMapObject tileObj) {
                    TiledMapTile tile = tileObj.getTile();
                    x = tileObj.getX();
                    y = tileObj.getY();

                    if (obj.getProperties().containsKey("width"))
                        width = ((Number) obj.getProperties().get("width")).floatValue();
                    else
                        width = tile.getTextureRegion().getRegionWidth();

                    if (obj.getProperties().containsKey("height"))
                        height = ((Number) obj.getProperties().get("height")).floatValue();
                    else
                        height = tile.getTextureRegion().getRegionHeight();

                    if (tile.getObjects() != null && tile.getObjects().getCount() > 0) {
                        System.out.printf(" Tile '%s' contient %d objet(s) de collision%n",
                                name, tile.getObjects().getCount());
                        for (MapObject shape : tile.getObjects()) {
                            if (shape instanceof EllipseMapObject ellipse)
                                System.out.printf("    Ellipse (%.1f, %.1f, %.1f, %.1f)%n",
                                        ellipse.getEllipse().x, ellipse.getEllipse().y,
                                        ellipse.getEllipse().width, ellipse.getEllipse().height);
                            if (shape instanceof RectangleMapObject rect)
                                System.out.printf("    Rectangle (%.1f, %.1f, %.1f, %.1f)%n",
                                        rect.getRectangle().x, rect.getRectangle().y,
                                        rect.getRectangle().width, rect.getRectangle().height);
                        }
                    }
                } else continue;

                // Création de l’entité selon son type
                switch (name) {

                    case "ball" -> {
                        Ball ball = new Ball(
                                new Texture(Gdx.files.internal("tilesets/ball.png")),
                                x, y, width, height
                        );
                        ball.applyTiledProperties(obj.getProperties());
                        entities.add(ball);
                        initialBallX = x;
                        initialBallY = y;
                        System.out.printf("OK BALL → (x=%.1f, y=%.1f) %.1fx%.1f%n", x, y, width, height);
                    }

                    case "bumper" -> {
                        Bumper bumper = new Bumper(
                                new Texture(Gdx.files.internal("tilesets/bumper.png")),
                                x, y, width, height
                        );
                        bumper.applyTiledProperties(obj.getProperties());
                        entities.add(bumper);
                        initialBumperX = x;
                        initialBumperY = y;
                        System.out.printf("OK BUMPER → (x=%.1f, y=%.1f) %.1fx%.1f%n", x, y, width, height);
                    }

                    case "block" -> {
                        // Valeurs par défaut
                        String color = "orange";
                        int hp = 1;
                        boolean destroyable = true;
                        int scoreValue = 10;

                        // Lecture des propriétés custom (si elles st présentent dans tiled)
                        if (obj.getProperties().containsKey("color"))
                            color = obj.getProperties().get("color", String.class).toLowerCase();
                        if (obj.getProperties().containsKey("hp"))
                            hp = obj.getProperties().get("hp", Integer.class);
                        if (obj.getProperties().containsKey("destroyable"))
                            destroyable = obj.getProperties().get("destroyable", Boolean.class);
                        if (obj.getProperties().containsKey("scoreValue"))
                            scoreValue = obj.getProperties().get("scoreValue", Integer.class);

                        // Couleur aléatoire si pas définie
                        if (color.isEmpty() || color.equals("orange")) {
                            String[] randomColors = {"red", "green", "orange"};
                            color = randomColors[rand.nextInt(randomColors.length)];
                        }

                        // Sélection texture selon couleur
                        String texturePath = switch (color) {
                            case "red" -> "tilesets/blocks/block_red.png";
                            case "green" -> "tilesets/blocks/block_green.png";
                            default -> "tilesets/blocks/block_orange.png";
                        };

                        // Création du bloc
                        Block block = new Block(
                                new Texture(Gdx.files.internal(texturePath)),
                                x, y, width, height
                        );

                        // Lecture displayWidth/Height
                        float displayWidth = width;
                        float displayHeight = height;

                        if (obj.getProperties().containsKey("displayWidth"))
                            displayWidth = ((Number) obj.getProperties().get("displayWidth")).floatValue();
                        else if (obj instanceof TiledMapTileMapObject tileObj) {
                            TiledMapTile tile = tileObj.getTile();
                            if (tile.getProperties().containsKey("displayWidth"))
                                displayWidth = ((Number) tile.getProperties().get("displayWidth")).floatValue();
                        }

                        if (obj.getProperties().containsKey("displayHeight"))
                            displayHeight = ((Number) obj.getProperties().get("displayHeight")).floatValue();
                        else if (obj instanceof TiledMapTileMapObject tileObj) {
                            TiledMapTile tile = tileObj.getTile();
                            if (tile.getProperties().containsKey("displayHeight"))
                                displayHeight = ((Number) tile.getProperties().get("displayHeight")).floatValue();
                        }

                        // Injection des propriétés calculées
                        obj.getProperties().put("displayWidth", displayWidth);
                        obj.getProperties().put("displayHeight", displayHeight);
                        obj.getProperties().put("hp", hp);
                        obj.getProperties().put("destroyable", destroyable);
                        obj.getProperties().put("scoreValue", scoreValue);

                        block.applyTiledProperties(obj.getProperties());
                        entities.add(block);

                        System.out.printf("""
                                 BLOCK → (x=%.1f, y=%.1f)
                                    hitbox=%.1fx%.1f |  display=%.1fx%.1f
                                    color=%s | ❤️ hp=%d |  destroyable=%b |  score=%d
                                """,
                                x, y, width, height,
                                block.getDisplayWidth(), block.getDisplayHeight(),
                                color, hp, destroyable, scoreValue);
                    }
                }
            }
        }

        System.out.println("===============================");
        System.out.println(" FIN DU CHARGEMENT DES ENTITÉS TILED");
        System.out.println("===============================");
        return entities;
    }
}