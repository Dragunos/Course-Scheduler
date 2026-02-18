package vn.edu.haui.scheduler.infrastructure.io.exports;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import vn.edu.haui.scheduler.application.port.out.FileExportPort;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ExcelExporter implements FileExportPort
{
	public void exportExcel(Path outputPath, List<String> headers, List<Map<String, String>> rows) throws IOException
	{
		if(outputPath.getParent() != null) {
			Files.createDirectories(outputPath.getParent());
		}

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("DanhSachLop");

			CellStyle headerStyle = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			headerStyle.setFont(font);

			Row headerRow = sheet.createRow(0);
			for(int i = 0; i < headers.size(); i++) {
				Cell c = headerRow.createCell(i);
				c.setCellValue(headers.get(i));
				c.setCellStyle(headerStyle);
			}

			int r = 1;
			for(Map<String, String> rowMap : rows) {
				Row row = sheet.createRow(r++);
				for(int c = 0; c < headers.size(); c++) {
					String h = headers.get(c);
					String v = rowMap.getOrDefault(h, "");
					Cell cell = row.createCell(c);
					cell.setCellValue(v);
				}
			}

			for(int i = 0; i < headers.size(); i++) {
				sheet.autoSizeColumn(i);
			}

			try (OutputStream out = Files.newOutputStream(outputPath)) {
				workbook.write(out);
			}
		}
	}

	@Override
	public void exportCsv(Path outputPath,
			List<String> headers,
			List<Map<String, String>> rows)
	{
		throw new UnsupportedOperationException("CSV not supported");
	}

	@Override
	public void exportPdf(Path outputPath,
			String title,
			List<String> headers,
			List<Map<String, String>> rows)
	{
		throw new UnsupportedOperationException("PDF not supported");
	}
}
