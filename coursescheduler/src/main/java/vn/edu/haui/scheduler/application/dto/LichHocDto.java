package vn.edu.haui.scheduler.application.dto;

public class LichHocDto
{
	private Long id;

	private Long lopHocPhanId;

	private Integer thu;

	private Integer tietBatDau;

	private Integer tietKetThuc;

	public LichHocDto()
	{
	}

	public LichHocDto(Long id,
			Long lopHocPhanId,
			Integer thu,
			Integer tietBatDau,
			Integer tietKetThuc)
	{
		this.id = id;
		this.lopHocPhanId = lopHocPhanId;
		this.thu = thu;
		this.tietBatDau = tietBatDau;
		this.tietKetThuc = tietKetThuc;
	}

	public Long getId()
	{
		return id;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public Integer getThu()
	{
		return thu;
	}

	public Integer getTietBatDau()
	{
		return tietBatDau;
	}

	public Integer getTietKetThuc()
	{
		return tietKetThuc;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setLopHocPhanId(Long lopHocPhanId)
	{
		this.lopHocPhanId = lopHocPhanId;
	}

	public void setThu(Integer thu)
	{
		this.thu = thu;
	}

	public void setTietBatDau(Integer tietBatDau)
	{
		this.tietBatDau = tietBatDau;
	}

	public void setTietKetThuc(Integer tietKetThuc)
	{
		this.tietKetThuc = tietKetThuc;
	}
}