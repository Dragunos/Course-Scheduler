package vn.edu.haui.scheduler.infrastructure.io.exports;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class IcsExporter
{
	private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

	public void export(Path output, String calendarName, List<Map<String, String>> rows) throws IOException
	{
		try (BufferedWriter w = Files.newBufferedWriter(output)) {
			w.write("BEGIN:VCALENDAR");
			w.newLine();
			w.write("VERSION:2.0");
			w.newLine();
			w.write("PRODID:-//coursescheduler//vn.edu.haui//EN");
			w.newLine();
			w.write("X-WR-CALNAME:" + (calendarName == null ? "Thoi khoa bieu" : calendarName));
			w.newLine();

			LocalDate baseMonday = nextOrSameMonday(LocalDate.now());
			for(Map<String, String> r : rows) {
				String maLop = r.getOrDefault("ma_lop", "");
				String tenHocPhan = r.getOrDefault("ten_hoc_phan", "");
				String tenGiangVien = r.getOrDefault("ten_giang_vien", "");
				String diaDiem = r.getOrDefault("dia_diem", "");
				String thuS = r.getOrDefault("thu", "");
				String tbS = r.getOrDefault("tiet_bat_dau", "");
				String teS = r.getOrDefault("tiet_ket_thuc", "");

				if(thuS == null || thuS.isEmpty()) {
					continue;
				}
				int thu;
				int tietBat = 0;
				int tietKet = 0;
				try {
					thu = Integer.parseInt(thuS);
					tietBat = tbS == null || tbS.isEmpty() ? 1 : Integer.parseInt(tbS);
					tietKet = teS == null || teS.isEmpty() ? tietBat : Integer.parseInt(teS);
				}
				catch(NumberFormatException ex) {
					continue;
				}

				LocalDate date = baseMonday.plusDays(thu - 2L);
				LocalTime start = mapPeriodToStartTime(tietBat);
				LocalTime end = mapPeriodToEndTime(tietKet);
				LocalDateTime dtStart = LocalDateTime.of(date, start);
				LocalDateTime dtEnd = LocalDateTime.of(date, end);

				String uid = "tkb-" + maLop + "-" + dtStart.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				w.write("BEGIN:VEVENT");
				w.newLine();
				w.write("UID:" + uid);
				w.newLine();
				w.write("DTSTAMP:" + LocalDateTime.now().format(DT_FMT));
				w.newLine();
				w.write("DTSTART:" + dtStart.format(DT_FMT));
				w.newLine();
				w.write("DTEND:" + dtEnd.format(DT_FMT));
				w.newLine();
				w.write("SUMMARY:" + safe(tenHocPhan) + " [" + safe(maLop) + "]");
				w.newLine();
				w.write("LOCATION:" + safe(diaDiem));
				w.newLine();
				w.write("DESCRIPTION:Giảng viên: " + safe(tenGiangVien) + "\\nTiết: " + tietBat + "-" + tietKet);
				w.newLine();
				w.write("END:VEVENT");
				w.newLine();
			}

			w.write("END:VCALENDAR");
			w.newLine();
		}
	}

	private static String safe(String s)
	{
		return s == null ? "" : s.replace("\n", " ").replace("\r", " ");
	}

	private static LocalDate nextOrSameMonday(LocalDate from)
	{
		int dow = from.getDayOfWeek().getValue();
		int daysToAdd = (dow <= 1) ? (1 - dow) : (8 - dow);
		return from.plusDays(daysToAdd);
	}

	private static LocalTime mapPeriodToStartTime(int period)
	{
		int hour = 7 + (period - 1) * 50 / 60;
		int minute = ((period - 1) * 50) % 60;
		if(period <= 2) {
			return LocalTime.of(7 + (period - 1), 0);
		}
		return LocalTime.of(7 + (period - 1), 0);
	}

	private static LocalTime mapPeriodToEndTime(int period)
	{
		LocalTime start = mapPeriodToStartTime(period);
		return start.plusMinutes(50);
	}
}
