package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
		BooleanProperty importing = new SimpleBooleanProperty(false);

		if(!isAuthenticated()) {
			showLoginWarning();
			return;
		}

		centerContainer.getChildren().clear();
		centerContainer.setSpacing(20);
		centerContainer.setPadding(new Insets(30));

		Label title = new Label("Nhập danh sách lớp học phần");
		title.getStyleClass().add("home-title");

		// ===== FORM GRID =====
		GridPane formGrid = new GridPane();
		formGrid.setHgap(15);
		formGrid.setVgap(15);
		formGrid.setPadding(new Insets(20));
		formGrid.setMaxWidth(600);

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(30);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(70);
		col2.setHgrow(Priority.ALWAYS);

		formGrid.getColumnConstraints().addAll(col1, col2);

		// ===== Fields =====
		Label tenLabel = new Label("Tên danh sách:");
		TextField tenField = new TextField();
		tenField.setPromptText("Nhập tên danh sách");

		Label fileLabel = new Label("Tệp Excel:");
		TextField fileField = new TextField();
		fileField.setEditable(false);

		Button chooseBtn = new Button("📂 Chọn tệp");
		chooseBtn.setOnAction(e -> chooseFile(fileField));

		HBox fileBox = new HBox(10, fileField, chooseBtn);
		HBox.setHgrow(fileField, Priority.ALWAYS);

		// ===== Progress =====
		ProgressIndicator progressIndicator = new ProgressIndicator();
		progressIndicator.setVisible(false);
		progressIndicator.setMaxSize(40, 40);

		// ===== Import Button =====
		Button importBtn = new Button("⬆ Nhập dữ liệu");

		// ===== Realtime Validation =====
		BooleanBinding invalidInput = tenField.textProperty().isEmpty()
				.or(fileField.textProperty().isEmpty());

		importBtn.disableProperty().bind(
				invalidInput.or(importing));

		// ===== Layout positioning =====
		formGrid.add(tenLabel, 0, 0);
		formGrid.add(tenField, 1, 0);
		formGrid.add(fileLabel, 0, 1);
		formGrid.add(fileBox, 1, 1);

		HBox actionBox = new HBox(15, importBtn, progressIndicator);
		actionBox.setAlignment(Pos.CENTER_LEFT);

		VBox wrapper = new VBox(20, title, formGrid, actionBox);
		wrapper.setAlignment(Pos.TOP_CENTER);

		centerContainer.getChildren().add(wrapper);

		// ===== Import Action with Background Task =====
		importBtn.setOnAction(
				e -> runImportTask(tenField, fileField, importBtn, chooseBtn, progressIndicator, importing));
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
				new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));

		File file = chooser.showOpenDialog(window);

		if(file != null) {
			fileField.setText(file.getAbsolutePath());
		}
	}

	// ===== Background Import =====
	private void runImportTask(TextField tenField,
			TextField fileField,
			Button importBtn,
			Button chooseBtn,
			ProgressIndicator progressIndicator,
			BooleanProperty importing)
	{
		Task<Void> task = new Task<>()
		{
			@Override
			protected Void call() throws Exception
			{
				handleImport(tenField, fileField);
				return null;
			}
		};

		task.setOnRunning(e -> {
			importing.set(true);
			chooseBtn.setDisable(true);
			progressIndicator.setVisible(true);
		});

		task.setOnSucceeded(e -> {
			importing.set(false);
			chooseBtn.setDisable(false);
			progressIndicator.setVisible(false);

			UiUtils.showAlert(
					"Thành công",
					"Nhập hoàn tất.",
					Alert.AlertType.INFORMATION);
		});

		task.setOnFailed(e -> {
			importing.set(false);
			chooseBtn.setDisable(false);
			progressIndicator.setVisible(false);

			Throwable ex = task.getException();

			if(ex instanceof BusinessException) {
				UiUtils.showAlert("Lỗi dữ liệu",
						ex.getMessage(),
						Alert.AlertType.ERROR);
			}
			else if(ex instanceof ImportDanhSachLopException) {
				UiUtils.showAlert("Lỗi hệ thống",
						"Không thể nhập dữ liệu.",
						Alert.AlertType.ERROR);
			}
			else if(ex instanceof IOException) {
				UiUtils.showAlert("Lỗi đọc tệp",
						"Không thể đọc tệp.",
						Alert.AlertType.ERROR);
			}
			else if(ex instanceof IllegalArgumentException) {
				UiUtils.showAlert("Lỗi nhập liệu",
						ex.getMessage(),
						Alert.AlertType.ERROR);
			}
			else {
				UiUtils.showAlert("Lỗi không xác định",
						ex.getMessage(),
						Alert.AlertType.ERROR);
			}
		});

		new Thread(task).start();
	}

	// ===== Business Logic giữ nguyên =====
	private void handleImport(TextField tenField, TextField fileField) throws Exception
	{
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
	}

	private void showError(String title, String message)
	{
		Platform.runLater(() -> UiUtils.showAlert(title, message, Alert.AlertType.ERROR));
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