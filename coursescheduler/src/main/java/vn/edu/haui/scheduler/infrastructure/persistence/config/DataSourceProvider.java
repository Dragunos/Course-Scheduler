package vn.edu.haui.scheduler.infrastructure.persistence.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public final class DataSourceProvider
{

	private static final HikariDataSource dataSource;

	static {
		HikariConfig config = new HikariConfig();

		config.setJdbcUrl(DatabaseConfig.getJdbcUrl());
		config.setDriverClassName("org.sqlite.JDBC");

		config.setMaximumPoolSize(DatabaseConfig.MAX_POOL_SIZE);
		config.setMinimumIdle(DatabaseConfig.MIN_IDLE);

		config.setConnectionTimeout(DatabaseConfig.CONNECTION_TIMEOUT);
		config.setIdleTimeout(DatabaseConfig.IDLE_TIMEOUT);
		config.setMaxLifetime(DatabaseConfig.MAX_LIFETIME);

		dataSource = new HikariDataSource(config);
	}

	private DataSourceProvider()
	{
	}

	public static DataSource getDataSource()
	{
		return dataSource;
	}

	public static void shutdown()
	{
		dataSource.close();
	}
}
