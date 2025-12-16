package com.stackoverflowgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;

// Écran d’intro et transition vers le jeu

public class StackOverflowGame extends Game {

    public SpriteBatch batch;
    private BitmapFont font;
    private Texture starTexture, buttonTexture;
    private float[][] stars;

    private boolean showIntro = true;
    private boolean showButton = false;
    private boolean showTransition = false;

    private float timer = 0f;
    private float transitionTimer = 0f;
    private float starAlpha = 1f;

    private final String introText = """
    Ceci est l'oeuvre de Riad Hechaichi, Dan Toubalem et Samy Yadi\n
    dans le cadre du projet noté en PCOO à l'Université Nice Côte d'Azur
    """;

    @Override
    public void create() {
        batch = new SpriteBatch();
        initStars();
        initFont();
        initButton();
    }

    // Crée les étoiles d’arrière-plan 
    private void initStars() {
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(1, 1, 1);
        starTexture = new Texture(pixmap);
        pixmap.dispose();

        int numStars = 100;
        stars = new float[numStars][3];
        for (int i = 0; i < numStars; i++) {
            stars[i][0] = (float) (Math.random() * Gdx.graphics.getWidth());
            stars[i][1] = (float) (Math.random() * Gdx.graphics.getHeight());
            stars[i][2] = 20 + (float) (Math.random() * 60);
        }
    }

    // définir la police d’écriture
    private void initFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/gameover.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 22;
        parameter.color = Color.YELLOW;
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    // Crée la texture du bouton
    private void initButton() {
        Pixmap btn = new Pixmap(220, 60, Pixmap.Format.RGBA8888);
        btn.setColor(Color.DARK_GRAY);
        btn.fillRectangle(0, 0, 220, 60);
        btn.setColor(Color.YELLOW);
        btn.drawRectangle(0, 0, 220, 60);
        buttonTexture = new Texture(btn);
        btn.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (showIntro || showButton || showTransition)
            renderIntro();
        else
            super.render();
    }

    // Gestion du rendu de l’intro
    private void renderIntro() {
        batch.begin();
        drawStars();

        if (showIntro) {
            drawIntroText();
        } else if (showButton) {
            drawContinueButton();
        } else if (showTransition) {
            drawTransition();
        }

        batch.end();
    }

    // Texte d’intro
    private void drawIntroText() {
        GlyphLayout layout = new GlyphLayout(font, introText, Color.YELLOW,
                Gdx.graphics.getWidth() * 0.8f, Align.center, true);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float y = (Gdx.graphics.getHeight() + layout.height) / 2f;
        font.draw(batch, layout, x, y);

        timer += Gdx.graphics.getDeltaTime();
        if (timer > 5f) { // TIMER TEXTE AU DÉBUT: SAMY, DAN => POUR CHANGER LA DURÉE DE L'INTRO DU TEXTE AU DÉBUT, C'EST ICI, ÇA PEUT ÊTRE RELOU POUR LES TESTS, ALORS FAITES VOUS PLAISIR 5F = 5 SECONDES
            showIntro = false;
            showButton = true;
        }
    }

    // pour le rendu du fond avec les étoiles
    private void drawStars() {
        batch.setColor(1, 1, 1, starAlpha);
        for (float[] star : stars) {
            batch.draw(starTexture, star[0], star[1], 2, 2);
            star[1] -= star[2] * Gdx.graphics.getDeltaTime();
            if (star[1] < 0) {
                star[1] = Gdx.graphics.getHeight();
                star[0] = (float) (Math.random() * Gdx.graphics.getWidth());
            }
        }
        batch.setColor(Color.WHITE);
    }

    // Bouton principal “Continuer”
    private void drawContinueButton() {
        float btnWidth = 220, btnHeight = 60;
        float btnX = (Gdx.graphics.getWidth() - btnWidth) / 2;
        float btnY = Gdx.graphics.getHeight() / 3f;

        batch.draw(buttonTexture, btnX, btnY);
        GlyphLayout btnText = new GlyphLayout(font, "Continuer");
        float textX = btnX + (btnWidth - btnText.width) / 2f;
        float textY = btnY + (btnHeight + btnText.height) / 2f - 5;
        font.draw(batch, btnText, textX, textY);

        if (Gdx.input.justTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (mouseX >= btnX && mouseX <= btnX + btnWidth &&
                mouseY >= btnY && mouseY <= btnY + btnHeight) {
                showButton = false;
                showTransition = true;
                transitionTimer = 0f;
            }
        }
    }

    // Transition avant le lancement du jeu
    private void drawTransition() {
        GlyphLayout layout = new GlyphLayout(font, "Le jeu va commencer...", Color.YELLOW, 0, Align.center, false);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = Gdx.graphics.getHeight() / 2 + layout.height / 2;
        font.draw(batch, layout, x, y);

        // pour que les étoiles disparaissent peu à peu
        transitionTimer += Gdx.graphics.getDeltaTime();
        starAlpha = Math.max(0f, 1f - (transitionTimer / 2f)); // durée du fondu = 2 secondes

        if (transitionTimer >= 2f) {
            showTransition = false;
            setScreen(new GameScreen(this)); // lancement du jeu
        }
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (starTexture != null) starTexture.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
    }
}