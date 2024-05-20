
package Controllers;

import Entities.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ExcelManager {

    public void writeExcel(List<Contribuyente> listaContribyente){
         String filePath = "src/resources/SistemasAgua.xlsx";
         try {
            FileInputStream fileInputStream = new FileInputStream(new File(filePath));
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            int actualRow = 1;
            System.out.println("Escribiendo datos corregidos en el archivo Excel...");
            
            for (Contribuyente actualContribuyente : listaContribyente) {
                Row row = sheet.createRow(actualRow++);

                if (!isEmptyContribuyente(actualContribuyente)) {
                    writeContribuyenteToRow(actualContribuyente, row);
                }
            System.out.println("Progreso: " + (actualRow - 1) + "/" + listaContribyente.size());
           }
            
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

            System.out.println("Los datos se han escrito correctamente en el archivo Excel.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Contribuyente> readExcelContribuyente() {
        FileInputStream f = null;
        XSSFWorkbook libro = null;
        List<Contribuyente> entradasExcel = new ArrayList<>();
        try {
            f = new FileInputStream("src/resources/SistemasAgua.xlsx");
            libro = new XSSFWorkbook(f);
            XSSFSheet hojaContribuyente = libro.getSheetAt(0);

            Row encabezado = hojaContribuyente.getRow(0);

            if (encabezado != null) {
                List<String> nombresColumnas = new ArrayList<>();
                Iterator<Cell> celdasEncabezado = encabezado.cellIterator();
                while (celdasEncabezado.hasNext()) {
                    Cell celda = celdasEncabezado.next();
                    nombresColumnas.add(celda.getStringCellValue());
                }

                Iterator<Row> filas = hojaContribuyente.iterator();
                filas.next(); // Saltar la primera fila (encabezado)
                Long actualId = 2L;
                while (filas.hasNext()) {
                    Row fila = filas.next();
                    Iterator<Cell> celdas = fila.cellIterator();
                    Contribuyente contribuyente = new Contribuyente();
                    contribuyente.setId(actualId);
                    // Recorremos las celdas de la fila
                    while (celdas.hasNext()) {
                        Cell celda = celdas.next();
                        int indiceCelda = celda.getColumnIndex();
                        String nombreColumna = nombresColumnas.get(indiceCelda);
                        asignarValorContribuyente(contribuyente, nombreColumna, celda);
                    }

                    // Agregar el contribuyente a la lista
                    entradasExcel.add(contribuyente);
                    actualId++;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (libro != null) {
                    libro.close();
                }
                if (f != null) {
                    f.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entradasExcel;
    }
    
    public List<Ordenanza> readExcelOrdenanza() {
        FileInputStream f = null;
        XSSFWorkbook libro = null;
        List<Ordenanza> entradasExcel = new ArrayList<>();
        try {
            f = new FileInputStream("src/resources/SistemasAgua.xlsx");
            libro = new XSSFWorkbook(f);
            XSSFSheet hojaOrdenanza = libro.getSheetAt(1);

            Row encabezado = hojaOrdenanza.getRow(0);

            if (encabezado != null) {
                List<String> nombresColumnas = new ArrayList<>();
                Iterator<Cell> celdasEncabezado = encabezado.cellIterator();
                while (celdasEncabezado.hasNext()) {
                    Cell celda = celdasEncabezado.next();
                    nombresColumnas.add(celda.getStringCellValue());
                }

                Iterator<Row> filas = hojaOrdenanza.iterator();
                filas.next(); // Saltar la primera fila (encabezado)
                Long actualId = 2L;
                while (filas.hasNext()) {
                    Row fila = filas.next();
                    Iterator<Cell> celdas = fila.cellIterator();
                    Ordenanza ordenanza = new Ordenanza();
                    // Recorremos las celdas de la fila
                    while (celdas.hasNext()) {
                        Cell celda = celdas.next();
                        int indiceCelda = celda.getColumnIndex();
                        String nombreColumna = nombresColumnas.get(indiceCelda);
                        asignarValorOrdenanza(ordenanza, nombreColumna, celda);
                    }

                    // Agregar el contribuyente a la lista
                    entradasExcel.add(ordenanza);
                    actualId++;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (libro != null) {
                    libro.close();
                }
                if (f != null) {
                    f.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entradasExcel;
    }

    private String getCellValue(Cell celda) {
        if (celda == null) {
            return null;
        }

        switch (celda.getCellType()) {
            case STRING:
                String value = celda.getStringCellValue();
                return value.isEmpty() ? null : value;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(celda)) {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    return dateFormat.format(celda.getDateCellValue());
                } else {
                    return String.valueOf(celda.getNumericCellValue());
                }
                
            case BLANK:
                return null;
            default:
                return null;
        }

    }
    
    private void asignarValorOrdenanza(Ordenanza ordenanza, String nombreColumna, Cell celda) {
        switch (nombreColumna) {
            case "Pueblo":
                ordenanza.setPueblo(getCellValue(celda));
                break;
            case "TipoCalculo":
                ordenanza.setTipoCalculo(getCellValue(celda));
                break;
            case "idOrdenanza":
                ordenanza.setId(getCellValue(celda));
                break;
            case "Concepto":
                ordenanza.setConcepto(getCellValue(celda));
                break;
            case "Subconcepto":
                ordenanza.setSubconcepto(getCellValue(celda));
                break;
            case "Descripcion":
                ordenanza.setDescripcion(getCellValue(celda));
                break;
            case "Acumulable":
                ordenanza.setAcumulable(getCellValue(celda));
                break;
            case "Precio fijo":
                ordenanza.setPrecioFijo(getCellValue(celda));
                break;
            case "m3 incluidos":
                ordenanza.setM3incluidos(getCellValue(celda));
                break;
            case "Precio m3":
                ordenanza.setPreciom3(getCellValue(celda));
                break;
            case "PorcentajeSobreOtroConcepto":
                ordenanza.setPorcentajeSobreOtroConcepto(getCellValue(celda));
                break;
            case "SobreQueConcepto":
                ordenanza.setSobreQueConcepto(getCellValue(celda));
                break;
            case "IVA":
                ordenanza.setIVA(getCellValue(celda));
                break;
        }
    }

    private void asignarValorContribuyente(Contribuyente contribuyente, String nombreColumna, Cell celda) {
        switch (nombreColumna) {
            case "Nombre":
                contribuyente.setNombre(getCellValue(celda));
                break;
            case "Apellido1":
                contribuyente.setApellido1(getCellValue(celda));
                break;
            case "Apellido2":
                contribuyente.setApellido2(getCellValue(celda));
                break;
            case "NIFNIE":
                contribuyente.setNIFNIE(getCellValue(celda));
                break;
            case "Direccion":
                contribuyente.setDireccion(getCellValue(celda));
                break;
            case "Numero":
                contribuyente.setNumero(getCellValue(celda));
                break;
            case "PaisCCC":
                contribuyente.setPaisCCC(getCellValue(celda));
                break;
            case "CCC":
                contribuyente.setCCC(getCellValue(celda));
                break;
            case "IBAN":
                contribuyente.setIBAN(getCellValue(celda));
                break;
            case "Email":
                contribuyente.setEmail(getCellValue(celda));
                break;
            case "Exencion":
                contribuyente.setExencion(getCellValue(celda));
                break;
            case "Bonificacion":
                contribuyente.setBonificacion(getCellValue(celda));
                break;
            case "LecturaAnterior":
                contribuyente.setLecturaAnterior(getCellValue(celda));
                break;
            case "LecturaActual":
                contribuyente.setLecturaActual(getCellValue(celda));
                break;
            case "FechaAlta":
                contribuyente.setFechaAlta(getCellValue(celda));
                break;
            case "FechaBaja":
                contribuyente.setFechaBaja(getCellValue(celda));
                break;
            case "conceptosACobrar":
                contribuyente.setConceptosACobrar(getCellValue(celda));
                break;
        }
    }
    
    private void writeContribuyenteToRow(Contribuyente contribuyente, Row row) {
        row.createCell(0).setCellValue(contribuyente.getNombre());
        row.createCell(1).setCellValue(contribuyente.getApellido1());
        row.createCell(2).setCellValue(contribuyente.getApellido2());
        row.createCell(3).setCellValue(contribuyente.getNIFNIE());
        row.createCell(4).setCellValue(contribuyente.getDireccion());
        row.createCell(5).setCellValue(contribuyente.getNumero());
        row.createCell(6).setCellValue(contribuyente.getPaisCCC());
        row.createCell(7).setCellValue(contribuyente.getCCC());
        row.createCell(8).setCellValue(contribuyente.getIBAN());
        row.createCell(9).setCellValue(contribuyente.getEmail());
        row.createCell(10).setCellValue(contribuyente.getExencion());
        row.createCell(11).setCellValue(contribuyente.getBonificacion());
        row.createCell(12).setCellValue(contribuyente.getLecturaAnterior());
        row.createCell(13).setCellValue(contribuyente.getLecturaActual());
        row.createCell(14).setCellValue(contribuyente.getFechaAlta());
        row.createCell(15).setCellValue(contribuyente.getFechaBaja());
        row.createCell(16).setCellValue(contribuyente.getConceptosACobrar());
    }

    public boolean isEmptyContribuyente(Contribuyente contribuyente) {
        return (contribuyente == null || (
                contribuyente.getNIFNIE() == null &&
                contribuyente.getCCC() == null &&
                contribuyente.getIBAN() == null &&
                contribuyente.getEmail() == null &&
                contribuyente.getExencion() == null &&
                contribuyente.getBonificacion() == null &&
                contribuyente.getLecturaAnterior() == null &&
                contribuyente.getLecturaActual() == null &&
                contribuyente.getFechaAlta() == null &&
                contribuyente.getFechaBaja() == null &&
                contribuyente.getConceptosACobrar() == null));
    }
}
