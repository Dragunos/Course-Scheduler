package vn.edu.haui.scheduler.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.application.service.AuthSession;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.viewmodel.AuthViewModel;

public class RegisterController
{

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private PasswordField confirmField;

	@FXML
	private Button registerButton;

	@FXML
	private Label messageLabel;

	private AuthViewModel viewModel;

	private ScreenManager screenManager;

	public void init(AuthUseCase authUseCase, AuthSession session, ScreenManager screenManager)
	{
		this.viewModel = new AuthViewModel(authUseCase, session);
		this.screenManager = screenManager;
		bindFields();
	}

	private void bindFields()
	{
		usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
		passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
		confirmField.textProperty().bindBidirectional(viewModel.confirmProperty());
		messageLabel.textProperty().bind(viewModel.messageProperty());
		registerButton.disableProperty().bind(viewModel.busyProperty());
	}

	@FXML
	private void onRegisterClicked()
	{
		try {
			viewModel.register();
			screenManager.showLogin();
		}
		catch(Exception e) {
			viewModel.setMessage("Đăng ký thất bại");
		}
	}

	@FXML
	private void onGoLogin()
	{
		screenManager.showLogin();
	}
}
