package vn.edu.haui.scheduler.ui.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vn.edu.haui.scheduler.application.port.in.*;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.ui.controller.*;

public class ScreenManager
{
	private final Stage stage;

	private final AuthUseCase authUseCase;

	private ImportDanhSachLopUseCase importDanhSachLopUseCase;

	private ManageDanhSachLopUseCase manageDanhSachLopUseCase;

	private ExportDanhSachLopUseCase exportDanhSachLopUseCase;

	private GenerateThoiKhoaBieuUseCase generateThoiKhoaBieuUseCase;

	private ManageThoiKhoaBieuUseCase manageThoiKhoaBieuUseCase;

	private ExportThoiKhoaBieuUseCase exportThoiKhoaBieuUseCase;

	private AdminDanhSachLopUseCase adminDanhSachLopUseCase;

	private Scene scene;

	private NguoiDungDto currentUser;

	public ScreenManager(Stage stage, AuthUseCase authUseCase)
	{
		this.stage = stage;
		this.authUseCase = authUseCase;
	}

	// ===== IMPORT =====

	public void setImportDanhSachLopUseCase(ImportDanhSachLopUseCase uc)
	{
		this.importDanhSachLopUseCase = uc;
	}

	public ImportDanhSachLopUseCase getImportDanhSachLopUseCase()
	{
		return importDanhSachLopUseCase;
	}

	public void setQuanLyDanhSachLopUseCase(ManageDanhSachLopUseCase uc)
	{
		this.manageDanhSachLopUseCase = uc;
	}

	public ManageDanhSachLopUseCase getQuanLyDanhSachLopUseCase()
	{
		return manageDanhSachLopUseCase;
	}

	public void setXuatDanhSachLopUseCase(ExportDanhSachLopUseCase uc)
	{
		this.exportDanhSachLopUseCase = uc;
	}

	public ExportDanhSachLopUseCase getXuatDanhSachLopUseCase()
	{
		return this.exportDanhSachLopUseCase;
	}

	public void setSinhThoiKhoaBieuUseCase(GenerateThoiKhoaBieuUseCase uc)
	{
		this.generateThoiKhoaBieuUseCase = uc;
	}

	public GenerateThoiKhoaBieuUseCase getSinhThoiKhoaBieuUseCase()
	{
		return generateThoiKhoaBieuUseCase;
	}

	public void setQuanLyThoiKhoaBieuUseCase(ManageThoiKhoaBieuUseCase uc)
	{
		this.manageThoiKhoaBieuUseCase = uc;
	}

	public ManageThoiKhoaBieuUseCase getQuanLyThoiKhoaBieuUseCase()
	{
		return manageThoiKhoaBieuUseCase;
	}

	public void setXuatThoiKhoaBieuUseCase(ExportThoiKhoaBieuUseCase uc)
	{
		this.exportThoiKhoaBieuUseCase = uc;
	}

	public ExportThoiKhoaBieuUseCase getXuatThoiKhoaBieuUseCase()
	{
		return exportThoiKhoaBieuUseCase;
	}

	public void setQuanTriDanhSachLopUseCase(AdminDanhSachLopUseCase uc)
	{
		this.adminDanhSachLopUseCase = uc;
	}

	public AdminDanhSachLopUseCase getQuanTriDanhSachLopUseCase()
	{
		return adminDanhSachLopUseCase;
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
			Parent root = loader.load();

			HomeController controller = loader.getController();
			controller.init(this);

			stage.setTitle("Trang chủ");
			setRoot(root);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void showLogin()
	{
		if(isAuthenticated()) {
			showHome();
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth.fxml"));
			Parent root = loader.load();

			AuthController controller = loader.getController();
			controller.init(authUseCase, this);
			controller.showLoginPane();

			stage.setTitle("Login");
			setRoot(root);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void showRegister()
	{
		if(isAuthenticated()) {
			showHome();
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth.fxml"));
			Parent root = loader.load();

			AuthController controller = loader.getController();
			controller.init(authUseCase, this);
			controller.showRegisterPane();

			stage.setTitle("Register");
			setRoot(root);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}