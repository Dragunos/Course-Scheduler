package vn.edu.haui.scheduler.domain.model;

import vn.edu.haui.scheduler.domain.constraint.RangBuocToiUu;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class YeuCauDangKy
{
	private final Long id;

	private final List<LopHocPhan> danhSachLopHocPhan;

	private final List<RangBuocToiUu> danhSachRangBuoc;

	public YeuCauDangKy(
			Long id,
			List<LopHocPhan> danhSachLopHocPhan,
			List<RangBuocToiUu> danhSachRangBuoc)
	{
		this.id = id;
		this.danhSachLopHocPhan = Collections.unmodifiableList(
				List.copyOf(
						Objects.requireNonNull(danhSachLopHocPhan)));

		this.danhSachRangBuoc = Collections.unmodifiableList(
				List.copyOf(
						Objects.requireNonNull(danhSachRangBuoc)));
	}

	public Long getId()
	{
		return id;
	}

	public List<LopHocPhan> getDanhSachLopHocPhan()
	{
		return danhSachLopHocPhan;
	}

	public List<RangBuocToiUu> getDanhSachRangBuoc()
	{
		return danhSachRangBuoc;
	}
}
