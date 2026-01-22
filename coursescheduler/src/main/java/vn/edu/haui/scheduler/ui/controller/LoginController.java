package vn.edu.haui.scheduler.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
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

	public void init(AuthUseCase authService, ScreenManager screenManager)
	{
		this.viewModel = new AuthViewModel(authService);
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
			NguoiDung user = viewModel.login();
			screenManager.setCurrentUser(user);
			screenManager.showHome();
		}
		catch(Exception e) {
			viewModel.setMessage("Tên đăng nhập hoặc mật khẩu không đúng");
		}
	}

	@FXML
	private void onGoRegister()
	{
		screenManager.showRegister();
	}
}
