package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.AdminDanhSachLopUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.io.File;
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

		AdminDanhSachLopUseCase useCase = screenManager.getQuanTriDanhSachLopUseCase();

		if(useCase == null) {
			UiUtils.showAlert("Lỗi cấu hình",
					"Chưa cấu hình quản trị danh sách.",
					Alert.AlertType.ERROR);
			return;
		}

		centerContainer.getChildren().clear();

		Label title = new Label("Danh sách lớp hệ thống");
		title.getStyleClass().add("home-title");

		VBox listBox = new VBox(10);

		try {
			List<DanhSachLopDto> list = useCase.listDanhSachCongKhai();

			if(list == null || list.isEmpty()) {
				listBox.getChildren()
						.add(new Label("Không có danh sách công khai."));
			}
			else {
				for(DanhSachLopDto dto : list) {
					listBox.getChildren().add(createCard(dto));
				}
			}

			Button importBtn = new Button("Nhập danh sách");
			importBtn.setOnAction(e -> importDanhSach());

			centerContainer.getChildren()
					.addAll(title, importBtn, listBox);

		}
		catch(DataAccessException e) {
			UiUtils.showAlert("Lỗi hệ thống",
					e.getMessage(),
					Alert.AlertType.ERROR);
		}
	}

	private VBox createCard(DanhSachLopDto dto)
	{
		Label ten = new Label("Tên: " + dto.getTenDanhSach());
		Label hocKy = new Label("Học kỳ: " + dto.getHocKyId());

		Button view = new Button("Xem chi tiết");
		Button edit = new Button("Chỉnh sửa");
		Button delete = new Button("Xóa");

		view.setOnAction(e -> viewChiTiet(dto.getId()));
		edit.setOnAction(e -> editDanhSach(dto));
		delete.setOnAction(e -> deleteDanhSach(dto.getId()));

		HBox actions = new HBox(10, view, edit, delete);

		VBox card = new VBox(5, ten, hocKy, actions);
		card.setStyle("-fx-padding:10; -fx-border-color:#ccc;");

		return card;
	}

	private void viewChiTiet(Long id)
	{
		try {
			DanhSachLopDto dto = screenManager
					.getQuanTriDanhSachLopUseCase()
					.getChiTiet(id);

			centerContainer.getChildren().clear();

			Label title = new Label("Chi tiết: " + dto.getTenDanhSach());
			title.getStyleClass().add("home-title");

			VBox listBox = new VBox(8);

			if(dto.getChiTiet() == null
					|| dto.getChiTiet().isEmpty()) {

				listBox.getChildren()
						.add(new Label("Không có lớp học phần."));
			}
			else {
				for(DanhSachLopChiTietDto ct : dto.getChiTiet()) {

					Label row = new Label(
							ct.getMaLop() + " - " +
									ct.getTenHocPhan() + " - " +
									ct.getTenGiangVien());

					listBox.getChildren().add(row);
				}
			}

			Button back = new Button("Quay lại");
			back.setOnAction(e -> showDanhSachCongKhai());

			centerContainer.getChildren()
					.addAll(title, listBox, back);

		}
		catch(Exception ex) {
			UiUtils.showAlert("Lỗi",
					ex.getMessage(),
					Alert.AlertType.ERROR);
		}
	}

	private void editDanhSach(DanhSachLopDto dto)
	{
		centerContainer.getChildren().clear();

		Label title = new Label("Chỉnh sửa danh sách");
		title.getStyleClass().add("home-title");

		TextField tenField = new TextField(dto.getTenDanhSach());

		TextField hocKyField = new TextField(
				dto.getHocKyId() != null ? dto.getHocKyId().toString() : "");

		Button save = new Button("Lưu");
		save.setOnAction(e -> {
			try {
				UpdateDanhSachLopRequestDto req = new UpdateDanhSachLopRequestDto(
						dto.getId(),
						tenField.getText(),
						Long.valueOf(hocKyField.getText()),
						null);

				screenManager
						.getQuanTriDanhSachLopUseCase()
						.updateDanhSach(req);

				UiUtils.showAlert("Thành công",
						"Đã cập nhật.",
						Alert.AlertType.INFORMATION);

				showDanhSachCongKhai();
			}
			catch(Exception ex) {
				UiUtils.showAlert("Lỗi",
						ex.getMessage(),
						Alert.AlertType.ERROR);
			}
		});

		Button cancel = new Button("Hủy");

		cancel.setOnAction(e -> showDanhSachCongKhai());

		centerContainer.getChildren()
				.addAll(title,
						new Label("Tên"),
						tenField,
						new Label("Học kỳ"),
						hocKyField,
						new HBox(10, save, cancel));
	}

	private void deleteDanhSach(Long id)
	{
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

		confirm.setContentText("Xác nhận xóa?");

		Optional<ButtonType> result = confirm.showAndWait();

		if(result.isPresent()
				&& result.get() == ButtonType.OK) {

			try {
				screenManager
						.getQuanTriDanhSachLopUseCase()
						.deleteDanhSach(id);

				showDanhSachCongKhai();
			}
			catch(Exception ex) {
				UiUtils.showAlert("Lỗi",
						ex.getMessage(),
						Alert.AlertType.ERROR);
			}
		}
	}

	private void importDanhSach()
	{
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Chọn file Excel");

		File file = chooser.showOpenDialog(centerContainer.getScene().getWindow());

		if(file == null) return;

		try {
			NguoiDungDto user = screenManager.getCurrentUser();

			ImportDanhSachLopRequestDto req = new ImportDanhSachLopRequestDto(
					file.getAbsolutePath(),
					"Danh sách hệ thống",
					user.getId(),
					null,
					true);

			screenManager
					.getQuanTriDanhSachLopUseCase()
					.importDanhSach(req);

			UiUtils.showAlert("Thành công",
					"Đã nhập danh sách.",
					Alert.AlertType.INFORMATION);

			showDanhSachCongKhai();
		}
		catch(Exception ex) {
			UiUtils.showAlert("Lỗi",
					ex.getMessage(),
					Alert.AlertType.ERROR);
		}
	}

	private boolean isAdmin()
	{
		if(!screenManager.isAuthenticated()) {
			UiUtils.showAlert("Chưa đăng nhập",
					"Bạn cần đăng nhập.",
					Alert.AlertType.WARNING);
			return false;
		}

		NguoiDungDto user = screenManager.getCurrentUser();

		if(!"ADMIN".equalsIgnoreCase(user.getVaiTro())) {
			UiUtils.showAlert("Không có quyền",
					"Chỉ quản trị viên mới được truy cập.",
					Alert.AlertType.WARNING);
			return false;
		}

		return true;
	}
}
