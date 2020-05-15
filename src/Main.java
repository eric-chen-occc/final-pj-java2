
/*
 *	Chance Hughes and Tengnan Yao(Team Faceplant)
 *	OCCC spring 2020
 *	advanced java final project: Typing Game
 *  Brief Program Description:
 *          Our project is a typing game where the user will be given a random paragraph or sentence
 *      that they will have to spell out correctly in order to get a higher score. The program
 *      will first ask the user for a name so we can keep track of the player’s high score in a
 *      txt file. The paragraph will be randomly selected from a set and displayed on the screen.
 *      The game will check 1 letter a`t a time while checking if the word is being spelled
 *      correctly. Once the user presses space, the program will check if the word was spelled
 *      correctly and move to the next word. When the user reaches the end of the paragraph it will
 *      give you a score based how accurate and the speed at which the user typed.
 */



import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.effect.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import java.io.*;
import java.util.*;



public class Main extends Application {

    // global variables
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
    TextFlow textOverflow;
    TextField userNameInput;
    Group startGroup, rankingGroup;
    Button btnStart, btnTryAgain, btnSwitchUser, btnRanking, btnExit, btnClose;
    Label scoreLabel;
    TableView<User> table;
    TableColumn c1, c2, c3, c4, c5;

    ArrayList<Text> letters = new ArrayList<>();
    ArrayList<Integer> incorrectIndexes = new ArrayList<>();
    TextArea userInput = new TextArea();

    // main function
    public static void main (String[] args)
    {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        // base setup
        Pane root = new Pane();

        Scene scene = new Scene(root);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Digital Keyboard");

        primaryStage.show();



        /* GUI part */

        // textbox
        textOverflow = new TextFlow();
        textOverflow.setMaxSize(1200, 300);
        textOverflow.setLayoutX(40);
        textOverflow.setLayoutY(40);
        root.getChildren().add(textOverflow);

        // user input textarea
        userInput.setWrapText(true);
        userInput.setLayoutX(40);
        userInput.setLayoutY(100);
        userInput.setPrefSize(1196, 200);
        userInput.setFont(new Font("Arial", 20));
        userInput.addEventHandler(KeyEvent.KEY_TYPED, this::onKeyPressed);
        root.getChildren().add(userInput);


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

        // popup page
        startGroup = new Group();
        startGroup.setLayoutX(0);
        startGroup.setLayoutY(0);

        // user name section
        userNameHBox = new HBox();
        Label yourNameLabel = new Label("Your name:");
        yourNameLabel.setFont(new Font("Arial", 20));
        userNameInput = new TextField ();
        userNameHBox.getChildren().addAll(yourNameLabel, userNameInput);
        userNameHBox.setSpacing(10);
        userNameHBox.setLayoutX(500);
        userNameHBox.setLayoutY(300);
        startGroup.getChildren().add(userNameHBox);

        // result section
        resultHBox = new HBox();
        scoreLabel = new Label();
        scoreLabel.setFont(new Font("Arial", 20));
        resultHBox.getChildren().add(scoreLabel);
        resultHBox.setLayoutX(500);
        resultHBox.setLayoutY(150);
        startGroup.getChildren().add(resultHBox);
        resultHBox.setVisible(false);

        // functional buttons
        btnStart = createNewButton("Start", 550, 380, 180, 40);
        btnTryAgain = createNewButton("Try Again", 550, 380, 180, 40);
        btnSwitchUser = createNewButton("Switch User", 570, 450, 140, 40);
        btnRanking = createNewButton("Ranking", 1130, 550, 100, 40);
        btnExit = createNewButton("Exit", 1130, 600, 100, 40);
        startGroup.getChildren().addAll(btnStart, btnTryAgain, btnSwitchUser, btnRanking, btnExit);

        //init button visibilities
        btnTryAgain.setVisible(false);
        btnSwitchUser.setVisible(false);
        btnRanking.setVisible(false);

        root.getChildren().add(startGroup);


        // Rankings popup
        rankingGroup = new Group();
        rankingGroup.setLayoutX(0);
        rankingGroup.setLayoutY(0);

        // a table showing the ranking info
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

    // create new digital keyboard button with style
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

    // actions when hit a button
    public void pressAButton(Button btn) {
        btn.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {

                // style change when hitting a keyboard key
                btn.setEffect(ds0);
                btn.setTranslateX(1);
                btn.setTranslateY(1);
                btn.setStyle("-fx-background-color: "+ keyPressedColor + "; -fx-text-fill: white;");


                // game start when hitting start
                if (btn.getText() == "Start") {

                    // avoid there is spaces in user name
                    userName = userNameInput.getText().replaceAll("\\s","");

                    // user name can not be empty
                    if (userName.length() == 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Need a user name");
                        alert.setHeaderText(null);
                        alert.setContentText("User name can not be empty.");
                        alert.showAndWait();
                    } else {
                        letters.clear();
                        userInput.clear();
                        incorrectIndexes.clear();
                        passage = getPassage();
                        textOverflow.getChildren().clear();
                        showText(passage, incorrectIndexes);
                        gameStarted = true;
                        t0 = new Date().getTime();

                        startGroup.setVisible(false);
                        popup.setVisible(false);
                    }


                }

                // try again with the same user name
                if (btn.getText() == "Try Again") {

                    letters.clear();
                    userInput.clear();
                    incorrectIndexes.clear();
                    passage = getPassage();
                    textOverflow.getChildren().clear();
                    showText(passage, incorrectIndexes);
                    gameStarted = true;
                    t0 = new Date().getTime();

                    startGroup.setVisible(false);
                    popup.setVisible(false);

                }

                // change to another user to play
                if (btn.getText() == "Switch User") {

                    btnSwitchUser.setVisible(false);
                    btnTryAgain.setVisible(false);
                    btnStart.setVisible(true);
                    resultHBox.setVisible(false);
                    userNameHBox.setVisible(true);

                }

                // show rankings
                if (btn.getText() == "Ranking") {

                    ArrayList<User> arr = new ArrayList<>();

                    try {

                        // init the ranking info
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

                // close the ranking popup
                if (btn.getText() == "Close") {
                    startGroup.setVisible(true);
                    rankingGroup.setVisible(false);
                }

                // exit
                if (btn.getText() == "Exit") {
                    ((Stage) btn.getScene().getWindow()).close();
                }

            }
        });

        // style changes on the digital keyboard keys when released
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

    // support to print an array list
    public static void printAnArrayList(ArrayList<User> list) {
        System.out.println("");
        ListIterator<User> li = list.listIterator();
        while(li.hasNext()) {
            System.out.print(li.next().getScore() + " ");
        }
        System.out.println("");
    }

    // ranking sorting using insertion sort
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

    // typing and showing the incorrect characters
    public void showText(String passage, ArrayList<Integer> incorrectIndexes) {

        String str = userInput.getText();

        for (int i = 0; i < passage.length(); i++) {
            Text text = new Text(Character.toString(passage.charAt(i)));
            letters.add(text);
            text.setFont(Font.font("Verdana", 20));
            if (i < str.length()) {
                text.setFill(Color.GREEN);
            }
            for (int j = 0; j < incorrectIndexes.size(); j++) {
                if (i == incorrectIndexes.get(j)) {
                    text.setFill(Color.RED);
                }
            }
            textOverflow.getChildren().add(text);
        }

    }

    // monitoring user typing action
    private void onKeyPressed(KeyEvent event) {
        if (gameStarted) {
            String str = userInput.getText();

            int sl = str.length();
            int pl = passage.length();

            // adding incorrect characters' indexes
            ArrayList<Integer> incorrectIndexesTemp = new ArrayList<>();
            for (int i = 0; i < sl; i++) {
                if (str.charAt(i) != passage.charAt(i)) {
                    incorrectIndexesTemp.add(i);
                }
            }
            incorrectIndexes = incorrectIndexesTemp;

            textOverflow.getChildren().clear();
            showText(passage, incorrectIndexes);


            // automatically show the result when user finishes typing
            if (sl == pl) {
                gameStarted = false;

                accuracy = (pl - incorrectIndexes.size())/(double)pl;
                long t1 = new Date().getTime();
                speed = pl / ((t1 - t0) / 1000.0 / 60.0);

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

                // write user's result to txt file
                try {
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

                } catch (IOException event1) {
                    System.out.println(event1.toString());
                }

            }

        }
    }

    // randomly generate a passage
    public String getPassage() {
        String[] passages = new String[] {
            "She reached her goal, exhausted. Even more chilling to her was that the euphoria that she thought she'd feel upon reaching it wasn't there.",
            "Something wasn't right. Was this the only feeling she'd have for over five years of hard work?",
            "He looked at the sand. Picking up a handful, he wondered how many grains were in his hand.",
            "Hundreds of thousands? Not enough, the said under his breath. I need more.",
            "The computer wouldn't start. She banged on the side and tried again. Nothing.",
            "She lifted it up and dropped it to the table. Still nothing. She banged her closed fist against the top.",
            "It was at this moment she saw the irony of trying to fix the machine with violence."
        };
        int index = (int)Math.floor(Math.random() * passages.length);
        return passages[index];
    }

}