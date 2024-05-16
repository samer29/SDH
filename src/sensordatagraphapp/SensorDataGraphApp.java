package sensordatagraphapp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;

public class SensorDataGraphApp extends Application {

    private final XYChart.Series<Number, Number> xSeries;
    private final XYChart.Series<Number, Number> ySeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> zSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> xgSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> ygSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> zgSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> tempSeries = new XYChart.Series<>();

    private NumberAxis xAxis;
    private int seconds; // Default value
    private final Spinner<Integer> timeSpinner = new Spinner<>();
    private final Spinner<Integer> hoursSpinner = new Spinner<>();
    private final Spinner<Integer> minutesSpinner = new Spinner<>();
    private final Spinner<Integer> secondsSpinner = new Spinner<>();

    String cssFile = getClass().getResource("style.css").toExternalForm();

    public SensorDataGraphApp() {
        this.xSeries = new XYChart.Series<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) {
        // Create axes with default upper bound
        xAxis = new NumberAxis(0, seconds, 1);

        // Create chart
        LineChart<Number, Number> chart = new LineChart<>(xAxis, new NumberAxis());
        chart.setTitle("Sensor Data");

        // Set names for series (graph) - Legend
        xSeries.setName("X");
        ySeries.setName("Y");
        zSeries.setName("Z");

        xgSeries.setName("Xg");
        ygSeries.setName("Yg");
        zgSeries.setName("Zg");

        tempSeries.setName("Temp");

        // Add series to chart
        chart.getData().addAll(xSeries, ySeries, zSeries, xgSeries, ygSeries, zgSeries, tempSeries);

        // Create checkboxes for parameters
        CheckBox xCheckBox = new CheckBox("X");
        CheckBox yCheckBox = new CheckBox("Y");
        CheckBox zCheckBox = new CheckBox("Z");

        CheckBox xgCheckBox = new CheckBox("Xg");
        CheckBox ygCheckBox = new CheckBox("Yg");
        CheckBox zgCheckBox = new CheckBox("Zg");

        CheckBox tempCheckBox = new CheckBox("Temp");

        // Set default selected checkboxes
        xCheckBox.setSelected(true);
        yCheckBox.setSelected(true);
        zCheckBox.setSelected(true);
        xgCheckBox.setSelected(true);
        ygCheckBox.setSelected(true);
        zgCheckBox.setSelected(true);
        tempCheckBox.setSelected(true);

        // Add event handlers for checkboxes
        xCheckBox.setOnAction(event -> updateChartVisibility(xCheckBox, xSeries, chart));
        yCheckBox.setOnAction(event -> updateChartVisibility(yCheckBox, ySeries, chart));
        zCheckBox.setOnAction(event -> updateChartVisibility(zCheckBox, zSeries, chart));

        xgCheckBox.setOnAction(event -> updateChartVisibility(xgCheckBox, xgSeries, chart));
        ygCheckBox.setOnAction(event -> updateChartVisibility(ygCheckBox, ygSeries, chart));
        zgCheckBox.setOnAction(event -> updateChartVisibility(zgCheckBox, zgSeries, chart));
        tempCheckBox.setOnAction(event -> updateChartVisibility(tempCheckBox, tempSeries, chart));

        // Create HBox for checkboxes
        HBox checkboxesBox = new HBox(xCheckBox, yCheckBox, zCheckBox, xgCheckBox, ygCheckBox, zgCheckBox,
                tempCheckBox);
        checkboxesBox.setSpacing(10);
        checkboxesBox.setAlignment(Pos.CENTER);

        // Create labels for the spinners
        Label timeLabel = new Label("Time:");
        Label hoursLabel = new Label("Hours:");
        Label minutesLabel = new Label("Minutes:");
        Label secondsLabel = new Label("Seconds:");

        // Create SpinnerValueFactory instances for each spinner
        SpinnerValueFactory<Integer> timeValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1200, 0);
        SpinnerValueFactory<Integer> hoursValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 0);
        SpinnerValueFactory<Integer> minutesValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        SpinnerValueFactory<Integer> secondsValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);

        // Set the value factories to the spinners
        timeSpinner.setValueFactory(timeValueFactory);
        hoursSpinner.setValueFactory(hoursValueFactory);
        minutesSpinner.setValueFactory(minutesValueFactory);
        secondsSpinner.setValueFactory(secondsValueFactory);

        // Create VBox instances for each spinner and label
        VBox timeBox = new VBox(timeLabel, timeSpinner);
        VBox hoursBox = new VBox(hoursLabel, hoursSpinner);
        VBox minutesBox = new VBox(minutesLabel, minutesSpinner);
        VBox secondsBox = new VBox(secondsLabel, secondsSpinner);

        Button validateButton = new Button("Validate");
        validateButton.setOnAction(event -> handleValidate(timeSpinner.getValue().toString()));

        // Create inputBox HBox and add spinners and labels
        HBox inputBox = new HBox(timeBox, hoursBox, minutesBox, secondsBox, validateButton);
        inputBox.setSpacing(10);
        inputBox.setAlignment(Pos.CENTER); // Align children in the center horizontally

        VBox vbox = new VBox(inputBox, chart, checkboxesBox);
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);

        // Create scene
        Scene scene = new Scene(vbox, 800, 600);
        scene.getStylesheets().add(cssFile);

        // Set scene and show stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sensor Data Graph App");
        // primaryStage.getIcons().add(new Image("/src/logo.png")); // Check this line
        primaryStage.show();

    }

    private void updateChartVisibility(CheckBox checkBox, XYChart.Series<Number, Number> series,
            LineChart<Number, Number> chart) {
        if (checkBox.isSelected()) {
            if (!chart.getData().contains(series)) {
                chart.getData().add(series);
            }
        } else {
            chart.getData().remove(series);
        }
    }

    private void readDataFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            List<Integer> secondsList = new ArrayList<>();
            List<Double> xgList = new ArrayList<>();
            List<Double> ygList = new ArrayList<>();
            List<Double> zgList = new ArrayList<>();
            List<Double> xList = new ArrayList<>();
            List<Double> yList = new ArrayList<>();
            List<Double> zList = new ArrayList<>();
            List<Double> temp = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                if (line.startsWith("Hour:")) {
                    String[] parts = line.split(":");
                    if (parts.length >= 4) {
                        int hours = Integer.parseInt(parts[1].trim());
                        int minutes = Integer.parseInt(parts[2].trim());
                        int seconds = Integer.parseInt(parts[3].trim());
                        int HOURS2 = hoursSpinner.getValue();
                        int MIN2 = minutesSpinner.getValue();
                        int SEC2 = secondsSpinner.getValue();

                        // Convert time to seconds
                        int totalSeconds = hours * 3600 + minutes * 60 + seconds - (HOURS2 * 3600 + MIN2 * 60) - SEC2;
                        secondsList.add(totalSeconds);
                    }
                } else if (line.startsWith("Acceleration (g)")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 4) {
                        try {
                            xgList.add(Double.parseDouble(parts[2].split("=")[1]));
                            ygList.add(Double.parseDouble(parts[3].split("=")[1]));
                            zgList.add(Double.parseDouble(parts[4].split("=")[1]));

                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            System.err.println("Error parsing data: " + line);
                        }
                    } else {
                        System.err.println("Invalid data format: " + line);
                    }
                } else if (line.startsWith("Angular velocity (degree/sec)")) {
                    // Define a pattern to match X, Y, and Z values
                    Pattern pattern = Pattern.compile("X=(-?\\d+\\.?\\d*)\\s+Y=(-?\\d+\\.?\\d*)\\s+Z=(-?\\d+\\.?\\d*)");

                    // Match the pattern against the line
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        try {
                            // Extract X, Y, and Z values from the matched groups
                            double xValue = Double.parseDouble(matcher.group(1));
                            double yValue = Double.parseDouble(matcher.group(2));
                            double zValue = Double.parseDouble(matcher.group(3));

                            xList.add(xValue);
                            yList.add(yValue);
                            zList.add(zValue);

                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing data: " + line);
                        }
                    } else {
                        System.err.println("Invalid data format: " + line);
                    }
                } else if (line.startsWith("Temperature")) {
                    // Updated code for parsing temperature data
                    String[] parts = line.split("="); // Split at "="
                    if (parts.length >= 2) {
                        String tempValueStr = parts[1].trim().split("\\s+")[0]; // Extract temperature value
                        try {
                            double temperature = Double.parseDouble(tempValueStr);
                            temp.add(temperature);
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing temperature data: " + line);
                        }
                    } else {
                        System.err.println("Invalid temperature data format: " + line);
                    }
                }
            }

            // Add data to series
            for (int i = 0; i < Math.min(secondsList.size(), Math.min(xgList.size(), Math.min(ygList.size(), Math.min(
                    zgList.size(),
                    Math.min(xList.size(), Math.min(yList.size(), Math.min(zList.size(), temp.size()))))))); i++) {
                xSeries.getData().add(new XYChart.Data<>(secondsList.get(i), xgList.get(i)));
                ySeries.getData().add(new XYChart.Data<>(secondsList.get(i), ygList.get(i)));
                zSeries.getData().add(new XYChart.Data<>(secondsList.get(i), zgList.get(i)));

                xgSeries.getData().add(new XYChart.Data<>(secondsList.get(i), xList.get(i)));
                ygSeries.getData().add(new XYChart.Data<>(secondsList.get(i), yList.get(i)));
                zgSeries.getData().add(new XYChart.Data<>(secondsList.get(i), zList.get(i)));

                tempSeries.getData().add(new XYChart.Data<>(secondsList.get(i), temp.get(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleValidate(String input) {
        try {
            int secondsValue = Integer.parseInt(input);
            clearChartData(); // Clear chart data before reading new data

            if (secondsValue < 0 || secondsValue > 3600) {
                showAlert("Invalid input! Please enter seconds between 0 and 3600.");
            } else {
                seconds = secondsValue; // Update seconds if valid
                xAxis.setUpperBound(seconds);
                readDataFromFile("data.txt"); // Move this inside the valid input block
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid input! Please enter a valid integer for seconds.");
        }
    }

    private void clearChartData() {
        xSeries.getData().clear();
        ySeries.getData().clear();
        zSeries.getData().clear();

        xgSeries.getData().clear();
        ygSeries.getData().clear();
        zgSeries.getData().clear();

        tempSeries.getData().clear();

    }

    private void showAlert(String message) {
        Stage alertStage = new Stage();
        VBox alertBox = new VBox(new Label(message));
        Scene alertScene = new Scene(alertBox, 200, 100);
        alertStage.setScene(alertScene);
        alertStage.setTitle("Error");
        alertStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
