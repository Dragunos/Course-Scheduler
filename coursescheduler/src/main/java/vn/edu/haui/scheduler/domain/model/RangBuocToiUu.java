package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class RangBuocToiUu
{
	private final Long id;

	private final YeuCau yeuCau;

	private final ThoiKhoaBieu thoiKhoaBieu;

	private final String loaiRangBuoc;

	private final String targetType;

	private final String targetValue;

	private final String attribute;

	private final String operator;

	private final String value;

	private final boolean laCung;

	private final double trongSo;

	private final NguoiDung nguoiTao;

	private final LocalDateTime ngayTao;

	private RangBuocToiUu(
			Long id,
			YeuCau yeuCau,
			ThoiKhoaBieu thoiKhoaBieu,
			String loaiRangBuoc,
			String targetType,
			String targetValue,
			String attribute,
			String operator,
			String value,
			boolean laCung,
			double trongSo,
			NguoiDung nguoiTao,
			LocalDateTime ngayTao)
	{
		validateInvariant(yeuCau, thoiKhoaBieu, loaiRangBuoc, ngayTao);

		this.id = id;
		this.yeuCau = yeuCau;
		this.thoiKhoaBieu = thoiKhoaBieu;
		this.loaiRangBuoc = loaiRangBuoc;
		this.targetType = targetType;
		this.targetValue = targetValue;
		this.attribute = attribute;
		this.operator = operator;
		this.value = value;
		this.laCung = laCung;
		this.trongSo = trongSo;
		this.nguoiTao = nguoiTao;
		this.ngayTao = ngayTao;
	}

	public static RangBuocToiUu createForYeuCau(
			YeuCau yeuCau,
			String loaiRangBuoc,
			boolean laCung,
			double trongSo,
			NguoiDung nguoiTao)
	{
		return new RangBuocToiUu(
				null,
				yeuCau,
				null,
				loaiRangBuoc,
				null,
				null,
				null,
				null,
				null,
				laCung,
				trongSo,
				nguoiTao,
				LocalDateTime.now());
	}

	public static RangBuocToiUu reconstruct(
			Long id,
			YeuCau yeuCau,
			ThoiKhoaBieu thoiKhoaBieu,
			String loaiRangBuoc,
			String targetType,
			String targetValue,
			String attribute,
			String operator,
			String value,
			boolean laCung,
			double trongSo,
			NguoiDung nguoiTao,
			LocalDateTime ngayTao)
	{
		if(id == null)
			throw new IllegalStateException("Persisted RangBuocToiUu must have id");

		return new RangBuocToiUu(
				id,
				yeuCau,
				thoiKhoaBieu,
				loaiRangBuoc,
				targetType,
				targetValue,
				attribute,
				operator,
				value,
				laCung,
				trongSo,
				nguoiTao,
				ngayTao);
	}

	private static void validateInvariant(
			YeuCau yeuCau,
			ThoiKhoaBieu thoiKhoaBieu,
			String loaiRangBuoc,
			LocalDateTime ngayTao)
	{
		if(yeuCau == null && thoiKhoaBieu == null)
			throw new IllegalArgumentException(
					"Phai thuoc YeuCau hoac ThoiKhoaBieu");

		if(loaiRangBuoc == null || loaiRangBuoc.isBlank())
			throw new IllegalArgumentException(
					"Loai rang buoc khong hop le");

		if(ngayTao == null)
			throw new IllegalArgumentException(
					"Ngay tao null");
	}

	public boolean apDungChoYeuCau(YeuCau yc)
	{
		return yeuCau != null && yeuCau.equals(yc);
	}

	public Long getId()
	{
		return id;
	}

	public YeuCau getYeuCau()
	{
		return yeuCau;
	}

	public ThoiKhoaBieu getThoiKhoaBieu()
	{
		return thoiKhoaBieu;
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

	public boolean isLaCung()
	{
		return laCung;
	}

	public double getTrongSo()
	{
		return trongSo;
	}

	public NguoiDung getNguoiTao()
	{
		return nguoiTao;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public String getValue()
	{
		return value;
	}

	public boolean isPersisted()
	{
		return id != null;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof RangBuocToiUu)) return false;
		RangBuocToiUu that = (RangBuocToiUu) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(id);
	}
}