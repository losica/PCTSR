package GUI;

import heuristics.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.PathResult;
import model.Place;
import reader.DataReader;

import java.util.List;
import java.util.Locale;

/**
 * Intro to JavaFX - https://openjfx.io/openjfx-docs/#introduction
 */
public class GUI extends Application {
    private Canvas canvas;
    // used to draw inside our main canvas
    private double[][] distanceMatrix;
    private Place[] places;
    private GUIUtil guiUtil;
    private PathResult[] pathResults;
    // fields needed to store for the interactive panel information (text inputs, error messages, results)
    private TextField startVertexTxt;
    private TextField minProfitTxt;
    private TextField agentsNumberTxt;
    private Label errorMsg;
    private int startVertex = -1;
    private TextFlow totalResultTxt;
    //button to save results to the file
    private Button saveResultsBtn;
    //fields used for writing to the file
    private String resHeuristic = "";
    private String resDescription = "";
    private final int FONT_SIZE_BODY = 13;
    private final String FONT_NAME = "Corbel Light";
    //color settings
    private Color TEXT_COLOR = Color.rgb(31, 41, 34);
    private final Color[] color = {
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.VIOLET,
            Color.AQUA,
            Color.HOTPINK,
            Color.LIME,
            Color.BROWN,
            Color.GREY
    };
    private final Color CENTRAL_PANEL_COLOR = Color.rgb(255, 255, 255);
    private final Color LEFT_PANEL_COLOR = Color.rgb(242, 242, 242);
    private final Color HEADCOLOR = Color.rgb(115, 115, 115);

    public void run() {
        // method from Application to set up the program as Java FX app
        launch();
    }

    // abstract method from Application
    @Override
    public void start(Stage primaryStage) {
        final String TITLE = "PCTSR";
        //prepare data
        DataReader reader = new DataReader();
        this.distanceMatrix = reader.getDistanceMatrix();
        this.places = reader.getAllCompanies();
        this.guiUtil = new GUIUtil(this.places);

        //prepare window
        // in JavaFX, the window is called Stage
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(getScene());
        primaryStage.show();
    }

    // creates the 'canvas'
    private Scene getScene() {
        final int WIDTH = 860;
        final int HEIGHT = 700;

        // the layouts are called panes. This is a default layout from JavaFX - https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(this.CENTRAL_PANEL_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        // set the layout sections
        //font related settings
        int fontSizeHeader = 22;
        int fontSizeFooter = 14;
        pane.setTop(getHorizontalTextBox("Prize Collecting Traveling Sales Representative", fontSizeHeader));
        pane.setBottom(getHorizontalTextBox("Created by: adwi@itu.dk & kald@itu.dk", fontSizeFooter));
        pane.setLeft(getInteractionPanel());
        pane.setCenter(getMainCanvas());

        return new Scene(pane, WIDTH, HEIGHT);
    }

    private Pane getMainCanvas() {
        Pane wrapperPane = new Pane();
        // add background image
        wrapperPane.setStyle("-fx-background-image: url('/GUI/dk.png');-fx-background-size: 100% 100%;-fx-background-repeat: no-repeat;");

        //add canvas
        this.canvas = new Canvas();
        wrapperPane.getChildren().add(canvas);
        // Bind the width/height property to the wrapper Pane
        this.canvas.widthProperty().bind(wrapperPane.widthProperty());
        this.canvas.heightProperty().bind(wrapperPane.heightProperty());

        // redraw when resized
        this.canvas.widthProperty().addListener(event -> draw(this.canvas));
        this.canvas.heightProperty().addListener(event -> draw(this.canvas));
        draw(canvas);

        return wrapperPane;
    }

    private VBox getInteractionPanel() {
        final TextAlignment alignment = TextAlignment.LEFT;
        initializeTextInputs();
        VBox box = new VBox();
        createErrorMsgLabel(alignment, FONT_SIZE_BODY);
        this.totalResultTxt = new TextFlow();
        box.getChildren().addAll(
                createLabel("Starting vertex:", alignment, FONT_SIZE_BODY),
                this.startVertexTxt,
                createLabel("Desired profit (bn. DKK):", alignment, FONT_SIZE_BODY),
                this.minProfitTxt,
                createLabel("Number of agents:", alignment, FONT_SIZE_BODY),
                this.agentsNumberTxt,
                createHeuristicButton("HeuristicOne", "one"),
                createHeuristicButton("HeuristicTwo", "two"),
                createHeuristicButton("HeuristicThree", "three"),
                createHeuristicButton("HeuristicFour", "four"),
                this.errorMsg,
                this.totalResultTxt,
                createSaveResultsButton()
        );

        box.setBackground(new Background(new BackgroundFill(this.LEFT_PANEL_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        box.setSpacing(8);
        List<Node> children = box.getChildren();
        for (int i = 0; i < children.size(); i++) {
            // only set the top margin for the input fields
            if (i % 2 == 0 && i < 7) {
                box.setMargin(children.get(i), new Insets(8, 10, 0, 5));
            } else {
                box.setMargin(children.get(i), new Insets(0, 10, 0, 5));
            }
        }
        return box;
    }

    private Button createSaveResultsButton() {
        this.saveResultsBtn = new Button("Save results") {{
            setOnAction(e -> {
                saveResultPathsToFile();
                setText("Saved");
                setDisable(true);
            });
            setPrefWidth(150);
            setFont(new Font(FONT_NAME, FONT_SIZE_BODY));
            //hide the button
            setVisible(false);
        }};
        return this.saveResultsBtn;
    }

    private void saveResultPathsToFile() {
        ResultsPrinter.printToFile(this.resHeuristic, this.pathResults, this.resDescription);
    }

    private Button createHeuristicButton(String buttonText, String heuristic) {
        return new Button(buttonText) {{
            setOnAction(e -> {
                getInputAndDraw(heuristic);
            });
            setPrefWidth(150);
            setFont(new Font(FONT_NAME, FONT_SIZE_BODY));
        }};
    }

    private void getInputAndDraw(String heuristic) {
        //clear the error message, total distance and profit
        this.errorMsg.setText("");
        //clear detailed info text
        this.totalResultTxt.getChildren().clear();
        try {
            int startingV = Integer.parseInt(this.startVertexTxt.getText());
            int minProfit = Integer.parseInt(this.minProfitTxt.getText());
            int agentsNumber = Integer.parseInt(this.agentsNumberTxt.getText());
            if (startingV >= 0 && startingV < 91 && minProfit > 0.0 && minProfit < this.guiUtil.getTotalProfit() - this.places[startingV].getFirmProfit() && agentsNumber > 0 && agentsNumber <= 10) {
                // reset the save button
                this.saveResultsBtn.setDisable(false);
                this.saveResultsBtn.setText("Save results");
                getResultPath(heuristic, startingV, agentsNumber, minProfit);
                System.out.println(heuristic + ", startVertex " + startingV + ", agents " + agentsNumber + " minPof: " + minProfit);
                draw(this.canvas);
                //show the save results button
                this.saveResultsBtn.setVisible(true);
            } else {
                //set the error message
                String error = "Invalid input.\n";
                if (startingV < 0 || startingV > 90) {
                    error += "Starting vertex must be a number in range 0-90.\n";
                }
                if (startingV >= 0 && startingV < 91 && (minProfit < 0.0 || minProfit > this.guiUtil.getTotalProfit() - this.places[startingV].getFirmProfit())) {
                    error += "For the chosen staring point,\nprofit can't be bigger than " + getAvailableProfitFromVertex(startingV) + ".\n";
                }
                if (agentsNumber < 0 || agentsNumber > 10) {
                    error += "Max no. of agents 10.";
                }
                this.errorMsg.setText(error);
                System.out.println("Invalid input");
                this.pathResults = null;
                draw(this.canvas);
                //hide the save results button
                this.saveResultsBtn.setVisible(false);
                //reset resDescription and resHeristic (variables used to print in the file)
                this.resDescription = "";
                this.resHeuristic = "";
            }
        } catch (NumberFormatException e) {
            this.errorMsg.setText("Invalid input type.\nPlease fill all input fields correctly.");
            e.getStackTrace();
            //hide the save results button
            this.saveResultsBtn.setVisible(false);
            //reset resDescription and resHeristic (variables used to print in the file)
            this.resDescription = "";
            this.resHeuristic = "";
        }
    }

    private void initializeTextInputs() {
        // initialize the input fields
        this.startVertexTxt = new TextField();
        this.minProfitTxt = new TextField();
        this.agentsNumberTxt = new TextField();

        // add placeholders
        String defaultMaxProfit = " default max 299";
        this.startVertexTxt.setPromptText(" 0 to 90");
        this.minProfitTxt.setPromptText(defaultMaxProfit);
        this.agentsNumberTxt.setPromptText(" 1 to 10");

        // keep the prompt text visible on focus if field is empty
        bindPromptTextFormatOnFocus(this.startVertexTxt);
        bindPromptTextFormatOnFocus(this.minProfitTxt);
        bindPromptTextFormatOnFocus(this.agentsNumberTxt);

        // add watcher for dynamic max profit update
        this.startVertexTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0 && newValue.matches("[0-9]+") && !newValue.matches("[a-zA-Z]+")) {
                this.minProfitTxt.setPromptText(" max " + getAvailableProfitFromVertex(Integer.parseInt(newValue)));
            } else {
                this.minProfitTxt.setPromptText(defaultMaxProfit);
            }
        });
    }

    private void bindPromptTextFormatOnFocus(TextField textField) {
        String visualPromptText = "-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);";
        textField.styleProperty().bind(
                Bindings
                        .when(textField.focusedProperty())
                        .then(visualPromptText)
                        .otherwise(visualPromptText));
    }

    // creates a node that contains a text box
    private HBox getHorizontalTextBox(String text, int fontSize) {
        Label label = createLabel(text, TextAlignment.CENTER, fontSize);
        label.setTextFill(CENTRAL_PANEL_COLOR);
        return new HBox() {{
            getChildren().add(label);
            setMargin(getChildren().get(0), new Insets(5));
            setAlignment(Pos.CENTER);
            setBackground(new Background(new BackgroundFill(HEADCOLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        }};
    }

    // create a simple text
    private Label createLabel(String text, TextAlignment alignment, int fontSize) {
        return new Label(text) {{
            setTextAlignment(alignment);
            setFont(new Font(FONT_NAME, fontSize));
            setTextFill(TEXT_COLOR);
        }};
    }

    // create a simple text
    private Text createText(String text, TextAlignment alignment, int fontSize, Color color) {
        return new Text(text) {{
            setTextAlignment(alignment);
            setFont(new Font(FONT_NAME, fontSize));
            setFill(color);
        }};
    }

    private void createErrorMsgLabel(TextAlignment alignment, int fontSize) {
        this.errorMsg = new Label("");
        this.errorMsg.setTextAlignment(alignment);
        this.errorMsg.setFont(new Font("Arial", fontSize - 2));
        this.errorMsg.setTextFill(Color.RED);
    }


    private void draw(Canvas canvas) {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //clear canvas
        gc.clearRect(0, 0, width, height);
        //set line color
        gc.setStroke(Color.BLACK);
        //set oval color
        gc.setFill(Color.BLACK);
        //draw places
        drawPlaces(gc, width, height);
        if (this.pathResults != null) {
            drawPaths(gc, width, height);
        }
    }

    private void drawPlaces(GraphicsContext gc, int width, int height) {
        final int diameter = 3;
        if (this.places != null) {
            for (int i = 0; i < this.places.length; i++) {
                Place p = this.places[i];
                int x = this.guiUtil.getPlaceXposition(p, width);
                int y = this.guiUtil.getPlaceYposition(p, height);
                if (i == this.startVertex) {
                    gc.setFill(Color.GREEN);
                    gc.fillOval(x, y, diameter, diameter);
                    gc.setFill(Color.BLACK);
                } else {
                    gc.fillOval(x, y, diameter, diameter);
                }
            }
        }
    }

    private void drawPaths(GraphicsContext gc, int width, int height) {
        final int vertexRadius = 2;
        for (int i = 0; i < this.pathResults.length; i++) {
            gc.setStroke(this.color[i]);
            List<Place> rp = this.pathResults[i].getResultPath();
            gc.beginPath();
            int startX = this.guiUtil.getPlaceXposition(rp.get(0), width) + vertexRadius;
            int startY = this.guiUtil.getPlaceYposition(rp.get(0), height) + vertexRadius;

            gc.moveTo(startX, startY);
            for (int j = 1; j < rp.size() - 1; j++) {
                Place p = rp.get(j);
                int x1 = this.guiUtil.getPlaceXposition(p, width) + vertexRadius;
                int y1 = this.guiUtil.getPlaceYposition(p, height) + vertexRadius;
                gc.lineTo(x1, y1);
            }
            gc.lineTo(startX, startY);
            gc.stroke();
        }
    }

    private void getResultPath(String heuristic, int startVertex, int agentsNumber, int minProfit) {
        this.startVertex = startVertex;
        Heuristic h = null;
        switch (heuristic) {
            case "one":
                h = new HeuristicOne(this.distanceMatrix, this.places, startVertex, agentsNumber, minProfit);
                this.resHeuristic = "HeuristicOne";
                break;
            case "two":
                h = new HeuristicTwo(this.distanceMatrix, this.places, startVertex, agentsNumber, minProfit);
                this.resHeuristic = "HeuristicTwo";
                break;
            case "three":
                h = new HeuristicThree(this.distanceMatrix, this.places, startVertex, agentsNumber, minProfit);
                this.resHeuristic = "HeuristicThree";
                break;
            case "four":
                h = new HeuristicFour(this.distanceMatrix, this.places, startVertex, agentsNumber, minProfit);
                this.resHeuristic = "HeuristicFour";
                break;
        }
        this.pathResults = h.getResultPaths();
        //set detailed info used for GUI
        setDetailedInfo(h.getSumProfit());
        //set info used for printing results to the file
        this.resDescription = "Starting vertex: " + startVertex + "\n"
                + "Minimum profit: " + minProfit + "\n"
                + "Number of agents: " + agentsNumber + "\n"
                + "Total profit collected (bn. DKK): " + String.format(Locale.US, "%.2f", h.getSumProfit()) + "\n"
                + "Total distance travelled (km): " + String.format(Locale.US, "%.2f", getTotalDistance()) + "\n\n";
    }

    private void setDetailedInfo(double sumProfit) {
        String totalInfo = "Total profit (bn. DKK): " + String.format("%.2f", sumProfit) + "\n"
                + "Total distance (km): " + String.format("%.2f", getTotalDistance()) + "\n\n"
                + "Distance per agent (km):\n";
        Text infoTXT = createText(totalInfo, TextAlignment.LEFT, FONT_SIZE_BODY, Color.BLACK);
        this.totalResultTxt.getChildren().add(infoTXT);
        for (int i = 0; i < this.pathResults.length; i++) {
            PathResult p = this.pathResults[i];
            String agentNo = "#" + (i + 1) + " : ";
            String text;
            if (i != pathResults.length - 1) {
                text = String.format("%.2f", (p.getPathLength() / 1000)) + "\n";
            } else {
                text = String.format("%.2f", (p.getPathLength() / 1000));
            }
            Text t1 = createText(agentNo, TextAlignment.LEFT, FONT_SIZE_BODY, color[i]);
            Text t2 = createText(text, TextAlignment.LEFT, FONT_SIZE_BODY, Color.BLACK);
            this.totalResultTxt.getChildren().addAll(t1, t2);
        }
    }

    private double getTotalDistance() {
        double distance = 0.0;
        for (PathResult p : this.pathResults) {
            distance += p.getPathLength();
        }
        return distance / 1000;
    }

    private String getAvailableProfitFromVertex(int startVertex) {
        return String.valueOf((int) (this.guiUtil.getTotalProfit() - this.places[startVertex].getFirmProfit()));
    }
}