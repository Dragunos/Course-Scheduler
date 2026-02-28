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
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ExportDanhSachLopUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.io.File;
import java.util.Optional;

public class ExportDanhSachLopPaneHandler {
    private final ScreenManager screenManager;
    private final VBox centerContainer;

    public ExportDanhSachLopPaneHandler(ScreenManager screenManager, VBox centerContainer) {
        this.screenManager = screenManager;
        this.centerContainer = centerContainer;
    }

    public void showExportPane(DanhSachLopDto dto) {
        centerContainer.getChildren().clear();

        Label title = new Label("Xuất danh sách: " + dto.getTenDanhSach());
        title.getStyleClass().add("home-title");

        // Format selection
        RadioButton csvRb = new RadioButton("CSV");
        RadioButton excelRb = new RadioButton("Excel");
        ToggleGroup tg = new ToggleGroup();
        csvRb.setToggleGroup(tg);
        excelRb.setToggleGroup(tg);
        csvRb.setSelected(true);

        HBox formatBox = new HBox(8, new Label("Định dạng:"), csvRb, excelRb);
        formatBox.setAlignment(Pos.CENTER_LEFT);

        // Path chooser
        TextField pathField = new TextField();
        pathField.setEditable(false);
        pathField.setPromptText("Chọn nơi lưu... (mặc định: thư mục người dùng)");
        HBox.setHgrow(pathField, Priority.ALWAYS);

        Button chooseBtn = new Button("Chọn nơi lưu");
        chooseBtn.setTooltip(new Tooltip("Chọn đường dẫn và tên tệp để lưu xuất"));

        // Preview filename label (tách để người dùng thấy file sẽ có tên gì)
        Label previewLabel = new Label();
        previewLabel.getStyleClass().add("muted-label");

        chooseBtn.setOnAction(e -> {
            Window w = centerContainer.getScene().getWindow();
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Lưu danh sách: " + dto.getTenDanhSach());
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));

            String defaultName = sanitizeFileName(Optional.ofNullable(dto.getTenDanhSach()).orElse("danh-sach"));

            if (csvRb.isSelected()) {
                chooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Tệp CSV", "*.csv"));
                chooser.setInitialFileName(defaultName + ".csv");
            } else {
                chooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Tệp Excel", "*.xlsx"));
                chooser.setInitialFileName(defaultName + ".xlsx");
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
                if (pathField.getText() == null || pathField.getText().isBlank())
                    throw new ValidationException("Bạn chưa chọn nơi lưu file.");

                NguoiDungDto user = screenManager.getCurrentUser();
                String format = csvRb.isSelected() ? "CSV" : "EXCEL";

                ExportDanhSachLopUseCase useCase = screenManager.getXuatDanhSachLopUseCase();

                // Không thay đổi logic nghiệp vụ — chỉ gọi use case như trước
                useCase.exportDanhSach(
                        user.getId(),
                        dto.getId(),
                        format,
                        pathField.getText());

                UiUtils.showAlert(
                        "Thành công",
                        "Xuất danh sách thành công.",
                        Alert.AlertType.INFORMATION);

                new ManageDanhSachLopPaneHandler(
                        screenManager,
                        centerContainer).showDanhSach();
            } catch (ValidationException ve) {
                UiUtils.showAlert(
                        "Không hợp lệ",
                        ve.getMessage(),
                        Alert.AlertType.WARNING);
            } catch (Exception ex) {
                UiUtils.showAlert(
                        "Lỗi",
                        ex.getMessage(),
                        Alert.AlertType.ERROR);
            }
        });

        cancelBtn.setOnAction(e -> new ManageDanhSachLopPaneHandler(screenManager, centerContainer)
                .showDanhSach());

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox form = new VBox(10);
        form.setPadding(new Insets(12));
        form.getChildren().addAll(title, formatBox, pathField, chooseBtn, previewLabel, buttons);
        form.setFillWidth(true);

        centerContainer.getChildren().add(form);
    }

    // Very small sanitization for initial filename
    private String sanitizeFileName(String input) {
        return input.replaceAll("[\\/:*?\"<>|]", "-").trim();
    }
}