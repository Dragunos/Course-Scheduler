package vn.edu.haui.scheduler.domain.model;

public class VaiTro
{
	private Integer id;

	private String tenVaiTro;

	public VaiTro(Integer id, String tenVaiTro)
	{
		this.id = id;
		this.tenVaiTro = tenVaiTro;
	}

	public Integer getId()
	{
		return id;
	}

	public String getTenVaiTro()
	{
		return tenVaiTro;
	}
}
