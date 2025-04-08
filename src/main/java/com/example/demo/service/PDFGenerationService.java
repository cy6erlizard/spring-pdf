package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class PDFGenerationService {

    // Folder where HTML templates are stored; default is "templates"
    @Value("${pdf.templates.path:templates}")
    private String templatesPath;

    // Full (absolute) path to the wkhtmltopdf executable
    @Value("${pdf.wkhtmltopdf.path}")
    private String wkhtmltopdfPath;

    // Folder to store the generated PDF files
    @Value("${pdf.storage.path}")
    private String pdfStoragePath;

    /**
     * Generates a PDF file using the given HTML template and placeholder replacements.
     *
     * @param templateName the HTML template file name (e.g., "resume.html")
     * @param replacements key/value pairs to replace within the template (e.g., "$name")
     * @return a File object pointing to the generated PDF file
     * @throws Exception if there is an error during file creation or PDF generation
     */
    public File generatePdf(String templateName, Map<String, String> replacements) throws Exception {
        // 1. Load the HTML template from the classpath (src/main/resources/templates)
        InputStream templateStream = getClass().getClassLoader().getResourceAsStream(templatesPath + "/" + templateName);
        if (templateStream == null) {
            throw new FileNotFoundException("Template file " + templateName + " not found in " + templatesPath);
        }
        String htmlContent = new String(templateStream.readAllBytes());

        // 2. Replace placeholders in the HTML (e.g., "$name") with actual values
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            // You can customize how you do replacements; here we simply replace "$key" with its value.
            htmlContent = htmlContent.replace("$" + entry.getKey(), entry.getValue());
        }

        // 3. Write the processed HTML to a temporary file
        Path tempHtmlPath = Files.createTempFile("temp_pdf_", ".html");
        Files.writeString(tempHtmlPath, htmlContent);

        // 4. Ensure the PDF storage directory exists
        Files.createDirectories(Path.of(pdfStoragePath));

        // 5. Define a unique file name for the PDF
        String pdfFileName = "doc_" + System.currentTimeMillis() + ".pdf";
        Path pdfFilePath = Path.of(pdfStoragePath, pdfFileName);

        // 6. Build the command to execute wkhtmltopdf using the absolute path.
        // Enclose paths in quotes to handle spaces in filenames.
        String command = String.format("\"%s\" \"%s\" \"%s\"",
                wkhtmltopdfPath,
                tempHtmlPath.toAbsolutePath().toString(),
                pdfFilePath.toAbsolutePath().toString());

        // 7. Execute the command
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();

        // 8. Remove the temporary HTML file
        Files.deleteIfExists(tempHtmlPath);

        // 9. Check for errors
        if (exitCode != 0) {
            // Capture error output from wkhtmltopdf for debugging
            InputStream errorStream = process.getErrorStream();
            String errorOutput = new String(errorStream.readAllBytes());
            throw new RuntimeException("wkhtmltopdf execution failed with exit code " + exitCode + ": " + errorOutput);
        }

        // 10. Return the generated PDF file
        return pdfFilePath.toFile();
    }
}
