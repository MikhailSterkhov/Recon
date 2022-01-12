package org.itzstonlex.recon.ui.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.itzstonlex.recon.ui.ReconLauncherApplication;
import org.itzstonlex.recon.ui.ReconUILauncher;
import org.itzstonlex.recon.ui.controller.AbstractPageController;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public final class ScenesUtils {

    // Для быстрого перехода между страницами, будем кешировать их на 5 минут
    private static final Cache<String, Scene> sceneCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    // Для получения контроллеров, чтобы инициализировать кешированные сцены
    private static final Map<Parent, AbstractPageController> sceneControllersMap
            = new HashMap<>();


    public static Stage CURRENT_SCREEN          = null;

    public static final String LOADING_FXML     = ("loading.fxml");
    public static final String HOME_FXML        = ("home.fxml");

    private static Parent loadFxml(String fxml) {
        URL fxmlUrl = ReconLauncherApplication.class.getResource("/markup/" + fxml);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Parent parent = fxmlLoader.load();

            sceneControllersMap.put(parent, fxmlLoader.getController());

            return parent;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static void redirect(String fxml) {
        ReconUILauncher.getInstance().getSchedulerManager().cancelAll();
        sceneCache.cleanUp();

        synchronized (ReconUILauncher.getInstance()) {

            if (CURRENT_SCREEN != null && sceneCache.asMap().containsKey(fxml)) {
                Scene scene = sceneCache.asMap().get(fxml);
                sceneControllersMap.get(scene.getRoot()).initialize();

                CURRENT_SCREEN.setScene(scene);

            } else {

                Scene scene = new Scene(loadFxml(fxml));

                CURRENT_SCREEN.setScene(scene);
                sceneCache.put(fxml, scene);
            }
        }
    }

    public static void open(Stage primaryStage, String fxml) {
        ReconUILauncher.getInstance().getSchedulerManager().cancelAll();
        sceneCache.cleanUp();

        synchronized (ReconUILauncher.getInstance()) {
            Scene scene = CURRENT_SCREEN != null ? sceneCache.asMap().get(fxml) : null;

            if (scene == null) {
                sceneCache.put(fxml, scene = new Scene(loadFxml(fxml)));

            } else {

                sceneControllersMap.get(scene.getRoot()).initialize();
            }

            primaryStage.setResizable(false);

            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event -> ReconUILauncher.getInstance().shutdown());

            //primaryStage.getIcons().add(new Image(ReconUILauncher.class.getResourceAsStream("/images/Icon.jpg")));

            primaryStage.setTitle(Constants.LAUNCHER_TITLE + " " + Constants.LAUNCHER_VERSION);

            (CURRENT_SCREEN = primaryStage).show();
        }
    }

    public static ContextMenu initContextMenu(Node node,
                                              BiConsumer<ActionEvent, MenuItem> itemClickAction,
                                              String... menuItems) {

        synchronized (ReconUILauncher.getInstance()) {
            ContextMenu contextMenu = new ContextMenu();

            for (String menuItemText : menuItems) {

                MenuItem menuItem = new MenuItem(menuItemText);
                menuItem.setOnAction(event -> itemClickAction.accept(event, menuItem));

                contextMenu.getItems().add(menuItem);
            }

            node.setOnMouseClicked(event -> contextMenu.show(node, event.getScreenX(), event.getScreenY()));
            return contextMenu;
        }
    }

}
