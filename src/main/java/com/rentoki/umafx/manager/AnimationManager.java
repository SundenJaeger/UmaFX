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
import java.util.stream.Collectors;

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

    /* ---------------- Public API ---------------- */

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
        currentFrame = 0;

        if (currentSheet == null) {
            return;
        }

        imageView.setImage(currentSheet.image);
        updateFrame(imageView);

        startAnimation(imageView, currentSheet.loop);
    }

    public void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationTimeline = null;
        }
        currentSheet = null;
        onAnimationComplete = null;
        currentFrame = 0;
    }

    public List<String> getAnimationVariants(String baseName) {
        return spriteSheetMap.keySet().stream()
                .filter(name -> name.startsWith(baseName + "_"))
                .collect(Collectors.toList());
    }

    public void playRandomVariant(String baseName, ImageView imageView, Runnable onAnimationComplete) {
        List<String> variants = getAnimationVariants(baseName);
        if (variants.isEmpty()) {
            playAnimation(baseName, imageView, onAnimationComplete);
            return;
        }

        String randomVariant = variants.get((int) (Math.random() * variants.size()));
        playAnimation(randomVariant, imageView, onAnimationComplete);
    }

    /* ---------------- Helpers ---------------- */

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
        if (currentSheet == null || currentFrame >= currentSheet.totalFrames) {
            return;
        }

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
