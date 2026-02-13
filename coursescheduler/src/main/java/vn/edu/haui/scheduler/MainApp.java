package vn.edu.haui.scheduler;

import javafx.application.Application;
import javafx.stage.Stage;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.application.service.AuthAppService;
import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.service.ImportDanhSachLopAppService;
import vn.edu.haui.scheduler.application.port.in.QuanLyDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.service.QuanLyDanhSachLopAppService;
import vn.edu.haui.scheduler.application.port.in.XuatDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.service.XuatDanhSachLopAppService;
import vn.edu.haui.scheduler.infrastructure.io.exports.CsvExporter;
import vn.edu.haui.scheduler.infrastructure.io.exports.ExcelExporter;
import vn.edu.haui.scheduler.infrastructure.io.imports.ExcelCourseImporter;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.*;
import vn.edu.haui.scheduler.application.port.out.*;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;
import vn.edu.haui.scheduler.infrastructure.security.PasswordHasher;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.fx.config.FxConfig;

public class MainApp extends Application
{
	@Override
	public void start(Stage stage)
	{
		FxConfig.apply(stage);

		AuthUseCase authUseCase = createAuthUseCase();

		ScreenManager screenManager = new ScreenManager(stage, authUseCase);

		ExcelCourseImporter importer = new ExcelCourseImporter();
		HocPhanRepositoryPort hocPhanRepo = new JdbcHocPhanRepository();
		GiangVienRepositoryPort giangVienRepo = new JdbcGiangVienRepository();
		LopHocPhanRepositoryPort lopRepo = new JdbcLopHocPhanRepository();
		LichHocRepositoryPort lichRepo = new JdbcLichHocRepository();
		DanhSachLopRepositoryPort danhSachRepo = new JdbcDanhSachLopRepository();
		TepTaiLenRepositoryPort tepRepo = new JdbcTepTaiLenRepository();

		CsvExporter csvExporter = new CsvExporter();
		ExcelExporter excelExporter = new ExcelExporter();

		ImportDanhSachLopUseCase importUc = new ImportDanhSachLopAppService(
				importer, hocPhanRepo, giangVienRepo, lopRepo, lichRepo, danhSachRepo, tepRepo);

		screenManager.setImportDanhSachLopUseCase(importUc);

		QuanLyDanhSachLopUseCase quanLyUc = new QuanLyDanhSachLopAppService(danhSachRepo);

		screenManager.setQuanLyDanhSachLopUseCase(quanLyUc);

		XuatDanhSachLopUseCase xuatUc = new XuatDanhSachLopAppService(danhSachRepo, csvExporter, excelExporter);

		screenManager.setXuatDanhSachLopUseCase(xuatUc);

		screenManager.init();
		stage.show();
	}

	private AuthUseCase createAuthUseCase()
	{
		return new AuthAppService(
				new JdbcNguoiDungRepository(),
				new JdbcVaiTroRepository(),
				new PasswordHasher());
	}

	@Override
	public void stop()
	{
		DataSourceProvider.shutdown();
	}
}
