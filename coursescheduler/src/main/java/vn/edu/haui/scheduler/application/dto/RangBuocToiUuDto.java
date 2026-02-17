package vn.edu.haui.scheduler.application.dto;

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

	private boolean laCung;

	private double trongSo;

	private String ghiChu;

	private Long nguoiTaoId;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getThoiKhoaBieuId()
	{
		return thoiKhoaBieuId;
	}

	public void setThoiKhoaBieuId(Long thoiKhoaBieuId)
	{
		this.thoiKhoaBieuId = thoiKhoaBieuId;
	}

	public Long getYeuCauId()
	{
		return yeuCauId;
	}

	public void setYeuCauId(Long yeuCauId)
	{
		this.yeuCauId = yeuCauId;
	}

	public String getLoaiRangBuoc()
	{
		return loaiRangBuoc;
	}

	public void setLoaiRangBuoc(String loaiRangBuoc)
	{
		this.loaiRangBuoc = loaiRangBuoc;
	}

	public String getTargetType()
	{
		return targetType;
	}

	public void setTargetType(String targetType)
	{
		this.targetType = targetType;
	}

	public String getTargetValue()
	{
		return targetValue;
	}

	public void setTargetValue(String targetValue)
	{
		this.targetValue = targetValue;
	}

	public String getAttribute()
	{
		return attribute;
	}

	public void setAttribute(String attribute)
	{
		this.attribute = attribute;
	}

	public String getOperator()
	{
		return operator;
	}

	public void setOperator(String operator)
	{
		this.operator = operator;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public boolean isLaCung()
	{
		return laCung;
	}

	public void setLaCung(boolean laCung)
	{
		this.laCung = laCung;
	}

	public double getTrongSo()
	{
		return trongSo;
	}

	public void setTrongSo(double trongSo)
	{
		this.trongSo = trongSo;
	}

	public String getGhiChu()
	{
		return ghiChu;
	}

	public void setGhiChu(String ghiChu)
	{
		this.ghiChu = ghiChu;
	}

	public Long getNguoiTaoId()
	{
		return nguoiTaoId;
	}

	public void setNguoiTaoId(Long nguoiTaoId)
	{
		this.nguoiTaoId = nguoiTaoId;
	}
}
