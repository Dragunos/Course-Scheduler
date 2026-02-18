package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ManageDanhSachLopUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.util.List;
import java.util.Optional;

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

		VBox listBox = new VBox(10);

		try {
			Long userId = screenManager.getCurrentUser().getId();
			List<DanhSachLopDto> list = useCase.listDanhSachChoNguoiDung(userId);

			if(list == null || list.isEmpty()) {
				listBox.getChildren().add(new Label("Không có danh sách nào."));
			}
			else {
				for(DanhSachLopDto dto : list) {
					listBox.getChildren().add(createCard(dto));
				}
			}

			centerContainer.getChildren().addAll(title, listBox);

		}
		catch(DataAccessException e) {
			UiUtils.showAlert("Lỗi hệ thống", e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private VBox createCard(DanhSachLopDto dto)
	{

		String ten = dto.getTenDanhSach() != null ? dto.getTenDanhSach() : "<không tên>";
		String hocKy = dto.getHocKyId() != null ? String.valueOf(dto.getHocKyId()) : "N/A";

		Label tenLabel = new Label("Tên: " + ten);
		Label hocKyLabel = new Label("Học kỳ: " + hocKy);

		Button viewBtn = new Button("Xem chi tiết");
		Button editBtn = new Button("Chỉnh sửa");
		Button deleteBtn = new Button("Xóa");
		Button exportBtn = new Button("Xuất danh sách");

		viewBtn.setOnAction(e -> viewChiTiet(dto));
		editBtn.setOnAction(e -> editDanhSach(dto));
		deleteBtn.setOnAction(e -> deleteDanhSach(dto));
		exportBtn.setOnAction(e -> new ExportDanhSachLopPaneHandler(screenManager, centerContainer)
				.showExportPane(dto));

		HBox actions = new HBox(10, viewBtn, editBtn, deleteBtn, exportBtn);

		VBox card = new VBox(5, tenLabel, hocKyLabel, actions);
		card.setStyle("-fx-padding:10; -fx-border-color:#ccc;");

		return card;
	}

	private void viewChiTiet(DanhSachLopDto dto)
	{
		if(!isAuthenticated()) return;

		centerContainer.getChildren().clear();

		try {
			Long userId = screenManager.getCurrentUser().getId();
			ManageDanhSachLopUseCase useCase = screenManager.getQuanLyDanhSachLopUseCase();

			DanhSachLopDto detail = useCase.getChiTietDanhSach(userId, dto.getId());

			Label title = new Label("Chi tiết: " + dto.getTenDanhSach());
			title.getStyleClass().add("home-title");

			VBox listBox = new VBox(8);

			if(detail == null || detail.getChiTiet() == null
					|| detail.getChiTiet().isEmpty()) {

				listBox.getChildren().add(new Label("Không có lớp học phần."));
			}
			else {
				for(DanhSachLopChiTietDto ct : detail.getChiTiet()) {
					Label row = new Label(
							ct.getMaLop() + " - " +
									ct.getTenHocPhan() + " - " +
									ct.getTenGiangVien());
					listBox.getChildren().add(row);
				}
			}

			Button backBtn = new Button("Quay lại");
			backBtn.setOnAction(e -> showDanhSach());

			centerContainer.getChildren().addAll(title, listBox, backBtn);

		}
		catch(ValidationException ve) {
			UiUtils.showAlert("Không hợp lệ", ve.getMessage(), Alert.AlertType.WARNING);
		}
		catch(DataAccessException pe) {
			UiUtils.showAlert("Lỗi hệ thống", pe.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void editDanhSach(DanhSachLopDto dto)
	{
		if(!isAuthenticated()) return;

		centerContainer.getChildren().clear();

		Label title = new Label("Chỉnh sửa danh sách");
		title.getStyleClass().add("home-title");

		TextField tenField = new TextField(dto.getTenDanhSach());

		Button saveBtn = new Button("Lưu");

		saveBtn.setOnAction(e -> {
			try {
				UpdateDanhSachLopRequestDto req = new UpdateDanhSachLopRequestDto(dto.getId(),
						tenField.getText(), null, null);

				Long userId = screenManager.getCurrentUser().getId();

				screenManager.getQuanLyDanhSachLopUseCase()
						.updateDanhSach(userId, req);

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

		centerContainer.getChildren()
				.addAll(title, new Label("Tên danh sách"),
						tenField, saveBtn);
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
						.deleteDanhSach(userId, dto.getId());

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
}
