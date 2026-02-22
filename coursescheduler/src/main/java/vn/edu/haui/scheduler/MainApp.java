package vn.edu.haui.scheduler;

import javafx.application.Application;
import javafx.stage.Stage;

import vn.edu.haui.scheduler.application.port.in.*;
import vn.edu.haui.scheduler.application.service.*;
import vn.edu.haui.scheduler.application.port.out.*;

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

		// ===== REPOSITORIES =====
		NguoiDungRepository nguoiDungRepo = new JdbcNguoiDungRepository(txManager);
		VaiTroRepository vaiTroRepo = new JdbcVaiTroRepository(txManager);
		HocPhanRepository hocPhanRepo = new JdbcHocPhanRepository(txManager);
		GiangVienRepository giangVienRepo = new JdbcGiangVienRepository(txManager);
		LopHocPhanRepository lopRepo = new JdbcLopHocPhanRepository(txManager);
		DanhSachLopRepository danhSachRepo = new JdbcDanhSachLopRepository(txManager);
		TepTaiLenRepository tepRepo = new JdbcTepTaiLenRepository(txManager);
		HocKyRepository hocKyRepo = new JdbcHocKyRepository(txManager);

		// ===== IMPORTER =====
		ExcelDanhSachLopImporter importer = new ExcelDanhSachLopImporter();

		// ===== USE CASES =====

		AuthUseCase authUseCase = new AuthService(
				nguoiDungRepo,
				vaiTroRepo,
				new PasswordHasher());

		ImportDanhSachLopUseCase importUc = new ImportDanhSachLopService(
				importer,
				danhSachRepo,
				hocPhanRepo,
				giangVienRepo,
				lopRepo,
				nguoiDungRepo,
				hocKyRepo,
				tepRepo,
				txManager);

		// ===== UI =====
		ScreenManager screenManager = new ScreenManager(stage, authUseCase);

		screenManager.setImportDanhSachLopUseCase(importUc);

		screenManager.init();
		stage.show();
	}

	@Override
	public void stop()
	{
		DataSourceProvider.shutdown();
	}
}