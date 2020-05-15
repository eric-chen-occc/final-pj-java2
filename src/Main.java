import javafx.application.Application;
//import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.effect.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.io.*;
import java.util.*;

//new imports for text generation
import javafx.scene.text.Text;
import javafx.scene.text.Font;


public class Main extends Application {

    String[][] keys = {
            {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "+"},
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "\'"},
            {"Z", "X", "C", "V", "B", "N", "M", ",", ".", "?"}
    };
    double keySize = 40;
    double keyMargin = 6;
    String keyColor = "#666";
    String keyPressedColor = "#777";
    String keyboardColor = "#ccc";
    //    DropShadow ds0 = new DropShadow(0, 0, 0, Color.web("#333", 0));
    DropShadow ds0 = new DropShadow(0, 1, 1, Color.web("#333"));
    DropShadow ds1 = new DropShadow(1, 2, 2, Color.web("#333"));
    DropShadow ds2 = new DropShadow(2, 3, 3, Color.web("#333"));

    String passage = "";

    long t0 = 0;

    String userName = "";
    double accuracy = 0;
    double speed = 0;
    boolean gameStarted = false;

    Rectangle popup;
    HBox userNameHBox, resultHBox;
    TextField userNameInput;
    Group startGroup, rankingGroup;
    Button btnStart, btnTryAgain, btnSwitchUser, btnRanking, btnExit, btnClose;
    Label scoreLabel;
    TableView<User> table;
    TableColumn c1, c2, c3, c4, c5;

    public static void main (String[] args)
    {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        Pane root = new Pane();



        // GUI

        // textbox
        String passage = getPassage();
        Text text = new Text(40, 40, passage);
        text.setFont(Font.font ("Verdana", 20));
        root.getChildren().add(text);


        Scene scene = new Scene(root);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Digital Keyboard");


        primaryStage.show();

        // add keyboard
//        Pane paneKeys = new Pane();


        // add keys
        Group keysGroup = new Group();
        keysGroup.setLayoutX(300);
        keysGroup.setLayoutY(380);

        // add keyboard background
        Rectangle keyboard = new Rectangle(-20, -20, 720, 268);
        keyboard.setArcHeight(15);
        keyboard.setArcWidth(15);
        keyboard.setFill(Color.web(keyboardColor));
        keysGroup.getChildren().add(keyboard);

        // add main keys
        double blockSize = keySize + keyMargin;
        for(int i = 0; i < keys.length; i++) {
            for(int j = 0; j < keys[i].length; j++) {
                Button btn = createNewButton(
                        keys[i][j],
                        (j + 1) * blockSize + blockSize / 2 * i,
                        i * blockSize,
                        keySize,
                        keySize
                );
                keysGroup.getChildren().add(btn);
            }
        }

        // add other keys
        Button btnDot = createNewButton("~", 0, 0, keySize, keySize);
        Button btnDel = createNewButton(
                "Del",
                blockSize * (keys[0].length + 1),
                0,
                keySize * 2,
                keySize
        );
        Button btnTab = createNewButton("Tab", 0, blockSize, keySize * 1.6, keySize);
        Button btnBackslash = createNewButton(
                "\\",
                blockSize * keys[1].length + keySize * 1.57 + keyMargin,
                blockSize,
                keySize * 1.42,
                keySize
        );
        Button btnCaps = createNewButton("Caps", 0, blockSize * 2, keySize * 2 + keyMargin , keySize);
        Button btnEnter = createNewButton(
                "Enter",
                blockSize * (keys[2].length) + blockSize * 2,
                blockSize * 2,
                keySize * 2,
                keySize
        );
        Button btnShift1 = createNewButton("Shift", 0, blockSize * 3, keySize * 2.75, keySize);
        Button btnShift2 = createNewButton(
                "Shift",
                blockSize * (keys[3].length) + keySize * 2.75 + keyMargin,
                blockSize * 3,
                keySize * 2.56,
                keySize
        );
        Button btnCtrl1 = createNewButton("Ctrl", 0, blockSize * 4, keySize, keySize);
        Button btnFn = createNewButton("Fn", blockSize, blockSize * 4, keySize, keySize);
        Button btnAlt1 = createNewButton("Alt", blockSize * 2, blockSize * 4, keySize, keySize);
        Button btnSpace = createNewButton("Space", blockSize * 3, blockSize * 4, blockSize * 8.73, keySize);
        Button btnAlt2 = createNewButton("Alt", blockSize * 11 + keySize, blockSize * 4, keySize, keySize);
        Button btnBlock = createNewButton("", blockSize * 12 + keySize, blockSize * 4, keySize, keySize);
        Button btnCtrl2 = createNewButton("Ctrl", blockSize * 13 + keySize, blockSize * 4, keySize, keySize);

        keysGroup.getChildren().add(btnDot);
        keysGroup.getChildren().add(btnDel);
        keysGroup.getChildren().add(btnTab);
        keysGroup.getChildren().add(btnBackslash);
        keysGroup.getChildren().add(btnCaps);
        keysGroup.getChildren().add(btnEnter);
        keysGroup.getChildren().add(btnShift1);
        keysGroup.getChildren().add(btnShift2);
        keysGroup.getChildren().add(btnCtrl1);
        keysGroup.getChildren().add(btnFn);
        keysGroup.getChildren().add(btnAlt1);
        keysGroup.getChildren().add(btnSpace);
        keysGroup.getChildren().add(btnAlt2);
        keysGroup.getChildren().add(btnBlock);
        keysGroup.getChildren().add(btnCtrl2);

        root.getChildren().add(keysGroup);


        // popup background
        popup = new Rectangle(0, 0, 1280, 720);
        popup.setFill(Color.web(keyboardColor));
        root.getChildren().add(popup);

        // Start Page
        startGroup = new Group();
        startGroup.setLayoutX(0);
        startGroup.setLayoutY(0);

        userNameHBox = new HBox();
        Label yourNameLabel = new Label("Your name:");
        yourNameLabel.setFont(new Font("Arial", 20));
        userNameInput = new TextField ();
        userNameHBox.getChildren().addAll(yourNameLabel, userNameInput);
        userNameHBox.setSpacing(10);
        userNameHBox.setLayoutX(500);
        userNameHBox.setLayoutY(300);
        startGroup.getChildren().add(userNameHBox);

        resultHBox = new HBox();
        scoreLabel = new Label();
        scoreLabel.setFont(new Font("Arial", 20));
        resultHBox.getChildren().add(scoreLabel);
        resultHBox.setLayoutX(500);
        resultHBox.setLayoutY(150);
        startGroup.getChildren().add(resultHBox);
        resultHBox.setVisible(false);

        btnStart = createNewButton("Start", 550, 380, 180, 40);

        btnTryAgain = createNewButton("Try Again", 550, 380, 180, 40);

        btnSwitchUser = createNewButton("Switch User", 570, 450, 140, 40);

        btnRanking = createNewButton("Ranking", 1130, 550, 100, 40);

        btnExit = createNewButton("Exit", 1130, 600, 100, 40);

        startGroup.getChildren().addAll(btnStart, btnTryAgain, btnSwitchUser, btnRanking, btnExit);

        btnTryAgain.setVisible(false);
        btnSwitchUser.setVisible(false);
        btnRanking.setVisible(false);

        root.getChildren().add(startGroup);

        // user typing

        Button btnFinish = createNewButton("Finish", 1130, 100, 100, 40);
        root.getChildren().add(btnFinish);

        // show score to user


        // save score

        // show rankings
        rankingGroup = new Group();
        rankingGroup.setLayoutX(0);
        rankingGroup.setLayoutY(0);


        table = new TableView<User>();


        c1 = new TableColumn("Name");
        c1.setPrefWidth(150);
        c2 = new TableColumn("Score");
        c2.setPrefWidth(100);
        c3 = new TableColumn("Accuracy");
        c3.setPrefWidth(80);
        c4 = new TableColumn("Speed(letters/min)");
        c4.setPrefWidth(150);
        c5 = new TableColumn("Date");
        c5.setPrefWidth(300);

        table.getColumns().addAll(c1, c2, c3, c4, c5);
        table.setPrefWidth(780);
        table.setPrefHeight(720);
        table.setLayoutX(250);
        table.setLayoutY(0);

        rankingGroup.getChildren().add(table);

        btnClose = createNewButton("Close", 1130, 550, 100, 40);
        rankingGroup.getChildren().add(btnClose);

        root.getChildren().add(rankingGroup);
        rankingGroup.setVisible(false);




    }

    public Button createNewButton(String keyName, double x, double y, double width, double height) {
        Button btn = new Button(keyName);
        btn.setLayoutX(x);
        btn.setLayoutY(y);
        btn.setId("key-" + keyName);
        btn.setEffect(ds1);
        btn.setEffect(ds2);
        btn.setPrefSize(width, height);
        btn.setStyle("-fx-background-color: "+ keyColor + "; -fx-text-fill: white;");
        pressAButton(btn);
        return btn;
    }

    public void pressAButton(Button btn) {
        btn.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                btn.setEffect(ds0);
                btn.setTranslateX(1);
                btn.setTranslateY(1);
                btn.setStyle("-fx-background-color: "+ keyPressedColor + "; -fx-text-fill: white;");
                System.out.println("You hit the key " + btn.getText());

                if (btn.getText() == "Start") {


                    userName = userNameInput.getText().replaceAll("\\s","");
                    if (userName.length() == 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Need a user name");
                        alert.setHeaderText(null);
                        alert.setContentText("User name can not be empty.");
                        alert.showAndWait();
                    } else {
                        startGroup.setVisible(false);
                        popup.setVisible(false);

                        t0 = new Date().getTime();
                    }

                    System.out.println(userName);

                }

                if (btn.getText() == "Finish") {

                    // test
                    accuracy = Math.random();
                    long t1 = new Date().getTime();
                    speed = passage.length() / ((t1 - t0) / 1000.0 / 60.0);

                    User u = new User(userName, accuracy, speed, t1);

                    scoreLabel.setText(
                        "Result: \n\n" +
                        "Score: " + u.getScore() + "\n" +
                        "Accuracy: " + u.getAccuracy() + "\n" +
                        "Speed(letters/min): " + u.getSpeed() + "\n" +
                        "date: " + u.getDate() + "\n"
                    );

                    btnRanking.setVisible(true);
                    btnSwitchUser.setVisible(true);
                    btnTryAgain.setVisible(true);
                    btnStart.setVisible(false);
                    resultHBox.setVisible(true);
                    userNameHBox.setVisible(false);
                    startGroup.setVisible(true);
                    popup.setVisible(true);

                    try {
                        File f = new File("ranking.txt");
                        if (f.createNewFile()) {
                            System.out.println("File created: " + f.getName());
                        }

                        FileWriter fw = new FileWriter("ranking.txt", true);
                        BufferedWriter bfw = new BufferedWriter(fw);
                        bfw.write(
                            userName + " " +
                            u.getScore() + " " +
                            accuracy + " " +
                            speed + " " +
                            t1 +
                            "\n"
                        );
                        bfw.close();

                    } catch (IOException event) {
                        System.out.println(event.toString());
                    }

                }

                if (btn.getText() == "Try Again") {

                    t0 = new Date().getTime();

                    startGroup.setVisible(false);
                    popup.setVisible(false);

                }

                if (btn.getText() == "Switch User") {

                    btnSwitchUser.setVisible(false);
                    btnTryAgain.setVisible(false);
                    btnStart.setVisible(true);
                    resultHBox.setVisible(false);
                    userNameHBox.setVisible(true);

                }

                if (btn.getText() == "Ranking") {
                    ArrayList<User> arr = new ArrayList<>();
                    try {
                        Scanner s = new Scanner(new File("ranking.txt"));
                        int count = 0;

                        String name = s.next();
                        int score = Integer.parseInt(s.next());
                        double accuracy = Double.parseDouble(s.next());
                        double speed = Double.parseDouble(s.next());
                        long date = Long.parseLong(s.next());
                        User u = new User(name, accuracy, speed, date);
                        arr.add(u);

                        while(s.hasNext()) {
                            switch (count%5) {
                                case 0: {
                                    name = s.next();
                                    break;
                                }
                                case 1: {
                                    score = Integer.parseInt(s.next());
                                    break;
                                }
                                case 2: {
                                    accuracy = Double.parseDouble(s.next());
                                    break;
                                }
                                case 3: {
                                    speed = Double.parseDouble(s.next());
                                    break;
                                }
                                case 4: {
                                    date = Long.parseLong(s.next());
                                    u = new User(name, accuracy, speed, date);
                                    arr.add(u);
                                    break;
                                }
                            }
                            count++;
                        }
                        arr = insertionSort(arr, 0);
                        // printAnArrayList(arr);

                        c1.setCellValueFactory(new PropertyValueFactory<>("name"));
                        c2.setCellValueFactory(new PropertyValueFactory<>("score"));
                        c3.setCellValueFactory(new PropertyValueFactory<>("accuracy"));
                        c4.setCellValueFactory(new PropertyValueFactory<>("speed"));
                        c5.setCellValueFactory(new PropertyValueFactory<>("date"));

                        for (User ele : arr) {
                            table.getItems().add(ele);
                        }
                    } catch (FileNotFoundException event) {
                        System.out.println(event.toString());
                    }
                    startGroup.setVisible(false);
                    rankingGroup.setVisible(true);
                }

                if (btn.getText() == "Close") {
                    startGroup.setVisible(true);
                    rankingGroup.setVisible(false);
                }

                if (btn.getText() == "Exit") {
                    ((Stage) btn.getScene().getWindow()).close();
                }

            }
        });

        btn.setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                btn.setEffect(ds1);
                btn.setEffect(ds2);
                btn.setTranslateX(0);
                btn.setTranslateY(0);
                btn.setStyle("-fx-background-color: "+ keyColor + "; -fx-text-fill: white;");
            }
        });
    }

    public static void printAnArrayList(ArrayList<User> list) {
        System.out.println("");
        ListIterator<User> li = list.listIterator();
        while(li.hasNext()) {
            System.out.print(li.next().getScore() + " ");
        }
        // System.out.println(list.size());
        System.out.println("");
    }

    public static ArrayList<User> insertionSort(ArrayList<User> arr, int index) {

        ArrayList<User> result = arr;
        int indexTemp = index;

        if (indexTemp >= 0) {
            if (indexTemp < result.size() - 1) {
                User u = result.get(indexTemp);
                if (result.get(indexTemp).getScore() < result.get(indexTemp + 1).getScore()) {
                    result.set(indexTemp, arr.get(indexTemp + 1));
                    result.set(indexTemp+1, u);
                    indexTemp--;
                    insertionSort(result, indexTemp);
                } else {
                    insertionSort(result, index + 1);
                }
            } else {
                return result;
            }
        } else {
            insertionSort(result, index + 1);
        }
        return result;
    }






    // randomly generate a passage
    public String getPassage() {
        Random rand = new Random();
        int result = rand.nextInt(3);
        if(result == 0)
        {
            passage = "She reached her goal, exhausted. Even more chilling to her was that"
                    + " the euphoria that she thought she'd feel upon \nreaching it wasn't there."
                    + " Something wasn't right. Was this the only feeling she'd have for over five years of hard work?";
        }
        else if(result == 1) {
            passage = "He looked at the sand. Picking up a handful, he wondered how many grains were in his hand."
                    + " Hundreds of thousands?\n\"Not enough,\" the said under his breath. I need more.";
        }
        else {
            passage = "The computer wouldn't start. She banged on the side and tried again. Nothing."
                    + " She lifted it up and dropped it to \nthe table. Still nothing. She banged her closed fist against the top."
                    + " It was at this moment she saw the irony of \ntrying to fix the machine with violence.";
        }
        return passage;
    }

}