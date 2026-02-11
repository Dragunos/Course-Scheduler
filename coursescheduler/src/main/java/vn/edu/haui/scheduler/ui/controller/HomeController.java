package vn.edu.haui.scheduler.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;

import java.io.File;

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
	private VBox centerContainer;

	@FXML
	private Button importMenuButton;

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

	@FXML
	private void onShowImportPane()
	{
		centerContainer.getChildren().clear();

		Label title = new Label("Nhập danh sách lớp học phần");
		title.getStyleClass().add("home-title");

		Label instruction = new Label(
				"Chọn tệp Excel theo định dạng: ma_hp, ten_hp, so_tin_chi, ma_lop, ten_giang_vien, hinh_thuc_day, dia_diem, thu, tiet_bat, tiet_ket");
		instruction.setWrapText(true);

		TextField tenDanhSachField = new TextField();
		tenDanhSachField.setPromptText("Tên danh sách (ví dụ: DS HK1 2026)");

		TextField filePathField = new TextField();
		filePathField.setEditable(false);
		filePathField.setPromptText("Chưa chọn tệp");

		Button chooseButton = new Button("Chọn tệp...");
		chooseButton.setOnAction(e -> {
			Window w = centerContainer.getScene().getWindow();
			FileChooser chooser = new FileChooser();
			chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files", "*.xlsx", "*.xls"));
			File selected = chooser.showOpenDialog(w);
			if(selected != null) {
				filePathField.setText(selected.getAbsolutePath());
			}
		});

		Button importButton = new Button("Nhập");
		Label statusLabel = new Label();
		importButton.setOnAction(e -> {
			String path = filePathField.getText();
			String tenDanhSach = tenDanhSachField.getText();
			if(path == null || path.trim().isEmpty()) {
				showAlert("Chưa chọn tệp", "Vui lòng chọn tệp Excel để nhập.", Alert.AlertType.WARNING);
				return;
			}
			if(tenDanhSach == null || tenDanhSach.trim().isEmpty()) {
				showAlert("Tên danh sách trống", "Vui lòng nhập tên danh sách.", Alert.AlertType.WARNING);
				return;
			}
			NguoiDungDto user = screenManager.getCurrentUser();
			if(user == null) {
				showAlert("Chưa đăng nhập", "Bạn cần đăng nhập để thực hiện chức năng này.", Alert.AlertType.WARNING);
				return;
			}

			ImportDanhSachLopUseCase importUseCase = screenManager.getImportDanhSachLopUseCase();

			if(importUseCase == null) {
				showAlert("Lỗi cấu hình",
						"Tính năng nhập chưa được cấu hình trong hệ thống.",
						Alert.AlertType.ERROR);
				return;
			}

			statusLabel.setText("Đang nhập...");

			final ImportDanhSachLopUseCase finalUseCase = importUseCase;
			new Thread(() -> {
				try {
					ImportDanhSachLopRequestDto req = new ImportDanhSachLopRequestDto(
							path,
							tenDanhSach,
							(int) user.getId(),
							null,
							false);

					finalUseCase.importDanhSach(req);

					Platform.runLater(() -> {
						statusLabel.setText("Nhập thành công.");
						showAlert("Thành công",
								"Nhập danh sách hoàn tất.",
								Alert.AlertType.INFORMATION);
					});
				}
				catch(Exception ex) {
					Platform.runLater(() -> {
						statusLabel.setText("Lỗi: " + ex.getMessage());
						showAlert("Lỗi khi nhập",
								ex.getMessage(),
								Alert.AlertType.ERROR);
					});
				}
			}).start();
		});

		VBox box = new VBox(8, title, instruction, tenDanhSachField, filePathField, chooseButton, importButton,
				statusLabel);
		box.setMaxWidth(800);
		centerContainer.getChildren().add(box);
	}

	private void showAlert(String title, String message, Alert.AlertType type)
	{
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
