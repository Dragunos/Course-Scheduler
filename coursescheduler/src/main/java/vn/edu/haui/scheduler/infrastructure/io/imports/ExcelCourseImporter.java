package vn.edu.haui.scheduler.infrastructure.io.imports;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelCourseImporter
{
	public List<ImportedLopRow> importFrom(File file) throws Exception
	{
		List<ImportedLopRow> rows = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(file); Workbook wb = WorkbookFactory.create(fis)) {
			Sheet sheet = wb.getSheetAt(0);
			Iterator<Row> it = sheet.iterator();
			if(!it.hasNext()) throw new IllegalArgumentException("Empty sheet");
			it.next();
			while(it.hasNext()) {
				Row r = it.next();
				ImportedLopRow row = new ImportedLopRow();
				row.maHocPhan = getString(r, 0);
				row.tenHocPhan = getString(r, 1);
				row.soTinChi = getInteger(r, 2);
				row.maLop = getString(r, 3);
				row.tenGiangVien = getString(r, 4);
				row.hinhThucDay = getString(r, 5);
				row.diaDiem = getString(r, 6);
				Integer thu = getInteger(r, 7);
				Integer tietBat = getInteger(r, 8);
				Integer tietKet = getInteger(r, 9);
				if(thu != null && tietBat != null && tietKet != null) {
					row.buoiList.add(new ImportedLopRow.Buoi(thu, tietBat, tietKet));
				}
				rows.add(row);
			}
		}
		return rows;
	}

	private String getString(Row r, int idx)
	{
		Cell c = r.getCell(idx);
		if(c == null) return null;
		if(c.getCellType() == CellType.NUMERIC) return String.valueOf((long) c.getNumericCellValue());
		return c.getStringCellValue().trim();
	}

	private Integer getInteger(Row r, int idx)
	{
		Cell c = r.getCell(idx);
		if(c == null) return null;
		if(c.getCellType() == CellType.NUMERIC) return (int) c.getNumericCellValue();
		try {
			return Integer.parseInt(getString(r, idx));
		}
		catch(Exception e) {
			return null;
		}
	}
}