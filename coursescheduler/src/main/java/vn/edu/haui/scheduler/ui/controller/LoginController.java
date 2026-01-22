package vn.edu.haui.scheduler.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import vn.edu.haui.scheduler.application.service.AuthAppService;
import vn.edu.haui.scheduler.application.service.AuthSession;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.viewmodel.AuthViewModel;

public class LoginController
{
	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button loginButton;

	@FXML
	private Label messageLabel;

	private AuthViewModel viewModel;
	private ScreenManager screenManager;

	public void init(AuthAppService authService, AuthSession session, ScreenManager screenManager)
	{
		this.viewModel = new AuthViewModel(authService, session);
		this.screenManager = screenManager;
		bindFields();
	}

	private void bindFields()
	{
		usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
		passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
		messageLabel.textProperty().bind(viewModel.messageProperty());
		loginButton.disableProperty().bind(viewModel.busyProperty());
	}

	@FXML
	private void onLoginClicked()
	{
		try {
			viewModel.login();
			if(viewModel.isAuthenticated()) {
				screenManager.showHome();
			}
		}
		catch(Exception e) {
			viewModel.setMessage("Đăng nhập thất bại");
		}
	}

	@FXML
	private void onGoRegister()
	{
		screenManager.showRegister();
	}
}