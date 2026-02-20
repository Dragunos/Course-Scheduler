package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import vn.edu.haui.scheduler.application.dto.DanhSachLopChiTietDto;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.LichHocDto;
import vn.edu.haui.scheduler.application.dto.UpdateDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ManageDanhSachLopUseCase;
import vn.edu.haui.scheduler.domain.enums.ThuTrongTuan;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ManageDanhSachLopPaneHandler
{
	private final ScreenManager screenManager;

	private final VBox centerContainer;

	public ManageDanhSachLopPaneHandler(ScreenManager screenManager, VBox centerContainer)
	{
		this.screenManager = screenManager;
		this.centerContainer = centerContainer;
	}

	public void showDanhSach()
	{
		if(!isAuthenticated()) return;

		ManageDanhSachLopUseCase useCase = screenManager.getQuanLyDanhSachLopUseCase();
		if(useCase == null) {
			UiUtils.showAlert("Lỗi cấu hình",
					"Tính năng quản lý danh sách chưa được cấu hình.",
					Alert.AlertType.ERROR);
			return;
		}

		centerContainer.getChildren().clear();

		Label title = new Label("Danh sách lớp học phần");
		title.getStyleClass().add("home-title");

		TableView<DanhSachLopDto> table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

		TableColumn<DanhSachLopDto, String> tenCol = new TableColumn<>("Tên Lớp học phần");
		tenCol.setCellValueFactory(c -> new SimpleStringProperty(
				c.getValue().getTenDanhSach() != null ? c.getValue().getTenDanhSach() : "<không tên>"));

		TableColumn<DanhSachLopDto, String> hocKyCol = new TableColumn<>("Học kỳ");
		hocKyCol.setCellValueFactory(c -> {
			Long hkId = c.getValue().getHocKyId();
			return new SimpleStringProperty(hkId != null ? String.valueOf(hkId) : "N/A");
		});

		TableColumn<DanhSachLopDto, Void> actionCol = new TableColumn<>("Hành động");
		actionCol.setMinWidth(180);
		actionCol.setCellFactory(col -> new TableCell<>()
		{
			private final Button viewBtn = new Button("🔍");

			private final Button editBtn = new Button("✏");

			private final Button deleteBtn = new Button("🗑");

			private final Button exportBtn = new Button("📤");

			private final HBox hbox = new HBox(8, viewBtn, editBtn, deleteBtn, exportBtn);

			{
				hbox.setAlignment(Pos.CENTER);
				viewBtn.setFocusTraversable(false);
				editBtn.setFocusTraversable(false);
				deleteBtn.setFocusTraversable(false);
				exportBtn.setFocusTraversable(false);

				viewBtn.setOnAction(e -> {
					DanhSachLopDto dto = getTableView().getItems().get(getIndex());
					viewChiTiet(dto);
				});

				editBtn.setOnAction(e -> {
					DanhSachLopDto dto = getTableView().getItems().get(getIndex());
					editDanhSach(dto);
				});

				deleteBtn.setOnAction(e -> {
					DanhSachLopDto dto = getTableView().getItems().get(getIndex());
					deleteDanhSach(dto);
				});

				exportBtn.setOnAction(e -> {
					DanhSachLopDto dto = getTableView().getItems().get(getIndex());
					new ExportDanhSachLopPaneHandler(screenManager, centerContainer)
							.showExportPane(dto);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty)
			{
				super.updateItem(item, empty);
				if(empty) setGraphic(null);
				else setGraphic(hbox);
			}
		});

		table.getColumns().clear();
		table.getColumns().add(tenCol);
		table.getColumns().add(hocKyCol);
		table.getColumns().add(actionCol);

		try {
			Long userId = screenManager.getCurrentUser().getId();
			List<DanhSachLopDto> list = useCase.getAllDanhSachLopByNguoiDungId(userId);

			if(list == null || list.isEmpty()) {
				table.setItems(FXCollections.observableArrayList());
				Label empty = new Label("Không có danh sách nào.");
				VBox wrapper = new VBox(8, title, empty, table);
				wrapper.setFillWidth(true);
				centerContainer.getChildren().add(wrapper);
			}
			else {
				table.setItems(FXCollections.observableArrayList(list));
				VBox wrapper = new VBox(12, title, table);
				wrapper.setFillWidth(true);

				VBox.setVgrow(table, Priority.ALWAYS);

				centerContainer.getChildren().add(wrapper);
			}
		}
		catch(DataAccessException e) {
			UiUtils.showAlert("Lỗi hệ thống", e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void viewChiTiet(DanhSachLopDto dto)
	{
		if(!isAuthenticated()) return;

		centerContainer.getChildren().clear();

		try {
			Long userId = screenManager.getCurrentUser().getId();
			ManageDanhSachLopUseCase useCase = screenManager.getQuanLyDanhSachLopUseCase();

			DanhSachLopDto detail = useCase.getDanhSachLopById(userId, dto.getId());

			Label title = new Label(
					"Chi tiết: " +
							(detail.getTenDanhSach() != null
									? detail.getTenDanhSach()
									: "<không tên>"));
			title.getStyleClass().add("home-title");

			TableView<DanhSachLopChiTietDto> table = new TableView<>();
			table.setColumnResizePolicy(
					TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

			TableColumn<DanhSachLopChiTietDto, String> maLopCol = new TableColumn<>("Mã Lớp");

			maLopCol.setCellValueFactory(c -> new SimpleStringProperty(
					c.getValue().getMaLop()));

			TableColumn<DanhSachLopChiTietDto, String> tenHpCol = new TableColumn<>("Tên Học Phần");

			tenHpCol.setCellValueFactory(c -> new SimpleStringProperty(
					c.getValue().getTenHocPhan()));

			TableColumn<DanhSachLopChiTietDto, String> gvCol = new TableColumn<>("Giảng viên");

			gvCol.setCellValueFactory(c -> new SimpleStringProperty(
					c.getValue().getTenGiangVien()));

			TableColumn<DanhSachLopChiTietDto, String> maHpCol = new TableColumn<>("Mã HP");

			maHpCol.setCellValueFactory(c -> new SimpleStringProperty(
					c.getValue().getMaHocPhan()));

			TableColumn<DanhSachLopChiTietDto, String> hinhThucCol = new TableColumn<>("Hình thức dạy");

			hinhThucCol.setCellValueFactory(c -> new SimpleStringProperty(
					c.getValue().getHinhThucDay()));

			TableColumn<DanhSachLopChiTietDto, String> diaDiemCol = new TableColumn<>("Địa điểm");

			diaDiemCol.setCellValueFactory(c -> new SimpleStringProperty(
					c.getValue().getDiaDiem()));

			TableColumn<DanhSachLopChiTietDto, String> lichHocCol = new TableColumn<>("Lịch học");

			lichHocCol.setCellValueFactory(c -> {

				List<LichHocDto> lich = c.getValue().getLichHoc();

				if(lich == null || lich.isEmpty())
					return new SimpleStringProperty("");

				String value = lich.stream()
						.map(l -> toThuLabel(l.getThu()) +
								" (Tiết " +
								l.getTietBatDau() +
								"-" +
								l.getTietKetThuc() +
								")")
						.collect(Collectors.joining("; "));

				return new SimpleStringProperty(value);
			});

			table.getColumns().clear();
			table.getColumns().add(maHpCol);
			table.getColumns().add(tenHpCol);
			table.getColumns().add(maLopCol);
			table.getColumns().add(lichHocCol);
			table.getColumns().add(hinhThucCol);
			table.getColumns().add(diaDiemCol);
			table.getColumns().add(gvCol);

			table.setItems(
					FXCollections.observableArrayList(
							detail.getChiTiet()));

			VBox wrapper = new VBox(12, title, table);
			wrapper.setFillWidth(true);
			VBox.setVgrow(table, Priority.ALWAYS);

			Button backBtn = new Button("Quay lại");
			backBtn.setOnAction(e -> showDanhSach());

			centerContainer.getChildren().addAll(wrapper, backBtn);
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
	}

	private void editDanhSach(DanhSachLopDto dto)
	{
		if(!isAuthenticated()) return;

		centerContainer.getChildren().clear();

		Label title = new Label("Chỉnh sửa danh sách");
		title.getStyleClass().add("home-title");

		TextField tenField = new TextField(dto.getTenDanhSach());
		tenField.setPromptText("Tên danh sách");

		TextField hocKyField = new TextField();
		hocKyField.setPromptText("Học kỳ (id) - để trống nếu không thay đổi");
		hocKyField.setText(dto.getHocKyId() != null ? String.valueOf(dto.getHocKyId()) : "");

		Button saveBtn = new Button("Lưu");
		saveBtn.setOnAction(e -> {
			try {
				Long hocKyId = null;
				String hkText = hocKyField.getText();
				if(hkText != null && !hkText.trim().isEmpty()) {
					try {
						hocKyId = Long.valueOf(hkText.trim());
					}
					catch(NumberFormatException nfe) {
						throw new ValidationException("Học kỳ phải là số (id).");
					}
				}

				UpdateDanhSachLopRequestDto req = new UpdateDanhSachLopRequestDto(
						dto.getId(),
						tenField.getText(),
						hocKyId,
						null // không chỉnh sửa chi tiết ở UI này
				);

				Long userId = screenManager.getCurrentUser().getId();

				screenManager.getQuanLyDanhSachLopUseCase()
						.updateDanhSachLop(userId, req);

				UiUtils.showAlert("Thành công", "Đã cập nhật.",
						Alert.AlertType.INFORMATION);

				showDanhSach();
			}
			catch(ValidationException ve) {
				UiUtils.showAlert("Không hợp lệ", ve.getMessage(),
						Alert.AlertType.WARNING);
			}
			catch(DataAccessException pe) {
				UiUtils.showAlert("Lỗi hệ thống", pe.getMessage(),
						Alert.AlertType.ERROR);
			}
		});

		Button cancelBtn = new Button("Hủy");
		cancelBtn.setOnAction(e -> showDanhSach());

		HBox actions = new HBox(8, saveBtn, cancelBtn);

		centerContainer.getChildren().addAll(title, new Label("Tên danh sách"), tenField,
				new Label("Học kỳ (id)"), hocKyField, actions);
	}

	private void deleteDanhSach(DanhSachLopDto dto)
	{
		if(!isAuthenticated()) return;

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Xác nhận");
		confirm.setHeaderText(null);
		confirm.setContentText("Bạn có chắc muốn xóa?");

		Optional<ButtonType> result = confirm.showAndWait();

		if(result.isPresent() && result.get() == ButtonType.OK) {
			try {
				Long userId = screenManager.getCurrentUser().getId();

				screenManager.getQuanLyDanhSachLopUseCase()
						.deleteDanhSachLop(userId, dto.getId());

				UiUtils.showAlert("Thành công", "Đã xóa.",
						Alert.AlertType.INFORMATION);

				showDanhSach();

			}
			catch(Exception ex) {
				UiUtils.showAlert("Lỗi", ex.getMessage(),
						Alert.AlertType.ERROR);
			}
		}
	}

	private boolean isAuthenticated()
	{
		if(screenManager == null || !screenManager.isAuthenticated()) {
			UiUtils.showAlert("Chưa đăng nhập",
					"Bạn cần đăng nhập.",
					Alert.AlertType.WARNING);
			return false;
		}
		return true;
	}

	private String toThuLabel(ThuTrongTuan thu)
	{
		switch(thu) {
			case THU_2:
				return "Thứ 2";
			case THU_3:
				return "Thứ 3";
			case THU_4:
				return "Thứ 4";
			case THU_5:
				return "Thứ 5";
			case THU_6:
				return "Thứ 6";
			case THU_7:
				return "Thứ 7";
			case CHU_NHAT:
				return "Chủ nhật";
			default:
				return thu.name();
		}
	}
}
