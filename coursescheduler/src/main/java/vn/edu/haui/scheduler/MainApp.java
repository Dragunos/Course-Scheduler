package vn.edu.haui.scheduler;

import javafx.application.Application;
import javafx.stage.Stage;

import vn.edu.haui.scheduler.application.auth.AuthService;
import vn.edu.haui.scheduler.application.auth.AuthSession;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.JdbcNguoiDungRepository;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.JdbcVaiTroRepository;
import vn.edu.haui.scheduler.infrastructure.security.PasswordHasher;
import vn.edu.haui.scheduler.ui.fx.ScreenManager;
import vn.edu.haui.scheduler.ui.fx.config.FxConfig;

public class MainApp extends Application
{
	@Override
	public void start(Stage stage)
	{
		FxConfig.apply(stage);

		AuthService authService = createAuthService();
		AuthSession session = new AuthSession();

		ScreenManager screenManager = new ScreenManager(stage, authService, session);

		screenManager.init();

		stage.show();
	}

	private AuthService createAuthService()
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
