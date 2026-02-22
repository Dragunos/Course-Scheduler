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
import vn.edu.haui.scheduler.application.port.in.GenerateThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.in.ManageDanhSachLopUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

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
			UiUtils.showAlert("Chưa đăng nhập",
					"Bạn cần đăng nhập.",
					Alert.AlertType.WARNING);
			return;
		}

		GenerateThoiKhoaBieuUseCase useCase = screenManager.getSinhThoiKhoaBieuUseCase();
		ManageDanhSachLopUseCase dsUseCase = screenManager.getQuanLyDanhSachLopUseCase();

		if(useCase == null || dsUseCase == null) {
			UiUtils.showAlert("Lỗi cấu hình",
					"Tính năng sinh thời khóa biểu chưa được cấu hình.",
					Alert.AlertType.ERROR);
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
			dsList = dsUseCase.findAllByUser(userId);
		}
		catch(Exception ex) {
			UiUtils.showAlert("Lỗi hệ thống", ex.getMessage(), Alert.AlertType.ERROR);
			return;
		}

		if(dsList == null || dsList.isEmpty()) {
			content.getChildren().add(new Label(
					"Bạn chưa có danh sách lớp. Hãy tạo hoặc import danh sách trước."));
			centerContainer.getChildren().addAll(title, content);
			return;
		}

		ChoiceBox<DanhSachLopDto> choice = new ChoiceBox<>();
		choice.getItems().addAll(dsList);
		choice.setPrefWidth(500);

		choice.setConverter(new javafx.util.StringConverter<>()
		{
			@Override
			public String toString(DanhSachLopDto object)
			{
				if(object == null) return "";
				return object.getTenDanhSach() != null
						? object.getTenDanhSach()
						: "#" + object.getId();
			}

			@Override
			public DanhSachLopDto fromString(String string)
			{
				return null;
			}
		});

		TextField topKField = new TextField("5");
		topKField.setPromptText("Top-K (ví dụ 5)");

		Button runBtn = new Button("Chạy tối ưu");

		HBox controls = new HBox(10,
				new Label("Danh sách:"), choice,
				new Label("Top-K:"), topKField,
				runBtn);

		VBox resultBox = new VBox(8);

		runBtn.setOnAction(e -> {
			DanhSachLopDto selected = choice.getValue();

			if(selected == null) {
				UiUtils.showAlert("Không hợp lệ",
						"Chưa chọn danh sách.",
						Alert.AlertType.WARNING);
				return;
			}

			int topK;

			try {
				topK = Integer.parseInt(topKField.getText().trim());
			}
			catch(Exception ex) {
				UiUtils.showAlert("Không hợp lệ",
						"Top-K phải là số nguyên.",
						Alert.AlertType.WARNING);
				return;
			}

			try {
				Long userId = screenManager.getCurrentUser().getId();

				// ⭐ Create requirement + generate solution graph
				long yeuCauId = screenManager
						.getSinhThoiKhoaBieuUseCase()
						.generate(userId, selected.getId(), topK)
						.stream()
						.findFirst()
						.map(ThoiKhoaBieuDto::getId)
						.orElseThrow();

				List<ThoiKhoaBieuDto> solutions = screenManager.getSinhThoiKhoaBieuUseCase()
						.generate(userId, selected.getId(), topK);

				resultBox.getChildren().clear();

				if(solutions == null || solutions.isEmpty()) {
					resultBox.getChildren().add(
							new Label("Không tìm được phương án hợp lệ."));
				}
				else {
					int idx = 1;

					for(ThoiKhoaBieuDto pa : solutions) {
						VBox card = renderSolutionCard(pa,
								screenManager.getSinhThoiKhoaBieuUseCase(),
								selected);

						Label header = new Label(
								"Phương án " + (idx++)
										+ " - Điểm: " + pa.getDiemDanhGia());

						card.getChildren().add(0, header);
						resultBox.getChildren().add(card);
					}
				}
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
			catch(Exception ex) {
				UiUtils.showAlert("Lỗi",
						ex.getMessage(),
						Alert.AlertType.ERROR);
			}
		});

		content.getChildren().addAll(controls, new Separator(), resultBox);
		centerContainer.getChildren().addAll(title, content);
	}

	private VBox renderSolutionCard(
			ThoiKhoaBieuDto pa,
			GenerateThoiKhoaBieuUseCase useCase,
			DanhSachLopDto danhSach)
	{
		VBox card = new VBox(6);
		card.setStyle("-fx-padding:8; -fx-border-color:#ddd; -fx-background-color:#fff;");

		StringBuilder sb = new StringBuilder();

		if(pa.getDanhSachLopHocPhan() != null) {
			for(LopHocPhanDto lop : pa.getDanhSachLopHocPhan()) {
				sb.append(lop.getMaLop()).append(", ");
			}

			if(sb.length() > 2)
				sb.setLength(sb.length() - 2);
		}
		else sb.append("<rỗng>");

		Label body = new Label(sb.toString());

		Button saveBtn = new Button("Lưu phương án");

		saveBtn.setOnAction(e -> {
			try {
				List<ThoiKhoaBieuDto> list = new ArrayList<>();
				list.add(pa);

				useCase.saveAll(
						screenManager.getCurrentUser().getId(),
						list,
						false);

				UiUtils.showAlert("Thành công",
						"Đã lưu phương án.",
						Alert.AlertType.INFORMATION);
			}
			catch(Exception ex) {
				UiUtils.showAlert("Lỗi khi lưu",
						ex.getMessage(),
						Alert.AlertType.ERROR);
			}
		});

		card.getChildren().addAll(body, saveBtn);
		return card;
	}
}