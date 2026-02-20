package vn.edu.haui.scheduler.infrastructure.io.imports;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;

public class ExcelDanhSachLopImporter
{
	private static final int ROWS_TO_SKIP = 3;

	private final DataFormatter formatter = new DataFormatter();

	private final FormulaEvaluator evaluator;

	public ExcelDanhSachLopImporter()
	{
		this.evaluator = null;
	}

	public ImportFileResult importFrom(File file) throws IOException, InvalidFormatException
	{
		List<ImportedLopHocPhanRow> rows = new ArrayList<>();
		List<String> warnings = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(file); Workbook wb = WorkbookFactory.create(fis)) {
			Sheet sheet = wb.getSheetAt(0);
			FormulaEvaluator ev = wb.getCreationHelper().createFormulaEvaluator();
			int last = sheet.getLastRowNum();
			for(int i = ROWS_TO_SKIP; i <= last; i++) {
				Row r = sheet.getRow(i);
				if(r == null) continue;

				String maLop = cellToString(r, 2, ev);
				if(isEmpty(maLop)) {
					warnings.add("Hàng " + (i + 1) + ": maLop trống → bỏ qua");
					continue;
				}

				String maHocPhan = cellToString(r, 3, ev);
				String tenHocPhan = cellToString(r, 5, ev);
				Integer soTinChi = parseInteger(cellToString(r, 15, ev));
				String tenGiangVien = cellToString(r, 9, ev);
				String hinhThuc = cellToString(r, 16, ev);
				String diaDiem = cellToString(r, 8, ev);
				String thuRaw = cellToString(r, 7, ev);
				String tietRaw = cellToString(r, 6, ev);

				Integer thu = parseThu(thuRaw);
				List<Integer> tietList = parseTietIntegers(tietRaw, warnings, i + 1);
				if(tietList.isEmpty()) {
					warnings.add("Hàng " + (i + 1) + ": không có tiết hợp lệ, bỏ qua session (maLop=" + maLop + ")");
				}

				ImportedLopHocPhanRow imported = new ImportedLopHocPhanRow();
				imported.maLop = normalize(maLop);
				imported.maHocPhan = normalize(maHocPhan);
				imported.tenHocPhan = normalize(tenHocPhan);
				imported.soTinChi = soTinChi;
				imported.tenGiangVien = normalize(tenGiangVien);
				imported.hinhThucDay = normalize(hinhThuc);
				imported.diaDiem = normalize(diaDiem);

				if(thu != null && !tietList.isEmpty()) {
					List<ImportedLopHocPhanRow.Buoi> buois = convertTietListToBuoi(thu, tietList);
					imported.buoiList.addAll(buois);
				}
				else {
					if(thu == null && !tietList.isEmpty()) {
						warnings.add("Hàng " + (i + 1) + ": thu không hợp lệ ('" + thuRaw
								+ "'), vẫn giữ nhưng thu=null (maLop=" + maLop + ")");
					}
				}

				rows.add(imported);
			}
		}

		return new ImportFileResult(rows, warnings);
	}

	private String cellToString(Row r, int idx, FormulaEvaluator ev)
	{
		if(r == null) return null;
		Cell c = r.getCell(idx);
		if(c == null) return null;
		try {
			if(c.getCellType() == CellType.FORMULA) {
				CellValue evaluated = ev.evaluate(c);
				if(evaluated == null) return null;
				switch(evaluated.getCellType()) {
					case STRING:
						return safeTrim(evaluated.getStringValue());
					case NUMERIC:
						double d = evaluated.getNumberValue();
						if(d == Math.rint(d)) return String.valueOf((long) d);
						return String.valueOf(d);
					case BOOLEAN:
						return String.valueOf(evaluated.getBooleanValue());
					default:
						return null;
				}
			}
			else {
				String s = formatter.formatCellValue(c, ev);
				return safeTrim(s);
			}
		}
		catch(Exception ignored) {
			return null;
		}
	}

	private static String safeTrim(String s)
	{
		if(s == null) return null;
		String t = s.trim().replace('\u00A0', ' ');
		if(t.isEmpty()) return null;
		return t;
	}

	private static String normalize(String s)
	{
		if(s == null) return null;
		String t = s.trim();
		t = Normalizer.normalize(t, Normalizer.Form.NFKC);
		t = t.replaceAll("\\s+", " ");
		return t.isEmpty() ? null : t;
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
	
	public static class ImportFileResult {
        public final List<ImportedLopHocPhanRow> rows;
        public final List<String> warnings;
        public ImportFileResult(List<ImportedLopHocPhanRow> rows, List<String> warnings) {
            this.rows = rows;
            this.warnings = warnings;
        }
    }
}
