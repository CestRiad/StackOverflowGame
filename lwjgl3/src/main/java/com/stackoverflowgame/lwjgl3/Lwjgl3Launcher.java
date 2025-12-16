package com.stackoverflowgame.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.stackoverflowgame.StackOverflowGame;

public class Lwjgl3Launcher {

    public static void main(String[] args) {
        // Dans LibGDX, le constructeur de Lwjgl3Application démarre directement la boucle de jeu. L’instance n’a pas vocation à être manipulée ensuite.
        new Lwjgl3Application(new StackOverflowGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // Nom de la fenêtre
        config.setTitle("StackOverflowGame");

        // Taille basée sur ta map Tiled
        int tileWidth = 32;
        int tileHeight = 32;
        int mapWidth = 40;
        int mapHeight = 23;

        int windowWidth = tileWidth * mapWidth;
        int windowHeight = tileHeight * mapHeight;

        config.setWindowedMode(windowWidth, windowHeight);

        // Fenêtre fixe (important pour collisions / Tiled)
        config.setResizable(false);

        // Perf
        config.useVsync(true);
        config.setForegroundFPS(60);

        return config;
    }
}