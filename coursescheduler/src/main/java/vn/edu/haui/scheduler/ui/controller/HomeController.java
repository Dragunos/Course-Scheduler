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

	private HomeAuthHandler authHandler;

	private DanhSachLopPaneHandler danhSachHandler;

	private ImportPaneHandler importHandler;

	private SinhThoiKhoaBieuPaneHandler sinhTKBHandler;

	private QuanLyThoiKhoaBieuPaneHandler quanLyThoiKhoaBieuHandler;

	private QuanTriDanhSachLopPaneHandler quanTriDanhSachHandler;

	public void init(ScreenManager screenManager)
	{
		this.screenManager = screenManager;

		authHandler = new HomeAuthHandler(
				screenManager,
				loginButton,
				registerButton,
				logoutButton,
				adminListButton,
				welcomeLabel);

		adminListButton.setVisible(false);

		danhSachHandler = new DanhSachLopPaneHandler(screenManager, centerContainer);
		importHandler = new ImportPaneHandler(screenManager, centerContainer);

		sinhTKBHandler = new SinhThoiKhoaBieuPaneHandler(screenManager, centerContainer);
		quanLyThoiKhoaBieuHandler = new QuanLyThoiKhoaBieuPaneHandler(screenManager, centerContainer);

		quanTriDanhSachHandler = new QuanTriDanhSachLopPaneHandler(screenManager, centerContainer);

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
