package vn.edu.haui.scheduler.infrastructure.io.exports;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class PdfExporter implements FileExporter
{

	private static final float MARGIN = 50;

	private static final float ROW_HEIGHT = 18;

	private static final float FONT_SIZE = 11;

	private final String fontResourcePath;

	public PdfExporter(String fontResourcePath)
	{
		this.fontResourcePath = fontResourcePath;
	}

	@Override
	public void exportPdf(Path outputPath,
			String title,
			List<String> headers,
			List<Map<String, String>> rows) throws IOException
	{

		if(outputPath.getParent() != null) {
			Files.createDirectories(outputPath.getParent());
		}

		try (PDDocument document = new PDDocument()) {

			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);

			try (var fontStream = getClass().getClassLoader()
					.getResourceAsStream(fontResourcePath)) {

				if(fontStream == null) {
					throw new IOException("Không tìm thấy font: " + fontResourcePath);
				}

				PDType0Font font = PDType0Font.load(document, fontStream);

				try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

					float yStart = page.getMediaBox().getHeight() - MARGIN;
					float xStart = MARGIN;

					contentStream.beginText();
					contentStream.setFont(font, 16);
					contentStream.newLineAtOffset(xStart, yStart);
					contentStream.showText(title);
					contentStream.endText();

					yStart -= 30;

					contentStream.setFont(font, FONT_SIZE);

					float tableWidth = page.getMediaBox().getWidth() - 2 * MARGIN;
					float colWidth = tableWidth / headers.size();

					float yPosition = yStart;

					for(int i = 0; i < headers.size(); i++) {
						float xPosition = xStart + i * colWidth;

						contentStream.beginText();
						contentStream.newLineAtOffset(xPosition + 2, yPosition);
						contentStream.showText(headers.get(i));
						contentStream.endText();
					}

					yPosition -= ROW_HEIGHT;

					for(Map<String, String> row : rows) {

						for(int i = 0; i < headers.size(); i++) {

							float xPosition = xStart + i * colWidth;
							String value = row.getOrDefault(headers.get(i), "");

							contentStream.beginText();
							contentStream.newLineAtOffset(xPosition + 2, yPosition);
							contentStream.showText(value);
							contentStream.endText();
						}

						yPosition -= ROW_HEIGHT;

						if(yPosition <= MARGIN) {
							contentStream.close();

							page = new PDPage(PDRectangle.A4);
							document.addPage(page);

							yPosition = page.getMediaBox().getHeight() - MARGIN;

							contentStream.beginText();
							contentStream.setFont(font, FONT_SIZE);
							contentStream.newLineAtOffset(xStart, yPosition);
							contentStream.endText();
						}
					}
				}
			}
			document.save(outputPath.toFile());
		}
	}

	@Override
	public void exportCsv(Path outputPath,
			List<String> headers,
			List<Map<String, String>> rows)
	{
		throw new UnsupportedOperationException("Use CsvExporter adapter instead");
	}

	@Override
	public void exportExcel(Path outputPath,
			List<String> headers,
			List<Map<String, String>> rows)
	{
		throw new UnsupportedOperationException("Excel not supported");
	}
}
