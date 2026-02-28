package vn.edu.haui.scheduler.application.exception;

public class EntityNotFoundException extends BusinessException
{
	public EntityNotFoundException(String entityName, Long id)
	{
		super("ENTITY_NOT_FOUND",
				entityName + " với ID = " + id + " không tồn tại");
	}

	public EntityNotFoundException(String entityName, String field, String value)
	{
		super("ENTITY_NOT_FOUND",
				entityName + " với " + field + " '" + value + "' không tồn tại");
	}
}