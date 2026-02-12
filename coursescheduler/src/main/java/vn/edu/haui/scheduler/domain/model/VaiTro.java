package vn.edu.haui.scheduler.domain.model;

public class VaiTro
{
	private Long id;

	private String tenVaiTro;

	public VaiTro(Long id, String tenVaiTro)
	{
		this.id = id;
		this.tenVaiTro = tenVaiTro;
	}

	public Long getId()
	{
		return id;
	}

	public String getTenVaiTro()
	{
		return tenVaiTro;
	}
}
