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
	private Button adminListButton;

	@FXML
	private VBox centerContainer;

	private ScreenManager screenManager;

	private AuthPaneHandler authHandler;

	private ManageDanhSachLopPaneHandler danhSachHandler;

	private ImportPaneHandler importHandler;

	private GenerateThoiKhoaBieuPaneHandler sinhTKBHandler;

	private ManageThoiKhoaBieuPaneHandler quanLyThoiKhoaBieuHandler;

	private AdminDanhSachLopPaneHandler quanTriDanhSachHandler;

	public void init(ScreenManager screenManager)
	{
		this.screenManager = screenManager;

		authHandler = new AuthPaneHandler(
				screenManager,
				loginButton,
				registerButton,
				logoutButton,
				adminListButton,
				welcomeLabel);

		adminListButton.setVisible(false);

		danhSachHandler = new ManageDanhSachLopPaneHandler(screenManager, centerContainer);
		importHandler = new ImportPaneHandler(screenManager, centerContainer);

		sinhTKBHandler = new GenerateThoiKhoaBieuPaneHandler(screenManager, centerContainer);
		quanLyThoiKhoaBieuHandler = new ManageThoiKhoaBieuPaneHandler(screenManager, centerContainer);

		quanTriDanhSachHandler = new AdminDanhSachLopPaneHandler(screenManager, centerContainer);

		authHandler.updateView();
	}

	@FXML
	private void onLogin()
	{
	    screenManager.showLogin();
	}

	@FXML
	private void onRegister()
	{
	    screenManager.showRegister();
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

	@FXML
	private void onShowThoiKhoaBieuPane()
	{
		quanLyThoiKhoaBieuHandler.showThoiKhoaBieu();
	}

	@FXML
	private void onShowAdminDanhSachPane()
	{
		quanTriDanhSachHandler.showDanhSachCongKhai();
	}
}
