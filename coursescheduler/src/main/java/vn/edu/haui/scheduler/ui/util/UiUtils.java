package vn.edu.haui.scheduler.ui.util;

import javafx.scene.control.Alert;

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
}
