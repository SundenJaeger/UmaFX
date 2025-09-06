package com.rentoki.umafx.manager;

import com.rentoki.umafx.model.SpriteSheet;
import com.rentoki.umafx.util.SpriteLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class AnimationManager {
    private static final int FRAMES_PER_SECOND = 30;
    private final SpriteLoader spriteLoader;
    private Timeline animationTimeline;
    private int currentFrame;
    private SpriteSheet currentSheet;

    public AnimationManager() {
        this.spriteLoader = new SpriteLoader();
    }

    public void loadAndStartAnimation(String characterFolder, ImageView imageView) {
        try {
            List<SpriteSheet> sheets = spriteLoader.loadSpriteSheets(characterFolder);
            currentSheet = sheets.getFirst();
            currentFrame = 0;

            imageView.setImage(currentSheet.image);
            startAnimation(imageView);

        } catch (IOException e) {
            System.err.println("Failed to load animation: " + e.getMessage());
        }
    }

    public void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }
    }

    private void startAnimation(ImageView imageView) {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }

        Duration frameDuration = Duration.seconds(1.0 / FRAMES_PER_SECOND);
        animationTimeline = new Timeline(new KeyFrame(frameDuration, event -> {
            currentFrame = (currentFrame + 1) % currentSheet.totalFrames;
            updateFrame(imageView);
        }));

        animationTimeline.setCycleCount(Timeline.INDEFINITE);
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
