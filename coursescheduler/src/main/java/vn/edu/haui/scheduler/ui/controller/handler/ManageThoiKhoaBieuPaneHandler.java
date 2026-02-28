package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import vn.edu.haui.scheduler.application.dto.LopHocPhanDto;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;
import vn.edu.haui.scheduler.application.port.in.ManageThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ManageThoiKhoaBieuPaneHandler
{
    private final ScreenManager screenManager;
    private final VBox centerContainer;

    public ManageThoiKhoaBieuPaneHandler(ScreenManager screenManager, VBox centerContainer)
    {
        this.screenManager = screenManager;
        this.centerContainer = centerContainer;
    }

    public void showThoiKhoaBieu()
    {
        if(!isAuthenticated()) return;

        ManageThoiKhoaBieuUseCase useCase =
                screenManager.getQuanLyThoiKhoaBieuUseCase();

        if(useCase == null) {
            UiUtils.showAlert("Lỗi cấu hình",
                    "Tính năng quản lý thời khóa biểu chưa được cấu hình.",
                    Alert.AlertType.ERROR);
            return;
        }

        centerContainer.getChildren().clear();

        Label title = new Label("Thời khóa biểu của tôi");
        title.getStyleClass().add("home-title");

        TableView<ThoiKhoaBieuDto> table = new TableView<>();
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<ThoiKhoaBieuDto, String> tenCol =
                new TableColumn<>("Tên phương án");

        tenCol.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getTenPhuongAn() != null
                                ? c.getValue().getTenPhuongAn()
                                : "<không tên>"
                ));

        TableColumn<ThoiKhoaBieuDto, String> soLopCol =
                new TableColumn<>("Số lớp");

        soLopCol.setCellValueFactory(c -> {
            List<LopHocPhanDto> list =
                    c.getValue().getDanhSachLopHocPhan();
            int size = (list == null) ? 0 : list.size();
            return new SimpleStringProperty(String.valueOf(size));
        });

        TableColumn<ThoiKhoaBieuDto, Void> actionCol =
                new TableColumn<>("Hành động");

        actionCol.setMinWidth(250);

        actionCol.setCellFactory(col -> new TableCell<>()
        {
            private final Button viewBtn =
                    new Button("🔍 Xem");

            private final Button renameBtn =
                    new Button("✏ Đổi tên");

            private final Button deleteBtn =
                    new Button("🗑 Xóa");

            private final Button exportBtn =
                    new Button("📤 Xuất");

            private final HBox box =
                    new HBox(8, viewBtn, renameBtn,
                            deleteBtn, exportBtn);

            {
                box.setAlignment(Pos.CENTER);

                viewBtn.setOnAction(e -> {
                    ThoiKhoaBieuDto dto =
                            getTableView().getItems().get(getIndex());
                    viewChiTiet(dto);
                });

                renameBtn.setOnAction(e -> {
                    ThoiKhoaBieuDto dto =
                            getTableView().getItems().get(getIndex());
                    doiTen(dto);
                });

                deleteBtn.setOnAction(e -> {
                    ThoiKhoaBieuDto dto =
                            getTableView().getItems().get(getIndex());
                    xoa(dto);
                });

                exportBtn.setOnAction(e -> {
                    ThoiKhoaBieuDto dto =
                            getTableView().getItems().get(getIndex());

                    new ExportThoiKhoaBieuPaneHandler(
                            screenManager, centerContainer)
                            .showExportPane(dto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty)
            {
                super.updateItem(item, empty);
                if(empty) setGraphic(null);
                else setGraphic(box);
            }
        });

        table.getColumns().addAll(tenCol, soLopCol, actionCol);

        try {
            Long userId =
                    screenManager.getCurrentUser().getId();

            List<ThoiKhoaBieuDto> list =
                    useCase.findAllByUser(userId);

            table.setItems(FXCollections.observableArrayList(list));

            VBox wrapper = new VBox(12, title, table);
            wrapper.setFillWidth(true);
            VBox.setVgrow(table, Priority.ALWAYS);

            centerContainer.getChildren().add(wrapper);
        }
        catch(Exception e) {
            UiUtils.showAlert("Lỗi hệ thống",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void viewChiTiet(ThoiKhoaBieuDto dto)
    {
        if(!isAuthenticated()) return;

        centerContainer.getChildren().clear();

        try {
            Long userId =
                    screenManager.getCurrentUser().getId();

            ManageThoiKhoaBieuUseCase useCase =
                    screenManager.getQuanLyThoiKhoaBieuUseCase();

            ThoiKhoaBieuDto detail =
                    useCase.findDetail(userId, dto.getId());

            Label title = new Label(
                    "Chi tiết: " +
                            (detail.getTenPhuongAn() != null
                                    ? detail.getTenPhuongAn()
                                    : "<không tên>")
            );

            title.getStyleClass().add("home-title");

            TableView<LopHocPhanDto> table =
                    new TableView<>();

            table.setColumnResizePolicy(
                    TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

            TableColumn<LopHocPhanDto, String> maLopCol =
                    new TableColumn<>("Mã lớp");

            maLopCol.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getMaLop() != null
                                    ? c.getValue().getMaLop()
                                    : ""
                    ));

            TableColumn<LopHocPhanDto, String> gvCol =
                    new TableColumn<>("Giảng viên");

            gvCol.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getGiangVien() != null
                                    ? c.getValue().getGiangVien()
                                            .getTenGiangVien()
                                    : ""
                    ));

            TableColumn<LopHocPhanDto, String> lichCol =
                    new TableColumn<>("Lịch học");

            lichCol.setCellValueFactory(c -> {

                if(c.getValue().getLichHocDanhSach() == null
                        || c.getValue()
                        .getLichHocDanhSach().isEmpty())
                    return new SimpleStringProperty("");

                String value =
                        c.getValue()
                         .getLichHocDanhSach()
                         .stream()
                         .map(l ->
                                 "Thứ " + l.getThu()
                                         + " (" +
                                         l.getTietBatDau()
                                         + "-"
                                         + l.getTietKetThuc()
                                         + ")")
                         .collect(Collectors.joining("; "));

                return new SimpleStringProperty(value);
            });

            table.getColumns().addAll(maLopCol, gvCol, lichCol);

            table.setItems(
                    FXCollections.observableArrayList(
                            detail.getDanhSachLopHocPhan()
                    )
            );

            Button backBtn = new Button("Quay lại");
            backBtn.setOnAction(e -> showThoiKhoaBieu());

            VBox wrapper = new VBox(12, title, table, backBtn);
            wrapper.setFillWidth(true);
            VBox.setVgrow(table, Priority.ALWAYS);

            centerContainer.getChildren().add(wrapper);
        }
        catch(Exception ex) {
            UiUtils.showAlert("Lỗi",
                    ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void doiTen(ThoiKhoaBieuDto dto)
    {
        if(!isAuthenticated()) return;

        centerContainer.getChildren().clear();

        Label title =
                new Label("Đổi tên thời khóa biểu");

        title.getStyleClass().add("home-title");

        TextField tenField =
                new TextField(dto.getTenPhuongAn());

        tenField.setPromptText("Nhập tên mới");

        Button saveBtn = new Button("Lưu");
        saveBtn.setOnAction(e -> {
            try {
                Long userId =
                        screenManager.getCurrentUser().getId();

                screenManager
                        .getQuanLyThoiKhoaBieuUseCase()
                        .rename(userId,
                                dto.getId(),
                                tenField.getText());

                UiUtils.showAlert("Thành công",
                        "Đã cập nhật tên.",
                        Alert.AlertType.INFORMATION);

                showThoiKhoaBieu();
            }
            catch(Exception ex) {
                UiUtils.showAlert("Lỗi",
                        ex.getMessage(),
                        Alert.AlertType.ERROR);
            }
        });

        Button cancelBtn = new Button("Hủy");
        cancelBtn.setOnAction(e -> showThoiKhoaBieu());

        HBox actions = new HBox(8, saveBtn, cancelBtn);

        centerContainer.getChildren().addAll(
                title,
                new Label("Tên mới"),
                tenField,
                actions
        );
    }

    private void xoa(ThoiKhoaBieuDto dto)
    {
        if(!isAuthenticated()) return;

        Alert confirm =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirm.setTitle("Xác nhận");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn xóa?");

        Optional<ButtonType> result =
                confirm.showAndWait();

        if(result.isPresent()
                && result.get() == ButtonType.OK)
        {
            try {
                Long userId =
                        screenManager.getCurrentUser().getId();

                screenManager
                        .getQuanLyThoiKhoaBieuUseCase()
                        .delete(userId, dto.getId());

                UiUtils.showAlert("Thành công",
                        "Đã xóa.",
                        Alert.AlertType.INFORMATION);

                showThoiKhoaBieu();
            }
            catch(Exception ex) {
                UiUtils.showAlert("Lỗi",
                        ex.getMessage(),
                        Alert.AlertType.ERROR);
            }
        }
    }

    private boolean isAuthenticated()
    {
        if(screenManager == null
                || !screenManager.isAuthenticated())
        {
            UiUtils.showAlert("Chưa đăng nhập",
                    "Bạn cần đăng nhập.",
                    Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
}