package org.dotsandboxestcp.ui;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class UIUtils {
    public static Circle createDot(int radius) {
        Circle c = new Circle(radius);
        c.setFill(Color.BLACK);
        return c;
    }

    public static Line createLine(double width, double thickness) {
        Line line = new Line(0, 0, width, 0);
        line.setStroke(Color.LIGHTGRAY);
        line.setStrokeWidth(thickness);
        line.setCursor(Cursor.HAND);
        return line;
    }

    public static Line createVerticalLine(double height, double thickness) {
        Line line = new Line(0, 0, 0, height);
        line.setStroke(Color.LIGHTGRAY);
        line.setStrokeWidth(thickness);
        line.setCursor(Cursor.HAND);
        return line;
    }

    public static void addColumn(GridPane grid, int size) {
        ColumnConstraints cc = new ColumnConstraints(size);
        cc.setHalignment(HPos.CENTER);
        grid.getColumnConstraints().add(cc);
    }

    public static void addRow(GridPane grid, int size) {
        RowConstraints rc = new RowConstraints(size);
        rc.setValignment(VPos.CENTER);
        grid.getRowConstraints().add(rc);
    }
}
