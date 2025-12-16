package com.stackoverflowgame.utils;

// Contient les constantes de configuration du jeu selon le niveau de difficulté. Utilisée par GameScreen et les entités (Ball, Bumper...).

public class GameConfig {

    // Difficultés disponibles
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    // Valeur par défaut
    public static Difficulty currentDifficulty = Difficulty.MEDIUM;

    // Configuration des vitesses selon la difficulté
    public static float getBallMaxSpeed() {
        return switch (currentDifficulty) {
            case EASY -> 600f;
            case MEDIUM -> 800f;
            case HARD -> 1000f;
        };
    }

    public static float getBallMinSpeed() {
        return switch (currentDifficulty) {
            case EASY -> 200f;
            case MEDIUM -> 350f;
            case HARD -> 450f;
        };
    }

    public static float getBumperSpeed() {
        return switch (currentDifficulty) {
            case EASY -> 500f;
            case MEDIUM -> 700f;
            case HARD -> 900f;
        };
    }

    public static float getGravity() {
        return switch (currentDifficulty) {
            case EASY -> -700f;
            case MEDIUM -> -900f;
            case HARD -> -1100f;
        };
    }

    public static float getFallSpeed() {
        return switch (currentDifficulty) {
            case EASY -> -350f;
            case MEDIUM -> -450f;
            case HARD -> -600f;
        };
    }

    // Fichier TMX selon la difficulté
    public static String getLevelPath() {
        return switch (currentDifficulty) {
            case EASY -> "maps/level1.tmx";
            case MEDIUM -> "maps/level2.tmx";
            case HARD -> "maps/level3.tmx";
        };
    }

    // Passer à la difficulté suivante
    public static void nextDifficulty() {
        currentDifficulty = switch (currentDifficulty) {
            case EASY -> Difficulty.MEDIUM;
            case MEDIUM -> Difficulty.HARD;
            case HARD -> Difficulty.EASY;
        };
    }
}