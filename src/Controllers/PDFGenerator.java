/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.util.HashMap;
import java.util.List;
import org.jdom2.Element;

/**
 *
 * @author moasin
 */
public class PDFGenerator {

    private static String path = "src/resources/";

    void createPDFContribuyente(Element contribuyente) {
        HashMap<String, String> map = new HashMap<>();
        List<Element> elementos = contribuyente.getChildren(); // Obtener todos los elementos hijos

        for (Element elemento : elementos) {
            String nombreElemento = elemento.getName(); // Obtener el nombre de la etiqueta
            String valorElemento = elemento.getText(); // Obtener el valor del elemento
            map.put(nombreElemento, valorElemento); // Agregar al HashMap
        }

        // Aquí tienes el HashMap con los elementos
        for (String clave : map.keySet()) {
            System.out.println(clave + ": " + map.get(clave));
        }

        createPDF(map);
    }

    private void createPDF(HashMap<String, String> map) {
        String nombreRuta = map.get("NIF") + map.get("nombre") + map.get("primerApellido") + map.get("segundoApellido") + ".pdf";

        try {

            PdfWriter writer = new PdfWriter(nombreRuta);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc, PageSize.LETTER);

            /**
             * *****************
             * CUADRO DATOS DEL AYUNTAMIENTO *****************
             */
            Table tabla1 = new Table(2);
            tabla1.setWidth(UnitValue.createPercentValue(100));

            // Primera celda de la primera tabla
            Cell cell1 = new Cell();
            cell1.setBorder(new SolidBorder(1));
            cell1.setWidth(UnitValue.createPercentValue(50));
            cell1.setTextAlignment(TextAlignment.CENTER);
            cell1.add(new Paragraph("ASTORGA"));
            cell1.add(new Paragraph("CIF: "));
            cell1.add(new Paragraph("Dirección del Ayuntamiento"));
            cell1.add(new Paragraph("Código postal-Población"));
            tabla1.addCell(cell1);

            // Segunda celda de la primera tabla
            Cell cell2 = new Cell();
            cell2.setBorder(Border.NO_BORDER);
            cell2.setPadding(10);
            cell2.setTextAlignment(TextAlignment.RIGHT);
            String IBAN = "IBAN: " + map.get("IBAN");
            cell2.add(new Paragraph(IBAN));
            String TipoCalculo = "Tipo de cálculo: " + map.get("TipoCalculo");
            cell2.add(new Paragraph(TipoCalculo));
            String fechaAlta = "Fecha de alta: " + map.get("fechaAlta");
            cell2.add(new Paragraph(fechaAlta));
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
