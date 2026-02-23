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

		TransactionManagerImpl txManager = new TransactionManagerImpl(DataSourceProvider.getDataSource());

		CompositeFileExporter fileExporter = new CompositeFileExporter(
				new CsvExporter(),
				new ExcelExporter(),
				new PdfExporter(PDF_FONT_PATH));
		IcsExporter icsExporter = new IcsExporter();

		// ===== REPOSITORIES =====
		NguoiDungRepository nguoiDungRepo = new JdbcNguoiDungRepository(txManager);
		VaiTroRepository vaiTroRepo = new JdbcVaiTroRepository(txManager);
		HocPhanRepository hocPhanRepo = new JdbcHocPhanRepository(txManager);
		GiangVienRepository giangVienRepo = new JdbcGiangVienRepository(txManager);
		LopHocPhanRepository lopRepo = new JdbcLopHocPhanRepository(txManager);
		DanhSachLopRepository danhSachRepo = new JdbcDanhSachLopRepository(txManager);
		TepTaiLenRepository tepRepo = new JdbcTepTaiLenRepository(txManager);
		HocKyRepository hocKyRepo = new JdbcHocKyRepository(txManager);
		ThoiKhoaBieuRepository thoiKbRepo = new JdbcThoiKhoaBieuRepository(txManager);
		YeuCauRepository yeuCauRepo = new JdbcYeuCauRepository(txManager);

		// ===== IMPORTER =====
		ExcelDanhSachLopImporter importer = new ExcelDanhSachLopImporter();

		// ===== USE CASES =====

		AuthUseCase authUseCase = new AuthService(
				nguoiDungRepo,
				vaiTroRepo,
				new PasswordHasher(),
				txManager);

		ImportDanhSachLopUseCase importDanhSachLopUc = new ImportDanhSachLopService(
				importer,
				danhSachRepo,
				hocPhanRepo,
				giangVienRepo,
				lopRepo,
				nguoiDungRepo,
				hocKyRepo,
				tepRepo,
				txManager);
		ManageDanhSachLopUseCase manageDanhSachLopUc = new ManageDanhSachLopService(danhSachRepo, nguoiDungRepo,
				hocKyRepo, lopRepo, txManager);

		ExportDanhSachLopUseCase exportDanhSachLopUc = new ExportDanhSachLopService(danhSachRepo, nguoiDungRepo,
				fileExporter);

		GenerateThoiKhoaBieuUseCase generateThoiKhoaBieuUc = new GenerateThoiKhoaBieuService(nguoiDungRepo,
				danhSachRepo, thoiKbRepo, yeuCauRepo, txManager);

		ManageThoiKhoaBieuUseCase manageThoiKhoaBieuUc = new ManageThoiKhoaBieuService(thoiKbRepo);

		ExportThoiKhoaBieuUseCase exportThoiKhoaBieuUc = new ExportThoiKhoaBieuService(thoiKbRepo, fileExporter,
				icsExporter);

		AdminDanhSachLopUseCase adminDanhSachLopUc = new AdminDanhSachLopService(danhSachRepo, nguoiDungRepo, hocKyRepo,
				importDanhSachLopUc, txManager);

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