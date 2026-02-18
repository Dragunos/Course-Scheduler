package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;

public class AuthPaneHandler
{

	private final ScreenManager screenManager;

	private final Button loginButton;

	private final Button registerButton;

	private final Button logoutButton;

	private final Label welcomeLabel;

	private final Button adminListButton;

	public AuthPaneHandler(
			ScreenManager screenManager,
			Button loginButton,
			Button registerButton,
			Button logoutButton,
			Button adminListButton,
			Label welcomeLabel)
	{
		this.screenManager = screenManager;
		this.loginButton = loginButton;
		this.registerButton = registerButton;
		this.logoutButton = logoutButton;
		this.adminListButton = adminListButton;
		this.welcomeLabel = welcomeLabel;
	}

	public void updateView()
	{
		boolean loggedIn = screenManager != null &&
				screenManager.isAuthenticated();

		loginButton.setVisible(!loggedIn);
		registerButton.setVisible(!loggedIn);
		logoutButton.setVisible(loggedIn);

		boolean userIsAdmin = false;

		if(loggedIn) {
			NguoiDungDto user = screenManager.getCurrentUser();

			if(user != null) {
				userIsAdmin = "ADMIN".equalsIgnoreCase(user.getVaiTro());
			}
		}

		adminListButton.setVisible(userIsAdmin);

		if(loggedIn) {
			NguoiDungDto user = screenManager.getCurrentUser();

			if(user != null) {
				welcomeLabel.setText(
						"Xin chào, " +
								user.getTenDangNhap());
			}
			else {
				welcomeLabel.setText("Xin chào");
			}
		}
		else {
			welcomeLabel.setText("Chào mừng bạn");
		}
	}

	public void logout()
	{
		screenManager.clearCurrentUser();
		screenManager.showHome();
	}
}
