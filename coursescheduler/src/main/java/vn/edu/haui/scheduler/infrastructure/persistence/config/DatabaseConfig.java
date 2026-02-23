package vn.edu.haui.scheduler.infrastructure.persistence.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class DatabaseConfig
{
	private static final String APP_DIR_NAME = ".coursescheduler";

	private static final String DB_NAME = "coursescheduler.db";

	public static final int MAX_POOL_SIZE = 2;

	public static final int MIN_IDLE = 1;

	public static final long CONNECTION_TIMEOUT = 5000;

	public static final long IDLE_TIMEOUT = 30000;

	public static final long MAX_LIFETIME = 300000;

	private DatabaseConfig()
	{
	}

	public static String getJdbcUrl()
	{
		Path dbPath = getDatabasePath();
		return "jdbc:sqlite:" + dbPath.toAbsolutePath();
	}

	private static Path getDatabasePath()
	{
		try {
			String userHome = System.getProperty("user.home");
			Path appDir = Paths.get(userHome, APP_DIR_NAME);

			if(!Files.exists(appDir)) {
				Files.createDirectories(appDir);
			}

			return appDir.resolve(DB_NAME);
		}
		catch(Exception e) {
			throw new RuntimeException("Không thể khởi tạo thư mục database", e);
		}
	}
}
