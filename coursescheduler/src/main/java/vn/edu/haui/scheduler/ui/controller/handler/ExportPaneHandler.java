package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ExportDanhSachLopUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.io.File;

public class ExportPaneHandler
{

	private final ScreenManager screenManager;

	private final VBox centerContainer;

	public ExportPaneHandler(ScreenManager screenManager, VBox centerContainer)
	{
		this.screenManager = screenManager;
		this.centerContainer = centerContainer;
	}

	public void showExportPane(DanhSachLopDto dto)
	{

		centerContainer.getChildren().clear();

		Label title = new Label("Xuất danh sách: " + dto.getTenDanhSach());
		title.getStyleClass().add("home-title");

		RadioButton csvRb = new RadioButton("CSV");
		RadioButton excelRb = new RadioButton("Excel");

		ToggleGroup tg = new ToggleGroup();
		csvRb.setToggleGroup(tg);
		excelRb.setToggleGroup(tg);
		csvRb.setSelected(true);

		TextField pathField = new TextField();
		pathField.setEditable(false);

		Button chooseBtn = new Button("Chọn nơi lưu");
		chooseBtn.setOnAction(e -> {
			Window w = centerContainer.getScene().getWindow();
			FileChooser chooser = new FileChooser();

			if(csvRb.isSelected()) {
				chooser.getExtensionFilters().add(
						new FileChooser.ExtensionFilter("CSV files", "*.csv"));
			}
			else {
				chooser.getExtensionFilters().add(
						new FileChooser.ExtensionFilter("Excel files", "*.xlsx"));
			}

			File file = chooser.showSaveDialog(w);
			if(file != null) {
				pathField.setText(file.getAbsolutePath());
			}
		});

		Button saveBtn = new Button("Lưu");
		saveBtn.setOnAction(e -> {
			try {
				NguoiDungDto user = screenManager.getCurrentUser();
				String format = csvRb.isSelected() ? "CSV" : "EXCEL";

				ExportDanhSachLopUseCase useCase = screenManager.getXuatDanhSachLopUseCase();

				useCase.xuatDanhSach(
						user.getId(),
						dto.getId(),
						pathField.getText(),
						format);

				UiUtils.showAlert("Thành công",
						"Xuất danh sách thành công.",
						Alert.AlertType.INFORMATION);

				new DanhSachLopPaneHandler(screenManager, centerContainer)
						.showDanhSach();

			}
			catch(ValidationException ve) {
				UiUtils.showAlert("Không hợp lệ",
						ve.getMessage(),
						Alert.AlertType.WARNING);
			}
			catch(DataAccessException pe) {
				UiUtils.showAlert("Lỗi hệ thống",
						pe.getMessage(),
						Alert.AlertType.ERROR);
			}
			catch(Exception ex) {
				UiUtils.showAlert("Lỗi",
						ex.getMessage(),
						Alert.AlertType.ERROR);
			}
		});

		Button cancelBtn = new Button("Hủy");
		cancelBtn.setOnAction(e -> new DanhSachLopPaneHandler(screenManager, centerContainer)
				.showDanhSach());

		centerContainer.getChildren().add(
				new VBox(8, title,
						csvRb, excelRb,
						pathField, chooseBtn,
						new HBox(10, saveBtn, cancelBtn)));
	}
}
