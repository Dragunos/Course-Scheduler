package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.io.File;

public class ImportPaneHandler
{

	private final ScreenManager screenManager;

	private final VBox centerContainer;

	public ImportPaneHandler(ScreenManager screenManager, VBox centerContainer)
	{
		this.screenManager = screenManager;
		this.centerContainer = centerContainer;
	}

	public void showImportPane()
	{

		if(!screenManager.isAuthenticated()) {
			UiUtils.showAlert("Chưa đăng nhập",
					"Bạn cần đăng nhập.",
					Alert.AlertType.WARNING);
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
		chooseBtn.setOnAction(e -> {
			Window w = centerContainer.getScene().getWindow();
			FileChooser chooser = new FileChooser();
			File file = chooser.showOpenDialog(w);
			if(file != null) {
				fileField.setText(file.getAbsolutePath());
			}
		});

		Button importBtn = new Button("Nhập");
		importBtn.setOnAction(e -> {
			try {
				NguoiDungDto user = screenManager.getCurrentUser();

				ImportDanhSachLopRequestDto req = new ImportDanhSachLopRequestDto(
						fileField.getText(),
						tenField.getText(),
						user.getId(),
						null,
						false);

				ImportDanhSachLopUseCase useCase = screenManager.getImportDanhSachLopUseCase();

				useCase.importDanhSachLop(req);

				UiUtils.showAlert("Thành công",
						"Nhập hoàn tất.",
						Alert.AlertType.INFORMATION);

			}
			catch(Exception ex) {
				UiUtils.showAlert("Lỗi",
						ex.getMessage(),
						Alert.AlertType.ERROR);
			}
		});

		centerContainer.getChildren()
				.addAll(title, tenField, fileField, chooseBtn, importBtn);
	}
}
