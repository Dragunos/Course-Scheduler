package vn.edu.haui.scheduler.infrastructure.io.exports;

import vn.edu.haui.scheduler.application.port.out.FileExportPort;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class CompositeFileExporter implements FileExportPort
{
	private final CsvExporter csvExporter;

	private final ExcelExporter excelExporter;

	private final PdfExporter pdfExporter;

	public CompositeFileExporter(
			CsvExporter csvExporter,
			ExcelExporter excelExporter,
			PdfExporter pdfExporter)
	{
		this.csvExporter = csvExporter;
		this.excelExporter = excelExporter;
		this.pdfExporter = pdfExporter;
	}

	@Override
	public void exportCsv(
			Path outputPath,
			List<String> headers,
			List<Map<String, String>> rows) throws IOException
	{
		csvExporter.exportCsv(outputPath, headers, rows);
	}

	@Override
	public void exportExcel(
			Path outputPath,
			List<String> headers,
			List<Map<String, String>> rows) throws IOException
	{
		excelExporter.exportExcel(outputPath, headers, rows);
	}

	@Override
	public void exportPdf(
			Path outputPath,
			String title,
			List<String> headers,
			List<Map<String, String>> rows) throws IOException
	{
		pdfExporter.exportPdf(outputPath, title, headers, rows);
	}
}
