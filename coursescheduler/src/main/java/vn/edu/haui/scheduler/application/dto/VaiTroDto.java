package vn.edu.haui.scheduler.application.dto;

public class VaiTroDto
{
	private Long id;

	private String tenVaiTro;

	public VaiTroDto()
	{
	}

	public VaiTroDto(Long id, String tenVaiTro)
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

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setTenVaiTro(String tenVaiTro)
	{
		this.tenVaiTro = tenVaiTro;
	}
}