package vn.edu.haui.scheduler.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import vn.edu.haui.scheduler.ui.controller.handler.*;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;

public class HomeController
{

	@FXML
	private Button loginButton;

	@FXML
	private Button registerButton;

	@FXML
	private Button logoutButton;

	@FXML
	private Label welcomeLabel;

	@FXML
	private VBox centerContainer;

	private ScreenManager screenManager;

	private HomeAuthHandler authHandler;

	private DanhSachLopPaneHandler danhSachHandler;

	private ImportPaneHandler importHandler;

	private ExportPaneHandler exportHandler;

	private SinhThoiKhoaBieuPaneHandler sinhTKBHandler;

	public void init(ScreenManager screenManager)
	{
		this.screenManager = screenManager;

		authHandler = new HomeAuthHandler(
				screenManager,
				loginButton,
				registerButton,
				logoutButton,
				welcomeLabel);

		danhSachHandler = new DanhSachLopPaneHandler(screenManager, centerContainer);
		importHandler = new ImportPaneHandler(screenManager, centerContainer);
		exportHandler = new ExportPaneHandler(screenManager, centerContainer);

		sinhTKBHandler = new SinhThoiKhoaBieuPaneHandler(screenManager, centerContainer);

		authHandler.updateView();
	}

	@FXML
	private void onLogin()
	{
		screenManager.showAuth();
	}

	@FXML
	private void onRegister()
	{
		screenManager.showAuth();
	}

	@FXML
	private void onLogout()
	{
		authHandler.logout();
	}

	@FXML
	private void onShowDanhSachLopPane()
	{
		danhSachHandler.showDanhSach();
	}

	@FXML
	private void onShowImportPane()
	{
		importHandler.showImportPane();
	}

	@FXML
	private void onShowOptimizerPane()
	{
		sinhTKBHandler.showOptimizerPane();
	}
}
