package org.itzstonlex.recon.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import org.itzstonlex.recon.metrics.ReconMetrics;
import org.itzstonlex.recon.side.Client;
import org.itzstonlex.recon.side.Server;
import org.itzstonlex.recon.util.ReconThreadsStorage;

import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ReconLauncherApplication extends Application {

    public static final ReconUILauncher launcher = new ReconUILauncher();

    public static void startApplicationUI() {
        main(new String[0]);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void runTestConnections() {
        Executors.newCachedThreadPool().submit(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    new Server().bindLocal(1000 + i, config -> ReconMetrics.newMetricInstance().initPipelines(config.pipeline()));
                    Thread.sleep(1000);
                }

                for (int i = 0; i < 10; i++) {
                    new Client().connectLocal(1000 + i, config -> ReconMetrics.newMetricInstance().initPipelines(config.pipeline()));
                    Thread.sleep(1000);
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void start(Stage primaryStage) {
        // runTestConnections();

        launcher.start(primaryStage);
    }

}
