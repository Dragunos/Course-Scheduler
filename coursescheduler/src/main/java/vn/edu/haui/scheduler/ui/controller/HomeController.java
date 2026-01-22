package vn.edu.haui.scheduler.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import vn.edu.haui.scheduler.application.service.AuthSession;
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

	private AuthSession session;

	private ScreenManager screenManager;

	public void init(AuthSession session, ScreenManager screenManager)
	{
		this.session = session;
		this.screenManager = screenManager;
		updateView();
	}

	private void updateView()
	{
		boolean loggedIn = session.isAuthenticated();

		loginButton.setVisible(!loggedIn);
		registerButton.setVisible(!loggedIn);
		logoutButton.setVisible(loggedIn);

		if(loggedIn) {
			welcomeLabel.setText(
					"Xin chào, " + session.getCurrentUser().getTenDangNhap());
		}
		else {
			welcomeLabel.setText("Chào mừng bạn");
		}
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
		session.logout();
		screenManager.showHome();
	}
}
