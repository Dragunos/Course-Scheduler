package vn.edu.haui.scheduler.domain.constraint;

import vn.edu.haui.scheduler.domain.enums.*;

import java.util.Objects;

public final class RangBuocToiUu
{
	private final LoaiRangBuoc loaiRangBuoc;

	private final boolean laCung;

	private final double trongSo;

	private final TargetType targetType;

	private final String targetValue;

	private final String attribute;

	private final ToanTuSoSanh operator;

	private final String value;

	public RangBuocToiUu(
			LoaiRangBuoc loaiRangBuoc,
			boolean laCung,
			double trongSo,
			TargetType targetType,
			String targetValue,
			String attribute,
			ToanTuSoSanh operator,
			String value)
	{
		this.loaiRangBuoc = Objects.requireNonNull(loaiRangBuoc);
		this.laCung = laCung;
		this.trongSo = trongSo;
		this.targetType = targetType;
		this.targetValue = targetValue;
		this.attribute = attribute;
		this.operator = operator;
		this.value = value;
	}

	public LoaiRangBuoc getLoaiRangBuoc()
	{
		return loaiRangBuoc;
	}

	public boolean isLaCung()
	{
		return laCung;
	}

	public double getTrongSo()
	{
		return trongSo;
	}

	public TargetType getTargetType()
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

	public ToanTuSoSanh getOperator()
	{
		return operator;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof RangBuocToiUu)) return false;
		RangBuocToiUu other = (RangBuocToiUu) o;
		return laCung == other.laCung
				&& Double.compare(other.trongSo, trongSo) == 0
				&& loaiRangBuoc == other.loaiRangBuoc
				&& targetType == other.targetType
				&& Objects.equals(targetValue, other.targetValue)
				&& Objects.equals(attribute, other.attribute)
				&& operator == other.operator
				&& Objects.equals(value, other.value);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(
				loaiRangBuoc,
				laCung,
				trongSo,
				targetType,
				targetValue,
				attribute,
				operator,
				value);
	}
}
