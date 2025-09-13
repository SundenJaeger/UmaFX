package com.rentoki.umafx.manager;

import com.rentoki.umafx.model.SpriteSheet;
import com.rentoki.umafx.util.SpriteLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationManager {
    private static final int FRAMES_PER_SECOND = 30;
    private final SpriteLoader spriteLoader;
    private Timeline animationTimeline;
    private int currentFrame;
    private SpriteSheet currentSheet;
    private final Map<String, SpriteSheet> spriteSheetMap = new HashMap<>();
    private Runnable onAnimationComplete;

    public AnimationManager() {
        this.spriteLoader = new SpriteLoader();
    }

    public void loadAnimation(String characterFolder, ImageView imageView) {
        spriteSheetMap.clear();
        try {
            List<SpriteSheet> sheets = spriteLoader.loadSpriteSheets(characterFolder);

            for (SpriteSheet sheet : sheets) {
                spriteSheetMap.put(sheet.name, sheet);
            }

            playAnimation("idle", imageView, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void playAnimation(String name, ImageView imageView, Runnable onAnimationComplete) {
        stopAnimation();
        this.onAnimationComplete = onAnimationComplete;
        currentSheet = spriteSheetMap.get(name);

        if (currentSheet == null) {
            return;
        }

        currentFrame = 0;
        imageView.setImage(currentSheet.image);
        startAnimation(imageView, currentSheet.loop);
    }

    public void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }
    }

    private void startAnimation(ImageView imageView, boolean loop) {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }

        Duration frameDuration = Duration.seconds(1.0 / FRAMES_PER_SECOND);
        animationTimeline = new Timeline(new KeyFrame(frameDuration, event -> {
            currentFrame = (currentFrame + 1) % currentSheet.totalFrames;
            updateFrame(imageView);
        }));

        if (loop) {
            animationTimeline.setCycleCount(Timeline.INDEFINITE);
        } else {
            animationTimeline.setCycleCount(currentSheet.totalFrames);
            animationTimeline.setOnFinished(event -> {
                if (onAnimationComplete != null) {
                    onAnimationComplete.run();
                }
            });
        }

        animationTimeline.play();
    }

    private void updateFrame(ImageView imageView) {
        int column = currentFrame % currentSheet.columns;
        int row = currentFrame / currentSheet.columns;

        Rectangle2D viewport = new Rectangle2D(
                column * currentSheet.frameWidth,
                row * currentSheet.frameHeight,
                currentSheet.frameWidth,
                currentSheet.frameHeight
        );

        imageView.setViewport(viewport);
    }
}
