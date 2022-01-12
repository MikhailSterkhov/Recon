package org.itzstonlex.recon.ui.util;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.effect.Glow;
import javafx.stage.Window;
import javafx.util.Duration;
import org.itzstonlex.recon.ui.scheduler.TaskScheduler;

import java.util.concurrent.TimeUnit;

public final class TimelineUtils {

    public DoubleProperty createProperty() {
        return new SimpleDoubleProperty(0);
    }

    public DoubleProperty createProperty(double initialValue) {
        return new SimpleDoubleProperty(initialValue);
    }

    public DoubleProperty createProperty(double initialValue, ChangeListener<Number> listener) {
        DoubleProperty property = createProperty(initialValue);
        property.addListener(listener);

        return property;
    }

    public void playTimeline(double initialValue, double maxValue, ChangeListener<Number> listener, Duration duration) {
        DoubleProperty property = createProperty(initialValue, listener);

        Timeline timeline = new Timeline();

        timeline.getKeyFrames().add(new KeyFrame(duration, new KeyValue(property, maxValue, Interpolator.EASE_BOTH)));
        timeline.play();
    }


    public void playRepeatedGlowing(Node node) {
        new TaskScheduler() {

            private boolean direction = true;
            private long glowCounter = 0;

            @Override
            public void run() {

                if (direction) {
                    if (glowCounter >= 100) {
                        direction = false;
                    }

                    glowCounter++;

                } else {
                    if (glowCounter <= 0) {
                        direction = true;
                    }

                    glowCounter--;
                }

                node.setEffect(new Glow(glowCounter / 100d));
            }

        }.runTimer(1, 15, TimeUnit.MILLISECONDS);
    }

    public void playOpacityHide(Node node, Runnable success) {
        playTimeline(0, 1,  (ob, n, n1) -> {

            node.setOpacity(1D - n1.doubleValue());

            if (node.getOpacity() <= 0) {
                success.run();
            }

        }, Duration.seconds(0.5));
    }

    public void playOpacityHide(Node node) {
        playOpacityHide(node, null);
    }


    public void playWindowOpacityHide(Window window, Runnable success) {
        playTimeline(0, 1,  (ob, n, n1) -> {

            window.setOpacity(1D - n1.doubleValue());

            if (window.getOpacity() <= 0) {
                success.run();
            }

        }, Duration.seconds(0.5));
    }

    public void playWindowOpacityHide(Window window) {
        playWindowOpacityHide(window, null);
    }

    public void playOpacityShow(Node node) {
        playTimeline(0, 1, (ob, n, n1) -> node.setOpacity(n1.doubleValue()), Duration.seconds(1.8));
    }

}
