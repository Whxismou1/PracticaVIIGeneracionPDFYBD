/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Entities.Ordenanza;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.jdom2.Attribute;
import org.jdom2.Element;

/**
 *
 * @author moasin
 */
public class PDFGenerator {

    private static String path = "src/resources/recibos/";

    void createPDFContribuyente(Element contribuyente, List<List<String>> listaInfoConceptos, int trimestre, int año) {
        HashMap<String, String> map = new HashMap<>();
        List<Element> elementos = contribuyente.getChildren(); // Obtener todos los elementos hijos

        for (Element elemento : elementos) {
            String nombreElemento = elemento.getName(); // Obtener el nombre de la etiqueta
            String valorElemento = elemento.getText(); // Obtener el valor del elemento
            map.put(nombreElemento, valorElemento); // Agregar al HashMap
        }

        // Aquí tienes el HashMap con los elementos
//        for (String clave : map.keySet()) {
//            System.out.println(clave + ": " + map.get(clave));
//        }

        String trimestreFrase = "";
        switch(trimestre){
            case 1:
                trimestreFrase = "Primer trimestre de " + año;
                break;
            case 2:
                trimestreFrase = "Segundo trimestre de " + año;
                break;
            case 3:
                trimestreFrase = "Tercer trimestre de " + año;
                break;
            case 4:
                trimestreFrase = "Cuarto trimestre de " + año;
                break;
            default:
                break;
        }





        createPDF(map, listaInfoConceptos, trimestreFrase);
    }

    private void createPDF(HashMap<String, String> map, List<List<String>> listaInfoConceptos, String trimestreFrase) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String ruta = path + map.get("NIF") + map.get("nombre") + map.get("primerApellido") + map.get("segundoApellido") + ".pdf";
        String rutaIMG = "src/resources/img.png";

        try {
            PdfWriter writer = new PdfWriter(ruta);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc, PageSize.LETTER);
            List<String> listaConceptos = listaInfoConceptos.get(0);
            List<String> listaSubconceptos = listaInfoConceptos.get(1);
            List<String> listaM3Incluidos = listaInfoConceptos.get(2);
            List<String> listaBaseImponible = listaInfoConceptos.get(3);
            List<String> listaPorcentajeIVA = listaInfoConceptos.get(4);
            List<String> listaImporteIVA = listaInfoConceptos.get(5);
            List<String> listaBonificacion = listaInfoConceptos.get(6);
            
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
            otherSymbols.setDecimalSeparator(',');
            DecimalFormat df = new DecimalFormat("#", otherSymbols);;
            df.setDecimalFormatSymbols(otherSymbols);
            df.setMinimumIntegerDigits(2);
            df.setMinimumFractionDigits(2);
            df.setMaximumFractionDigits(2);
            DecimalFormat integers = new DecimalFormat("#", otherSymbols);
            integers.setMaximumFractionDigits(0);
            
        
            // Iterar sobre las listas de datos y redondear los números flotantes al formato de dos decimales
            for (int i = 0; i < listaBaseImponible.size(); i++) {
                float m3incluidos = Float.parseFloat(listaM3Incluidos.get(i));
                float baseImponible = Float.parseFloat(listaBaseImponible.get(i));
                float porcentajeIVA = Float.parseFloat(listaPorcentajeIVA.get(i));
                float importeIVA = Float.parseFloat(listaImporteIVA.get(i));
                float bonificacion = Float.parseFloat(listaBonificacion.get(i));

                listaM3Incluidos.set(i, df.format(m3incluidos));
                listaBaseImponible.set(i, df.format(baseImponible));
                listaPorcentajeIVA.set(i, df.format(porcentajeIVA));
                listaImporteIVA.set(i, df.format(importeIVA));
                listaBonificacion.set(i, df.format(bonificacion));
            }
            /**
             * *****************
             * CUADRO DATOS DEL AYUNTAMIENTO *****************
             */
            Table tabla1 = new Table(2);
            tabla1.setWidth(UnitValue.createPercentValue(100));
            Ordenanza ord = new Ordenanza();
            // Primera celda de la primera tabla
            Cell cell1 = new Cell();
            cell1.setBorder(new SolidBorder(1));
            cell1.setWidth(UnitValue.createPercentValue(50));
            cell1.setTextAlignment(TextAlignment.CENTER);
            cell1.add(new Paragraph(map.get("ordenanza")));
            cell1.add(new Paragraph("P24001017F"));
            cell1.add(new Paragraph("Calle de la Iglesia, 13"));
            cell1.add(new Paragraph("24280 Astorga León"));
            tabla1.addCell(cell1);

            // Segunda celda de la primera tabla
            Cell cell2 = new Cell();
            cell2.setBorder(Border.NO_BORDER);
            cell2.setTextAlignment(TextAlignment.RIGHT);
            cell2.setPadding(10);
            cell2.add(new Paragraph("IBAN: " + map.get("IBAN")));
            cell2.add(new Paragraph("Tipo de cálculo: " + map.get("TipoCalculo")));
            cell2.add(new Paragraph("Fecha de alta: " + map.get("fechaAlta")));
            tabla1.addCell(cell2);

            Cell cell3img = new Cell();
            cell3img.setBorder(Border.NO_BORDER);
            ImageData imageData = ImageDataFactory.create(rutaIMG);
            Image img = new Image(imageData);
            img.scaleToFit(60, 60);
            img.setAutoScale(true);
            cell3img.add(img);

            tabla1.addCell(cell3img);

            Cell cell4Dest = new Cell();
            cell4Dest.setBorder(new SolidBorder(1));
            cell4Dest.setTextAlignment(TextAlignment.RIGHT);
            cell4Dest.add(new Paragraph("Destinatario:").addStyle(new Style().setBold()).setTextAlignment(TextAlignment.LEFT));
            cell4Dest.add(new Paragraph(map.get("nombre") + " " + map.get("primerApellido") + " " + map.get("segundoApellido")));
            cell4Dest.add(new Paragraph("DNI: " + map.get("NIF")));
            cell4Dest.add(new Paragraph(map.get("direccion")));
            cell4Dest.add(new Paragraph("Astorga"));
            tabla1.addCell(cell4Dest);
            // Añadir la primera tabla al documento

            Table tabla2 = new Table(3);
            tabla2.setWidth(UnitValue.createPercentValue(100));
            tabla2.setMarginTop(30);

            Cell cellLectActual = new Cell();
            cellLectActual.setTextAlignment(TextAlignment.CENTER);
            cellLectActual.setBorderRight(Border.NO_BORDER);
            cellLectActual.add(new Paragraph("Lectura actual: " + integers.format(Float.parseFloat(map.get("lecturaActual")))));
            tabla2.addCell(cellLectActual);

            Cell cellLectAnterior = new Cell();
            cellLectAnterior.setBorderLeft(Border.NO_BORDER);
            cellLectAnterior.setBorderRight(Border.NO_BORDER);
            cellLectAnterior.setTextAlignment(TextAlignment.CENTER);
            cellLectAnterior.add(new Paragraph("Lectura anterior: " + integers.format(Float.parseFloat(map.get("lecturaAnterior")))));
            tabla2.addCell(cellLectAnterior);

            Cell cellConsumo = new Cell();
            cellConsumo.setBorderLeft(Border.NO_BORDER);
            cellConsumo.setTextAlignment(TextAlignment.CENTER);
            cellConsumo.add(new Paragraph("Consumo: " + integers.format(Float.parseFloat(map.get("consumo"))) + " metros cúbicos"));
            tabla2.addCell(cellConsumo);

            Table tabla3ParafoRecibo = new Table(1);
            tabla3ParafoRecibo.setBorder(Border.NO_BORDER);
            tabla3ParafoRecibo.setItalic();
            tabla3ParafoRecibo.setHeight(100);
            tabla3ParafoRecibo.setWidth(UnitValue.createPercentValue(100));
            Cell contentRecibo = new Cell();
            contentRecibo.add(new Paragraph("Recibo agua: " + trimestreFrase)).setVerticalAlignment(VerticalAlignment.MIDDLE).setBold();
            contentRecibo.setBorder(Border.NO_BORDER);
            tabla3ParafoRecibo.addCell(contentRecibo);
            tabla3ParafoRecibo.setTextAlignment(TextAlignment.CENTER);

            Table tabla4Recibos = new Table(7);
            tabla4Recibos.setWidth(UnitValue.createPercentValue(100));
            tabla4Recibos.setTextAlignment(TextAlignment.CENTER);
            Cell concepto = new Cell().add(new Paragraph("Concepto"));
            concepto.setBorder(Border.NO_BORDER);
            concepto.setBorderTop(new SolidBorder(2));
            concepto.setBorderBottom(new SolidBorder(2));

            Cell subConcepto = new Cell().add(new Paragraph("Subconcepto"));
            subConcepto.setBorder(Border.NO_BORDER);
            subConcepto.setBorderTop(new SolidBorder(2));
            subConcepto.setBorderBottom(new SolidBorder(2));

            Cell m3Incluidos = new Cell().add(new Paragraph("M3 incluídos"));
            m3Incluidos.setBorder(Border.NO_BORDER);
            m3Incluidos.setBorderTop(new SolidBorder(2));
            m3Incluidos.setBorderBottom(new SolidBorder(2));

            Cell baseImponible = new Cell().add(new Paragraph("B.Imponible"));
            baseImponible.setBorder(Border.NO_BORDER);
            baseImponible.setBorderTop(new SolidBorder(2));
            baseImponible.setBorderBottom(new SolidBorder(2));

            Cell IVA = new Cell().add(new Paragraph("IVA %"));
            IVA.setBorder(Border.NO_BORDER);
            IVA.setBorderTop(new SolidBorder(2));
            IVA.setBorderBottom(new SolidBorder(2));

            Cell importe = new Cell().add(new Paragraph("Importe"));
            importe.setBorder(Border.NO_BORDER);
            importe.setBorderTop(new SolidBorder(2));
            importe.setBorderBottom(new SolidBorder(2));

            Cell descuento = new Cell().add(new Paragraph("Descuento"));
            descuento.setBorder(Border.NO_BORDER);
            descuento.setBorderTop(new SolidBorder(2));
            descuento.setBorderBottom(new SolidBorder(2));

            Cell conceptoElem = new Cell();
            conceptoElem.setBorder(Border.NO_BORDER);

            Cell subConceptoElem = new Cell();
            subConceptoElem.setBorder(Border.NO_BORDER);

            Cell m3IncluidosElem = new Cell();
            m3IncluidosElem.setBorder(Border.NO_BORDER);

            Cell baseImponibleElem = new Cell();
            baseImponibleElem.setBorder(Border.NO_BORDER);

            Cell IVAElem = new Cell();
            IVAElem.setBorder(Border.NO_BORDER);

            Cell importeElem = new Cell();
            importeElem.setBorder(Border.NO_BORDER);

            Cell descuentoElem = new Cell();
            descuentoElem.setBorder(Border.NO_BORDER);

            for (int i = 0; i < listaConceptos.size(); i++) {
                conceptoElem.add(new Paragraph(listaConceptos.get(i)));
                subConceptoElem.add(new Paragraph(listaSubconceptos.get(i)));
                m3IncluidosElem.add(new Paragraph(listaM3Incluidos.get(i)));
                baseImponibleElem.add(new Paragraph(listaBaseImponible.get(i)));
                IVAElem.add(new Paragraph(listaPorcentajeIVA.get(i) + "%"));
                importeElem.add(new Paragraph(listaImporteIVA.get(i)));
                descuentoElem.add(new Paragraph(listaBonificacion.get(i)));
            }

            tabla4Recibos.addCell(concepto);
            tabla4Recibos.addCell(subConcepto);
            tabla4Recibos.addCell(m3Incluidos);
            tabla4Recibos.addCell(baseImponible);
            tabla4Recibos.addCell(IVA);
            tabla4Recibos.addCell(importe);
            tabla4Recibos.addCell(descuento);
            //elementos 
            tabla4Recibos.addCell(conceptoElem);
            tabla4Recibos.addCell(subConceptoElem);
            tabla4Recibos.addCell(m3IncluidosElem);
            tabla4Recibos.addCell(baseImponibleElem);
            tabla4Recibos.addCell(IVAElem);
            tabla4Recibos.addCell(importeElem);
            tabla4Recibos.addCell(descuentoElem);

            Table tabla5Tot = new Table(3);
            tabla5Tot.setMarginTop(20);
            tabla5Tot.setWidth(UnitValue.createPercentValue(100));
            tabla5Tot.setTextAlignment(TextAlignment.CENTER);
            Cell totalText = new Cell().add(new Paragraph("TOTALES"));
            totalText.setBorder(Border.NO_BORDER);
            totalText.setBorderTop(new SolidBorder(2));
            
            
            Cell totalBaseImp = new Cell().add(new Paragraph(df.format(Float.parseFloat(map.get("baseImponibleRecibo")))));
            totalBaseImp.setBorder(Border.NO_BORDER);
            totalBaseImp.setBorderTop(new SolidBorder(2));

            Cell totalIVA = new Cell().add(new Paragraph(df.format(Float.parseFloat(map.get("ivaRecibo")))));
            totalIVA.setTextAlignment(TextAlignment.RIGHT);
            totalIVA.setBorder(Border.NO_BORDER);
            totalIVA.setBorderTop(new SolidBorder(2));

            tabla5Tot.addCell(totalText);
            tabla5Tot.addCell(totalBaseImp);
            tabla5Tot.addCell(totalIVA);

            Table tabla6TotalesValor = new Table(2);
            tabla6TotalesValor.setMarginTop(30);
            tabla6TotalesValor.setBorder(Border.NO_BORDER);
            tabla6TotalesValor.setWidth(UnitValue.createPercentValue(100));
            Cell totalBases = new Cell().add(new Paragraph("TOTAL BASE IMPONIBLE......................................"));
            totalBases.add(new Paragraph("TOTAL IVA................................................."));
            totalBases.setBorder(Border.NO_BORDER);

            Cell totalValores = new Cell().add(new Paragraph(df.format(Float.parseFloat(map.get("baseImponibleRecibo")))));
            totalValores.setBorder(Border.NO_BORDER);
            totalValores.add(new Paragraph(df.format(Float.parseFloat(map.get("ivaRecibo")))));
            totalValores.setTextAlignment(TextAlignment.RIGHT);
            tabla6TotalesValor.addCell(totalBases);
            tabla6TotalesValor.addCell(totalValores);

            Table tabla7ReciboFinal = new Table(2);
            tabla7ReciboFinal.setMarginTop(30);
            tabla7ReciboFinal.setWidth(UnitValue.createPercentValue(100));
            Cell totReciboFinal = new Cell().add(new Paragraph("TOTAL RECIBO..........................................................."));
            totReciboFinal.setTextAlignment(TextAlignment.LEFT);
            totReciboFinal.setBorderBottom(Border.NO_BORDER);
            totReciboFinal.setBorderLeft(Border.NO_BORDER);
            totReciboFinal.setBorderRight(Border.NO_BORDER);
            totReciboFinal.setBorderTop(new SolidBorder(2));

            Cell totalImporteFinal = new Cell().add(new Paragraph(df.format(Float.parseFloat(map.get("totalRecibo")))));
            totalImporteFinal.setTextAlignment(TextAlignment.RIGHT);
            totalImporteFinal.setBorderBottom(Border.NO_BORDER);
            totalImporteFinal.setBorderLeft(Border.NO_BORDER);
            totalImporteFinal.setBorderRight(Border.NO_BORDER);
            totalImporteFinal.setBorderTop(new SolidBorder(2));

            tabla7ReciboFinal.addCell(totReciboFinal);
            tabla7ReciboFinal.addCell(totalImporteFinal);

            doc.add(tabla1);
            doc.add(tabla2);
            doc.add(tabla3ParafoRecibo);
            doc.add(tabla4Recibos);
            doc.add(tabla5Tot);
            doc.add(tabla6TotalesValor);
            doc.add(tabla7ReciboFinal);
            // Cerrar el documento
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
        void createPDFResumen(Attribute totalBaseImponible, Attribute totalIVA, Attribute totalRecibos, int numTrimestre, int año) {
        String ruta = path + "resumen" + ".pdf";
        String trimestreFrase = "";
        switch(numTrimestre){
            case 1:
                trimestreFrase = "Primer trimestre de " + año;
                break;
            case 2:
                trimestreFrase = "Segundo trimestre de " + año;
                break;
            case 3:
                trimestreFrase = "Tercer trimestre de " + año;
                break;
            case 4:
                trimestreFrase = "Cuarto trimestre de " + año;
                break;
            default:
                break;
        }
        try {
            PdfWriter writer = new PdfWriter(ruta);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc, PageSize.LETTER);
            
            Table tabla1 = new Table(1);
            tabla1.setWidth(UnitValue.createPercentValue(100));
            Cell info = new Cell();
            info.add(new Paragraph("RESUMEN PADRON DE AGUA " + trimestreFrase));
            info.add(new Paragraph("TOTAL BASE IMPONIBLE.................." + totalBaseImponible.getValue()));
            info.add(new Paragraph("TOTAL IVA..........................................." + totalIVA.getValue()));
            info.add(new Paragraph("TOTAL RECIBOS..............................." + totalRecibos.getValue()));
            
            tabla1.addCell(info);
            doc.add(tabla1);
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
            
    }

}
