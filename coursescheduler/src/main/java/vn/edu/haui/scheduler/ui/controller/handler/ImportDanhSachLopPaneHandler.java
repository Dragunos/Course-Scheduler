package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.application.dto.TepTaiLenDto;
import vn.edu.haui.scheduler.application.exception.BusinessException;
import vn.edu.haui.scheduler.application.exception.ImportDanhSachLopException;
import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImportDanhSachLopPaneHandler
{
	private final ScreenManager screenManager;

	private final VBox centerContainer;

	public ImportDanhSachLopPaneHandler(ScreenManager screenManager, VBox centerContainer)
	{
		this.screenManager = screenManager;
		this.centerContainer = centerContainer;
	}

	public void showImportPane()
	{
		if(!isAuthenticated()) {
			showLoginWarning();
			return;
		}

		centerContainer.getChildren().clear();

		Label title = new Label("Nhập danh sách lớp học phần");
		title.getStyleClass().add("home-title");

		TextField tenField = new TextField();
		tenField.setPromptText("Tên danh sách");

		TextField fileField = new TextField();
		fileField.setEditable(false);

		Button chooseBtn = new Button("Chọn tệp");
		chooseBtn.setOnAction(e -> chooseFile(fileField));

		Button importBtn = new Button("Nhập");
		importBtn.setOnAction(e -> handleImport(tenField, fileField));

		centerContainer.getChildren()
				.addAll(title, tenField, fileField, chooseBtn, importBtn);
	}

	private boolean isAuthenticated()
	{
		return screenManager != null && screenManager.isAuthenticated();
	}

	private void showLoginWarning()
	{
		UiUtils.showAlert(
				"Chưa đăng nhập",
				"Bạn cần đăng nhập.",
				Alert.AlertType.WARNING);
	}

	private void chooseFile(TextField fileField)
	{
		Window window = centerContainer.getScene().getWindow();
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

		File file = chooser.showOpenDialog(window);
		if(file != null) {
			fileField.setText(file.getAbsolutePath());
		}
	}

	private void handleImport(TextField tenField, TextField fileField)
	{
		try {
			validateInput(tenField, fileField);

			NguoiDungDto user = screenManager.getCurrentUser();

			byte[] content = Files.readAllBytes(
					new File(fileField.getText()).toPath());

			TepTaiLenDto tep = buildTepTaiLenDto(fileField.getText(), content);

			ImportDanhSachLopUseCase useCase = screenManager.getImportDanhSachLopUseCase();

			useCase.importFromExcel(
					user.getId(),
					tenField.getText(),
					null,
					tep);

			UiUtils.showAlert(
					"Thành công",
					"Nhập hoàn tất.",
					Alert.AlertType.INFORMATION);

		}
		catch(BusinessException e) {
			UiUtils.showAlert(
					"Lỗi dữ liệu",
					e.getMessage(),
					Alert.AlertType.ERROR);
		}
		catch(ImportDanhSachLopException e) {
			UiUtils.showAlert(
					"Lỗi hệ thống",
					"Không thể nhập dữ liệu.",
					Alert.AlertType.ERROR);
		}
		catch(IOException e) {
			UiUtils.showAlert(
					"Lỗi đọc tệp",
					"Không thể đọc tệp.",
					Alert.AlertType.ERROR);
		}
	}

	private void validateInput(TextField tenField, TextField fileField)
	{
		if(tenField.getText() == null || tenField.getText().isBlank()) {
			throw new IllegalArgumentException("Tên danh sách không được rỗng");
		}

		if(fileField.getText() == null || fileField.getText().isBlank()) {
			throw new IllegalArgumentException("Bạn chưa chọn tệp");
		}
	}

	private TepTaiLenDto buildTepTaiLenDto(String path, byte[] content)
	{
		File file = new File(path);

		TepTaiLenDto dto = new TepTaiLenDto();
		dto.setTenTepGoc(file.getName());
		dto.setLoaiTep("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		dto.setDuongDan(path);
		dto.setStorageType("LOCAL");
		dto.setFileBlob(content);
		dto.setKichThuoc((long) content.length);

		return dto;
	}
}