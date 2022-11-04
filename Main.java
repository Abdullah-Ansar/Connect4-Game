package com.internshala.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

public class Main extends Application {


    private  Controller controller;
    @FXML
    private Pane pane1;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("app_view.fxml"));
        GridPane rootGridPane = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.createPlayground();
        MenuBar menuBar = createMenuBar();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        pane1 = (Pane) rootGridPane.getChildren().get(0);
        pane1.getChildren().add(menuBar);
        menuBar.setLayoutY(1);
        Scene scene = new Scene(rootGridPane);
        stage.setTitle("Connect Four");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public MenuBar createMenuBar() {
        Menu fileMenu = new Menu();
        fileMenu.setText("File");
        MenuItem newMenuItem = new MenuItem();
        newMenuItem.setText("New Game");
        newMenuItem.setOnAction(actionEvent -> {
            controller.resetGame();
        });
        MenuItem resetMenuItem = new MenuItem();
        resetMenuItem.setText("Reset Game");
        resetMenuItem.setOnAction(actionEvent -> {
            controller.resetGame();
            });
        MenuItem exitMenuItem = new MenuItem();
        exitMenuItem.setText("Exit Game");
        exitMenuItem.setOnAction(actionEvent -> {
            Platform.exit();
            System.exit(0);
            });
            SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
            fileMenu.getItems().addAll(newMenuItem, resetMenuItem, separatorMenuItem, exitMenuItem);
            Menu helpMenu = new Menu();
            helpMenu.setText("Help");
            MenuItem aboutUsMenuItem = new MenuItem();
            aboutUsMenuItem.setText("About Me");
            aboutUsMenuItem.setOnAction(actionEvent1 -> aboutMe());
            MenuItem aboutGameMenuItem = new MenuItem();
            aboutGameMenuItem.setText("About Connect Four");
            aboutGameMenuItem.setOnAction(actionEvent1 -> aboutGame());
            helpMenu.getItems().addAll(aboutGameMenuItem, aboutUsMenuItem);

            MenuBar menuBar = new MenuBar();
            menuBar.getMenus().addAll(fileMenu, helpMenu);
            return menuBar;
        }





    private void aboutGame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How To Play?");
        alert.setContentText("Connect Four is a two-player connection game in which the\nplayers first choose a color and then take turns dropping\ncolored discs from the top into a seven-column, six-row vertically suspended grid.\nThe pieces fall straight down, occupying the next available space within the column.\nThe objective of the game is to be the first to form a horizontal\nvertical, or diagonal line of four of one's own discs. Connect Four is a solved game.\nThe First player can always win by playing the right moves.");
        alert.show();
    }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("Abdullah Ansari");
        alert.setContentText("I love to play around with code and create games.\nConnect 4 is one of them.\nIn free time, I like to spend time with nears and dears.");
        alert.show();

    }

    public static void main(String[] args){
        launch(args);
    }

}