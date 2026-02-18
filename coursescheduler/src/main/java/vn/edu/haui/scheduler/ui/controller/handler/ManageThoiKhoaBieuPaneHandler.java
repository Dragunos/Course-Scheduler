package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.port.in.ManageThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.util.List;
import java.util.Optional;

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

		ManageThoiKhoaBieuUseCase useCase = screenManager.getQuanLyThoiKhoaBieuUseCase();
		if(useCase == null) {
			UiUtils.showAlert("Lỗi cấu hình",
					"Tính năng quản lý thời khóa biểu chưa được cấu hình.",
					Alert.AlertType.ERROR);
			return;
		}

		centerContainer.getChildren().clear();

		Label title = new Label("Thời khóa biểu của tôi");
		title.getStyleClass().add("home-title");

		VBox listBox = new VBox(10);

		try {
			Long userId = screenManager.getCurrentUser().getId();
			List<ThoiKhoaBieuDto> list = useCase.getAllThoiKhoaBieuByNguoiDungId(userId);

			if(list == null || list.isEmpty()) {
				listBox.getChildren().add(new Label("Không có thời khóa biểu nào."));
			}
			else {
				for(ThoiKhoaBieuDto dto : list) {
					listBox.getChildren().add(createCard(dto));
				}
			}

			centerContainer.getChildren().addAll(title, listBox);

		}
		catch(Exception e) {
			UiUtils.showAlert("Lỗi hệ thống", e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private VBox createCard(ThoiKhoaBieuDto dto)
	{
		String ten = dto.getTenPhuongAn() != null ? dto.getTenPhuongAn() : "<không tên>";

		Label tenLabel = new Label("Tên: " + ten);

		Button viewBtn = new Button("Xem chi tiết");
		Button renameBtn = new Button("Đổi tên");
		Button deleteBtn = new Button("Xóa");

		viewBtn.setOnAction(e -> viewChiTiet(dto));
		renameBtn.setOnAction(e -> doiTen(dto));
		deleteBtn.setOnAction(e -> xoa(dto));

		Button exportBtn = new Button("Xuất");
		exportBtn.setOnAction(
				e -> new ExportThoiKhoaBieuPaneHandler(screenManager, centerContainer).showExportPane(dto));

		HBox actions = new HBox(10, viewBtn, renameBtn, deleteBtn, exportBtn);

		VBox card = new VBox(5, tenLabel, actions);
		card.setStyle("-fx-padding:10; -fx-border-color:#ccc;");

		return card;
	}

	private void viewChiTiet(ThoiKhoaBieuDto dto)
	{
		if(!isAuthenticated()) return;

		centerContainer.getChildren().clear();

		try {
			Long userId = screenManager.getCurrentUser().getId();
			ManageThoiKhoaBieuUseCase useCase = screenManager.getQuanLyThoiKhoaBieuUseCase();

			ThoiKhoaBieuDto detail = useCase.getThoiKhoaBieuById(dto.getId(), userId);

			Label title = new Label(
					"Chi tiết: " + (dto.getTenPhuongAn() != null ? dto.getTenPhuongAn() : "<không tên>"));
			title.getStyleClass().add("home-title");

			VBox listBox = new VBox(8);

			if(detail == null || detail.getDanhSachLopHocPhan() == null || detail.getDanhSachLopHocPhan().isEmpty()) {
				listBox.getChildren().add(new Label("Không có lớp học phần."));
			}
			else {
				for(LopHocPhanDto lh : detail.getDanhSachLopHocPhan()) {
					String rowText = lh.getMaLop();
					if(lh.getGiangVien() != null) rowText += " - " + lh.getGiangVien().getTenGiangVien();
					if(lh.getLichHocDanhSach() != null && !lh.getLichHocDanhSach().isEmpty()) {
						rowText += " - " + lh.getLichHocDanhSach().size() + " buổi";
					}
					Label row = new Label(rowText);
					listBox.getChildren().add(row);
				}
			}

			Button backBtn = new Button("Quay lại");

			Button exportBtn = new Button("Xuất thời khóa biểu");
			exportBtn.setOnAction(
					e -> new ExportThoiKhoaBieuPaneHandler(screenManager, centerContainer).showExportPane(dto));
			centerContainer.getChildren().addAll(title, listBox, new HBox(10, exportBtn, backBtn));

			backBtn.setOnAction(e -> showThoiKhoaBieu());

			centerContainer.getChildren().addAll(title, listBox, backBtn);

		}
		catch(Exception ex) {
			UiUtils.showAlert("Lỗi", ex.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void doiTen(ThoiKhoaBieuDto dto)
	{
		if(!isAuthenticated()) return;

		centerContainer.getChildren().clear();

		Label title = new Label("Đổi tên thời khóa biểu");
		title.getStyleClass().add("home-title");

		TextField tenField = new TextField(dto.getTenPhuongAn());

		Button saveBtn = new Button("Lưu");
		saveBtn.setOnAction(e -> {
			try {
				Long userId = screenManager.getCurrentUser().getId();
				ManageThoiKhoaBieuUseCase useCase = screenManager.getQuanLyThoiKhoaBieuUseCase();

				ThoiKhoaBieuDto updated = useCase.updateTenThoiKhoaBieu(dto.getId(), userId, tenField.getText());

				UiUtils.showAlert("Thành công", "Đã cập nhật tên.", Alert.AlertType.INFORMATION);

				showThoiKhoaBieu();
			}
			catch(Exception ex) {
				UiUtils.showAlert("Lỗi", ex.getMessage(), Alert.AlertType.ERROR);
			}
		});

		Button cancelBtn = new Button("Hủy");
		cancelBtn.setOnAction(e -> showThoiKhoaBieu());

		centerContainer.getChildren().addAll(title, new Label("Tên mới"), tenField, new HBox(10, saveBtn, cancelBtn));
	}

	private void xoa(ThoiKhoaBieuDto dto)
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
				screenManager.getQuanLyThoiKhoaBieuUseCase().deleteThoiKhoaBieu(dto.getId(), userId);

				UiUtils.showAlert("Thành công", "Đã xóa.", Alert.AlertType.INFORMATION);

				showThoiKhoaBieu();

			}
			catch(Exception ex) {
				UiUtils.showAlert("Lỗi", ex.getMessage(), Alert.AlertType.ERROR);
			}
		}
	}

	private boolean isAuthenticated()
	{
		if(screenManager == null || !screenManager.isAuthenticated()) {
			UiUtils.showAlert("Chưa đăng nhập", "Bạn cần đăng nhập.", Alert.AlertType.WARNING);
			return false;
		}
		return true;
	}
}
