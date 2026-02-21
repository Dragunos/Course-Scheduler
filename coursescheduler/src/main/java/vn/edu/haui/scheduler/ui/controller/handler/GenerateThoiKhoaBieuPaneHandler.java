package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.LopHocPhanDto;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ManageDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.in.GenerateThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenerateThoiKhoaBieuPaneHandler
{
	private final ScreenManager screenManager;

	private final VBox centerContainer;

	public GenerateThoiKhoaBieuPaneHandler(ScreenManager screenManager, VBox centerContainer)
	{
		this.screenManager = screenManager;
		this.centerContainer = centerContainer;
	}

	public void showOptimizerPane()
	{
		if(screenManager == null || !screenManager.isAuthenticated()) {
			UiUtils.showAlert("Chưa đăng nhập", "Bạn cần đăng nhập.", Alert.AlertType.WARNING);
			return;
		}

		GenerateThoiKhoaBieuUseCase useCase = screenManager.getSinhThoiKhoaBieuUseCase();
		if(useCase == null) {
			UiUtils.showAlert("Lỗi cấu hình", "Tính năng sinh thời khóa biểu chưa được cấu hình.",
					Alert.AlertType.ERROR);
			return;
		}

		ManageDanhSachLopUseCase dsUseCase = screenManager.getQuanLyDanhSachLopUseCase();
		if(dsUseCase == null) {
			UiUtils.showAlert("Lỗi cấu hình", "Tính năng danh sách lớp chưa được cấu hình.", Alert.AlertType.ERROR);
			return;
		}

		centerContainer.getChildren().clear();

		Label title = new Label("Sinh thời khóa biểu tối ưu");
		title.getStyleClass().add("home-title");

		VBox content = new VBox(10);
		content.setPadding(new Insets(10));

		List<DanhSachLopDto> dsList;
		try {
			Long userId = screenManager.getCurrentUser().getId();
			dsList = dsUseCase.getAllDanhSachLopByNguoiDungId(userId);
		}
		catch(Exception ex) {
			UiUtils.showAlert("Lỗi hệ thống", ex.getMessage(), Alert.AlertType.ERROR);
			return;
		}

		if(dsList == null || dsList.isEmpty()) {
			content.getChildren()
					.add(new Label("Bạn không có danh sách nào. Vui lòng import hoặc tạo danh sách trước."));
			centerContainer.getChildren().addAll(title, content);
			return;
		}

		ChoiceBox<DanhSachLopDto> choice = new ChoiceBox<>();
		choice.getItems().addAll(dsList);
		choice.setPrefWidth(600);

		choice.setConverter(new javafx.util.StringConverter<>()
		{
			@Override
			public String toString(DanhSachLopDto object)
			{
				return object == null ? ""
						: (object.getTenDanhSach() != null ? object.getTenDanhSach() : ("#" + object.getId()));
			}

			@Override
			public DanhSachLopDto fromString(String string)
			{
				return null;
			}
		});

		TextField topKField = new TextField("5");
		topKField.setPromptText("top-K (ví dụ 5)");

		TextField timeLimitField = new TextField("5000");
		timeLimitField.setPromptText("time limit (ms)");

		Button runBtn = new Button("Chạy tối ưu");

		HBox controls = new HBox(10, new Label("Chọn danh sách:"), choice, new Label("Top-K:"), topKField,
				new Label("Time(ms):"), timeLimitField, runBtn);
		controls.setPadding(new Insets(6));

		VBox resultBox = new VBox(8);

		runBtn.setOnAction(e -> {
			DanhSachLopDto selected = choice.getValue();
			if(selected == null) {
				UiUtils.showAlert("Không hợp lệ", "Chưa chọn danh sách lớp.", Alert.AlertType.WARNING);
				return;
			}

			int topK;
			long timeLimit;
			try {
				topK = Integer.parseInt(topKField.getText().trim());
				timeLimit = Long.parseLong(timeLimitField.getText().trim());
			}
			catch(NumberFormatException nfe) {
				UiUtils.showAlert("Không hợp lệ", "Top-K và Time phải là số.", Alert.AlertType.WARNING);
				return;
			}

			try {
				Long userId = screenManager.getCurrentUser().getId();

				long yeuCauId = useCase.createYeuCau(userId, selected.getId());

				List<ThoiKhoaBieuDto> solutions = useCase.generateThoiKhoaBieu(yeuCauId, topK, timeLimit);

				resultBox.getChildren().clear();

				if(solutions == null || solutions.isEmpty()) {
					resultBox.getChildren().add(new Label("Không tìm được phương án hợp lệ."));
				}
				else {
					int idx = 1;
					for(ThoiKhoaBieuDto pa : solutions) {
						VBox card = new VBox(6);
						card.setStyle("-fx-padding:8; -fx-border-color:#ddd; -fx-background-color:#fafafa;");

						Label header = new Label("Phương án " + (idx++) + " - Điểm: " + pa.getDiemDanhGia());
						header.getStyleClass().add("home-subtitle");

						StringBuilder sb = new StringBuilder();
						if(pa.getDanhSachLopHocPhan() != null && !pa.getDanhSachLopHocPhan().isEmpty()) {
							for(LopHocPhanDto lop : pa.getDanhSachLopHocPhan()) {
								sb.append(lop.getMaLop()).append(", ");
							}
							if(sb.length() > 2) sb.setLength(sb.length() - 2);
						}
						else sb.append("<rỗng>");

						Label body = new Label(sb.toString());

						Button saveBtn = new Button("Lưu phương án");
						Button optimizeAgainBtn = new Button("Tối ưu lại phương án");

						HBox actions = new HBox(8, saveBtn, optimizeAgainBtn);

						saveBtn.setOnAction(ev -> {
							try {
								String tenPA = "PA " + System.currentTimeMillis();
								List<Long> ids = new ArrayList<>();
								if(pa.getDanhSachLopHocPhan() != null) {
									for(LopHocPhanDto lop : pa.getDanhSachLopHocPhan()) {
										ids.add(lop.getId());
									}
								}

								long savedId = useCase.saveThoiKhoaBieu(screenManager.getCurrentUser().getId(),
										selected.getId(), tenPA, pa.getDiemDanhGia(), ids);

								UiUtils.showAlert("Thành công", "Đã lưu phương án (id=" + savedId + ")",
										Alert.AlertType.INFORMATION);
							}
							catch(Exception ex) {
								UiUtils.showAlert("Lỗi khi lưu", ex.getMessage(), Alert.AlertType.ERROR);
							}
						});

						optimizeAgainBtn.setOnAction(ev -> {
							try {
								List<ThoiKhoaBieuDto> again = useCase.generateThoiKhoaBieu(yeuCauId, topK, timeLimit);
								resultBox.getChildren().clear();
								for(ThoiKhoaBieuDto pa2 : again) {
									resultBox.getChildren().add(renderSolutionNode(pa2, useCase, selected));
								}
							}
							catch(Exception ex) {
								UiUtils.showAlert("Lỗi", ex.getMessage(), Alert.AlertType.ERROR);
							}
						});

						card.getChildren().addAll(header, body, actions);
						resultBox.getChildren().add(card);
					}
				}
			}
			catch(ValidationException ve) {
				UiUtils.showAlert("Không hợp lệ", ve.getMessage(), Alert.AlertType.WARNING);
			}
			catch(DataAccessException pe) {
				UiUtils.showAlert("Lỗi hệ thống", pe.getMessage(), Alert.AlertType.ERROR);
			}
			catch(Exception ex) {
				UiUtils.showAlert("Lỗi", ex.getMessage(), Alert.AlertType.ERROR);
			}
		});

		content.getChildren().addAll(controls, new Separator(), resultBox);

		centerContainer.getChildren().addAll(title, content);
	}

	private Node renderSolutionNode(ThoiKhoaBieuDto pa, GenerateThoiKhoaBieuUseCase useCase,
			DanhSachLopDto danhSach)
	{
		VBox card = new VBox(6);
		card.setStyle("-fx-padding:8; -fx-border-color:#ddd; -fx-background-color:#fff;");

		Label header = new Label("Điểm: " + pa.getDiemDanhGia());
		StringBuilder sb = new StringBuilder();
		if(pa.getDanhSachLopHocPhan() != null) {
			for(LopHocPhanDto lop : pa.getDanhSachLopHocPhan()) {
				sb.append(lop.getId()).append(", ");
			}
			if(sb.length() > 2) sb.setLength(sb.length() - 2);
		}
		Label body = new Label(sb.length() == 0 ? "<rỗng>" : sb.toString());

		Button saveBtn = new Button("Lưu");
		saveBtn.setOnAction(e -> {
			try {
				List<Long> ids = new ArrayList<>();
				if(pa.getDanhSachLopHocPhan() != null) {
					for(LopHocPhanDto lop : pa.getDanhSachLopHocPhan()) {
						ids.add(lop.getId());
					}
				}

				long saved = useCase.saveThoiKhoaBieu(screenManager.getCurrentUser().getId(),
						danhSach.getId(),
						"PA " + System.currentTimeMillis(),
						pa.getDiemDanhGia(),
						ids);
				UiUtils.showAlert("Thành công", "Đã lưu (id=" + saved + ")", Alert.AlertType.INFORMATION);
			}
			catch(Exception ex) {
				UiUtils.showAlert("Lỗi khi lưu", ex.getMessage(), Alert.AlertType.ERROR);
			}
		});

		card.getChildren().addAll(header, body, saveBtn);
		return card;
	}
}
