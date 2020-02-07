/**
 * Converter docx to pdf
 */

package com.axeane.doc2pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class Converter {
  public void convert(String docPath, String pdfPath) {
    try {
      InputStream doc = new FileInputStream(new File(docPath));
      XWPFDocument document = new XWPFDocument(doc);
      PdfOptions options = PdfOptions.create();
      OutputStream out = new FileOutputStream(new File(pdfPath));
      PdfConverter.getInstance().convert(document, out, options);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }
}
