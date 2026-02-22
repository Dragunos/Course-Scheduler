package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;

public class AuthPaneHandler
{
	private static final Long ADMIN_ROLE_ID = 1L;

	private final ScreenManager screenManager;

	private final Button loginButton;

	private final Button registerButton;

	private final Button logoutButton;

	private final Button adminListButton;

	private final Label welcomeLabel;

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
		if(screenManager == null) {
			setLoggedOutState();
			return;
		}

		boolean loggedIn = screenManager.isAuthenticated();
		NguoiDungDto user = screenManager.getCurrentUser();

		updateAuthButtons(loggedIn);
		updateAdminButton(user);
		updateWelcomeLabel(loggedIn, user);
	}

	private void updateAuthButtons(boolean loggedIn)
	{
		loginButton.setVisible(!loggedIn);
		registerButton.setVisible(!loggedIn);
		logoutButton.setVisible(loggedIn);
	}

	private void updateAdminButton(NguoiDungDto user)
	{
		boolean isAdmin = false;

		if(user != null && user.getRoleId() != null) {
			isAdmin = ADMIN_ROLE_ID.equals(user.getRoleId());
		}

		adminListButton.setVisible(isAdmin);
	}

	private void updateWelcomeLabel(boolean loggedIn, NguoiDungDto user)
	{
		if(!loggedIn || user == null) {
			welcomeLabel.setText("Chào mừng bạn");
			return;
		}

		welcomeLabel.setText("Xin chào, " + user.getTenDangNhap());
	}

	private void setLoggedOutState()
	{
		loginButton.setVisible(true);
		registerButton.setVisible(true);
		logoutButton.setVisible(false);
		adminListButton.setVisible(false);
		welcomeLabel.setText("Chào mừng bạn");
	}

	public void logout()
	{
		if(screenManager == null) return;

		screenManager.clearCurrentUser();
		screenManager.showHome();
		updateView();
	}
}