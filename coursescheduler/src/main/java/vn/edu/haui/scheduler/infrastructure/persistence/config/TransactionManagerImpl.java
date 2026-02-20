package vn.edu.haui.scheduler.infrastructure.persistence.config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public class TransactionManagerImpl implements TransactionManager
{
	private final DataSource dataSource;

	private static final ThreadLocal<Connection> txConnection = new ThreadLocal<>();

	public TransactionManagerImpl(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	@Override
	public void begin()
	{
		try {
			Connection conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			txConnection.set(conn);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void commit()
	{
		try {
			Connection conn = txConnection.get();
			if(conn != null) {
				conn.commit();
				conn.close();
			}
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			txConnection.remove();
		}
	}

	@Override
	public void rollback()
	{
		try {
			Connection conn = txConnection.get();
			if(conn != null) {
				conn.rollback();
				conn.close();
			}
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			txConnection.remove();
		}
	}

	@Override
	public <T> T executeInTransaction(Supplier<T> action)
	{
		begin();
		try {
			T result = action.get();
			commit();
			return result;
		}
		catch(RuntimeException ex) {
			rollback();
			throw ex;
		}
		catch(Exception ex) {
			rollback();
			throw new RuntimeException(ex);
		}
	}

	public static Connection currentConnection()
	{
		return txConnection.get();
	}

	public Connection getConnection()
	{
		Connection conn = txConnection.get();

		if(conn != null) {
			return conn;
		}

		try {
			return dataSource.getConnection();
		}
		catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Connection getExistingConnection()
	{
		return txConnection.get();
	}

	public Connection getRequiredConnection()
	{
		Connection conn = txConnection.get();

		if(conn == null) {
			throw new IllegalStateException(
					"No active transaction found for write operation");
		}

		return conn;
	}
}
