package vn.edu.haui.scheduler.ui.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.ui.controller.HomeController;
import vn.edu.haui.scheduler.ui.controller.LoginController;
import vn.edu.haui.scheduler.ui.controller.RegisterController;

public class ScreenManager
{
	private final Stage stage;

	private final AuthUseCase authUseCase;

	private Scene scene;

	private NguoiDung currentUser;

	public ScreenManager(Stage stage, AuthUseCase authUseCase)
	{
		this.stage = stage;
		this.authUseCase = authUseCase;
	}

	public void init()
	{
		showLogin();
	}

	public boolean isAuthenticated()
	{
		return currentUser != null;
	}

	public NguoiDung getCurrentUser()
	{
		return currentUser;
	}

	public void setCurrentUser(NguoiDung user)
	{
		this.currentUser = user;
	}

	public void clearCurrentUser()
	{
		this.currentUser = null;
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
			controller.init(authUseCase, this);

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
			controller.init(authUseCase, this);

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
			controller.init(this);

			stage.setTitle("Trang chủ");
			setRoot(root);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
