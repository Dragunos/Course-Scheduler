package vn.edu.haui.scheduler.domain.model;

import java.util.Objects;

public class KhoangTiet
{
	private final int tietBatDau;

	private final int tietKetThuc;

	public KhoangTiet(int tietBatDau, int tietKetThuc)
	{
		if(tietBatDau > tietKetThuc) {
			throw new IllegalArgumentException("tietBatDau must be <= tietKetThuc");
		}
		this.tietBatDau = tietBatDau;
		this.tietKetThuc = tietKetThuc;
	}

	public int getTietBatDau()
	{
		return tietBatDau;
	}

	public int getTietKetThuc()
	{
		return tietKetThuc;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof KhoangTiet)) return false;
		KhoangTiet that = (KhoangTiet) o;
		return tietBatDau == that.tietBatDau &&
				tietKetThuc == that.tietKetThuc;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(tietBatDau, tietKetThuc);
	}

	@Override
	public String toString()
	{
		return "KhoangTiet{" +
				"tietBatDau=" + tietBatDau +
				", tietKetThuc=" + tietKetThuc +
				'}';
	}
}
