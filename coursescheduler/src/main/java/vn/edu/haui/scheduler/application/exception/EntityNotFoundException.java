package vn.edu.haui.scheduler.application.exception;

public class EntityNotFoundException extends BusinessException
{
	public EntityNotFoundException(String entityName, Long id)
	{
		super("ENTITY_NOT_FOUND", entityName + " with id " + id + " not found.");
	}
}