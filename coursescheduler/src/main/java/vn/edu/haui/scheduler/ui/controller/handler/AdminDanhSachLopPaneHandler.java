package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.AdminDanhSachLopUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class AdminDanhSachLopPaneHandler
{
    private final ScreenManager screenManager;
    private final VBox centerContainer;

    public AdminDanhSachLopPaneHandler(
            ScreenManager screenManager,
            VBox centerContainer)
    {
        this.screenManager = screenManager;
        this.centerContainer = centerContainer;
    }

    public void showDanhSachCongKhai()
    {
        if(!isAdmin()) return;

        AdminDanhSachLopUseCase useCase =
                screenManager.getQuanTriDanhSachLopUseCase();

        if(useCase == null) {
            UiUtils.showAlert(
                    "Lỗi cấu hình",
                    "Chưa cấu hình quản trị danh sách.",
                    Alert.AlertType.ERROR);
            return;
        }

        centerContainer.getChildren().clear();

        Label title = new Label("Danh sách lớp hệ thống");
        title.getStyleClass().add("home-title");

        Button importBtn = new Button("📥 Nhập danh sách");
        importBtn.setOnAction(e -> importDanhSach());

        HBox toolbar = new HBox(importBtn);
        toolbar.setAlignment(Pos.CENTER_RIGHT);
        toolbar.setPadding(new Insets(5,0,5,0));

        TableView<DanhSachLopDto> table = new TableView<>();
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<DanhSachLopDto, String> tenCol =
                new TableColumn<>("Tên danh sách");
        tenCol.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getTenDanhSach() != null
                                ? c.getValue().getTenDanhSach()
                                : "<không tên>"));

        TableColumn<DanhSachLopDto, String> hocKyCol =
                new TableColumn<>("Học kỳ");
        hocKyCol.setCellValueFactory(c -> {
            Long hk = c.getValue().getHocKyId();
            return new SimpleStringProperty(
                    hk != null ? String.valueOf(hk) : "N/A");
        });

        TableColumn<DanhSachLopDto, Void> actionCol =
                new TableColumn<>("Hành động");
        actionCol.setMinWidth(200);

        actionCol.setCellFactory(col -> new TableCell<>()
        {
            private final Button viewBtn =
                    new Button("🔍 Xem");

            private final Button deleteBtn =
                    new Button("🗑 Xóa");

            private final HBox box =
                    new HBox(8, viewBtn, deleteBtn);

            {
                box.setAlignment(Pos.CENTER);

                viewBtn.setOnAction(e -> {
                    DanhSachLopDto dto =
                            getTableView().getItems()
                                    .get(getIndex());
                    viewChiTiet(dto);
                });

                deleteBtn.setOnAction(e -> {
                    DanhSachLopDto dto =
                            getTableView().getItems()
                                    .get(getIndex());
                    deleteDanhSach(dto.getId());
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

        table.getColumns().addAll(tenCol, hocKyCol, actionCol);

        try {

            List<DanhSachLopDto> list =
                    useCase.findAllPublic();

            table.setItems(
                    FXCollections.observableArrayList(list));

        }
        catch(DataAccessException e) {
            UiUtils.showAlert(
                    "Lỗi hệ thống",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }

        VBox wrapper = new VBox(12, title, toolbar, table);
        wrapper.setPadding(new Insets(10));
        VBox.setVgrow(table, Priority.ALWAYS);

        centerContainer.getChildren().add(wrapper);
    }

    private void viewChiTiet(DanhSachLopDto dto)
    {
        try {
            NguoiDungDto user = screenManager.getCurrentUser();

            DanhSachLopDto detail =
                    screenManager
                            .getQuanTriDanhSachLopUseCase()
                            .findDetail(user.getId(), dto.getId());

            centerContainer.getChildren().clear();

            Label title = new Label(
                    "Chi tiết: " +
                            (detail.getTenDanhSach() != null
                                    ? detail.getTenDanhSach()
                                    : "<không tên>"));

            title.getStyleClass().add("home-title");

            TableView<DanhSachLopChiTietDto> table =
                    new TableView<>();

            table.setColumnResizePolicy(
                    TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

            TableColumn<DanhSachLopChiTietDto, String> maLopCol =
                    new TableColumn<>("Mã lớp");

            maLopCol.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getMaLop() != null
                                    ? c.getValue().getMaLop()
                                    : ""));

            TableColumn<DanhSachLopChiTietDto, String> tenHpCol =
                    new TableColumn<>("Học phần");

            tenHpCol.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getTenHocPhan() != null
                                    ? c.getValue().getTenHocPhan()
                                    : ""));

            TableColumn<DanhSachLopChiTietDto, String> gvCol =
                    new TableColumn<>("Giảng viên");

            gvCol.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getTenGiangVien() != null
                                    ? c.getValue().getTenGiangVien()
                                    : ""));

            table.getColumns().addAll(maLopCol, tenHpCol, gvCol);

            table.setItems(
                    FXCollections.observableArrayList(
                            detail.getChiTiet()));

            Button backBtn = new Button("← Quay lại");
            backBtn.setOnAction(e -> showDanhSachCongKhai());

            VBox wrapper = new VBox(12, title, table, backBtn);
            wrapper.setPadding(new Insets(10));
            VBox.setVgrow(table, Priority.ALWAYS);

            centerContainer.getChildren().add(wrapper);

        }
        catch(Exception ex) {
            UiUtils.showAlert(
                    "Lỗi",
                    ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void deleteDanhSach(Long id)
    {
        Alert confirm =
                new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setContentText("Xác nhận xóa?");

        Optional<ButtonType> result =
                confirm.showAndWait();

        if(result.isEmpty()
                || result.get() != ButtonType.OK)
            return;

        try {

            NguoiDungDto user =
                    screenManager.getCurrentUser();

            screenManager
                    .getQuanTriDanhSachLopUseCase()
                    .deletePublic(user.getId(), id);

            showDanhSachCongKhai();
        }
        catch(Exception ex) {
            UiUtils.showAlert(
                    "Lỗi",
                    ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void importDanhSach()
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn file Excel");

        File file = chooser.showOpenDialog(
                centerContainer.getScene().getWindow());

        if(file == null) return;

        try {

            NguoiDungDto user =
                    screenManager.getCurrentUser();

            byte[] data =
                    Files.readAllBytes(file.toPath());

            TepTaiLenDto tep = new TepTaiLenDto(
                    file.getName(),
                    file.getAbsolutePath(),
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    data);

            tep.setStorageType("LOCAL");

            screenManager
                    .getQuanTriDanhSachLopUseCase()
                    .importPublic(
                            user.getId(),
                            "Danh sách hệ thống",
                            null,
                            tep);

            UiUtils.showAlert(
                    "Thành công",
                    "Đã nhập danh sách.",
                    Alert.AlertType.INFORMATION);

            showDanhSachCongKhai();

        }
        catch(ValidationException ve) {
            UiUtils.showAlert(
                    "Không hợp lệ",
                    ve.getMessage(),
                    Alert.AlertType.WARNING);
        }
        catch(Exception ex) {
            UiUtils.showAlert(
                    "Lỗi",
                    ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private boolean isAdmin()
    {
        if(!screenManager.isAuthenticated()) {
            UiUtils.showAlert(
                    "Chưa đăng nhập",
                    "Bạn cần đăng nhập.",
                    Alert.AlertType.WARNING);
            return false;
        }

        if(!screenManager.isAdminUser()) {
            UiUtils.showAlert(
                    "Không có quyền",
                    "Chỉ quản trị viên mới được truy cập.",
                    Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }
}