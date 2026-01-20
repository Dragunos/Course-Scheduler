package vn.edu.haui.scheduler.ui.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import vn.edu.haui.scheduler.application.auth.AuthService;
import vn.edu.haui.scheduler.application.auth.AuthSession;
import vn.edu.haui.scheduler.ui.controller.HomeController;
import vn.edu.haui.scheduler.ui.controller.LoginController;
import vn.edu.haui.scheduler.ui.controller.RegisterController;

public class ScreenManager
{
	private final Stage stage;

	private final AuthService authService;

	private final AuthSession session;

	private Scene scene;

	public ScreenManager(Stage stage, AuthService authService, AuthSession session)
	{
		this.stage = stage;
		this.authService = authService;
		this.session = session;
	}

	public void init()
	{
		showHome();
	}

	private void setRoot(Parent root)
	{
		if(scene == null) {
			scene = new Scene(root);
			stage.setScene(scene);
		}
		else {
			scene.setRoot(root);
		}
	}

	public void showLogin()
	{
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/fxml/login.fxml"));
			Parent root = loader.load();

			LoginController controller = loader.getController();
			controller.init(authService, session, this);

			stage.setTitle("Đăng nhập");
			setRoot(root);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void showRegister()
	{
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/fxml/register.fxml"));
			Parent root = loader.load();

			RegisterController controller = loader.getController();
			controller.init(authService, session, this);

			stage.setTitle("Đăng ký");
			setRoot(root);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void showHome()
	{
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/fxml/home.fxml"));
			Parent root = loader.load();

			HomeController controller = loader.getController();
			controller.init(session, this);

			stage.setTitle("Trang chủ");
			setRoot(root);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}