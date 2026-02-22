package vn.edu.haui.scheduler.application.exception;

public class EntityNotFoundException extends BusinessException
{
	public EntityNotFoundException(String entityName, Long id)
	{
		super("ENTITY_NOT_FOUND",
				entityName + " with id " + id + " not found.");
	}

	public EntityNotFoundException(String entityName, String field, String value)
	{
		super("ENTITY_NOT_FOUND",
				entityName + " with " + field + " '" + value + "' not found.");
	}
}