package vn.edu.haui.scheduler.application.dto;

public class YeuCauChiTietDto
{

	private Long yeuCauId;

	private Long lopHocPhanId;

	private Integer batBuoc;

	private String loaiChiDinh;

	private Double trongSo;

	public YeuCauChiTietDto(Long yeuCauId,
			Long lopHocPhanId,
			Integer batBuoc,
			String loaiChiDinh,
			Double trongSo)
	{
		this.yeuCauId = yeuCauId;
		this.lopHocPhanId = lopHocPhanId;
		this.batBuoc = batBuoc;
		this.loaiChiDinh = loaiChiDinh;
		this.trongSo = trongSo;
	}

	public Long getYeuCauId()
	{
		return yeuCauId;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public Integer getBatBuoc()
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
}
