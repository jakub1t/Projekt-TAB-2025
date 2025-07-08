package com.polsl.firmakurierska.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UIBuilder {

    //rgb (95, 211, 141) = # 5FD38D

    private static final Background buttonBgInactiveDark = new Background(new BackgroundFill(Color.web("#4B4B4B"), null, null));
    private static final Background buttonBgActiveDark = new Background(new BackgroundFill(Color.web("#616161"), null, null));
    private static final Background buttonBgInactiveLight = new Background(new BackgroundFill(Color.web("#F8F8F8"), null, null));
    private static final Background buttonBgActiveLight = new Background(new BackgroundFill(Color.web("#FFFFFF"), null, null));

    private static final Background deleterBgInactive = new Background(new BackgroundFill(Color.web("#DE6363"), null, null));
    private static final Background deleterBgActive = new Background(new BackgroundFill(Color.web("#A82424"), null, null));

    private static final Background editerBgInactive = new Background(new BackgroundFill(Color.web("#A2E6BD"), null, null));
    private static final Background editerBgActive = new Background(new BackgroundFill(Color.web("#5FD38D"), null, null));
    
    private static final Background listItemBgInactive = new Background(new BackgroundFill(Color.web("#E0E0E0"), null, null));
    private static final Background listItemBgActive = new Background(new BackgroundFill(Color.web("#F0F0F0"), null, null));

    private static final Background columnBgDark = new Background(new BackgroundFill(Color.web("#2F2F2F"), null, null));
    private static final Background columnBgLight = new Background(new BackgroundFill(Color.web("#FFFFFF"), null, null));

    public final Background buttonCompletedDeliveryInactive = new Background(new BackgroundFill(Color.web("#8FDAB9"), null, null));
    public final Background buttonCompletedDeliveryActive = new Background(new BackgroundFill(Color.web("#5FD38D"), null, null));
    
    /**
     * Creates a new button
     * @param useDarkMode - True = Dark Mode / False = Light Mode
     * @param width - Preferred Width
     * @param buttonText
     * 
     */
    public Button createStylizedButton(boolean useDarkMode, int width, String buttonText) {
        Button styledButton = new Button(buttonText);

        if (useDarkMode) {
            styledButton.setBackground(buttonBgInactiveDark);

            styledButton.setStyle(
                "-fx-border-color: #BBBBBB; " + 
                "-fx-border-radius: 0; " +
                "-fx-border-width: 3; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: Arial, sans-serif; " +
                "-fx-text-fill: #BBBBBB; " +
                "-fx-cursor: hand; "
            );
            styledButton.setOnMouseEntered(e -> {styledButton.setBackground(buttonBgActiveDark); });
            styledButton.setOnMouseExited(e -> {styledButton.setBackground(buttonBgInactiveDark); });
            
        } else {
            styledButton.setBackground(buttonBgInactiveLight);

            styledButton.setStyle(
                "-fx-border-color: rgb(95, 211, 141); " + 
                "-fx-border-radius: 0; " +
                "-fx-border-width: 3; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: Arial, sans-serif; " +
                "-fx-cursor: hand; "
            );
            styledButton.setOnMouseEntered(e -> {styledButton.setBackground(buttonBgActiveLight); });
            styledButton.setOnMouseExited(e -> {styledButton.setBackground(buttonBgInactiveLight); });

        }

        styledButton.setPrefWidth(width);

        return styledButton;
    }

    /**
     * Creates 20px x 20px 'X' Button
     */
    public Button createStyledDeleteButton() {
        Button deleter = new Button("X");

        deleter.setStyle(
            "-fx-border-radius: 0; " +
            "-fx-border-width: 1; " +
            "-fx-cursor: hand; "
        );
        deleter.setBackground(deleterBgInactive);
        deleter.setPrefSize(20, 20);
        deleter.setTextFill(Color.WHITE);

        deleter.setOnMouseEntered(e -> { deleter.setBackground(deleterBgActive); });
        deleter.setOnMouseExited(e -> { deleter.setBackground(deleterBgInactive); });

        return deleter;
    } 

    /**
     * Creates 20px x 20px 'E' Button (this would be cool -> ✎ - 0x270E / would Rust fix this?)
     */
    public Button createStyledEditButton() {
        Button editer = new Button("E");

        editer.setStyle(
            "-fx-border-radius: 0; " +
            "-fx-border-width: 1; " +
            "-fx-cursor: hand; "
        );
        editer.setBackground(editerBgInactive);
        editer.setPrefSize(20, 20);

        editer.setOnMouseEntered(e -> { editer.setBackground(editerBgActive); });
        editer.setOnMouseExited(e -> { editer.setBackground(editerBgInactive); });

        return editer;
    } 

    /**
     * Dedicated for Lists such as DeliveryList
     * @param text - Button text
     * @param width - preferred width
     * @return
     */
    public Button createStyledListItem(String text, int width) {
        Button itemButton = new Button(text);

        itemButton.setBackground(listItemBgInactive);
        itemButton.setMaxWidth(Integer.MAX_VALUE);
        itemButton.setPrefWidth(width);

        itemButton.setStyle(
            "-fx-border-radius: 0; " +
            "-fx-border-width: 1; " +
            "-fx-cursor: hand; "
        );

        itemButton.setOnMouseEntered(e -> { itemButton.setBackground(listItemBgActive); });
        itemButton.setOnMouseExited(e -> { itemButton.setBackground(listItemBgInactive); });

        return itemButton;
    }

    /**
     * Creates a "Card" with two labels inside. By default, width is scaled to the maximum.
     * @param useDarkMode
     * @param labelText
     * @param valueText
     * @return
     */
    public VBox createStyledCard(boolean useDarkMode, String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        Label value = new Label(valueText);

        VBox box = new VBox(5, label, value);
        box.setPadding(new Insets(10));
        box.setPrefWidth(Integer.MAX_VALUE);
        

        if (useDarkMode) {
            label.setTextFill(Color.web("#5FD38D"));
            value.setTextFill(Color.WHITE);
            box.setBackground(buttonBgActiveDark);
            box.setStyle(
            "-fx-border-color: #BBBBBB; " +
            "-fx-border-radius: 0; " +
            "-fx-border-width: 3; "
        );
        } else {
            box.setBackground(buttonBgInactiveLight);
            box.setStyle(
            "-fx-border-color: #5FD38D; " +
            "-fx-border-radius: 0; " +
            "-fx-border-width: 3; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1); "
        );
        }

        return box;
    }

    public VBox createStylizedColumn(boolean useDarkMode, String title, int width, Node... contents) {
        Label header = new Label(title);
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        header.setMaxWidth(Integer.MAX_VALUE);

        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));
        
        VBox col = new VBox(headerBox);
        col.setPrefWidth(width);

        for (Node n : contents) {
            col.getChildren().add(n);
        }

        if (useDarkMode) {
            col.setBackground(columnBgDark);
            header.setTextFill(Color.web("#BBBBBB"));
        } else {
            col.setBackground(columnBgLight);
        }

        col.setMaxWidth(Integer.MAX_VALUE);
        col.setFillWidth(true);
        col.setPadding(new Insets(10));
        col.setAlignment(Pos.CENTER);
        col.setSpacing(10);

        return col;
    }

    public VBox createListContainer(boolean useDarkMode) {
        VBox listContainer = new VBox(5);
        listContainer.setPadding(new Insets(5));

        if (useDarkMode) {
            listContainer.setBackground(new Background(new BackgroundFill(Color.web("#696969"), null, null)));
        } else {
            listContainer.setBackground(new Background(new BackgroundFill(Color.web("#D5D5D5"), null, null)));
        }

        return listContainer;
    }

}