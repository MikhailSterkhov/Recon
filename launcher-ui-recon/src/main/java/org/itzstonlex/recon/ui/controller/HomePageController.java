package org.itzstonlex.recon.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.itzstonlex.recon.metrics.MetricCounter;
import org.itzstonlex.recon.metrics.ReconMetrics;
import org.itzstonlex.recon.ui.ReconUILauncher;
import org.itzstonlex.recon.ui.scheduler.TaskScheduler;
import org.itzstonlex.recon.util.ReconThreadsStorage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class HomePageController extends AbstractPageController {

    @FXML
    private MenuItem help_openDiscord;

    @FXML
    private MenuItem help_openVkontakte;


    @FXML
    private MenuItem github_openRepository;

    @FXML
    private MenuItem github_openModuleDocs;


    @FXML
    private Label usedMemoryProgress;

    @FXML
    private Label usedMemory;

    @FXML
    private ProgressBar memoryProgress;


    @FXML
    private GridPane runningThreadsPane;

    @FXML
    private GridPane graphicsGridPane;


    @FXML
    private CheckBox settings_createPoints;

    @FXML
    private CheckBox settings_chartsAnimated;

    @FXML
    private MenuItem settings_displaySide_left;

    @FXML
    private MenuItem settings_displaySide_right;

    @FXML
    private MenuButton settings_snippetTime;

    @FXML
    private MenuItem settings_snippets_lifetime;

    @FXML
    private MenuItem settings_snippets_5s;

    @FXML
    private MenuItem settings_snippets_10s;

    @FXML
    private MenuItem settings_snippets_15s;

    @FXML
    private MenuItem settings_snippets_30s;

    @FXML
    private MenuItem settings_snippets_40s;

    @FXML
    private MenuItem settings_snippets_50s;

    @FXML
    private MenuItem settings_snippets_1m;

    @FXML
    private MenuItem settings_snippets_3m;

    @FXML
    private MenuItem settings_snippets_5m;

    @FXML
    private MenuItem settings_snippets_10m;

    @FXML
    private CheckBox settings_stateColor;


    @FXML
    private Button garbageCollectorButton;


    @Override
    public void initialize() {
        addHelpMenuActions();
        addGithubMenuActions();

        startThreadsUpdater();
        startGraphicsUpdater();

        startMemoriesUpdater(Runtime.getRuntime());

        initGraphicsDisplaySettings();
        initGarbageCollector();
    }

    private void initGarbageCollector() {
        String text = garbageCollectorButton.getText();

        garbageCollectorButton.setOnAction(event -> {
            System.gc();

            // Timed block the button.
            garbageCollectorButton.setDisable(true);
            garbageCollectorButton.setText("In process...");

            ReconUILauncher.getInstance().getSchedulerManager().runLater("garbageCollector", () -> {
                garbageCollectorButton.setDisable(false);

                Platform.runLater(() -> garbageCollectorButton.setText(text));

            }, 2, TimeUnit.SECONDS);
        });
    }

    private final String threadTitleFormat = ("   Thread@%s");

    private void drawThreads() {
        Platform.runLater(() -> runningThreadsPane.getChildren().clear());

        ReconThreadsStorage.getAllThreads().forEach(new Consumer<Thread>() {
            private int counter = 0;

            @Override
            public void accept(Thread thread) {
                String threadTitle = String.format(threadTitleFormat, thread.getName());

                Label label = new Label(threadTitle);

                if (label.getFont().getSize() != 16) {
                    label.setFont(new Font(16));
                }

                if (settings_stateColor.isSelected()) {
                    switch (thread.getState()) {
                        case NEW: {
                            label.setTextFill(Color.LIME);
                            break;
                        }

                        case BLOCKED: {
                            label.setTextFill(Color.RED);
                            break;
                        }

                        case WAITING:
                        case TIMED_WAITING: {
                            label.setTextFill(Color.ORANGE);
                            break;
                        }

                        case TERMINATED: {
                            label.setTextFill(Color.DARKRED);
                            break;
                        }

                        case RUNNABLE: {
                            label.setTextFill(Color.GREEN);
                            break;
                        }
                    }
                }

                Platform.runLater(() -> runningThreadsPane.add(label, 0, counter++));
            }
        });
    }

    private void startThreadsUpdater() {
        new TaskScheduler("running_threads_recon") {

            @Override
            public void run() {
                drawThreads();
            }

        }.runTimer(0, 1000, TimeUnit.MILLISECONDS);
    }

    private final Map<Integer, LineChart<String, Number>> graphicsMap
            = new HashMap<>();

    private void initGraphicsDisplaySettings() {
        this.initGraphicsSnippetsSettings();

        settings_displaySide_left.setOnAction(event -> {

            for (LineChart<String, Number> lineChart : graphicsMap.values()) {
                lineChart.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }
        });

        settings_displaySide_right.setOnAction(event -> {

            for (LineChart<String, Number> lineChart : graphicsMap.values()) {
                lineChart.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            }
        });

        settings_createPoints.setOnAction(event -> {

            for (LineChart<String, Number> lineChart : graphicsMap.values()) {
                lineChart.setCreateSymbols(settings_createPoints.isSelected());
            }
        });

        settings_chartsAnimated.setOnAction(event -> {

            for (LineChart<String, Number> lineChart : graphicsMap.values()) {
                lineChart.setAnimated(settings_chartsAnimated.isSelected());
            }
        });
    }

    private long metricTimeSnippetMillis = TimeUnit.SECONDS.toMillis(10);
    private void initGraphicsSnippetsSettings() {
        settings_snippetTime.setText("10 sec");

        settings_snippets_lifetime.setOnAction(event -> {

            metricTimeSnippetMillis = TimeUnit.SECONDS.toMillis(1);
            settings_snippetTime.setText("Lifetime");

            this.startGraphicsUpdater();
        });

        settings_snippets_5s.setOnAction(event -> {

            metricTimeSnippetMillis = TimeUnit.SECONDS.toMillis(5);
            settings_snippetTime.setText("5 sec");

            this.startGraphicsUpdater();
        });

        settings_snippets_10s.setOnAction(event -> {

            metricTimeSnippetMillis = TimeUnit.SECONDS.toMillis(10);
            settings_snippetTime.setText("10 sec");

            this.startGraphicsUpdater();
        });

        settings_snippets_15s.setOnAction(event -> {

            metricTimeSnippetMillis = TimeUnit.SECONDS.toMillis(15);
            settings_snippetTime.setText("15 sec");

            this.startGraphicsUpdater();
        });

        settings_snippets_30s.setOnAction(event -> {

            metricTimeSnippetMillis = TimeUnit.SECONDS.toMillis(30);
            settings_snippetTime.setText("30 sec");

            this.startGraphicsUpdater();
        });

        settings_snippets_40s.setOnAction(event -> {

            metricTimeSnippetMillis = TimeUnit.SECONDS.toMillis(40);
            settings_snippetTime.setText("40 sec");

            this.startGraphicsUpdater();
        });

        settings_snippets_50s.setOnAction(event -> {

            metricTimeSnippetMillis = TimeUnit.SECONDS.toMillis(50);
            settings_snippetTime.setText("50 sec");

            this.startGraphicsUpdater();
        });

        settings_snippets_1m.setOnAction(event -> {

            metricTimeSnippetMillis = TimeUnit.MINUTES.toMillis(1);
            settings_snippetTime.setText("1 min");

            this.startGraphicsUpdater();
        });

        settings_snippets_3m.setOnAction(event -> {

            metricTimeSnippetMillis = TimeUnit.MINUTES.toMillis(3);
            settings_snippetTime.setText("3 min");

            this.startGraphicsUpdater();
        });

        settings_snippets_5m.setOnAction(event -> {

            metricTimeSnippetMillis = TimeUnit.MINUTES.toMillis(5);
            settings_snippetTime.setText("5 min");

            this.startGraphicsUpdater();
        });

        settings_snippets_10m.setOnAction(event -> {

            metricTimeSnippetMillis = TimeUnit.MINUTES.toMillis(10);
            settings_snippetTime.setText("10 min");

            this.startGraphicsUpdater();
        });
    }

    private void updateGraphicNode(MetricCounter metricCounter, XYChart.Series<String, Number> series) {
        series.getData().clear();

        for (int i = 1; i <= 30; i ++) {

            String value = TimeUnit.MILLISECONDS.toSeconds(i * metricTimeSnippetMillis) + "s";
            XYChart.Data<String, Number> data = new XYChart.Data<>(value, metricCounter.valueOf(i * metricTimeSnippetMillis));

            series.getData().add(data);
        }
    }

    private void updateGraphic(int row, MetricCounter metricCounter) {
        LineChart<String, Number> lineChart = graphicsMap.get(row);

        XYChart.Series<String, Number> series = lineChart.getData().get(0);

        updateGraphicNode(metricCounter, series);
    }

    private void createGraphics(int row, String title, MetricCounter metricCounter) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setAnimated(false);
        lineChart.setTitle(title);

        lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        updateGraphicNode(metricCounter, series);

        lineChart.getData().add(series);

        graphicsGridPane.add(lineChart, 0, row);
        graphicsMap.put(row, lineChart);
    }

    private void drawGraphicCharts(boolean isCreated) {
        Platform.runLater(() -> {

            if (!isCreated) {
                createGraphics(0, "Total Reads", ReconMetrics.TOTAL_BYTES_READ);
                createGraphics(1, "Total Writes", ReconMetrics.TOTAL_BYTES_WRITE);
                createGraphics(2, "Total Bytes", ReconMetrics.TOTAL_BYTES);
                createGraphics(3, "Total Clients", ReconMetrics.TOTAL_CLIENTS);

            } else {

                updateGraphic(0, ReconMetrics.TOTAL_BYTES_READ);
                updateGraphic(1, ReconMetrics.TOTAL_BYTES_WRITE);
                updateGraphic(2, ReconMetrics.TOTAL_BYTES);
                updateGraphic(3, ReconMetrics.TOTAL_CLIENTS);
            }
        });
    }

    private boolean isGraphicsCreated;
    private void startGraphicsUpdater() {
        String taskID = "graphics_updater_recon";

        ReconUILauncher.getInstance().getSchedulerManager().cancelScheduler(taskID);
        new TaskScheduler(taskID) {

            @Override
            public void run() {
                drawGraphicCharts(isGraphicsCreated);
                isGraphicsCreated = true;
            }

        }.runTimer(1000, metricTimeSnippetMillis, TimeUnit.MILLISECONDS);
    }

    private void updateMemoryStatus(Runtime runtime) {
        memoryProgress.setProgress((double) runtime.totalMemory() / runtime.maxMemory());

        Platform.runLater(() -> {

            usedMemory.setText("Memory used: [" + (runtime.totalMemory() / 1024 / 1024) + " MB / " + (runtime.maxMemory() / 1024 / 1024) + " MB]");
            usedMemoryProgress.setText(Math.ceil((double)runtime.totalMemory() / runtime.maxMemory() * 100) + "%");
        });
    }

    private void startMemoriesUpdater(Runtime runtime) {
        memoryProgress.setPrefHeight(16);
        memoryProgress.setMinHeight(16);
        memoryProgress.setMaxHeight(16);

        new TaskScheduler("memory_updater_recon") {

            @Override
            public void run() {
                updateMemoryStatus(runtime);
            }

        }.runTimer(0, 1, TimeUnit.SECONDS);
    }

    private void addHelpMenuActions() {
        Desktop desktop = Desktop.getDesktop();

        if (!(Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE))) {
            return;
        }

        help_openDiscord.setOnAction(event -> {
            try {
                desktop.browse(new URI("https://discord.gg/GmT9pUy8af"));

            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
            }
        });

        help_openVkontakte.setOnAction(event -> {
            try {
                desktop.browse(new URI("https://vk.com/itzstonlex"));

            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void addGithubMenuActions() {
        Desktop desktop = Desktop.getDesktop();

        if (!(Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE))) {
            return;
        }

        github_openRepository.setOnAction(event -> {
            try {
                desktop.browse(new URI("http://github.com/ItzStonlex/Recon"));

            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
            }
        });

        github_openModuleDocs.setOnAction(event -> {
            try {
                desktop.browse(new URI("http://github.com/ItzStonlex/Recon/tree/main/launcher-ui-recon"));

            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
            }
        });
    }

}
