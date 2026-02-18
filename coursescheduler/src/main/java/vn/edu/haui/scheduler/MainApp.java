package vn.edu.haui.scheduler;

import javafx.application.Application;
import javafx.stage.Stage;

import vn.edu.haui.scheduler.application.port.in.*;
import vn.edu.haui.scheduler.application.service.*;
import vn.edu.haui.scheduler.application.port.out.*;

import vn.edu.haui.scheduler.infrastructure.io.exports.*;
import vn.edu.haui.scheduler.infrastructure.io.imports.ExcelDanhSachLopImporter;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.*;
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

		ExcelDanhSachLopImporter importer = new ExcelDanhSachLopImporter();

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

		CsvExporter csvExporter = new CsvExporter();
		ExcelExporter excelExporter = new ExcelExporter();
		PdfExporter pdfExporter = new PdfExporter("fonts/SEGOEUI.TTF");
		IcsExporter icsExporter = new IcsExporter();

		FileExporter fileExporter = new CompositeFileExporter(
				csvExporter,
				excelExporter,
				pdfExporter);

		ImportDanhSachLopUseCase importUc = new ImportDanhSachLopService(
				importer,
				hocPhanRepo,
				giangVienRepo,
				lopRepo,
				lichRepo,
				danhSachRepo,
				tepRepo);

		screenManager.setImportDanhSachLopUseCase(importUc);

		ManageDanhSachLopUseCase quanLyUc = new ManageDanhSachLopService(danhSachRepo);
		screenManager.setQuanLyDanhSachLopUseCase(quanLyUc);

		ExportDanhSachLopUseCase xuatUc = new ExportDanhSachLopService(
				danhSachRepo,
				fileExporter);
		screenManager.setXuatDanhSachLopUseCase(xuatUc);

		GenerateThoiKhoaBieuUseCase sinhUc = new GenerateThoiKhoaBieuService(
				tkbRepo,
				yeuCauRepo,
				yeuCauChiTietRepo,
				danhSachRepo,
				lopRepo,
				lichRepo,
				rangBuocRepo);
		screenManager.setSinhThoiKhoaBieuUseCase(sinhUc);

		ManageThoiKhoaBieuUseCase quanLyTkbUc = new ManageThoiKhoaBieuService(
				tkbRepo,
				lopRepo,
				lichRepo,
				giangVienRepo);
		screenManager.setQuanLyThoiKhoaBieuUseCase(quanLyTkbUc);

		ExportThoiKhoaBieuUseCase xuatTkbUc = new ExportThoiKhoaBieuService(
				tkbRepo,
				lopRepo,
				lichRepo,
				hocPhanRepo,
				giangVienRepo,
				fileExporter,
				icsExporter);
		screenManager.setXuatThoiKhoaBieuUseCase(xuatTkbUc);

		AdminDanhSachLopUseCase quanTriUc =
		        new AdminDanhSachLopService(
		                danhSachRepo,
		                importUc);

		screenManager.setQuanTriDanhSachLopUseCase(quanTriUc);
		
		screenManager.init();
		stage.show();
	}

	private AuthUseCase createAuthUseCase()
	{
		return new AuthService(
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
