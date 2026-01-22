package vn.edu.haui.scheduler.ui.fx.config;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

public class FxConfig
{
	public static void apply(Stage stage)
	{
		stage.setTitle("Course Scheduler");

		stage.setMinWidth(900);
		stage.setMinHeight(600);

		Locale locale = new Locale.Builder()
				.setLanguage("vi")
				.setRegion("VN")
				.build();

		Locale.setDefault(locale);

	}

	public static void apply(Scene scene)
	{
		scene.getStylesheets().add(
				FxConfig.class.getResource("/css/style.css").toExternalForm());
	}
}
