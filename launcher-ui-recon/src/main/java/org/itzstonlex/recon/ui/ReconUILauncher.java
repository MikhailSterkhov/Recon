package org.itzstonlex.recon.ui;

import javafx.stage.Stage;
import org.itzstonlex.recon.ui.scheduler.SchedulerManager;
import org.itzstonlex.recon.ui.util.ScenesUtils;

public final class ReconUILauncher {

    public static ReconUILauncher getInstance() {
        return instance;
    }

    private static ReconUILauncher instance;

    private final SchedulerManager schedulerManager = new SchedulerManager();

    ReconUILauncher() {
        instance = this;
    }

    public void start(Stage primaryStage) {

        assert primaryStage != null;
        ScenesUtils.open(primaryStage, ScenesUtils.HOME_FXML);
    }

    public void shutdown() {
        // ...

        System.exit(0);
    }

    public SchedulerManager getSchedulerManager() {
        return schedulerManager;
    }
}
