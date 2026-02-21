package vn.edu.haui.scheduler.application.dto;

public class YeuCauChiTietDto
{
	private Long yeuCauId;

	private Long lopHocPhanId;

	private Boolean batBuoc;

	private String loaiChiDinh;

	private Double trongSo;

	public YeuCauChiTietDto()
	{
	}

	public Long getYeuCauId()
	{
		return yeuCauId;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public Boolean getBatBuoc()
	{
		return batBuoc;
	}

	public String getLoaiChiDinh()
	{
		return loaiChiDinh;
	}

	public Double getTrongSo()
	{
		return trongSo;
	}

	public void setYeuCauId(Long yeuCauId)
	{
		this.yeuCauId = yeuCauId;
	}

	public void setLopHocPhanId(Long lopHocPhanId)
	{
		this.lopHocPhanId = lopHocPhanId;
	}

	public void setBatBuoc(Boolean batBuoc)
	{
		this.batBuoc = batBuoc;
	}

	public void setLoaiChiDinh(String loaiChiDinh)
	{
		this.loaiChiDinh = loaiChiDinh;
	}

	public void setTrongSo(Double trongSo)
	{
		this.trongSo = trongSo;
	}
}