package com.stackoverflowgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.stackoverflowgame.entities.Ball;
import com.stackoverflowgame.entities.Block;
import com.stackoverflowgame.entities.Bumper;
import com.stackoverflowgame.entities.Entity;
import com.stackoverflowgame.utils.CollisionUtils;
import com.stackoverflowgame.utils.EntityFactory;
import com.stackoverflowgame.utils.GameConfig;


// +10 points par bloc détruit, reset du score à chaque niveau

public class GameScreen extends ScreenAdapter {

    private final StackOverflowGame game;
    private final OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private Ball ball;
    private Bumper bumper;
    private final List<Block> blocks = new ArrayList<>();
    private final List<Block> initialBlocks = new ArrayList<>();

    private boolean ballWaiting = true;
    private boolean ballFalling = false;
    private boolean gameStarted = false;
    private final Random rand = new Random();

    private float gravity;
    private float fallSpeed;
    private int score = 0;

    // --- UI ---
    private Rectangle menuButton;
    private boolean isMenuOpen = false;
    private final Rectangle[] menuOptions = new Rectangle[3];
    private final String[] levels = {"Facile", "Moyen", "Difficile"};
    private BitmapFont font;
    private Texture buttonTexture;
    private Texture scoreBubble;

    // --- Textes flottants (+10) ---
    private final List<FloatingText> floatingTexts = new ArrayList<>();

    public GameScreen(StackOverflowGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        initMenu();
        loadMap();
    }

    // Texte temporaire +10
    private static class FloatingText {
        String text;
        float x, y;
        float alpha = 1f;
        float life = 1f;

        FloatingText(String text, float x, float y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }

        void update(float delta) {
            life -= delta;
            y += 30 * delta;
            alpha = Math.max(0, life);
        }

        boolean isDead() { return life <= 0; }
    }

    // Initialise le menu et le score 
    private void initMenu() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        menuButton = new Rectangle(15, Gdx.graphics.getHeight() - 45, 95, 30);
        for (int i = 0; i < 3; i++)
            menuOptions[i] = new Rectangle(15, Gdx.graphics.getHeight() - 80 - (i * 35), 95, 28);

        buttonTexture = createMaterialButton((int) menuButton.width, (int) menuButton.height, new Color(0.13f, 0.13f, 0.13f, 0.8f), 10);
        scoreBubble = createMaterialButton(140, 30, new Color(0.13f, 0.13f, 0.13f, 0.8f), 10);
    }

    // Crée un bouton arrondi 
    private Texture createMaterialButton(int w, int h, Color bg, int radius) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setBlending(Pixmap.Blending.None);

        // fond (arrondi)
        p.setColor(bg);
        drawRoundedRect(p, 0, 0, w, h, radius);

        // ombre douce
        p.setColor(0, 0, 0, 0.25f);
        for (int i = 0; i < 2; i++)
            drawRoundedRect(p, i, i, w - 2 * i, h - 2 * i, radius);

        // bord léger
        p.setColor(1, 1, 1, 0.15f);
        drawRoundedRect(p, 0, 0, w, h, radius);

        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    // Dessine un rectangle arrondi
    private void drawRoundedRect(Pixmap p, int x, int y, int w, int h, int r) {
        p.fillRectangle(x + r, y, w - 2 * r, h);
        p.fillRectangle(x, y + r, w, h - 2 * r);
        p.fillCircle(x + r, y + r, r);
        p.fillCircle(x + w - r - 1, y + r, r);
        p.fillCircle(x + r, y + h - r - 1, r);
        p.fillCircle(x + w - r - 1, y + h - r - 1, r);
    }

    // Charge la carte correspondant à la difficulté
    private void loadMap() {
        if (map != null) map.dispose();
        blocks.clear();
        initialBlocks.clear();
        String mapPath = GameConfig.getLevelPath();
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1f);
        List<Entity> entities = EntityFactory.loadEntitiesFromMap(map);
        for (Entity e : entities) {
            if (e instanceof Ball b) ball = b;
            if (e instanceof Bumper bp) bumper = bp;
            if (e instanceof Block bl) {
                blocks.add(bl);
                initialBlocks.add(bl);
            }
        }
        gravity = GameConfig.getGravity();
        fallSpeed = GameConfig.getFallSpeed();
        resetBallOnBumper();
        score = 0;
        floatingTexts.clear();
    }

    @Override
    public void render(float delta) {
        updateLogic(delta);
        handleMenuInput();
        drawScene();
    }

    // Gère les clics du menu déroulant
    private void handleMenuInput() {
        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (menuButton.contains(mx, my)) {
                isMenuOpen = !isMenuOpen;
                return;
            }
            if (isMenuOpen) {
                for (int i = 0; i < menuOptions.length; i++) {
                    if (menuOptions[i].contains(mx, my)) {
                        switch (i) {
                            case 0 -> GameConfig.currentDifficulty = GameConfig.Difficulty.EASY;
                            case 1 -> GameConfig.currentDifficulty = GameConfig.Difficulty.MEDIUM;
                            case 2 -> GameConfig.currentDifficulty = GameConfig.Difficulty.HARD;
                        }
                        isMenuOpen = false;
                        loadMap();
                        return;
                    }
                }
            }
        }
    }

    // Logique du jeu
    private void updateLogic(float delta) {
        if (ball == null || bumper == null) return;
        bumper.update(delta);

        if (ballWaiting) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                ballFalling = true;
                ballWaiting = false;
            }
            return;
        }

        if (ballFalling) {
            fallSpeed += gravity * delta;
            ball.setY(ball.getY() + fallSpeed * delta);
            if (CollisionUtils.checkCollision(ball.getBounds(), bumper.getBounds())) {
                ball.setY(bumper.getY() + bumper.getHeight() - 5);
                float angle = rand.nextInt(60) + 45;
                float speed = GameConfig.getBallMaxSpeed();
                double rad = Math.toRadians(angle);
                float dir = rand.nextBoolean() ? 1f : -1f;
                ball.setDX((float) Math.cos(rad) * speed * dir);
                ball.setDY((float) Math.sin(rad) * speed);
                ballFalling = false;
                gameStarted = true;
            }
            return;
        }

        if (gameStarted) {
            ball.update(delta);
            if (ball.getX() <= 0) { ball.setX(0); ball.reverseDX(); }
            if (ball.getX() + ball.getWidth() >= Gdx.graphics.getWidth()) { ball.setX(Gdx.graphics.getWidth() - ball.getWidth()); ball.reverseDX(); }
            if (ball.getY() + ball.getHeight() >= Gdx.graphics.getHeight()) { ball.setY(Gdx.graphics.getHeight() - ball.getHeight()); ball.reverseDY(); }

            if (CollisionUtils.checkCollision(ball.getBounds(), bumper.getBounds()) && ball.getDY() < 0) {
                ball.setY(bumper.getY() + bumper.getHeight() - 5);
                ball.setDY(Math.abs(ball.getDY()) * 1.05f);
                float impact = (ball.getX() + ball.getWidth() / 2f) - (bumper.getX() + bumper.getWidth() / 2f);
                ball.setDX(impact * 5f);
            }

            for (Block block : blocks) {
                if (!block.isDestroyed() && CollisionUtils.checkCollision(ball.getBounds(), block.getBounds())) {
                    CollisionUtils.handleBounce(ball, block.getBounds());
                    block.destroy();
                    score += 10;
                    floatingTexts.add(new FloatingText("+10", block.getX() + block.getWidth() / 2f, block.getY() + block.getHeight()));
                }
            }

            floatingTexts.removeIf(FloatingText::isDead);
            floatingTexts.forEach(t -> t.update(delta));

            if (blocks.stream().allMatch(Block::isDestroyed)) {
                regenerateBlocks(true);
                centerBumper();
                resetBallOnBumper();
            }

            if (ball.getY() <= 0) resetGame(true);
        }
    }

    private void resetGame(boolean changeColors) {
        bumper.reset(EntityFactory.initialBumperX, EntityFactory.initialBumperY);
        ball.resetToOriginal();
        regenerateBlocks(changeColors);
        resetBallOnBumper();
        ballWaiting = true;
        ballFalling = false;
        gameStarted = false;
        gravity = GameConfig.getGravity();
        fallSpeed = GameConfig.getFallSpeed();
        score = 0;
        floatingTexts.clear();
    }

    private void regenerateBlocks(boolean changeColors) {
        blocks.clear();
        for (Block b : initialBlocks) {
            String color = getColorFromTexture(b.getTexture().toString());
            if (changeColors) {
                String[] colors = {"red", "green", "orange"};
                color = colors[rand.nextInt(colors.length)];
            }
            String texturePath = switch (color) {
                case "red" -> "tilesets/blocks/block_red.png";
                case "green" -> "tilesets/blocks/block_green.png";
                default -> "tilesets/blocks/block_orange.png";
            };
            Block clone = new Block(new Texture(Gdx.files.internal(texturePath)), b.getX(), b.getY(), b.getWidth(), b.getHeight());
            try {
                var fW = Block.class.getDeclaredField("displayWidth");
                var fH = Block.class.getDeclaredField("displayHeight");
                fW.setAccessible(true);
                fH.setAccessible(true);
                fW.setFloat(clone, b.getDisplayWidth());
                fH.setFloat(clone, b.getDisplayHeight());
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
            blocks.add(clone);
        }
    }

    private String getColorFromTexture(String name) {
        name = name.toLowerCase();
        if (name.contains("red")) return "red";
        if (name.contains("green")) return "green";
        return "orange";
    }

    private void resetBallOnBumper() {
        float cx = bumper.getX() + bumper.getWidth() / 2f - ball.getWidth() / 2f;
        float ty = bumper.getY() + bumper.getHeight() + 5f;
        ball.setX(cx);
        ball.setY(ty);
        ball.setDX(0);
        ball.setDY(0);
        ballWaiting = true;
        ballFalling = false;
        gameStarted = false;
        fallSpeed = GameConfig.getFallSpeed();
    }

    private void centerBumper() {
        float cx = (Gdx.graphics.getWidth() / 2f) - (bumper.getWidth() / 2f);
        bumper.setX(cx);
        bumper.setY(EntityFactory.initialBumperY);
    }

    // Rendu global
    private void drawScene() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        renderer.setView(camera);
        renderer.render();

        game.batch.begin();

        for (Block b : blocks)
            if (!b.isDestroyed()) b.render(game.batch);
        bumper.render(game.batch);
        ball.render(game.batch);

        // Bouton principal
        game.batch.draw(buttonTexture, menuButton.x, menuButton.y);
        font.draw(game.batch, "Niveau", menuButton.x + 18, menuButton.y + 20);

        // Menu déroulant
        if (isMenuOpen) {
            for (int i = 0; i < menuOptions.length; i++) {
                Rectangle r = menuOptions[i];
                Texture bg = createMaterialButton((int) r.width, (int) r.height, new Color(0.13f, 0.13f, 0.13f, 0.9f), 10);
                game.batch.draw(bg, r.x, r.y);
                font.draw(game.batch, levels[i], r.x + 18, r.y + 18);
                bg.dispose();
            }
        }

        // Score en format arrondi
        float sx = Gdx.graphics.getWidth() - 160;
        float sy = Gdx.graphics.getHeight() - 45;
        game.batch.draw(scoreBubble, sx, sy);
        font.draw(game.batch, "Score: " + score, sx + 20, sy + 20);

        // Textes flottants
        for (FloatingText t : floatingTexts) {
            font.setColor(1, 1, 0, t.alpha);
            font.draw(game.batch, t.text, t.x, t.y);
        }
        font.setColor(Color.WHITE);

        game.batch.end();
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (map != null) map.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
        if (scoreBubble != null) scoreBubble.dispose();
        if (font != null) font.dispose();
    }
}