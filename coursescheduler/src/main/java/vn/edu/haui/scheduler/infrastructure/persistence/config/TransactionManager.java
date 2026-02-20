package vn.edu.haui.scheduler.infrastructure.persistence.config;

import java.util.function.Supplier;

public interface TransactionManager
{
	void begin();

	void commit();

	void rollback();

	<T> T executeInTransaction(Supplier<T> action);
}
