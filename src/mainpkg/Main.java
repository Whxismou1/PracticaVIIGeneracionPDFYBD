package mainpkg;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

public class Main {

    public static void main(String[] args) {
 try {
            String ruta = "src/resources/72331034.pdf"; // Ruta donde quieres guardar el PDF

            PdfWriter writer = new PdfWriter(ruta);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc, PageSize.LETTER);

            /**
             * *****************
             * CUADRO DATOS DEL AYUNTAMIENTO
             ******************
             */
            Table tabla1 = new Table(2);
            tabla1.setWidth(UnitValue.createPercentValue(100));

            // Primera celda de la primera tabla
            Cell cell1 = new Cell();
            cell1.setBorder(new SolidBorder(1));
            cell1.setWidth(UnitValue.createPercentValue(50));
            cell1.setTextAlignment(TextAlignment.CENTER);
            cell1.add(new Paragraph("NOMBRE"));
            cell1.add(new Paragraph("CIF: "));
            cell1.add(new Paragraph("Dirección del Ayuntamiento"));
            cell1.add(new Paragraph("Código postal-Población"));
            tabla1.addCell(cell1);

            // Segunda celda de la primera tabla
            Cell cell2 = new Cell();
            cell2.setBorder(Border.NO_BORDER);
            cell2.setPadding(10);
            cell2.setTextAlignment(TextAlignment.RIGHT);
            cell2.add(new Paragraph("IBAN: "));
            cell2.add(new Paragraph("Tipo de cálculo: "));
            cell2.add(new Paragraph("Fecha de alta: "));
            tabla1.addCell(cell2);

            // Añadir la primera tabla al documento
            doc.add(tabla1);

            // Cerrar el documento
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
