package vn.edu.haui.scheduler.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
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

	private ScreenManager screenManager;

	public void init(ScreenManager screenManager)
	{
		this.screenManager = screenManager;
		updateView();
	}

	private void updateView()
	{
		boolean loggedIn = screenManager.isAuthenticated();

		loginButton.setVisible(!loggedIn);
		registerButton.setVisible(!loggedIn);
		logoutButton.setVisible(loggedIn);

		if(loggedIn) {
			NguoiDungDto user = screenManager.getCurrentUser();
			welcomeLabel.setText("Xin chào, " + user.getTenDangNhap());
		}
		else {
			welcomeLabel.setText("Chào mừng bạn");
		}
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
		screenManager.clearCurrentUser();
		screenManager.showHome();
	}
}
