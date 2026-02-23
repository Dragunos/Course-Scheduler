package vn.edu.haui.scheduler.infrastructure.persistence.config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public class TransactionManagerImpl implements TransactionManager
{
	private final DataSource dataSource;

	private static final ThreadLocal<Connection> txConnection = new ThreadLocal<>();

	private static final ThreadLocal<Boolean> txActive = new ThreadLocal<>();

	public TransactionManagerImpl(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	// Transaction Control
	@Override
	public void begin()
	{
		if(Boolean.TRUE.equals(txActive.get())) {
			throw new IllegalStateException(
					"Nested transaction is not supported");
		}

		try {
			Connection conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			txConnection.set(conn);
			txActive.set(true);
		}
		catch(Exception e) {
			throw new RuntimeException("Failed to begin transaction", e);
		}
	}

	@Override
	public void commit()
	{
		Connection conn = txConnection.get();

		if(conn == null) {
			return;
		}

		try {
			if(!conn.isClosed()) {
				conn.commit();
			}
		}
		catch(Exception e) {
			throw new RuntimeException("Transaction commit failed", e);
		}
		finally {
			cleanup();
		}
	}

	@Override
	public void rollback()
	{
		Connection conn = txConnection.get();

		if(conn == null) {
			return;
		}

		try {
			if(!conn.isClosed()) {
				conn.rollback();
			}
		}
		catch(Exception e) {
			throw new RuntimeException("Transaction rollback failed", e);
		}
		finally {
			cleanup();
		}
	}

	// Execution Wrapper
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

	// Connection Access
	public Connection getRequiredConnection()
	{
		Connection conn = txConnection.get();

		if(conn == null || isConnectionInvalid(conn)) {
			throw new IllegalStateException(
					"No active transaction found for write operation");
		}

		return conn;
	}

	public Connection getConnection()
	{
		Connection conn = txConnection.get();

		if(conn != null && !isConnectionInvalid(conn)) {
			return conn;
		}

		try {
			return dataSource.getConnection();
		}
		catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// Internal Utilities
	private boolean isConnectionInvalid(Connection conn)
	{
		try {
			return conn.isClosed();
		}
		catch(Exception e) {
			return true;
		}
	}

	private void cleanup()
	{
		Connection conn = txConnection.get();

		try {
			if(conn != null && !conn.isClosed()) {
				conn.close();
			}
		}
		catch(Exception ignored) {
			// Ignore cleanup exception
		}
		finally {
			txConnection.remove();
			txActive.remove();
		}
	}
}