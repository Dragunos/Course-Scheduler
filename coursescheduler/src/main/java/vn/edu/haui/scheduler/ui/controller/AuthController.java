package vn.edu.haui.scheduler.ui.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.viewmodel.AuthViewModel;

public class AuthController
{
	@FXML
	private VBox loginPane;

	@FXML
	private VBox registerPane;

	@FXML
	private TextField loginUsernameField;

	@FXML
	private PasswordField loginPasswordField;

	@FXML
	private Button loginButton;

	@FXML
	private Label loginMessageLabel;

	@FXML
	private TextField registerUsernameField;

	@FXML
	private PasswordField registerPasswordField;

	@FXML
	private PasswordField confirmPasswordField;

	@FXML
	private Button registerButton;

	@FXML
	private Label registerMessageLabel;

	private AuthViewModel viewModel;

	private ScreenManager screenManager;

	public void init(AuthUseCase authUseCase, ScreenManager screenManager)
	{
		this.screenManager = screenManager;
		this.viewModel = new AuthViewModel(authUseCase);

		bindLogin();
		bindRegister();
		bindStatus();
		bindErrorShake();
	}

	private void bindLogin()
	{
		loginUsernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
		loginPasswordField.textProperty().bindBidirectional(viewModel.passwordProperty());
		loginMessageLabel.textProperty().bind(viewModel.messageProperty());
		loginButton.disableProperty().bind(viewModel.busyProperty());
	}

	private void bindRegister()
	{
		registerUsernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
		registerPasswordField.textProperty().bindBidirectional(viewModel.passwordProperty());
		confirmPasswordField.textProperty().bindBidirectional(viewModel.confirmProperty());
		registerMessageLabel.textProperty().bind(viewModel.messageProperty());
		registerButton.disableProperty().bind(viewModel.busyProperty());
	}

	private void bindStatus()
	{
		viewModel.statusProperty().addListener((obs, old, status) -> {
			loginMessageLabel.getStyleClass().removeAll("message-error", "message-success");
			registerMessageLabel.getStyleClass().removeAll("message-error", "message-success");

			if(status == AuthViewModel.Status.ERROR) {
				loginMessageLabel.getStyleClass().add("message-error");
				registerMessageLabel.getStyleClass().add("message-error");
			}
			else if(status == AuthViewModel.Status.SUCCESS) {
				loginMessageLabel.getStyleClass().add("message-success");
				registerMessageLabel.getStyleClass().add("message-success");
			}
		});
	}

	private void bindErrorShake()
	{
		viewModel.errorCountProperty().addListener((obs, old, count) -> {
			if(count.intValue() >= 3 && !viewModel.busyProperty().get()) {
				shake(loginPane.isVisible() ? loginPane : registerPane);
			}
		});
	}

	@FXML
	private void onLoginClicked()
	{
		try {
			NguoiDungDto user = viewModel.login();
			if(user != null) {
				// Chỉ điều hướng khi đăng nhập thành công
				screenManager.setCurrentUser(user);
				screenManager.showHome();
			}
			// nếu user == null thì viewModel đã đặt message/status -> label sẽ hiển thị
		}
		catch(Exception e) {
			e.printStackTrace();
			// Hiển thị message hệ thống để user biết có lỗi không mong muốn
			viewModel.setTechnicalError("Lỗi hệ thống. Vui lòng thử lại.");
		}
	}

	@FXML
	private void onRegisterClicked()
	{
		try {
			NguoiDungDto user = viewModel.register();
			if(user != null) {
				// Đăng ký thành công -> chuyển về màn hình đăng nhập
				// (nếu muốn tự động điền username, có thể set viewModel.usernameProperty() trong showLoginPane)
				showLogin();
			}
			// nếu user == null thì viewModel đã đặt message/status -> label sẽ hiển thị
		}
		catch(Exception e) {
			e.printStackTrace();
			viewModel.setTechnicalError("Lỗi hệ thống. Vui lòng thử lại.");
		}
	}

	@FXML
	private void showRegister()
	{
		if(!canAnimate()) return;
		viewModel.clear();
		switchPane(loginPane, registerPane, true);
	}

	public void showRegisterPane()
	{
		registerPane.setVisible(true);
		registerPane.setOpacity(1);

		loginPane.setVisible(false);
		loginPane.setOpacity(0);
	}

	@FXML
	private void showLogin()
	{
		if(!canAnimate()) return;
		viewModel.clear();
		switchPane(registerPane, loginPane, false);
	}

	public void showLoginPane()
	{
		loginPane.setVisible(true);
		loginPane.setOpacity(1);

		registerPane.setVisible(false);
		registerPane.setOpacity(0);
	}

	@FXML
	private void onBackToHome()
	{
		if(viewModel.busyProperty().get()) {
			return;
		}
		viewModel.clear();
		screenManager.showHome();
	}

	private boolean canAnimate()
	{
		return !viewModel.busyProperty().get();
	}

	private void switchPane(VBox from, VBox to, boolean toRight)
	{
		from.setTranslateX(0);
		to.setTranslateX(0);

		double offset = toRight ? 40 : -40;

		FadeTransition fadeOut = new FadeTransition(Duration.millis(180), from);
		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);

		TranslateTransition slideOut = new TranslateTransition(Duration.millis(180), from);
		slideOut.setByX(-offset);

		FadeTransition fadeIn = new FadeTransition(Duration.millis(180), to);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);

		TranslateTransition slideIn = new TranslateTransition(Duration.millis(180), to);
		slideIn.setFromX(offset);
		slideIn.setToX(0);

		ParallelTransition out = new ParallelTransition(fadeOut, slideOut);
		ParallelTransition in = new ParallelTransition(fadeIn, slideIn);

		out.setOnFinished(e -> {
			from.setVisible(false);
			to.setVisible(true);
			in.play();
		});

		out.play();
	}

	private void shake(VBox pane)
	{
		pane.setTranslateX(0);

		TranslateTransition t1 = new TranslateTransition(Duration.millis(50), pane);
		t1.setByX(-10);

		TranslateTransition t2 = new TranslateTransition(Duration.millis(50), pane);
		t2.setByX(20);

		TranslateTransition t3 = new TranslateTransition(Duration.millis(50), pane);
		t3.setByX(-20);

		TranslateTransition t4 = new TranslateTransition(Duration.millis(50), pane);
		t4.setByX(10);

		new SequentialTransition(t1, t2, t3, t4).play();
	}
}
