package vn.edu.haui.scheduler.domain.model;

import java.util.Objects;

public class VaiTro
{
	private final Long id;

	private final String tenVaiTro;

	private VaiTro(Long id, String tenVaiTro)
	{
		validateInvariant(tenVaiTro);
		this.id = id;
		this.tenVaiTro = tenVaiTro;
	}

	public static VaiTro create(String tenVaiTro)
	{
		return new VaiTro(null, tenVaiTro);
	}

	public static VaiTro reconstruct(Long id, String tenVaiTro)
	{
		if(id == null) {
			throw new IllegalStateException("Persisted VaiTro must have id");
		}
		return new VaiTro(id, tenVaiTro);
	}

	private static void validateInvariant(String tenVaiTro)
	{
		if(tenVaiTro == null || tenVaiTro.isBlank()) {
			throw new IllegalArgumentException("Ten vai tro khong hop le");
		}
	}

	public Long getId()
	{
		return id;
	}

	public String getTenVaiTro()
	{
		return tenVaiTro;
	}

	public boolean isPersisted()
	{
		return id != null;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof VaiTro)) return false;
		VaiTro that = (VaiTro) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(id);
	}
}