package vn.edu.haui.scheduler.ui.controller.handler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.LopHocPhanDto;
import vn.edu.haui.scheduler.application.dto.RangBuocToiUuDto;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;
import vn.edu.haui.scheduler.application.exception.ApplicationException;
import vn.edu.haui.scheduler.application.port.in.GenerateThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.in.ManageDanhSachLopUseCase;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.util.UiUtils;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GenerateThoiKhoaBieuPaneHandler
{
	private final ScreenManager screenManager;

	private final VBox centerContainer;

	public GenerateThoiKhoaBieuPaneHandler(
			ScreenManager screenManager,
			VBox centerContainer)
	{
		this.screenManager = screenManager;
		this.centerContainer = centerContainer;
	}

	public void showOptimizerPane()
	{
		centerContainer.getChildren().clear();
		centerContainer.setPadding(new Insets(12));
		Label title = new Label("Sinh thời khóa biểu tối ưu");
		title.getStyleClass().add("home-title");

		HBox row1 = new HBox(8);
		row1.setPadding(new Insets(8));

		Label danhSachLabel = new Label("Danh sách lớp (ID hoặc chọn):");
		TextField danhSachIdField = new TextField();
		danhSachIdField.setPromptText("Nhập ID của Danh Sách Lớp nếu không load được danh sách");

		ComboBox<DanhSachLopDto> danhSachCombo = new ComboBox<>();
		danhSachCombo.setPrefWidth(420);
		danhSachCombo.setConverter(new StringConverter<>()
		{
			@Override
			public String toString(DanhSachLopDto object)
			{
				if(object == null) return "";
				try {
					Method m = object.getClass().getMethod("getTenDanhSach");
					Object v = m.invoke(object);
					return String.valueOf(v) + " (id=" + safeInvoke(object, "getId") + ")";
				}
				catch(Exception e) {
					return "DanhSachLop@" + safeInvoke(object, "getId");
				}
			}

			@Override
			public DanhSachLopDto fromString(String string)
			{
				return null;
			}
		});

		Button loadDanhSachBtn = new Button("Tải danh sách của bạn");
		loadDanhSachBtn.setOnAction(evt -> {
			try {
				List<DanhSachLopDto> lists = tryFetchDanhSachLopList();
				if(lists == null || lists.isEmpty()) {
					UiUtils.showAlert("Info", "Không tìm thấy danh sách lớp cho user", Alert.AlertType.INFORMATION);
					return;
				}
				System.out.println("(danhSachCombo.setItems) Danh sách load được: " + lists.size());
				
				danhSachCombo.setItems(FXCollections.observableArrayList(lists));
			}
			catch(Exception ex) {
				UiUtils.showException("Lỗi khi tải danh sách", ex);
			}
		});

		row1.getChildren().addAll(danhSachLabel, danhSachCombo, danhSachIdField, loadDanhSachBtn);

		HBox row2 = new HBox(8);
		row2.setPadding(new Insets(8));
		Label maHpLabel = new Label("Mã học phần cần đăng ký (CSV):");
		TextField maHpField = new TextField();
		maHpField.setPromptText("VD: FL6130,FL6131");
		row2.getChildren().addAll(maHpLabel, maHpField);

		HBox row3 = new HBox(8);
		row3.setPadding(new Insets(8));
		Label optionsLabel = new Label("Tùy chọn tối ưu (ví dụ):");

		CheckBox avoidOnline = new CheckBox("Không muốn Online");
		CheckBox want0003 = new CheckBox("Muốn lớp 20253FL6130003");
		CheckBox want0005 = new CheckBox("Muốn lớp 20253FL6130005");
		CheckBox avoidTiet12to15 = new CheckBox("Không muốn tiết 12-15");
		CheckBox avoidThu3 = new CheckBox("Không muốn ngày thứ 3");

		row3.getChildren().addAll(optionsLabel, avoidOnline, want0003, want0005, avoidTiet12to15, avoidThu3);

		HBox row4 = new HBox(8);
		row4.setPadding(new Insets(8));
		Label topKLabel = new Label("Số phương án (topK):");
		TextField topKField = new TextField("5");
		topKField.setPrefWidth(60);
		Label timeLimitLabel = new Label("Giới hạn (ms):");
		TextField timeLimitField = new TextField("5000");
		timeLimitField.setPrefWidth(100);

		row4.getChildren().addAll(topKLabel, topKField, timeLimitLabel, timeLimitField);

		HBox row5 = new HBox(8);
		row5.setPadding(new Insets(8));
		Button runBtn = new Button("Chạy tối ưu");
		Button clearBtn = new Button("Xóa");

		row5.getChildren().addAll(runBtn, clearBtn);

		VBox resultsContainer = new VBox(8);
		resultsContainer.setPadding(new Insets(8));

		runBtn.setOnAction(evt -> {
			try {
				if(screenManager.getCurrentUser() == null) {
					UiUtils.showAlert("Cần đăng nhập", "Vui lòng đăng nhập trước khi sinh thời khóa biểu",
							Alert.AlertType.WARNING);
					return;
				}
				Long nguoiDungId = safeInvokeLong(screenManager.getCurrentUser(), "getId");
				Long danhSachId = null;
				if(danhSachCombo.getValue() != null) {
					danhSachId = safeInvokeLong(danhSachCombo.getValue(), "getId");
				}
				else if(!danhSachIdField.getText().isBlank()) {
					try {
						danhSachId = Long.parseLong(danhSachIdField.getText().trim());
					}
					catch(NumberFormatException nfe) {
						UiUtils.showAlert("Lỗi", "danh_sach_lop_id không hợp lệ", Alert.AlertType.ERROR);
						return;
					}
				}
				if(danhSachId == null) {
					UiUtils.showAlert("Thiếu input", "Phải cung cấp danh_sach_lop_id hoặc chọn danh sách",
							Alert.AlertType.WARNING);
					return;
				}
				String maHpCsv = maHpField.getText().trim();
				List<String> maHocPhanDangKy = new ArrayList<>();
				if(!maHpCsv.isBlank()) {
					for(String s : maHpCsv.split(",")) {
						String t = s.trim();
						if(!t.isEmpty()) maHocPhanDangKy.add(t);
					}
				}
				if(maHocPhanDangKy.isEmpty()) {
					UiUtils.showAlert("Thiếu học phần", "Vui lòng nhập tối thiểu một mã học phần cần đăng ký",
							Alert.AlertType.WARNING);
					return;
				}
				int topK;
				long timeLimit;
				try {
					topK = Integer.parseInt(topKField.getText().trim());
				}
				catch(Exception e) {
					UiUtils.showAlert("Lỗi", "topK không hợp lệ", Alert.AlertType.ERROR);
					return;
				}
				try {
					timeLimit = Long.parseLong(timeLimitField.getText().trim());
				}
				catch(Exception e) {
					UiUtils.showAlert("Lỗi", "timeLimit không hợp lệ", Alert.AlertType.ERROR);
					return;
				}

				List<RangBuocToiUuDto> constraints = new ArrayList<>();
				if(avoidOnline.isSelected()) {
					RangBuocToiUuDto r = new RangBuocToiUuDto();
					try {
						r.setLaCung(false);
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setLoaiRangBuoc", String.class).invoke(r, "AVOID_MODE");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setAttribute", String.class).invoke(r, "hinh_thuc_day");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setOperator", String.class).invoke(r, "=");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setValue", String.class).invoke(r, "ONLINE");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setTrongSo", Double.class).invoke(r, 2.0);
					}
					catch(Exception ignore) {
					}
					constraints.add(r);
				}
				if(want0003.isSelected()) {
					RangBuocToiUuDto r = new RangBuocToiUuDto();
					try {
						r.setLaCung(false);
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setLoaiRangBuoc", String.class).invoke(r, "PREFER_SECTION");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setTargetType", String.class).invoke(r, "SECTION");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setTargetValue", String.class).invoke(r, "20253FL6130003");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setTrongSo", Double.class).invoke(r, 5.0);
					}
					catch(Exception ignore) {
					}
					constraints.add(r);
				}
				if(want0005.isSelected()) {
					RangBuocToiUuDto r = new RangBuocToiUuDto();
					try {
						r.setLaCung(false);
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setLoaiRangBuoc", String.class).invoke(r, "PREFER_SECTION");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setTargetType", String.class).invoke(r, "SECTION");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setTargetValue", String.class).invoke(r, "20253FL6130005");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setTrongSo", Double.class).invoke(r, 5.0);
					}
					catch(Exception ignore) {
					}
					constraints.add(r);
				}
				if(avoidTiet12to15.isSelected()) {
					RangBuocToiUuDto r = new RangBuocToiUuDto();
					try {
						r.setLaCung(false);
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setLoaiRangBuoc", String.class).invoke(r, "AVOID_TIME");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setAttribute", String.class).invoke(r, "tiet");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setOperator", String.class).invoke(r, "BETWEEN");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setValue", String.class).invoke(r, "12-15");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setTrongSo", Double.class).invoke(r, 3.0);
					}
					catch(Exception ignore) {
					}
					constraints.add(r);
				}
				if(avoidThu3.isSelected()) {
					RangBuocToiUuDto r = new RangBuocToiUuDto();
					try {
						r.setLaCung(false); 
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setLoaiRangBuoc", String.class).invoke(r, "AVOID_DAY");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setAttribute", String.class).invoke(r, "thu");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setOperator", String.class).invoke(r, "=");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setValue", String.class).invoke(r, "3");
					}
					catch(Exception ignore) {
					}
					try {
						r.getClass().getMethod("setTrongSo", Double.class).invoke(r, 4.0);
					}
					catch(Exception ignore) {
					}
					constraints.add(r);
				}

				GenerateThoiKhoaBieuUseCase useCase = screenManager.getGenerateThoiKhoaBieuUseCase();
				if(useCase == null) {
					UiUtils.showAlert("Lỗi", "UseCase GenerateThoiKhoaBieu chưa được khởi tạo", Alert.AlertType.ERROR);
					return;
				}

				List<ThoiKhoaBieuDto> results = useCase.generate(nguoiDungId, danhSachId, maHocPhanDangKy, constraints,
						topK, timeLimit);
				renderResults(results, useCase, resultsContainer);
			}
			catch(ApplicationException appEx) {
				UiUtils.showException("Lỗi nghiệp vụ", appEx);
			}
			catch(Exception ex) {
				UiUtils.showException("Lỗi khi chạy tối ưu", ex);
			}
		});

		clearBtn.setOnAction(evt -> {
			danhSachCombo.getItems().clear();
			danhSachIdField.clear();
			maHpField.clear();
			avoidOnline.setSelected(false);
			want0003.setSelected(false);
			want0005.setSelected(false);
			avoidTiet12to15.setSelected(false);
			avoidThu3.setSelected(false);
			topKField.setText("5");
			timeLimitField.setText("5000");
			resultsContainer.getChildren().clear();
		});

		centerContainer.getChildren().addAll(title, row1, row2, row3, row4, row5, new Separator(), resultsContainer);
	}

	private void renderResults(
			List<ThoiKhoaBieuDto> list,
			GenerateThoiKhoaBieuUseCase useCase,
			VBox container)
	{
		container.getChildren().clear();
		if(list == null || list.isEmpty()) {
			Label none = new Label("Không có phương án nào được sinh ra.");
			container.getChildren().add(none);
			return;
		}

		ObservableList<ThoiKhoaBieuDto> items = FXCollections.observableArrayList(list);
		ListView<ThoiKhoaBieuDto> lv = new ListView<>(items);
		lv.setCellFactory(param -> new ListCell<>()
		{
			@Override
			protected void updateItem(ThoiKhoaBieuDto item, boolean empty)
			{
				super.updateItem(item, empty);
				if(empty || item == null) {
					setText(null);
				}
				else {
					StringBuilder sb = new StringBuilder();
					sb.append("Phương án: ").append(safeInvokeString(item, "getTenPhuongAn")).append("\n");
					sb.append("Điểm: ").append(safeInvokeString(item, "getDiemDanhGia")).append("\n");
					// sb.append("Danh sách lớp: ").append(safeInvokeString(item, "getDanhSachLopHocPhan"));
					
					List<LopHocPhanDto> ds =
						    (List<LopHocPhanDto>) safeInvoke(item, "getDanhSachLopHocPhan");

						if (ds != null && !ds.isEmpty()) {
						    String formatted = ds.stream()
						        .map(l ->
						            l.getTenHocPhan() +
						            " (" + l.getMaLop() + ")"
						        )
						        .reduce((a, b) -> a + "; " + b)
						        .orElse("");

						    sb.append("Danh sách lớp: ").append(formatted);
						}
					
					setText(sb.toString());
				}
			}
		});
		lv.setPrefHeight(260);

		HBox actionRow = new HBox(8);
		Button saveBtn = new Button("Lưu phương án đã chọn");
		CheckBox overwrite = new CheckBox("Ghi đè (overwrite)");
		actionRow.getChildren().addAll(saveBtn, overwrite);

		saveBtn.setOnAction(evt -> {
			List<ThoiKhoaBieuDto> selected = new ArrayList<>(lv.getSelectionModel().getSelectedItems());
			if(selected.isEmpty()) {
				UiUtils.showAlert("Chọn phương án", "Vui lòng chọn ít nhất một phương án để lưu",
						Alert.AlertType.WARNING);
				return;
			}
			try {
				Long nguoiDungId = safeInvokeLong(screenManager.getCurrentUser(), "getId");
				Long danhSachId = null;
				try {
					Object first = selected.get(0);
					danhSachId = (Long) first.getClass().getMethod("getDanhSachLopId").invoke(first);
				}
				catch(Exception ignore) {
				}
				useCase.save(nguoiDungId, danhSachId, selected, overwrite.isSelected());
				UiUtils.showAlert("Thành công", "Đã lưu phương án", Alert.AlertType.INFORMATION);
			}
			catch(Exception ex) {
				UiUtils.showException("Lỗi khi lưu phương án", ex);
			}
		});

		container.getChildren().addAll(lv, actionRow);
	}

	private List<DanhSachLopDto> tryFetchDanhSachLopList() {
	    ManageDanhSachLopUseCase manage = screenManager.getQuanLyDanhSachLopUseCase();
	    if (manage == null) return List.of();

	    Object currentUser = screenManager.getCurrentUser();
	    if (currentUser == null) return List.of();

	    Long userId = safeInvokeLong(currentUser, "getId");
	    if (userId == null) return List.of();

	    return manage.findAllByUser(userId);
	}

	private static String safeInvokeString(Object obj, String getter)
	{
		try {
			Method m = obj.getClass().getMethod(getter);
			Object v = m.invoke(obj);
			return String.valueOf(v);
		}
		catch(Exception e) {
			try {
				Method m2 = obj.getClass().getMethod(convertGetter(getter));
				Object v2 = m2.invoke(obj);
				return String.valueOf(v2);
			}
			catch(Exception ex) {
				return "-";
			}
		}
	}

	private static Object safeInvoke(Object obj, String getter)
	{
		try {
			Method m = obj.getClass().getMethod(getter);
			return m.invoke(obj);
		}
		catch(Exception e) {
			return null;
		}
	}

	private static Long safeInvokeLong(Object obj, String getter)
	{
		try {
			Method m = obj.getClass().getMethod(getter);
			Object v = m.invoke(obj);
			if(v instanceof Number) return ((Number) v).longValue();
			else if(v instanceof String) return Long.parseLong((String) v);
		}
		catch(Exception ignored) {
		}
		try {
			Method m = obj.getClass().getMethod("getId");
			Object v = m.invoke(obj);
			if(v instanceof Number) return ((Number) v).longValue();
		}
		catch(Exception ignored) {
		}
		return null;
	}

	private static String convertGetter(String getter)
	{
		if(!getter.startsWith("get")) {
			if(getter.length() > 0) return "get" + Character.toUpperCase(getter.charAt(0)) + getter.substring(1);
			return getter;
		}
		return getter;
	}
}