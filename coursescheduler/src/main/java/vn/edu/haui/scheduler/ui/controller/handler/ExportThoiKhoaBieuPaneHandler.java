package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
import java.util.Optional;

public class ExportThoiKhoaBieuPaneHandler {

    private final ScreenManager screenManager;
    private final VBox centerContainer;

    public ExportThoiKhoaBieuPaneHandler(ScreenManager screenManager, VBox centerContainer) {
        this.screenManager = screenManager;
        this.centerContainer = centerContainer;
    }

    public void showExportPane(ThoiKhoaBieuDto dto) {
        centerContainer.getChildren().clear();

        Label title = new Label("Xuất thời khóa biểu: " +
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

        HBox formatBox = new HBox(8, new Label("Định dạng:"), csvRb, pdfRb, icsRb);
        formatBox.setAlignment(Pos.CENTER_LEFT);

        TextField pathField = new TextField();
        pathField.setEditable(false);
        pathField.setPromptText("Chọn nơi lưu... (mặc định: thư mục người dùng)");
        HBox.setHgrow(pathField, Priority.ALWAYS);

        Button chooseBtn = new Button("Chọn nơi lưu");
        chooseBtn.setTooltip(new Tooltip("Chọn đường dẫn và tên tệp để lưu xuất"));

        Label previewLabel = new Label();
        previewLabel.getStyleClass().add("muted-label");

        chooseBtn.setOnAction(e -> {
            Window w = centerContainer.getScene().getWindow();
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Lưu thời khóa biểu: " + (dto.getTenPhuongAn() == null ? "tkb" : dto.getTenPhuongAn()));
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));

            String defaultName = sanitizeFileName(Optional.ofNullable(dto.getTenPhuongAn()).orElse("thoikhoabieu"));

            if (csvRb.isSelected()) {
                chooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
                chooser.setInitialFileName(defaultName + ".csv");
            } else if (pdfRb.isSelected()) {
                chooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
                chooser.setInitialFileName(defaultName + ".pdf");
            } else {
                chooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("ICS files", "*.ics"));
                chooser.setInitialFileName(defaultName + ".ics");
            }

            File file = chooser.showSaveDialog(w);
            if (file != null) {
                pathField.setText(file.getAbsolutePath());
                previewLabel.setText("Tệp: " + file.getName());
            }
        });

        Button saveBtn = new Button("Lưu");
        saveBtn.setDefaultButton(true);
        Button cancelBtn = new Button("Hủy");
        cancelBtn.setCancelButton(true);

        // Disable save until a path is chosen
        saveBtn.disableProperty().bind(Bindings.createBooleanBinding(
                () -> pathField.getText() == null || pathField.getText().isBlank(),
                pathField.textProperty()));

        saveBtn.setOnAction(e -> {
            try {
                if (screenManager.getCurrentUser() == null) {
                    UiUtils.showAlert(
                            "Chưa đăng nhập",
                            "Bạn cần đăng nhập.",
                            Alert.AlertType.WARNING);
                    return;
                }

                String outputPath = pathField.getText();
                if (outputPath == null || outputPath.isBlank()) {
                    UiUtils.showAlert(
                            "Thiếu đường dẫn",
                            "Vui lòng chọn nơi lưu file.",
                            Alert.AlertType.WARNING);
                    return;
                }

                NguoiDungDto user = screenManager.getCurrentUser();

                String format;
                if (csvRb.isSelected()) format = "CSV";
                else if (pdfRb.isSelected()) format = "PDF";
                else format = "ICS";

                ExportThoiKhoaBieuUseCase useCase = screenManager.getXuatThoiKhoaBieuUseCase();

                if (useCase == null) {
                    UiUtils.showAlert(
                            "Lỗi cấu hình",
                            "Tính năng xuất thời khóa biểu chưa được cấu hình.",
                            Alert.AlertType.ERROR);
                    return;
                }

                // Giữ nguyên thứ tự tham số và logic nghiệp vụ
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

            } catch (ValidationException ve) {
                UiUtils.showAlert(
                        "Không hợp lệ",
                        ve.getMessage(),
                        Alert.AlertType.WARNING);
            } catch (DataAccessException pe) {
                UiUtils.showAlert(
                        "Lỗi hệ thống",
                        pe.getMessage(),
                        Alert.AlertType.ERROR);
            } catch (Exception ex) {
                UiUtils.showAlert(
                        "Lỗi",
                        ex.getMessage(),
                        Alert.AlertType.ERROR);
            }
        });

        cancelBtn.setOnAction(e -> new ManageThoiKhoaBieuPaneHandler(
                screenManager,
                centerContainer).showThoiKhoaBieu());

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox form = new VBox(10);
        form.setPadding(new Insets(12));
        form.getChildren().addAll(title, formatBox, pathField, chooseBtn, previewLabel, buttons);
        form.setFillWidth(true);

        centerContainer.getChildren().add(form);
    }

    private String sanitizeFileName(String input) {
        return input.replaceAll("[\\/:*?\"<>|]", "-").trim();
    }
}
