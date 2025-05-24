package com.ufscar.formularoute;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RealTimeTelemetry extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        var root = new Interface();
        var antialiasing = Platform.isSupported(ConditionalFeature.EFFECT)
                ? SceneAntialiasing.BALANCED
                : SceneAntialiasing.DISABLED;

        Scene scene = new Scene(root, 720, 768, false, antialiasing);

        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        //scene.getStylesheets().add(getClass().getResource("/mspm/pages/DashboardCSS.css").toString());

        stage.setScene(scene);

        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args){
        SpringApplication.run(RealTimeTelemetry.class, args);
        launch(args);
    }
}
