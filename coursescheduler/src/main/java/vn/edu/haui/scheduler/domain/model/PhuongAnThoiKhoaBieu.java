package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

public class PhuongAnThoiKhoaBieu
{
	private Long id;

	private Long nguoiDungId;

	private Long danhSachLopId;

	private String tenPhuongAn;

	private Double diemDanhGia;

	private LocalDateTime ngayTao;

	private Set<LopHocPhan> cacLopDuocChon;

	public PhuongAnThoiKhoaBieu()
	{
	}

	public PhuongAnThoiKhoaBieu(
			Long id,
			Long nguoiDungId,
			Long danhSachLopId,
			String tenPhuongAn,
			Double diemDanhGia,
			LocalDateTime ngayTao,
			Set<LopHocPhan> cacLopDuocChon)
	{
		this.id = id;
		this.nguoiDungId = nguoiDungId;
		this.danhSachLopId = danhSachLopId;
		this.tenPhuongAn = tenPhuongAn;
		this.diemDanhGia = diemDanhGia;
		this.ngayTao = ngayTao;
		setCacLopDuocChon(cacLopDuocChon);
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getNguoiDungId()
	{
		return nguoiDungId;
	}

	public void setNguoiDungId(Long nguoiDungId)
	{
		this.nguoiDungId = nguoiDungId;
	}

	public Long getDanhSachLopId()
	{
		return danhSachLopId;
	}

	public void setDanhSachLopId(Long danhSachLopId)
	{
		this.danhSachLopId = danhSachLopId;
	}

	public String getTenPhuongAn()
	{
		return tenPhuongAn;
	}

	public void setTenPhuongAn(String tenPhuongAn)
	{
		this.tenPhuongAn = tenPhuongAn;
	}

	public Double getDiemDanhGia()
	{
		return diemDanhGia;
	}

	public void setDiemDanhGia(Double diemDanhGia)
	{
		this.diemDanhGia = diemDanhGia;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public void setNgayTao(LocalDateTime ngayTao)
	{
		this.ngayTao = ngayTao;
	}

	public Set<LopHocPhan> getCacLopDuocChon()
	{
		if(cacLopDuocChon == null) {
			return Collections.emptySet();
		}
		return cacLopDuocChon;
	}

	public void setCacLopDuocChon(Set<LopHocPhan> cacLopDuocChon)
	{
		this.cacLopDuocChon = (cacLopDuocChon == null)
				? Collections.emptySet()
				: Collections.unmodifiableSet(Set.copyOf(cacLopDuocChon));
	}
}
