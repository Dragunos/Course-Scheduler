package vn.edu.haui.scheduler.application.dto.temp;

import java.util.List;

public class GenerateThoiKhoaBieuRequestDto
{
	private Long danhSachLopId;

	private List<String> maHocPhanBatBuoc;

	private List<String> maLopMuon; // mã lớp cụ thể mong muốn (ví dụ 20253FL6130003)

	private boolean excludeOnline; // ví dụ "Không muốn hình thức Online"

	private List<IntRangeDto> bannedPeriods; // danh sách khoảng tiết (start-end) cần tránh

	private List<Integer> bannedWeekdays; // các thứ cần tránh (2..8)

	private int topK;

	private long timeoutSeconds;

	public Long getDanhSachLopId()
	{
		return danhSachLopId;
	}

	public void setDanhSachLopId(Long danhSachLopId)
	{
		this.danhSachLopId = danhSachLopId;
	}

	public List<String> getMaHocPhanBatBuoc()
	{
		return maHocPhanBatBuoc;
	}

	public void setMaHocPhanBatBuoc(List<String> maHocPhanBatBuoc)
	{
		this.maHocPhanBatBuoc = maHocPhanBatBuoc;
	}

	public List<String> getMaLopMuon()
	{
		return maLopMuon;
	}

	public void setMaLopMuon(List<String> maLopMuon)
	{
		this.maLopMuon = maLopMuon;
	}

	public boolean isExcludeOnline()
	{
		return excludeOnline;
	}

	public void setExcludeOnline(boolean excludeOnline)
	{
		this.excludeOnline = excludeOnline;
	}

	public List<IntRangeDto> getBannedPeriods()
	{
		return bannedPeriods;
	}

	public void setBannedPeriods(List<IntRangeDto> bannedPeriods)
	{
		this.bannedPeriods = bannedPeriods;
	}

	public List<Integer> getBannedWeekdays()
	{
		return bannedWeekdays;
	}

	public void setBannedWeekdays(List<Integer> bannedWeekdays)
	{
		this.bannedWeekdays = bannedWeekdays;
	}

	public int getTopK()
	{
		return topK;
	}

	public void setTopK(int topK)
	{
		this.topK = topK;
	}

	public long getTimeoutSeconds()
	{
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(long timeoutSeconds)
	{
		this.timeoutSeconds = timeoutSeconds;
	}

	public static class IntRangeDto
	{
		private int from;

		private int to;

		public int getFrom()
		{
			return from;
		}

		public void setFrom(int from)
		{
			this.from = from;
		}

		public int getTo()
		{
			return to;
		}

		public void setTo(int to)
		{
			this.to = to;
		}
	}
}