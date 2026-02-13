package vn.edu.haui.scheduler.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.in.QuanLyDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.in.XuatDanhSachLopUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;

import java.io.File;
import java.util.List;
import java.util.Optional;

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
		boolean loggedIn = screenManager != null && screenManager.isAuthenticated();

		loginButton.setVisible(!loggedIn);
		registerButton.setVisible(!loggedIn);
		logoutButton.setVisible(loggedIn);

		if(loggedIn) {
			NguoiDungDto user = screenManager.getCurrentUser();
			if(user != null) {
				welcomeLabel.setText("Xin chào, " + user.getTenDangNhap());
			}
			else {
				welcomeLabel.setText("Xin chào");
			}
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
	private void onShowDanhSachLopPane()
	{
		if(screenManager == null || !screenManager.isAuthenticated()) {
			showAlert("Chưa đăng nhập", "Bạn cần đăng nhập.", Alert.AlertType.WARNING);
			return;
		}

		QuanLyDanhSachLopUseCase useCase = screenManager.getQuanLyDanhSachLopUseCase();
		if(useCase == null) {
			showAlert("Lỗi cấu hình", "Tính năng quản lý danh sách chưa được cấu hình.", Alert.AlertType.ERROR);
			return;
		}

		centerContainer.getChildren().clear();

		Label title = new Label("Danh sách lớp học phần");
		title.getStyleClass().add("home-title");

		VBox listBox = new VBox(10);

		try {
			NguoiDungDto user = screenManager.getCurrentUser();
			if(user == null) {
				showAlert("Chưa đăng nhập", "Bạn cần đăng nhập.", Alert.AlertType.WARNING);
				return;
			}
			Long userId = user.getId();

			List<DanhSachLopDto> list = useCase.listDanhSachChoNguoiDung(userId);

			if(list == null || list.isEmpty()) {
				listBox.getChildren().add(new Label("Không có danh sách nào."));
			}
			else {
				for(DanhSachLopDto dto : list) {
					listBox.getChildren().add(createDanhSachCard(dto));
				}
			}
		}
		catch(PersistenceException e) {
			showAlert("Lỗi hệ thống", e.getMessage(), Alert.AlertType.ERROR);
			return;
		}
		catch(Exception e) {
			showAlert("Lỗi", e.getMessage(), Alert.AlertType.ERROR);
			return;
		}

		centerContainer.getChildren().addAll(title, listBox);
	}

	private VBox createDanhSachCard(DanhSachLopDto dto)
	{
		String ten = dto != null && dto.getTenDanhSach() != null ? dto.getTenDanhSach() : "<không tên>";
		String hocKy = dto != null && dto.getHocKyId() != null ? String.valueOf(dto.getHocKyId()) : "N/A";

		Label tenLabel = new Label("Tên: " + ten);
		Label hocKyLabel = new Label("Học kỳ: " + hocKy);

		Button viewBtn = new Button("Xem chi tiết");
		Button editBtn = new Button("Chỉnh sửa");
		Button deleteBtn = new Button("Xóa");
		Button exportBtn = new Button("Xuất danh sách");

		viewBtn.setOnAction(e -> onViewChiTiet(dto));
		editBtn.setOnAction(e -> onEditDanhSach(dto));
		deleteBtn.setOnAction(e -> onDeleteDanhSach(dto));
		exportBtn.setOnAction(e -> onExportDanhSach(dto));

		HBox actions = new HBox(10, viewBtn, editBtn, deleteBtn, exportBtn);

		VBox card = new VBox(5, tenLabel, hocKyLabel, actions);
		card.setStyle("-fx-padding:10; -fx-border-color:#ccc;");

		return card;
	}

	private void onViewChiTiet(DanhSachLopDto dto)
	{
		if(dto == null) return;
		if(screenManager == null || !screenManager.isAuthenticated()) {
			showAlert("Chưa đăng nhập", "Bạn cần đăng nhập.", Alert.AlertType.WARNING);
			return;
		}

		QuanLyDanhSachLopUseCase useCase = screenManager.getQuanLyDanhSachLopUseCase();
		if(useCase == null) {
			showAlert("Lỗi cấu hình", "Tính năng quản lý danh sách chưa được cấu hình.", Alert.AlertType.ERROR);
			return;
		}

		centerContainer.getChildren().clear();

		String titleText = dto.getTenDanhSach() != null ? dto.getTenDanhSach() : "<không tên>";
		Label title = new Label("Chi tiết: " + titleText);
		title.getStyleClass().add("home-title");

		try {
			Long userId = screenManager.getCurrentUser().getId();

			DanhSachLopDto detail = useCase.getChiTietDanhSach(userId, dto.getId());

			VBox listBox = new VBox(8);

			if(detail == null || detail.getChiTiet() == null || detail.getChiTiet().isEmpty()) {
				listBox.getChildren().add(new Label("Không có lớp học phần."));
			}
			else {
				for(DanhSachLopChiTietDto ct : detail.getChiTiet()) {
					String ma = ct.getMaLop() != null ? ct.getMaLop() : "<ma>";
					String tenHp = ct.getTenHocPhan() != null ? ct.getTenHocPhan() : "<tên HP>";
					String gv = ct.getTenGiangVien() != null ? ct.getTenGiangVien() : "<giảng viên>";
					Label row = new Label(ma + " - " + tenHp + " - " + gv);
					listBox.getChildren().add(row);
				}
			}

			Button backBtn = new Button("Quay lại");
			backBtn.setOnAction(e -> onShowDanhSachLopPane());

			centerContainer.getChildren().addAll(title, listBox, backBtn);
		}
		catch(ValidationException ve) {
			showAlert("Không hợp lệ", ve.getMessage(), Alert.AlertType.WARNING);
		}
		catch(PersistenceException pe) {
			showAlert("Lỗi hệ thống", pe.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void onEditDanhSach(DanhSachLopDto dto)
	{
		if(dto == null) return;
		if(screenManager == null || !screenManager.isAuthenticated()) {
			showAlert("Chưa đăng nhập", "Bạn cần đăng nhập.", Alert.AlertType.WARNING);
			return;
		}

		centerContainer.getChildren().clear();

		Label title = new Label("Chỉnh sửa danh sách");
		title.getStyleClass().add("home-title");

		TextField tenField = new TextField(dto.getTenDanhSach() != null ? dto.getTenDanhSach() : "");

		Button saveBtn = new Button("Lưu");

		saveBtn.setOnAction(e -> {
			try {
				UpdateDanhSachLopRequestDto req = new UpdateDanhSachLopRequestDto(dto.getId(), tenField.getText(), null,
						null);

				Long userId = screenManager.getCurrentUser().getId();

				screenManager.getQuanLyDanhSachLopUseCase()
						.updateDanhSach(userId, req);

				showAlert("Thành công", "Đã cập nhật.", Alert.AlertType.INFORMATION);

				onShowDanhSachLopPane();
			}
			catch(ValidationException ve) {
				showAlert("Không hợp lệ", ve.getMessage(), Alert.AlertType.WARNING);
			}
			catch(PersistenceException pe) {
				showAlert("Lỗi hệ thống", pe.getMessage(), Alert.AlertType.ERROR);
			}
		});

		centerContainer.getChildren().addAll(title, new Label("Tên danh sách"), tenField, saveBtn);
	}

	private void onDeleteDanhSach(DanhSachLopDto dto)
	{
		if(dto == null) return;
		if(screenManager == null || !screenManager.isAuthenticated()) {
			showAlert("Chưa đăng nhập", "Bạn cần đăng nhập.", Alert.AlertType.WARNING);
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Xác nhận");
		confirm.setHeaderText(null);
		confirm.setContentText("Bạn có chắc muốn xóa?");

		Optional<ButtonType> result = confirm.showAndWait();

		if(result.isPresent() && result.get() == ButtonType.OK) {
			try {
				Long userId = screenManager.getCurrentUser().getId();

				screenManager.getQuanLyDanhSachLopUseCase()
						.deleteDanhSach(userId, dto.getId());

				showAlert("Thành công", "Đã xóa.", Alert.AlertType.INFORMATION);

				onShowDanhSachLopPane();
			}
			catch(ValidationException ve) {
				showAlert("Không hợp lệ", ve.getMessage(), Alert.AlertType.WARNING);
			}
			catch(PersistenceException pe) {
				showAlert("Lỗi hệ thống", pe.getMessage(), Alert.AlertType.ERROR);
			}
		}
	}

	@FXML
	private void onShowImportPane()
	{
		if(screenManager == null || !screenManager.isAuthenticated()) {
			showAlert("Chưa đăng nhập", "Bạn cần đăng nhập.", Alert.AlertType.WARNING);
			return;
		}

		centerContainer.getChildren().clear();

		Label title = new Label("Nhập danh sách lớp học phần");
		title.getStyleClass().add("home-title");

		TextField tenDanhSachField = new TextField();
		tenDanhSachField.setPromptText("Tên danh sách");

		TextField filePathField = new TextField();
		filePathField.setEditable(false);

		Button chooseButton = new Button("Chọn tệp");
		chooseButton.setOnAction(e -> {
			Window w = centerContainer.getScene().getWindow();
			FileChooser chooser = new FileChooser();
			File selected = chooser.showOpenDialog(w);
			if(selected != null) {
				filePathField.setText(selected.getAbsolutePath());
			}
		});

		Button importButton = new Button("Nhập");
		importButton.setOnAction(e -> {
			try {
				NguoiDungDto user = screenManager.getCurrentUser();
				if(user == null) {
					showAlert("Chưa đăng nhập", "Bạn cần đăng nhập.", Alert.AlertType.WARNING);
					return;
				}
				Long userId = user.getId();

				ImportDanhSachLopRequestDto req = new ImportDanhSachLopRequestDto(
						filePathField.getText(),
						tenDanhSachField.getText(),
						userId,
						null,
						false);

				ImportDanhSachLopUseCase importUc = screenManager.getImportDanhSachLopUseCase();
				if(importUc == null) {
					showAlert("Lỗi cấu hình", "Tính năng nhập chưa được cấu hình.", Alert.AlertType.ERROR);
					return;
				}

				importUc.importDanhSach(req);

				showAlert("Thành công", "Nhập hoàn tất.", Alert.AlertType.INFORMATION);
			}
			catch(Exception ex) {
				showAlert("Lỗi", ex.getMessage(), Alert.AlertType.ERROR);
			}
		});

		centerContainer.getChildren().addAll(title, tenDanhSachField, filePathField, chooseButton, importButton);
	}

	private void onExportDanhSach(DanhSachLopDto dto)
	{
		if(dto == null) return;
		if(screenManager == null || !screenManager.isAuthenticated()) {
			showAlert("Chưa đăng nhập", "Bạn cần đăng nhập.", Alert.AlertType.WARNING);
			return;
		}

		centerContainer.getChildren().clear();

		Label title = new Label("Xuất danh sách: " + (dto.getTenDanhSach() != null ? dto.getTenDanhSach() : ""));
		title.getStyleClass().add("home-title");

		Label formatLabel = new Label("Chọn định dạng:");
		RadioButton csvRb = new RadioButton("CSV");
		RadioButton excelRb = new RadioButton("Excel");
		ToggleGroup tg = new ToggleGroup();
		csvRb.setToggleGroup(tg);
		excelRb.setToggleGroup(tg);
		csvRb.setSelected(true);

		TextField savePathField = new TextField();
		savePathField.setEditable(false);

		Button choosePathBtn = new Button("Chọn nơi lưu");
		choosePathBtn.setOnAction(e -> {
			Window w = centerContainer.getScene().getWindow();
			FileChooser chooser = new FileChooser();
			if(csvRb.isSelected()) {
				chooser.getExtensionFilters().clear();
				chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
			}
			else {
				chooser.getExtensionFilters().clear();
				chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files", "*.xlsx"));
			}
			File chosen = chooser.showSaveDialog(w);
			if(chosen != null) {
				savePathField.setText(chosen.getAbsolutePath());
			}
		});

		Button saveBtn = new Button("Lưu");
		Button cancelBtn = new Button("Hủy");

		saveBtn.setOnAction(e -> {
			try {
				NguoiDungDto user = screenManager.getCurrentUser();
				if(user == null) {
					showAlert("Chưa đăng nhập", "Bạn cần đăng nhập.", Alert.AlertType.WARNING);
					return;
				}
				Long userId = user.getId();
				String path = savePathField.getText();
				String format = csvRb.isSelected() ? "CSV" : "EXCEL";

				XuatDanhSachLopUseCase xuatUc = screenManager.getXuatDanhSachLopUseCase();
				if(xuatUc == null) {
					showAlert("Lỗi cấu hình", "Tính năng xuất chưa được cấu hình.", Alert.AlertType.ERROR);
					return;
				}

				xuatUc.xuatDanhSach(userId, dto.getId(), path, format);

				showAlert("Thành công", "Xuất danh sách thành công.", Alert.AlertType.INFORMATION);
				onShowDanhSachLopPane();
			}
			catch(ValidationException ve) {
				showAlert("Không hợp lệ", ve.getMessage(), Alert.AlertType.WARNING);
			}
			catch(PersistenceException pe) {
				showAlert("Lỗi hệ thống", pe.getMessage(), Alert.AlertType.ERROR);
			}
			catch(Exception ex) {
				showAlert("Lỗi", ex.getMessage(), Alert.AlertType.ERROR);
			}
		});

		cancelBtn.setOnAction(e -> onViewChiTiet(dto));

		VBox form = new VBox(8, title, formatLabel, csvRb, excelRb, savePathField, choosePathBtn,
				new HBox(10, saveBtn, cancelBtn));
		centerContainer.getChildren().add(form);
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
