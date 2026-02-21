package vn.edu.haui.scheduler.application.dto;

import java.time.LocalDateTime;

public class RangBuocToiUuDto
{
	private Long id;

	private Long thoiKhoaBieuId;

	private Long yeuCauId;

	private String loaiRangBuoc;

	private String targetType;

	private String targetValue;

	private String attribute;

	private String operator;

	private String value;

	private Boolean laCung;

	private Double trongSo;

	private String ghiChu;

	private Long nguoiTaoId;

	private LocalDateTime ngayTao;

	public RangBuocToiUuDto()
	{
	}

	public Long getId()
	{
		return id;
	}

	public Long getThoiKhoaBieuId()
	{
		return thoiKhoaBieuId;
	}

	public Long getYeuCauId()
	{
		return yeuCauId;
	}

	public String getLoaiRangBuoc()
	{
		return loaiRangBuoc;
	}

	public String getTargetType()
	{
		return targetType;
	}

	public String getTargetValue()
	{
		return targetValue;
	}

	public String getAttribute()
	{
		return attribute;
	}

	public String getOperator()
	{
		return operator;
	}

	public String getValue()
	{
		return value;
	}

	public Boolean getLaCung()
	{
		return laCung;
	}

	public Double getTrongSo()
	{
		return trongSo;
	}

	public String getGhiChu()
	{
		return ghiChu;
	}

	public Long getNguoiTaoId()
	{
		return nguoiTaoId;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setThoiKhoaBieuId(Long thoiKhoaBieuId)
	{
		this.thoiKhoaBieuId = thoiKhoaBieuId;
	}

	public void setYeuCauId(Long yeuCauId)
	{
		this.yeuCauId = yeuCauId;
	}

	public void setLoaiRangBuoc(String loaiRangBuoc)
	{
		this.loaiRangBuoc = loaiRangBuoc;
	}

	public void setTargetType(String targetType)
	{
		this.targetType = targetType;
	}

	public void setTargetValue(String targetValue)
	{
		this.targetValue = targetValue;
	}

	public void setAttribute(String attribute)
	{
		this.attribute = attribute;
	}

	public void setOperator(String operator)
	{
		this.operator = operator;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public void setLaCung(Boolean laCung)
	{
		this.laCung = laCung;
	}

	public void setTrongSo(Double trongSo)
	{
		this.trongSo = trongSo;
	}

	public void setGhiChu(String ghiChu)
	{
		this.ghiChu = ghiChu;
	}

	public void setNguoiTaoId(Long nguoiTaoId)
	{
		this.nguoiTaoId = nguoiTaoId;
	}

	public void setNgayTao(LocalDateTime ngayTao)
	{
		this.ngayTao = ngayTao;
	}
}