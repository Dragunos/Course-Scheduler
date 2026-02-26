package vn.edu.haui.scheduler.ui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public class UiUtils
{
	private UiUtils()
	{
		throw new UnsupportedOperationException("Utility class");
	}

	public static void showAlert(String title, String message, Alert.AlertType type)
	{
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void showException(String title, Exception ex)
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(ex.getClass().getSimpleName());
		alert.setContentText(ex.getMessage());

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		TextArea textArea = new TextArea(sw.toString());
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expandableContent = new GridPane();
		expandableContent.setMaxWidth(Double.MAX_VALUE);
		expandableContent.add(textArea, 0, 0);

		alert.getDialogPane().setExpandableContent(expandableContent);

		alert.showAndWait();
	}
}