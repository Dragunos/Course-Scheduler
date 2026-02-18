package vn.edu.haui.scheduler.infrastructure.io.imports;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelDanhSachLopImporter
{
	private static final int ROWS_TO_SKIP = 3;

	private static final int IDX_MA_HOC_PHAN = 3; // D

	private static final int IDX_TEN_HOC_PHAN = 5; // F

	private static final int IDX_SO_TIN_CHI = 15; // P

	private static final int IDX_MA_LOP = 2; // C

	private static final int IDX_TEN_GV = 9; // J

	private static final int IDX_HINH_THUC = 16; // Q

	private static final int IDX_DIA_DIEM = 8; // I

	private static final int IDX_THU = 7; // H

	private static final int IDX_TIET_RAW = 6; // G

	public List<ImportedLopHocPhanRow> importFrom(File file) throws IOException, InvalidFormatException
	{
		List<ImportedLopHocPhanRow> rows = new ArrayList<>();
		List<String> warnings = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(file); Workbook wb = WorkbookFactory.create(fis)) {
			Sheet sheet = wb.getSheetAt(0);
			int last = sheet.getLastRowNum();
			for(int i = ROWS_TO_SKIP; i <= last; i++) {
				Row r = sheet.getRow(i);
				if(r == null) continue;

				String maLop = cellToString(r, IDX_MA_LOP);
				if(isEmpty(maLop)) {
					warnings.add("Hàng " + (i + 1) + ": maLop trống → bỏ qua");
					continue;
				}

				String maHocPhan = cellToString(r, IDX_MA_HOC_PHAN);
				String tenHocPhan = cellToString(r, IDX_TEN_HOC_PHAN);
				Integer soTinChi = parseInteger(cellToString(r, IDX_SO_TIN_CHI));
				String tenGiangVien = cellToString(r, IDX_TEN_GV);
				String hinhThuc = cellToString(r, IDX_HINH_THUC);
				String diaDiem = cellToString(r, IDX_DIA_DIEM);
				String thuRaw = cellToString(r, IDX_THU);
				String tietRaw = cellToString(r, IDX_TIET_RAW);

				Integer thu = parseThu(thuRaw);
				List<Integer> tietList = parseTietIntegers(tietRaw, warnings, i + 1);
				if(tietList.isEmpty()) {
					warnings.add("Hàng " + (i + 1) + ": không có tiết hợp lệ, bỏ qua session (maLop=" + maLop + ")");
				}

				ImportedLopHocPhanRow imported = new ImportedLopHocPhanRow();
				imported.maLop = maLop;
				imported.maHocPhan = maHocPhan;
				imported.tenHocPhan = tenHocPhan;
				imported.soTinChi = soTinChi;
				imported.tenGiangVien = tenGiangVien;
				imported.hinhThucDay = hinhThuc;
				imported.diaDiem = diaDiem;

				if(thu != null && !tietList.isEmpty()) {
					List<ImportedLopHocPhanRow.Buoi> buois = convertTietListToBuoi(thu, tietList);
					imported.buoiList.addAll(buois);
				}
				else {
					if(thu == null && !tietList.isEmpty()) {
						warnings.add("Hàng " + (i + 1) + ": thu không hợp lệ ('" + thuRaw
								+ "'), vẫn giữ nhưng thu=null (maLop=" + maLop + ")");
					}
					if(tietList.isEmpty()) {
						// already warned above
					}
				}

				rows.add(imported);
			}
		}

		if(!warnings.isEmpty()) {
			for(String w : warnings) {
				System.out.println("[IMPORT WARNING] " + w);
			}
		}

		return rows;
	}

	private static String cellToString(Row r, int idx)
	{
		if(r == null) return null;
		Cell c = r.getCell(idx);
		if(c == null) return null;
		try {
			if(c.getCellType() == CellType.STRING) {
				String s = c.getStringCellValue();
				return s == null ? null : s.trim();
			}
			if(c.getCellType() == CellType.NUMERIC) {
				double d = c.getNumericCellValue();
				if(d == Math.rint(d)) {
					return String.valueOf((long) d);
				}
				else {
					return String.valueOf(d);
				}
			}
			if(c.getCellType() == CellType.BOOLEAN) {
				return String.valueOf(c.getBooleanCellValue());
			}
			if(c.getCellType() == CellType.FORMULA) {
				try {
					return c.getStringCellValue().trim();
				}
				catch(Exception ex) {
					double d = c.getNumericCellValue();
					if(d == Math.rint(d)) {
						return String.valueOf((long) d);
					}
					else {
						return String.valueOf(d);
					}
				}
			}
		}
		catch(Exception ignored) {
		}
		return null;
	}

	private static boolean isEmpty(String s)
	{
		return s == null || s.trim().isEmpty();
	}

	private static Integer parseInteger(String s)
	{
		if(isEmpty(s)) return null;
		try {
			String clean = s.trim().replaceAll("\\.0+$", "");
			return Integer.parseInt(clean);
		}
		catch(Exception ex) {
			return null;
		}
	}

	private static Integer parseThu(String raw)
	{
		if(raw == null) return null;
		String t = raw.trim().toLowerCase(Locale.ROOT);
		t = t.replaceAll("\\s+", "");
		if(t.isEmpty()) return null;
		if(t.startsWith("thứ") || t.startsWith("tu")) {
			String num = t.replaceAll("[^0-9]", "");
			if(!num.isEmpty()) {
				try {
					return Integer.parseInt(num);
				}
				catch(Exception ignored) {
				}
			}
		}
		if(t.equals("cn") || t.contains("chủ") || t.contains("chu")) return 8;
		if(t.matches("^[1-7]$")) return Integer.parseInt(t);
		if(t.startsWith("t") && t.length() > 1 && Character.isDigit(t.charAt(1))) {
			try {
				return Integer.parseInt(t.substring(1, 2));
			}
			catch(Exception ignored) {
			}
		}
		return null;
	}

	private static List<Integer> parseTietIntegers(String raw, List<String> warnings, int rowNumber)
	{
		List<Integer> out = new ArrayList<>();
		if(raw == null) return out;
		String cleaned = raw.trim();
		if(cleaned.isEmpty()) return out;
		String[] parts = cleaned.split(",");
		for(String p : parts) {
			String s = p.trim();
			if(s.isEmpty()) continue;
			if(s.contains("-")) {
				String[] rng = s.split("-");
				if(rng.length == 2) {
					Integer a = parseInteger(rng[0]);
					Integer b = parseInteger(rng[1]);
					if(a != null && b != null && a <= b) {
						for(int x = a; x <= b; x++) out.add(x);
						continue;
					}
					else {
						warnings.add("Hàng " + rowNumber + ": dải tiết không hợp lệ '" + s + "'");
						continue;
					}
				}
			}
			Integer v = parseInteger(s);
			if(v == null) {
				warnings.add("Hàng " + rowNumber + ": không parse được tiết '" + s + "'");
				continue;
			}
			out.add(v);
		}
		Set<Integer> dedup = new TreeSet<>(out);
		return new ArrayList<>(dedup);
	}

	private static List<ImportedLopHocPhanRow.Buoi> convertTietListToBuoi(int thu, List<Integer> tietList)
	{
		List<ImportedLopHocPhanRow.Buoi> result = new ArrayList<>();
		if(tietList == null || tietList.isEmpty()) return result;
		List<Integer> sorted = new ArrayList<>(tietList);
		Collections.sort(sorted);
		int start = sorted.get(0);
		int prev = start;
		for(int i = 1; i < sorted.size(); i++) {
			int cur = sorted.get(i);
			if(cur == prev + 1) {
				prev = cur;
				continue;
			}
			else {
				result.add(new ImportedLopHocPhanRow.Buoi(thu, start, prev));
				start = cur;
				prev = cur;
			}
		}
		result.add(new ImportedLopHocPhanRow.Buoi(thu, start, prev));
		return result;
	}
}
