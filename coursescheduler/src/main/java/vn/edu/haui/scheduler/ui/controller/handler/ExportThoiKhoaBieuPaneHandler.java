package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ExportThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.io.File;

public class ExportThoiKhoaBieuPaneHandler
{

	private final ScreenManager screenManager;

	private final VBox centerContainer;

	public ExportThoiKhoaBieuPaneHandler(ScreenManager screenManager, VBox centerContainer)
	{
		this.screenManager = screenManager;
		this.centerContainer = centerContainer;
	}

	public void showExportPane(ThoiKhoaBieuDto dto)
	{
		centerContainer.getChildren().clear();

		Label title = new Label(
				"Xuất thời khóa biểu: " +
						(dto.getTenPhuongAn() != null ? dto.getTenPhuongAn() : "<không tên>"));
		title.getStyleClass().add("home-title");

		RadioButton csvRb = new RadioButton("CSV");
		RadioButton pdfRb = new RadioButton("PDF");
		RadioButton icsRb = new RadioButton("ICS");

		ToggleGroup tg = new ToggleGroup();
		csvRb.setToggleGroup(tg);
		pdfRb.setToggleGroup(tg);
		icsRb.setToggleGroup(tg);

		pdfRb.setSelected(true);

		TextField pathField = new TextField();
		pathField.setEditable(false);

		Button chooseBtn = new Button("Chọn nơi lưu");
		chooseBtn.setOnAction(e -> {

			Window w = centerContainer.getScene().getWindow();
			FileChooser chooser = new FileChooser();

			if(csvRb.isSelected())
				chooser.getExtensionFilters().add(
						new FileChooser.ExtensionFilter("CSV files", "*.csv"));
			else if(pdfRb.isSelected())
				chooser.getExtensionFilters().add(
						new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
			else
				chooser.getExtensionFilters().add(
						new FileChooser.ExtensionFilter("ICS files", "*.ics"));

			File file = chooser.showSaveDialog(w);
			if(file != null)
				pathField.setText(file.getAbsolutePath());
		});

		Button saveBtn = new Button("Lưu");
		saveBtn.setOnAction(e -> {

			try {

				if(screenManager.getCurrentUser() == null) {
					UiUtils.showAlert(
							"Chưa đăng nhập",
							"Bạn cần đăng nhập.",
							Alert.AlertType.WARNING);
					return;
				}

				String outputPath = pathField.getText();
				if(outputPath == null || outputPath.isBlank()) {
					UiUtils.showAlert(
							"Thiếu đường dẫn",
							"Vui lòng chọn nơi lưu file.",
							Alert.AlertType.WARNING);
					return;
				}

				NguoiDungDto user = screenManager.getCurrentUser();

				String format;
				if(csvRb.isSelected()) format = "CSV";
				else if(pdfRb.isSelected()) format = "PDF";
				else format = "ICS";

				ExportThoiKhoaBieuUseCase useCase = screenManager.getXuatThoiKhoaBieuUseCase();

				if(useCase == null) {
					UiUtils.showAlert(
							"Lỗi cấu hình",
							"Tính năng xuất thời khóa biểu chưa được cấu hình.",
							Alert.AlertType.ERROR);
					return;
				}

				// ⭐ Quan trọng: đúng thứ tự tham số
				useCase.export(
						user.getId(),
						dto.getId(),
						format,
						outputPath);

				UiUtils.showAlert(
						"Thành công",
						"Xuất thời khóa biểu thành công.",
						Alert.AlertType.INFORMATION);

				new ManageThoiKhoaBieuPaneHandler(
						screenManager,
						centerContainer).showThoiKhoaBieu();

			}
			catch(ValidationException ve) {
				UiUtils.showAlert(
						"Không hợp lệ",
						ve.getMessage(),
						Alert.AlertType.WARNING);
			}
			catch(DataAccessException pe) {
				UiUtils.showAlert(
						"Lỗi hệ thống",
						pe.getMessage(),
						Alert.AlertType.ERROR);
			}
			catch(Exception ex) {
				UiUtils.showAlert(
						"Lỗi",
						ex.getMessage(),
						Alert.AlertType.ERROR);
			}
		});

		Button cancelBtn = new Button("Hủy");
		cancelBtn.setOnAction(e -> new ManageThoiKhoaBieuPaneHandler(
				screenManager,
				centerContainer).showThoiKhoaBieu());

		centerContainer.getChildren().add(
				new VBox(8,
						title,
						csvRb,
						pdfRb,
						icsRb,
						pathField,
						chooseBtn,
						new HBox(10, saveBtn, cancelBtn)));
	}
}