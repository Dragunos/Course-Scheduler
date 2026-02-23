package vn.edu.haui.scheduler;

import javafx.application.Application;
import javafx.stage.Stage;

import vn.edu.haui.scheduler.application.port.in.*;
import vn.edu.haui.scheduler.application.service.*;
import vn.edu.haui.scheduler.application.port.out.*;
import vn.edu.haui.scheduler.infrastructure.io.exports.*;
import vn.edu.haui.scheduler.infrastructure.io.imports.ExcelDanhSachLopImporter;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.*;
import vn.edu.haui.scheduler.infrastructure.persistence.config.*;
import vn.edu.haui.scheduler.infrastructure.security.PasswordHasher;

import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.fx.config.FxConfig;

public class MainApp extends Application
{
	private static final String PDF_FONT_PATH = "fonts/SEGOEUI.TTF";

	@Override
	public void start(Stage stage)
	{
		FxConfig.apply(stage);

		CompositeFileExporter fileExporter = new CompositeFileExporter(
				new CsvExporter(),
				new ExcelExporter(),
				new PdfExporter(PDF_FONT_PATH));
		IcsExporter icsExporter = new IcsExporter();

		// ===== REPOSITORIES =====
		NguoiDungRepository nguoiDungRepo = new JdbcNguoiDungRepository();
		VaiTroRepository vaiTroRepo = new JdbcVaiTroRepository();
		HocPhanRepository hocPhanRepo = new JdbcHocPhanRepository();
		GiangVienRepository giangVienRepo = new JdbcGiangVienRepository();
		LopHocPhanRepository lopRepo = new JdbcLopHocPhanRepository();
		DanhSachLopRepository danhSachRepo = new JdbcDanhSachLopRepository();
		TepTaiLenRepository tepRepo = new JdbcTepTaiLenRepository();
		HocKyRepository hocKyRepo = new JdbcHocKyRepository();
		ThoiKhoaBieuRepository thoiKbRepo = new JdbcThoiKhoaBieuRepository();
		YeuCauRepository yeuCauRepo = new JdbcYeuCauRepository();

		// ===== IMPORTER =====
		ExcelDanhSachLopImporter importer = new ExcelDanhSachLopImporter();

		// ===== USE CASES =====

		AuthUseCase authUseCase = new AuthService(
				nguoiDungRepo,
				vaiTroRepo,
				new PasswordHasher());

		ImportDanhSachLopUseCase importDanhSachLopUc = new ImportDanhSachLopService(importer, danhSachRepo, hocPhanRepo,
				giangVienRepo, lopRepo, nguoiDungRepo, hocKyRepo, tepRepo);
		ManageDanhSachLopUseCase manageDanhSachLopUc = new ManageDanhSachLopService(danhSachRepo, nguoiDungRepo,
				hocKyRepo, lopRepo);

		ExportDanhSachLopUseCase exportDanhSachLopUc = new ExportDanhSachLopService(danhSachRepo, nguoiDungRepo,
				fileExporter);

		GenerateThoiKhoaBieuUseCase generateThoiKhoaBieuUc = new GenerateThoiKhoaBieuService(nguoiDungRepo,
				danhSachRepo, thoiKbRepo, yeuCauRepo);

		ManageThoiKhoaBieuUseCase manageThoiKhoaBieuUc = new ManageThoiKhoaBieuService(thoiKbRepo);

		ExportThoiKhoaBieuUseCase exportThoiKhoaBieuUc = new ExportThoiKhoaBieuService(thoiKbRepo, fileExporter,
				icsExporter);

		AdminDanhSachLopUseCase adminDanhSachLopUc = new AdminDanhSachLopService(danhSachRepo, nguoiDungRepo, hocKyRepo,
				importDanhSachLopUc);

		// ===== UI =====
		ScreenManager screenManager = new ScreenManager(stage, authUseCase);

		screenManager.setImportDanhSachLopUseCase(importDanhSachLopUc);
		screenManager.setQuanLyDanhSachLopUseCase(manageDanhSachLopUc);
		screenManager.setXuatDanhSachLopUseCase(exportDanhSachLopUc);
		screenManager.setSinhThoiKhoaBieuUseCase(generateThoiKhoaBieuUc);
		screenManager.setQuanLyThoiKhoaBieuUseCase(manageThoiKhoaBieuUc);
		screenManager.setXuatThoiKhoaBieuUseCase(exportThoiKhoaBieuUc);
		screenManager.setQuanTriDanhSachLopUseCase(adminDanhSachLopUc);

		screenManager.init();
		stage.show();
	}

	@Override
	public void stop()
	{
		DataSourceProvider.shutdown();
	}
}