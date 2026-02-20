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
	@Override
	public void start(Stage stage)
	{
		FxConfig.apply(stage);

		TransactionManagerImpl txManager = new TransactionManagerImpl(DataSourceProvider.getDataSource());

		// REPOSITORIES
		NguoiDungRepository nguoiDungRepo = new JdbcNguoiDungRepository(txManager);
		VaiTroRepository vaiTroRepo = new JdbcVaiTroRepository(txManager);
		HocPhanRepository hocPhanRepo = new JdbcHocPhanRepository();
		GiangVienRepository giangVienRepo = new JdbcGiangVienRepository();
		LopHocPhanRepository lopRepo = new JdbcLopHocPhanRepository();
		LichHocRepository lichRepo = new JdbcLichHocRepository();
		DanhSachLopRepository danhSachRepo = new JdbcDanhSachLopRepository();
		TepTaiLenRepository tepRepo = new JdbcTepTaiLenRepository();
		ThoiKhoaBieuRepository tkbRepo = new JdbcThoiKhoaBieuRepository();
		YeuCauRepository yeuCauRepo = new JdbcYeuCauRepository();
		YeuCauChiTietRepository yeuCauChiTietRepo = new JdbcYeuCauChiTietRepository();
		RangBuocToiUuRepository rangBuocRepo = new JdbcRangBuocToiUuRepository();

		// IO
		ExcelDanhSachLopImporter importer = new ExcelDanhSachLopImporter();

		FileExporter fileExporter = new CompositeFileExporter(
				new CsvExporter(),
				new ExcelExporter(),
				new PdfExporter("fonts/SEGOEUI.TTF"));

		IcsExporter icsExporter = new IcsExporter();

		// USE CASES
		AuthUseCase authUseCase = new AuthService(
				nguoiDungRepo,
				vaiTroRepo,
				new PasswordHasher(),
				txManager);

		ImportDanhSachLopUseCase importUc = new ImportDanhSachLopService(
				importer,
				hocPhanRepo,
				giangVienRepo,
				lopRepo,
				lichRepo,
				danhSachRepo,
				tepRepo,
				txManager);

		ManageDanhSachLopUseCase quanLyUc = new ManageDanhSachLopService(danhSachRepo, txManager);

		ExportDanhSachLopUseCase xuatUc = new ExportDanhSachLopService(danhSachRepo, fileExporter);

		GenerateThoiKhoaBieuUseCase sinhUc = new GenerateThoiKhoaBieuService(
				tkbRepo,
				yeuCauRepo,
				yeuCauChiTietRepo,
				danhSachRepo,
				lopRepo,
				lichRepo,
				rangBuocRepo);

		ManageThoiKhoaBieuUseCase quanLyTkbUc = new ManageThoiKhoaBieuService(
				tkbRepo,
				lopRepo,
				lichRepo,
				giangVienRepo);

		ExportThoiKhoaBieuUseCase xuatTkbUc = new ExportThoiKhoaBieuService(
				tkbRepo,
				lopRepo,
				lichRepo,
				hocPhanRepo,
				giangVienRepo,
				fileExporter,
				icsExporter);

		AdminDanhSachLopUseCase quanTriUc = new AdminDanhSachLopService(danhSachRepo, importUc);

		// UI
		ScreenManager screenManager = new ScreenManager(stage, authUseCase);

		screenManager.setImportDanhSachLopUseCase(importUc);
		screenManager.setQuanLyDanhSachLopUseCase(quanLyUc);
		screenManager.setXuatDanhSachLopUseCase(xuatUc);
		screenManager.setSinhThoiKhoaBieuUseCase(sinhUc);
		screenManager.setQuanLyThoiKhoaBieuUseCase(quanLyTkbUc);
		screenManager.setXuatThoiKhoaBieuUseCase(xuatTkbUc);
		screenManager.setQuanTriDanhSachLopUseCase(quanTriUc);

		screenManager.init();
		stage.show();
	}

	@Override
	public void stop()
	{
		DataSourceProvider.shutdown();
	}
}