package vn.edu.haui.scheduler;

import javafx.application.Application;
import javafx.stage.Stage;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.application.service.AuthAppService;
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

		AuthUseCase authUseCase = createAuthUseCase();

		ScreenManager screenManager = new ScreenManager(stage, authUseCase);
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
