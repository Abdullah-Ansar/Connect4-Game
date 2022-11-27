package com.internshala.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final int CIRCLE_DIAMETER = 80;
    private static final String discColor1 = "#24303e";
    private static final String discColor2 = "#4CAA88";
    private static String PLAYER_ONE = "Player One";
    private static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOneTurn = true;
    private boolean isAllowedtoInsert = true;
    private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];
    @FXML
    public GridPane rootGridPane;
    @FXML
    private Pane pane1,insertedDiscPane;
    @FXML
    private Label playerNameLabel;
    @FXML
    public TextField playerOne,playerTwo;
    @FXML
    public Button btnSetName;

    public void createPlayground(){
        btnSetName.setOnAction(actionEvent -> {
            PLAYER_ONE = playerOne.getText();
            PLAYER_TWO = playerTwo.getText();

        });
        Shape rectangleWithHoles = createGameStructuralGrid();
        rootGridPane.add(rectangleWithHoles,0,1);
        List<Rectangle> rectangleList = createClickableColumns();
        for (Rectangle rectangle:rectangleList) {
            rootGridPane.add(rectangle, 0,1);
        }
        btnSetName.setOnAction(actionEvent -> {
            PLAYER_ONE = playerOne.getText();
            PLAYER_TWO = playerTwo.getText();

        });

//        if(btnClicked.get() == btnSetName){
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setContentText("Enter names of Player");
//            alert.show();
//        }
    }
    private Shape createGameStructuralGrid(){
        Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1 )* CIRCLE_DIAMETER);
        for (int row = 0;row< ROWS;row++){
            for(int col = 0; col<COLUMNS;col++){
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2);
                circle.setCenterY(CIRCLE_DIAMETER / 2);
                circle.setSmooth(true);
                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
            }
        }
        rectangleWithHoles.setFill(Color.WHITE);
        return rectangleWithHoles;

    }
    private List<Rectangle> createClickableColumns(){
        List<Rectangle> rectangleList = new ArrayList<>();

        for(int col = 0;col<COLUMNS;col++){
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1 )* CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
            rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));
            final int column = col;

            rectangle.setOnMouseClicked(mouseEvent -> {
                if(isAllowedtoInsert){
                    isAllowedtoInsert = false;
                    insertDisc(new Disc(isPlayerOneTurn),column);
                }

            });
            rectangleList.add(rectangle);
        }

        return rectangleList;
    }

    private void insertDisc(Disc disc, int col) {

        int row = ROWS - 1;

        while (row >= 0){

            if(getDiscIfPresent(row,col) == null)
                break;
            row--;


        }

        if(row == 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Game will exit if you try to insert disc in filled column");
            alert.showAndWait();

        }
        if(row < 0){
            System.exit(0);
        }

        insertedDiscArray[row][col] = disc;
        insertedDiscPane.getChildren().add(disc);

        disc.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
        int currentRow = row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
        translateTransition.setOnFinished(actionEvent -> {
            isAllowedtoInsert = true;
            if (gameEnded(currentRow,col)){
                gameOver();
                return;
            }
            isPlayerOneTurn = !isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
        });
        translateTransition.play();


    }

    private boolean gameEnded(int row, int col) {
        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3,row + 3).mapToObj(r -> new Point2D(r,col)).collect(Collectors.toList());

        List<Point2D> horizontalPoints = IntStream.rangeClosed(col - 3,col + 3).mapToObj(c -> new Point2D(row,c)).collect(Collectors.toList());
        Point2D startPoint1 = new Point2D(row - 3,col + 3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint1.add(i,-i)).collect(Collectors.toList());
        Point2D startPoint2 = new Point2D(row - 3,col - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint2.add(i,i)).collect(Collectors.toList());
        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
        return isEnded;
    }
    private boolean checkCombinations(List<Point2D> points){
        int chain = 0;
        for (Point2D point : points) {

            int rowIndexForArray = (int) point.getX();
            int colIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, colIndexForArray);
            if (disc != null && disc.isPlayerMove == isPlayerOneTurn) {
                chain++;

            }
        }
        if(chain == 4){
            return true;
        }else return false;

    }
    private Disc getDiscIfPresent(int row , int col){
        if(row >= ROWS || row < 0 || col >= COLUMNS || col<0){
            return null;
        }
        return insertedDiscArray[row][col];
    }
    private void gameOver(){
        String winner = isPlayerOneTurn? PLAYER_ONE:PLAYER_TWO;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("The Winner is " + winner);
        alert.setContentText("Want to play again? ");
        ButtonType btnYes = new ButtonType("Yes");
        ButtonType btnNo = new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(btnYes,btnNo);

        Platform.runLater(() -> {
            Optional<ButtonType> btnClicked = alert.showAndWait();
            if(btnClicked.isPresent() && btnClicked.get() == btnYes){
                resetGame();
            }else{
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public void resetGame() {
        insertedDiscPane.getChildren().clear();
        for(int row = -1; row < insertedDiscArray.length; row++){
            for (int col = 0; col< insertedDiscArray[row].length; col++){
                insertedDiscArray[row][col] = null;
            }
        }
        isPlayerOneTurn = true;
        playerNameLabel.setText(PLAYER_ONE);
        createPlayground();
    }

    private static class Disc extends Circle{
        private final boolean isPlayerMove;
        public Disc(boolean isPlayerMove){
            this.isPlayerMove = isPlayerMove;
            setRadius(CIRCLE_DIAMETER / 2);
            setFill((isPlayerMove? Color.valueOf(discColor1):Color.valueOf(discColor2)));
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER / 2);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }
}