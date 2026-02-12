package vn.edu.haui.scheduler.ui.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.in.QuanLyDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.ui.controller.*;

public class ScreenManager
{
	private final Stage stage;

	private final AuthUseCase authUseCase;

	private ImportDanhSachLopUseCase importDanhSachLopUseCase;

	private QuanLyDanhSachLopUseCase quanLyDanhSachLopUseCase;

	private Scene scene;

	private NguoiDungDto currentUser;

	public ScreenManager(Stage stage, AuthUseCase authUseCase)
	{
		this.stage = stage;
		this.authUseCase = authUseCase;
	}

	public void setImportDanhSachLopUseCase(ImportDanhSachLopUseCase uc)
	{
		this.importDanhSachLopUseCase = uc;
	}

	public ImportDanhSachLopUseCase getImportDanhSachLopUseCase()
	{
		return importDanhSachLopUseCase;
	}

	public void setQuanLyDanhSachLopUseCase(QuanLyDanhSachLopUseCase uc)
	{
		this.quanLyDanhSachLopUseCase = uc;
	}

	public QuanLyDanhSachLopUseCase getQuanLyDanhSachLopUseCase()
	{
		return quanLyDanhSachLopUseCase;
	}

	public void init()
	{
		showHome();
	}

	public boolean isAuthenticated()
	{
		return currentUser != null;
	}

	public NguoiDungDto getCurrentUser()
	{
		return currentUser;
	}

	public void setCurrentUser(NguoiDungDto user)
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

	public void showAuth()
	{
		if(isAuthenticated()) {
			showHome();
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/fxml/auth.fxml"));
			Parent root = loader.load();

			AuthController controller = loader.getController();
			controller.init(authUseCase, this);

			stage.setTitle("Authentication");
			setRoot(root);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
