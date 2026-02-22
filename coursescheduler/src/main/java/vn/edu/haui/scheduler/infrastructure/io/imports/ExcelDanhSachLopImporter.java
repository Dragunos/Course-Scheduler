package vn.edu.haui.scheduler.infrastructure.io.imports;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import vn.edu.haui.scheduler.domain.model.TepTaiLen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;

public class ExcelDanhSachLopImporter
{
	private static final int ROWS_TO_SKIP = 3;

	private static final int COL_MA_LOP = 2;

	private static final int COL_MA_HOC_PHAN = 3;

	private static final int COL_TEN_HOC_PHAN = 5;

	private static final int COL_TIET = 6;

	private static final int COL_THU = 7;

	private static final int COL_DIA_DIEM = 8;

	private static final int COL_GIANG_VIEN = 9;

	private static final int COL_SO_TIN_CHI = 15;

	private static final int COL_HINH_THUC = 16;

	private final DataFormatter formatter = new DataFormatter();

	public ImportFileResult importFrom(File file)
			throws IOException, InvalidFormatException
	{
		List<ImportedLopHocPhanRaw> rows = new ArrayList<>();
		List<String> warnings = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(file);
				Workbook wb = WorkbookFactory.create(fis)) {

			if(wb.getNumberOfSheets() == 0) {
				warnings.add("File không có sheet nào.");
				return new ImportFileResult(rows, warnings);
			}

			Sheet sheet = wb.getSheetAt(0);
			if(sheet == null) {
				warnings.add("Sheet đầu tiên không tồn tại.");
				return new ImportFileResult(rows, warnings);
			}

			FormulaEvaluator ev = wb.getCreationHelper().createFormulaEvaluator();

			int last = sheet.getLastRowNum();

			for(int i = ROWS_TO_SKIP; i <= last; i++) {

				Row r = sheet.getRow(i);
				if(r == null) continue;

				String maLop = cellToString(r, COL_MA_LOP, ev, warnings, i);
				if(isEmpty(maLop)) {
					warnings.add("Hàng " + (i + 1) + ": maLop trống → bỏ qua");
					continue;
				}

				String maHocPhan = cellToString(r, COL_MA_HOC_PHAN, ev, warnings, i);

				String tenHocPhan = cellToString(r, COL_TEN_HOC_PHAN, ev, warnings, i);

				Integer soTinChi = parseInteger(cellToString(r, COL_SO_TIN_CHI, ev, warnings, i));

				String tenGiangVien = cellToString(r, COL_GIANG_VIEN, ev, warnings, i);

				String hinhThuc = cellToString(r, COL_HINH_THUC, ev, warnings, i);

				String diaDiem = cellToString(r, COL_DIA_DIEM, ev, warnings, i);

				String thuRaw = cellToString(r, COL_THU, ev, warnings, i);

				String tietRaw = cellToString(r, COL_TIET, ev, warnings, i);

				Integer thu = parseThu(thuRaw, warnings, i + 1);

				List<Integer> tietList = parseTietIntegers(tietRaw, warnings, i + 1);

				if(tietList.isEmpty()) {
					warnings.add("Hàng " + (i + 1)
							+ ": không có tiết hợp lệ (maLop=" + maLop + ")");
				}

				List<ImportedLopHocPhanRaw.LichHocRaw> buoiList = convertTietListToBuoi(thu, tietList);

				ImportedLopHocPhanRaw raw = new ImportedLopHocPhanRaw(
						normalize(maHocPhan),
						normalize(tenHocPhan),
						soTinChi,
						normalize(maLop),
						normalize(tenGiangVien),
						normalize(hinhThuc),
						normalize(diaDiem),
						List.copyOf(buoiList));

				rows.add(raw);
			}
		}

		return new ImportFileResult(rows, warnings);
	}

	private String cellToString(Row r,
			int idx,
			FormulaEvaluator ev,
			List<String> warnings,
			int rowIndex)
	{
		Cell c = r.getCell(idx);
		if(c == null) return null;

		try {
			if(c.getCellType() == CellType.FORMULA) {
				CellValue evaluated = ev.evaluate(c);
				if(evaluated == null) return null;

				return switch(evaluated.getCellType()) {
					case STRING -> safeTrim(evaluated.getStringValue());
					case NUMERIC -> {
						double d = evaluated.getNumberValue();
						yield (d == Math.rint(d))
								? String.valueOf((long) d)
								: String.valueOf(d);
					}
					case BOOLEAN -> String.valueOf(evaluated.getBooleanValue());
					default -> null;
				};
			}
			return safeTrim(formatter.formatCellValue(c, ev));
		}
		catch(RuntimeException ex) {
			warnings.add("Hàng " + (rowIndex + 1)
					+ ": lỗi đọc cell cột " + idx
					+ " (" + ex.getClass().getSimpleName() + ")");
			return null;
		}
	}

	private static Integer parseThu(String raw,
			List<String> warnings,
			int rowNumber)
	{
		if(raw == null) return null;

		String t = raw.trim().toLowerCase(Locale.ROOT);

		if(t.matches("^thứ\\s*[2-7]$")) {
			return Integer.parseInt(t.replaceAll("\\D", ""));
		}

		if(t.equals("cn") || t.equals("chủ nhật")) {
			return 8;
		}

		if(t.matches("^[2-7]$")) {
			return Integer.parseInt(t);
		}

		warnings.add("Hàng " + rowNumber
				+ ": thứ không hợp lệ ('" + raw + "')");

		return null;
	}

	private static List<Integer> parseTietIntegers(
			String raw,
			List<String> warnings,
			int rowNumber)
	{
		if(raw == null || raw.isBlank())
			return List.of();

		Set<Integer> result = new TreeSet<>();

		String[] parts = raw.split(",");

		for(String p : parts) {

			String s = p.trim();

			if(s.contains("-")) {
				String[] rng = s.split("-");
				if(rng.length == 2) {
					Integer a = parseInteger(rng[0]);
					Integer b = parseInteger(rng[1]);

					if(a != null && b != null && a <= b) {
						for(int x = a; x <= b; x++)
							result.add(x);
						continue;
					}
				}
				warnings.add("Hàng " + rowNumber
						+ ": dải tiết không hợp lệ '" + s + "'");
				continue;
			}

			Integer v = parseInteger(s);
			if(v == null) {
				warnings.add("Hàng " + rowNumber
						+ ": không parse được tiết '" + s + "'");
				continue;
			}
			result.add(v);
		}

		return new ArrayList<>(result);
	}

	private static List<ImportedLopHocPhanRaw.LichHocRaw> convertTietListToBuoi(Integer thu, List<Integer> tietList)
	{
		if(thu == null || tietList.isEmpty())
			return List.of();

		List<ImportedLopHocPhanRaw.LichHocRaw> result = new ArrayList<>();

		int start = tietList.get(0);
		int prev = start;

		for(int i = 1; i < tietList.size(); i++) {
			int cur = tietList.get(i);
			if(cur == prev + 1) {
				prev = cur;
			}
			else {
				result.add(new ImportedLopHocPhanRaw.LichHocRaw(thu, start, prev));
				start = cur;
				prev = cur;
			}
		}

		result.add(new ImportedLopHocPhanRaw.LichHocRaw(thu, start, prev));
		return result;
	}

	public ImportFileResult read(TepTaiLen tepTaiLen)
	{
		if(tepTaiLen == null)
			throw new IllegalArgumentException("TepTaiLen null");

		try {
			File file = new File(tepTaiLen.getDuongDan());
			return importFrom(file);
		}
		catch(Exception e) {
			throw new RuntimeException("Import file thất bại", e);
		}
	}

	private static Integer parseInteger(String s)
	{
		if(s == null || s.isBlank()) return null;
		try {
			return Integer.parseInt(
					s.trim().replaceAll("\\.0+$", ""));
		}
		catch(NumberFormatException ex) {
			return null;
		}
	}

	private static String safeTrim(String s)
	{
		if(s == null) return null;
		String t = s.trim().replace('\u00A0', ' ');
		return t.isEmpty() ? null : t;
	}

	private static String normalize(String s)
	{
		if(s == null) return null;
		String t = Normalizer.normalize(
				s.trim(),
				Normalizer.Form.NFKC);
		t = t.replaceAll("\\s+", " ");
		return t.isEmpty() ? null : t;
	}

	private static boolean isEmpty(String s)
	{
		return s == null || s.trim().isEmpty();
	}

	public static class ImportFileResult
	{
		public final List<ImportedLopHocPhanRaw> rows;

		public final List<String> warnings;

		public ImportFileResult(List<ImportedLopHocPhanRaw> rows,
				List<String> warnings)
		{
			this.rows = rows;
			this.warnings = warnings;
		}
	}
}