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
		System.out.println("BEGIN tx on thread " + Thread.currentThread().getName());

		if(Boolean.TRUE.equals(txActive.get())) {
			throw new IllegalStateException(
					"Nested transaction is not supported");
		}

		try {
			Connection conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			txConnection.set(conn);
			txActive.set(true);

			System.out.println("Connection obtained: " + conn + ", closed? " + conn.isClosed());
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
			System.out.println("commit(): no tx connection found on thread " + Thread.currentThread().getName());
			return;
		}

		try {
			if(!conn.isClosed()) {
				conn.commit();
				System.out
						.println("commit(): committed conn " + conn + " on thread " + Thread.currentThread().getName());
			}
		}
		catch(Exception e) {
			throw new RuntimeException("Transaction commit failed", e);
		}
		finally {
			closeAndCleanup(conn);
		}
	}

	@Override
	public void rollback()
	{
		Connection conn = txConnection.get();

		if(conn == null) {
			System.out.println("rollback(): no tx connection found on thread " + Thread.currentThread().getName());
			return;
		}

		try {
			if(!conn.isClosed()) {
				conn.rollback();
				System.out.println(
						"rollback(): rolled back conn " + conn + " on thread " + Thread.currentThread().getName());
			}
		}
		catch(Exception e) {
			throw new RuntimeException("Transaction rollback failed", e);
		}
		finally {
			closeAndCleanup(conn);
		}
	}

	// Execution Wrapper
	@Override
	public <T> T executeInTransaction(Supplier<T> action)
	{
		begin();

		boolean success = false;

		try {
			T result = action.get();
			success = true;
			commit();
			return result;
		}
		catch(Exception ex) {
			rollback();
			throw (ex instanceof RuntimeException)
					? (RuntimeException) ex
					: new RuntimeException(ex);
		}
		finally {
			if(!success) {
				rollback();
			}
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

		System.out.println("getRequiredConnection on thread " + Thread.currentThread().getName() + ", conn = " + conn);

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

	private void closeAndCleanup(Connection conn)
	{
		try {
			if(conn != null) {
				try {
					if(!conn.isClosed()) {
						conn.close();
						System.out.println("closeAndCleanup(): closed conn " + conn +
								" on thread " + Thread.currentThread().getName());
					}
				}
				catch(SQLException ex) {
					System.err.println("closeAndCleanup(): error closing conn: " + ex.getMessage());
				}
			}
		}
		finally {
			cleanup();
		}
	}

	private void cleanup()
	{
		txConnection.remove();
		txActive.remove();
		System.out.println("cleanup(): removed threadlocal on thread " + Thread.currentThread().getName());
	}
}