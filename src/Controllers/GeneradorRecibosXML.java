/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Entities.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author moasin
 */
public class GeneradorRecibosXML {


    private List<Element> listaContrValor = new ArrayList<>();
    private static String path = "src/resources/recibos.xml";

    public void generateRecibeXML(List<Contribuyente> listaContribuyentes, List<Ordenanza> listaOrdenanza, String userInput) {
        PDFGenerator pdf = new PDFGenerator();
        try {
            ExcelManager excMang = new ExcelManager();
            Element contribuyentes = new Element("Recibos");
            Document doc = new Document(contribuyentes);

            int numTrimestre = Integer.parseInt(userInput.substring(0, 1));
            int año = Integer.parseInt(userInput.substring(2).trim());
            System.out.println(numTrimestre + "T-" + año);

            // Calcular la fecha de inicio y fin del trimestre
            LocalDate fechaInicioTrimestre = calcularInicioTrimestre(numTrimestre, año);
            LocalDate fechaFinTrimestre = calcularFinTrimestre(numTrimestre, año);

            // Formateador para la fecha de alta del contribuyente
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            // Filtrar los contribuyentes por la fecha de alta
            List<Contribuyente> contribuyentesFiltrados = filtrarContribuyentesPorFecha(listaContribuyentes, fechaFinTrimestre, fechaInicioTrimestre, formatter);
//
            float baseImponible = 0;
            float iva = 0;
            float recibos = 0;

            for (Contribuyente contr : contribuyentesFiltrados) {
                LocalDate fechaAlta = LocalDate.parse(contr.getFechaAlta(), formatter);
                if (!excMang.isEmptyContribuyente(contr)) {
                    Element contribuyente = new Element("ReciboPDF");
                    Element contribuyenteXML = new Element("Recibo");
                    Attribute attr = new Attribute("id", contr.getId().toString());
                    contribuyenteXML.setAttribute(attr.clone());
                    contribuyente.setAttribute(attr);
                    
                    Element exencion = new Element("Exencion");
                    //    <idFilaExcel>2</idFilaExcel>
                    Element idFilaExcel = new Element("idFilaExcel");
                    //    <nombre>Juan</nombre>
                    Element nombre = new Element("nombre");
                    //    <primerApellido>Martinez</primerApellido>
                    Element apellido1 = new Element("primerApellido");
                    //    <segundoApellido>Dominguez</segundoApellido>
                    Element apellido2 = new Element("segundoApellido");
                    //    <NIF>09632539R</NIF>
                    Element nif = new Element("NIF");
                    Element direccion = new Element("direccion");
                    //    <IBAN>DK7331645124473461205164</IBAN>
                    Element iban = new Element("IBAN");
                    //    <lecturaActual>106</lecturaActual>
                    Element lecturaActual = new Element("lecturaActual");
                    //    <lecturaAnterior>21</lecturaAnterior>
                    Element lecturaAnterior = new Element("lecturaAnterior");
                    //    <consumo>85</consumo>
                    Element consumo = new Element("consumo");
                    Element bonificacion = new Element("bonificacion");
                    Element fechaAltaElem = new Element("fechaAlta");
                    Element tipoCalculo = new Element("TipoCalculo");

                    Element concepto = new Element("Concepto");
                    //    <lecturaAnterior>21</lecturaAnterior>
                    Element subconcepto = new Element("Subconcepto");
                    //    <consumo>85</consumo>
                    Element m3incluidos = new Element("m3incluidos");
                    Element baseImponibleRecibo = new Element("BaseImponible");
                    Element porcentajeIVA = new Element("PorcentajeIVA");
                    Element importeIVA = new Element("ImporteIVA");
                    Element bonificacionInfo = new Element("BonificacionInfo");
                    Element importeBonificacion = new Element("importeBonificacion");


                    //    <baseImponibleRecibo>34.5</baseImponibleRecibo>
                    Element baseImponibleReciboContribuyente = new Element("baseImponibleRecibo");
                    //    <ivaRecibo>7.245</ivaRecibo>
                    Element ivaReciboContribuyente = new Element("ivaRecibo");
                    //    <totalRecibo>41.745</totalRecibo>
                    Element totalReciboContribuyente = new Element("totalRecibo");

                    Element ordenanza = new Element("ordenanza");
                    
                    ordenanza.setText(listaOrdenanza.get(0).getPueblo());
                    
                    
                    exencion.setText(contr.getExencion());
                    idFilaExcel.setText(String.valueOf(contr.getId()));
                    nombre.setText(contr.getNombre());
                    apellido1.setText(contr.getApellido1());
                    apellido2.setText(contr.getApellido2());
                    nif.setText(contr.getNIFNIE());
                    String direccionCompleta = contr.getDireccion() + " " + contr.getNumero();
                    direccion.setText(direccionCompleta);
                    iban.setText(contr.getIBAN());
                    lecturaActual.setText(contr.getLecturaActual());
                    lecturaAnterior.setText(contr.getLecturaAnterior());
                    bonificacion.setText(contr.getBonificacion());
                    fechaAltaElem.setText(contr.getFechaAlta());

                    float cons = 0;
                    if (contr.getLecturaActual() != null && contr.getLecturaAnterior() != null) {
                        cons = Float.parseFloat(contr.getLecturaActual()) - Float.parseFloat(contr.getLecturaAnterior());
                        consumo.setText(String.valueOf(cons));
                    }

                    float baseEachOne = 0;
                    float IVAEachOne = 0;
                    float totalEachOne = 0;

                    float[] resultsXml = {0, 0, 0};

                    List<String> listaConceptos = new ArrayList<>();
                    List<String> listaSubconceptos = new ArrayList<>();
                    List<String> listaM3Incluidos = new ArrayList<>();
                    List<String> listaBaseImponible = new ArrayList<>();
                    List<String> listaPorcentajeIVA = new ArrayList<>();
                    List<String> listaImporteIVA = new ArrayList<>();
                    List<String> listaBonificacion = new ArrayList<>();
                    List<String> listaImporteBonificacion = new ArrayList<>();

                    List<List<String>> listaInfoConceptos = new ArrayList<>();
                    listaInfoConceptos.add(listaConceptos);
                    listaInfoConceptos.add(listaSubconceptos);
                    listaInfoConceptos.add(listaM3Incluidos);
                    listaInfoConceptos.add(listaBaseImponible);
                    listaInfoConceptos.add(listaPorcentajeIVA);
                    listaInfoConceptos.add(listaImporteIVA);
                    listaInfoConceptos.add(listaBonificacion);                    
                    listaInfoConceptos.add(listaImporteBonificacion);                    
                    
                    if (contr.getConceptosACobrar() != null) {
                        int[] conceptosInt = Arrays.stream(contr.getConceptosACobrar().split(" "))
                                                    .mapToInt(Integer::parseInt)
                                                    .toArray();

                        // Paso 2: Ordenar el array de int
                        Arrays.sort(conceptosInt);

                        // Paso 3: Convertir el array de int de nuevo a un array de String
                        String[] conceptos = Arrays.stream(conceptosInt).mapToObj(String::valueOf).toArray(String[]::new);
                        tipoCalculo.setText(getTipoCalculo(conceptos[0], listaOrdenanza));
                        Set<String> conceptosVisitados = new HashSet<>();
                        for (int j = 0; j < conceptos.length; j++) {
                            float[] resultado = conceptos(conceptos[j], contr.getBonificacion(), cons, baseEachOne, IVAEachOne, listaOrdenanza, listaInfoConceptos, conceptosVisitados);
                            baseEachOne = resultado[0];
                            //System.out.println("Concepto actual numero: " + conceptos[j]);
                            //System.out.println("Base del " + contr.getId() + "es: " + base);
                            IVAEachOne = resultado[1];
                            //System.out.println("IVA del " + contr.getId() + "es: " + IVA);
                            totalEachOne = baseEachOne + IVAEachOne;
                            //System.out.println("Total del " + contr.getId() + "es: " + total);
                        }
                    }
                    concepto.setText(listaInfoConceptos.get(0).toString());
                    subconcepto.setText(listaInfoConceptos.get(1).toString());
                    m3incluidos.setText(listaInfoConceptos.get(2).toString());
                    baseImponibleRecibo.setText(listaInfoConceptos.get(3).toString());
                    porcentajeIVA.setText(listaInfoConceptos.get(4).toString());
                    importeIVA.setText(listaInfoConceptos.get(5).toString());
                    bonificacionInfo.setText(listaInfoConceptos.get(6).toString());
                    importeBonificacion.setText(listaInfoConceptos.get(7).toString());
//                    System.out.println("waaa: " + importeBonificacion.getValue());
                    if (contr.getExencion().toUpperCase().equals("S")) {
                        baseEachOne = 0;
                        IVAEachOne = 0;
                        totalEachOne = 0;
                    }
                    
                    baseImponible += baseEachOne;
                    iva += IVAEachOne;
                    recibos += totalEachOne;

                    baseImponibleReciboContribuyente.setText(String.valueOf(baseEachOne));
                    ivaReciboContribuyente.setText(String.valueOf(IVAEachOne));
                    totalReciboContribuyente.setText(String.valueOf(totalEachOne));

                    contribuyenteXML.addContent(exencion.clone());
                    contribuyenteXML.addContent(idFilaExcel.clone());
                    contribuyenteXML.addContent(nombre.clone());
                    contribuyenteXML.addContent(apellido1.clone());
                    contribuyenteXML.addContent(apellido2.clone());
                    contribuyenteXML.addContent(nif.clone());
                    contribuyenteXML.addContent(iban.clone());
                    contribuyenteXML.addContent(lecturaActual.clone());  
                    contribuyenteXML.addContent(lecturaAnterior.clone());
                    contribuyenteXML.addContent(consumo.clone());
                    contribuyenteXML.addContent(baseImponibleReciboContribuyente.clone());
                    contribuyenteXML.addContent(ivaReciboContribuyente.clone());
                    contribuyenteXML.addContent(totalReciboContribuyente.clone());

                    contribuyente.addContent(idFilaExcel);
                    contribuyente.addContent(nombre);
                    contribuyente.addContent(apellido1);
                    contribuyente.addContent(apellido2);
                    contribuyente.addContent(nif);
                    contribuyente.addContent(direccion);
                    contribuyente.addContent(iban);
                    contribuyente.addContent(fechaAltaElem);
                    contribuyente.addContent(exencion);
                    contribuyente.addContent(bonificacion);
                    contribuyente.addContent(lecturaAnterior);
                    contribuyente.addContent(lecturaActual);
                    contribuyente.addContent(consumo);
                    contribuyente.addContent(tipoCalculo);
                    contribuyente.addContent(concepto);
                    contribuyente.addContent(subconcepto);
                    contribuyente.addContent(m3incluidos);
                    contribuyente.addContent(baseImponibleRecibo);
                    contribuyente.addContent(porcentajeIVA);
                    contribuyente.addContent(importeIVA);
                    contribuyente.addContent(bonificacionInfo);
                    contribuyente.addContent(importeBonificacion);
                    contribuyente.addContent(ordenanza);

                    contribuyente.addContent(baseImponibleReciboContribuyente);
                    contribuyente.addContent(ivaReciboContribuyente);
                    contribuyente.addContent(totalReciboContribuyente);
                    
                    listaContrValor.add(contribuyente);
                    pdf.createPDFContribuyente(contribuyente, listaInfoConceptos, numTrimestre, año);
                    contribuyentes.addContent(contribuyenteXML);
                }

            }

            Attribute fechaRecibo = new Attribute("fechaRecibo", LocalDate.now().format(formatter));
            Attribute fechaPadron = new Attribute("fechaPadron", userInput);
            Attribute totalBaseImponible = new Attribute("totalBaseImponible", String.valueOf(baseImponible));
            Attribute totalIVA = new Attribute("totalIVA", String.valueOf(iva));
            Attribute totalRecibos = new Attribute("totalRecibos", String.valueOf(recibos));
            
            contribuyentes.setAttribute(fechaRecibo);
            contribuyentes.setAttribute(fechaPadron);
            contribuyentes.setAttribute(totalBaseImponible);
            contribuyentes.setAttribute(totalIVA);
            contribuyentes.setAttribute(totalRecibos);

            XMLOutputter xml = new XMLOutputter();
            xml.setFormat(Format.getPrettyFormat());
            xml.output(doc, new FileWriter(path));
            
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
            otherSymbols.setDecimalSeparator(',');
            DecimalFormat df = new DecimalFormat("#", otherSymbols);;
            df.setDecimalFormatSymbols(otherSymbols);
            df.setMinimumIntegerDigits(2);
            df.setMinimumFractionDigits(2);
            df.setMaximumFractionDigits(2);
            
            pdf.createPDFResumen(totalBaseImponible, totalIVA, totalRecibos,numTrimestre, año, df);
        } catch (IOException ex) {
            Logger.getLogger(GeneradorRecibosXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private LocalDate calcularInicioTrimestre(int numTrimestre, int año) {
        switch (numTrimestre) {
            case 1:
                return LocalDate.of(año, 1, 1);
            case 2:
                return LocalDate.of(año, 4, 1);
            case 3:
                return LocalDate.of(año, 7, 1);
            case 4:
                return LocalDate.of(año, 10, 1);
            default:
                throw new IllegalArgumentException("Número de trimestre inválido: " + numTrimestre);
        }
    }

    private LocalDate calcularFinTrimestre(int numTrimestre, int año) {
        switch (numTrimestre) {
            case 1:
                return LocalDate.of(año, 3, 31);
            case 2:
                return LocalDate.of(año, 6, 30);
            case 3:
                return LocalDate.of(año, 9, 30);
            case 4:
                return LocalDate.of(año, 12, 31);
            default:
                throw new IllegalArgumentException("Número de trimestre inválido: " + numTrimestre);
        }
    }

    private List<Contribuyente> filtrarContribuyentesPorFecha(List<Contribuyente> contribuyentes, LocalDate fechaFinTrimestre, LocalDate fechaInicioTrimestre, DateTimeFormatter formatter) {
        List<Contribuyente> contribuyentesFiltrados = new ArrayList<>();
        ExcelManager excMang = new ExcelManager();
        for (Contribuyente contribuyente : contribuyentes) {
            if (!excMang.isEmptyContribuyente(contribuyente)) {
                String fechaAltaString = contribuyente.getFechaAlta();
                String fechaBajaString = contribuyente.getFechaBaja();
                try {
                    LocalDate fechaAlta = LocalDate.parse(fechaAltaString, formatter);
                    LocalDate fechaBaja = null;

                    if (fechaBajaString != null) {
                        fechaBaja = LocalDate.parse(fechaBajaString, formatter);
                    }

                    boolean estaActivoDuranteElTrimestre = fechaAlta.isBefore(fechaFinTrimestre)
                            && (fechaBaja == null || !fechaBaja.isBefore(fechaInicioTrimestre));
                    if (estaActivoDuranteElTrimestre) {
                        contribuyentesFiltrados.add(contribuyente);

                    }

                } catch (DateTimeParseException e) {
                    System.err.println("Fecha de alta inválida para el contribuyente " + contribuyente.getId() + ": " + fechaAltaString);
                }
            }
        }
        return contribuyentesFiltrados;
    }

    public void reciboInfo(List<List<String>> listaInfoConceptos, List<String> datos) {
        for (int i = 0; i < listaInfoConceptos.size(); i++) {
            listaInfoConceptos.get(i).add(datos.get(i));
        }
    }

    public float[] conceptos(String concepto, String bonificacion, float cons, float base, float IVA, List<Ordenanza> listaOrdenanza, List<List<String>> listaInfoConceptos, Set<String> conceptosVisitados) {
        float[] resultado = new float[2];
        List<String> datos = new ArrayList<>();
        for (int i = 0; i < listaOrdenanza.size(); i++) {
            float id = Float.parseFloat(listaOrdenanza.get(i).getId());
            if (concepto.equals(String.valueOf((int) id))) {
                float consTemp = cons;
                if ("Agua".equals(listaOrdenanza.get(i).getConcepto()) && "Fijo".equals(listaOrdenanza.get(i).getSubconcepto()) && "N".equals(listaOrdenanza.get(i).getAcumulable())) {
                    base += Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100));
                    IVA += base * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100;
                    consTemp -= Float.parseFloat(listaOrdenanza.get(i).getM3incluidos());
                    datos.add(listaOrdenanza.get(i).getConcepto());
                    datos.add(listaOrdenanza.get(i).getSubconcepto());
                    datos.add(listaOrdenanza.get(i).getM3incluidos());
                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100))));
                    datos.add(listaOrdenanza.get(i).getIVA());
                    datos.add(String.valueOf(base * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100));
                    datos.add(bonificacion);
                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (Float.parseFloat(bonificacion) / 100)));
                    reciboInfo(listaInfoConceptos, datos);
                    datos.clear();
                    if (consTemp > 0) {
                        for (int j = 1; j < listaOrdenanza.size() - i; j++) {
                            if (listaOrdenanza.get(i).getId().equals(listaOrdenanza.get(i + j).getId())) {
                                if (consTemp <= Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos())) {
                                    datos.add(listaOrdenanza.get(i + j).getConcepto());
                                    datos.add(listaOrdenanza.get(i + j).getSubconcepto());
                                    datos.add(String.valueOf(consTemp));
                                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100))));
                                    datos.add(listaOrdenanza.get(i + j).getIVA());
                                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * (1 - (Float.parseFloat(bonificacion) / 100))) );
                                    datos.add(bonificacion);
                                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * ((Float.parseFloat(bonificacion) / 100))));
                                    reciboInfo(listaInfoConceptos, datos);
                                    datos.clear();
                                    base += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100));
                                    IVA += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * (1 - (Float.parseFloat(bonificacion) / 100));
                                    consTemp = 0;
                                } else {
                                    datos.add(listaOrdenanza.get(i + j).getConcepto());
                                    datos.add(listaOrdenanza.get(i + j).getSubconcepto());
                                    datos.add(listaOrdenanza.get(i + j).getM3incluidos());
                                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos())  * (1 - (Float.parseFloat(bonificacion) / 100))));
                                    datos.add(listaOrdenanza.get(i + j).getIVA());
                                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos()) * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * (1 - (Float.parseFloat(bonificacion) / 100))));
                                    datos.add(bonificacion);
                                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos())  * ((Float.parseFloat(bonificacion) / 100))));
                                    reciboInfo(listaInfoConceptos, datos);
                                    datos.clear();
                                    base += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos()) * (1 - (Float.parseFloat(bonificacion) / 100));
                                    IVA += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos()) * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * (1 - (Float.parseFloat(bonificacion) / 100));
                                    consTemp -= Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos());
                                }
                            }
                        }
                    }
                } else if ("Agua".equals(listaOrdenanza.get(i).getConcepto()) && "Fijo".equals(listaOrdenanza.get(i).getSubconcepto()) && "S".equals(listaOrdenanza.get(i).getAcumulable())) {
                    base += Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100));
                    IVA += base * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100;
                    datos.add(listaOrdenanza.get(i).getConcepto());
                    datos.add(listaOrdenanza.get(i).getSubconcepto());
                    datos.add(listaOrdenanza.get(i).getM3incluidos());
                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100))));
                    datos.add(listaOrdenanza.get(i).getIVA());
                    datos.add(String.valueOf(base * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100));
                    datos.add(bonificacion);
                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * ((Float.parseFloat(bonificacion) / 100))));
                    reciboInfo(listaInfoConceptos, datos);
                    datos.clear();
                    float m3sum = 0;
                    m3sum += Float.parseFloat(listaOrdenanza.get(i).getM3incluidos());
                    for (int j = 1; j < listaOrdenanza.size(); j++) {
                        if (listaOrdenanza.get(i + j + 1).getM3incluidos() == null) {
                            datos.add(listaOrdenanza.get(i + j).getConcepto());
                            datos.add(listaOrdenanza.get(i + j).getSubconcepto());
                            datos.add(String.valueOf(0.0));
                            datos.add(String.valueOf(0.0));
                            datos.add(listaOrdenanza.get(i + j).getIVA());
                            datos.add(String.valueOf(0.0));
                            datos.add(bonificacion);
                            datos.add(String.valueOf(0.0));
                            reciboInfo(listaInfoConceptos, datos);
                            datos.clear();
                            break;
                        }
                        m3sum += Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos());
                        if (j == 1 && listaOrdenanza.get(i).getId().equals(listaOrdenanza.get(i + j).getId()) && consTemp <= m3sum  && consTemp >= m3sum - Float.parseFloat(listaOrdenanza.get(i).getM3incluidos())) {
                            datos.add(listaOrdenanza.get(i + j).getConcepto());
                            datos.add(listaOrdenanza.get(i + j).getSubconcepto());
                            datos.add(String.valueOf(consTemp));
                            datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100))));
                            datos.add(listaOrdenanza.get(i + j).getIVA());
                            datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * (1 - (Float.parseFloat(bonificacion) / 100))));
                            datos.add(bonificacion);
                            datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * ((Float.parseFloat(bonificacion) / 100))));
                            reciboInfo(listaInfoConceptos, datos);
                            datos.clear();
                            base += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100));
                            IVA += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * (1 - (Float.parseFloat(bonificacion) / 100));
                        } else if (listaOrdenanza.get(i).getId().equals(listaOrdenanza.get(i + j).getId()) && consTemp <= m3sum && consTemp >= m3sum - Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos())) {
                            datos.add(listaOrdenanza.get(i + j).getConcepto());
                            datos.add(listaOrdenanza.get(i + j).getSubconcepto());
                            datos.add(String.valueOf(consTemp));
                            datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100))));
                            datos.add(listaOrdenanza.get(i + j).getIVA());
                            datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * (1 - (Float.parseFloat(bonificacion) / 100))));
                            datos.add(bonificacion);
                            datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * ((Float.parseFloat(bonificacion) / 100))));
                            reciboInfo(listaInfoConceptos, datos);
                            datos.clear();
                            base += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100));
                            IVA += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * (1 - (Float.parseFloat(bonificacion) / 100));
                        } else if (!listaOrdenanza.get(i).getId().equals(listaOrdenanza.get(i + j + 1).getId()) && listaOrdenanza.get(i).getId().equals(listaOrdenanza.get(i + j).getId()) && consTemp <= m3sum  - Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos())) {
                            datos.add(listaOrdenanza.get(i + j).getConcepto());
                            datos.add(listaOrdenanza.get(i + j).getSubconcepto());
                            datos.add(String.valueOf(consTemp));
                            datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100))));
                            datos.add(listaOrdenanza.get(i + j).getIVA());
                            datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * (1 - (Float.parseFloat(bonificacion) / 100)))) ;
                            datos.add(bonificacion);
                            datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * ((Float.parseFloat(bonificacion) / 100))));
                            reciboInfo(listaInfoConceptos, datos);
                            datos.clear();
                            base += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100));
                            IVA += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * (1 - (Float.parseFloat(bonificacion) / 100));
                        } else {
                            datos.add(listaOrdenanza.get(i + j).getConcepto());
                            datos.add(listaOrdenanza.get(i + j).getSubconcepto());
                            datos.add(String.valueOf(0.0));
                            datos.add(String.valueOf(0.0));
                            datos.add(listaOrdenanza.get(i + j).getIVA());
                            datos.add(String.valueOf(0.0));
                            datos.add(bonificacion);
                            datos.add(String.valueOf(0.0));
                            reciboInfo(listaInfoConceptos, datos);
                            datos.clear();
                        }
                    }
                } else if ("Agua".equals(listaOrdenanza.get(i).getConcepto()) && "Concepto".equals(listaOrdenanza.get(i).getSubconcepto())) {
                    base += Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100));
                    if (consTemp <= Float.parseFloat(listaOrdenanza.get(i).getM3incluidos())) {
                        datos.add(listaOrdenanza.get(i).getConcepto());
                        datos.add(listaOrdenanza.get(i).getSubconcepto());
                        datos.add(String.valueOf(consTemp));
                        datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100))));
                        datos.add(listaOrdenanza.get(i).getIVA());
                        datos.add(String.valueOf(base * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100));
                        datos.add(bonificacion);
                        datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * ((Float.parseFloat(bonificacion) / 100))));
                        reciboInfo(listaInfoConceptos, datos);
                        datos.clear();
                        IVA += base * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100;
                    } else {
                        datos.add(listaOrdenanza.get(i).getConcepto());
                        datos.add(listaOrdenanza.get(i).getSubconcepto());
                        datos.add(String.valueOf(consTemp));
                        datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) + Float.parseFloat(listaOrdenanza.get(i).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100))));
                        datos.add(listaOrdenanza.get(i).getIVA());
                        datos.add(String.valueOf(base * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100));
                        datos.add(bonificacion);
                        datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) + Float.parseFloat(listaOrdenanza.get(i).getPreciom3()) * consTemp * ((Float.parseFloat(bonificacion) / 100))));
                        reciboInfo(listaInfoConceptos, datos);
                        datos.clear();
                        base += Float.parseFloat(listaOrdenanza.get(i).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100));
                        IVA += base * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100;
                    }
                } else if (listaOrdenanza.get(i).getPrecioFijo() != null) {
                    datos.add(listaOrdenanza.get(i).getConcepto());
                    datos.add(listaOrdenanza.get(i).getSubconcepto());
                    datos.add(String.valueOf(0.0));
                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100))));
                    datos.add(listaOrdenanza.get(i).getIVA());
                    datos.add(String.valueOf(base * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100));
                    datos.add(bonificacion);
                    datos.add(String.valueOf(Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * ((Float.parseFloat(bonificacion) / 100))));
                    reciboInfo(listaInfoConceptos, datos);
                    datos.clear();
                    base += Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100));
                    IVA += base * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100;
                } else if (listaOrdenanza.get(i).getPorcentajeSobreOtroConcepto() != null && listaOrdenanza.get(i).getSobreQueConcepto() != null) {
                    int conceptoTemp = (int) Float.parseFloat(listaOrdenanza.get(i).getSobreQueConcepto());
                    float[] resultConcepto = conceptosAux(String.valueOf(conceptoTemp), bonificacion, cons, 0, 0, listaOrdenanza, listaInfoConceptos, conceptosVisitados);
                    float porcentajeSobreOtroConcepto = Float.parseFloat(listaOrdenanza.get(i).getPorcentajeSobreOtroConcepto());
                    float baseIncremento = resultConcepto[0] * porcentajeSobreOtroConcepto / 100;
                    
                    if (!conceptosVisitados.contains(listaOrdenanza.get(i).getConcepto())) {
                        datos.add(listaOrdenanza.get(i).getConcepto());
                        datos.add(listaOrdenanza.get(i).getSubconcepto());
                        datos.add(String.valueOf(0.0));
                        datos.add(String.valueOf(baseIncremento));
                        datos.add(listaOrdenanza.get(i).getIVA());
                        datos.add(String.valueOf(baseIncremento * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100));
                        datos.add(bonificacion);
                        datos.add(String.valueOf((resultConcepto[0] * 100/(100 - Float.parseFloat(bonificacion)) - resultConcepto[0]) * porcentajeSobreOtroConcepto / 100));
                        reciboInfo(listaInfoConceptos, datos);
                        datos.clear();
                        conceptosVisitados.add(listaOrdenanza.get(i).getConcepto());
                    }
                    base += baseIncremento;
                    IVA += baseIncremento * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100;
                }
                resultado[0] = base;
                resultado[1] = IVA;
                return resultado;
            }
        }
        return resultado;
    }

    public float[] conceptosAux(String concepto, String bonificacion, float cons, float base, float IVA, List<Ordenanza> listaOrdenanza, List<List<String>> listaInfoConceptos, Set<String> conceptosVisitados) {
        float[] resultado = new float[2];
        List<String> datos = new ArrayList<>();
        for (int i = 0; i < listaOrdenanza.size(); i++) {
            float id = Float.parseFloat(listaOrdenanza.get(i).getId());
            if (concepto.equals(String.valueOf((int) id))) {
                float consTemp = cons;
                if ("Agua".equals(listaOrdenanza.get(i).getConcepto()) && "Fijo".equals(listaOrdenanza.get(i).getSubconcepto()) && "N".equals(listaOrdenanza.get(i).getAcumulable())) {
                    base += Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100));
                    consTemp -= Float.parseFloat(listaOrdenanza.get(i).getM3incluidos());
                    if (consTemp > 0) {
                        for (int j = 1; j < listaOrdenanza.size() - i; j++) {
                            if (listaOrdenanza.get(i).getId().equals(listaOrdenanza.get(i + j).getId())) {
                                if (consTemp <= Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos())) {
                                    base += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100));
                                    consTemp = 0;
                                } else {
                                    base += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos()) * (1 - (Float.parseFloat(bonificacion) / 100));
                                    consTemp -= Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos());
                                }
                            }
                        }
                    }
                } else if ("Agua".equals(listaOrdenanza.get(i).getConcepto()) && "Fijo".equals(listaOrdenanza.get(i).getSubconcepto()) && "S".equals(listaOrdenanza.get(i).getAcumulable())) {
                    base += Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100));
                    float m3sum = 0;
                    m3sum += Float.parseFloat(listaOrdenanza.get(i).getM3incluidos());
                    for (int j = 1; j < listaOrdenanza.size() - i; j++) {
                        if (listaOrdenanza.get(i + j + 1).getM3incluidos() == null) {
                            break;
                        }
                        m3sum += Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos());
                        if (j == 1 && listaOrdenanza.get(i).getId().equals(listaOrdenanza.get(i + j).getId()) && consTemp <= m3sum  && consTemp >= m3sum - Float.parseFloat(listaOrdenanza.get(i).getM3incluidos())) {
                            base += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100));
                            IVA += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * Float.parseFloat(listaOrdenanza.get(i + j).getIVA()) / 100 * (1 - (Float.parseFloat(bonificacion) / 100));
                        } else if (listaOrdenanza.get(i).getId().equals(listaOrdenanza.get(i + j).getId()) && consTemp <= m3sum && consTemp >= m3sum - Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos())) {
                            base += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100));
                            break;
                        } else if (!listaOrdenanza.get(i).getId().equals(listaOrdenanza.get(i + j + 1).getId()) && listaOrdenanza.get(i).getId().equals(listaOrdenanza.get(i + j).getId()) && consTemp <= m3sum  - Float.parseFloat(listaOrdenanza.get(i + j).getM3incluidos())) {
                            base += Float.parseFloat(listaOrdenanza.get(i + j).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100));
                            break;
                        }
                    }
                } else if ("Agua".equals(listaOrdenanza.get(i).getConcepto()) && "Concepto".equals(listaOrdenanza.get(i).getSubconcepto())) {
                    base += Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100));
                    if (consTemp <= Float.parseFloat(listaOrdenanza.get(i).getM3incluidos())) {
                        IVA += Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100 * base;
                    } else {
                        base += Float.parseFloat(listaOrdenanza.get(i).getPreciom3()) * consTemp * (1 - (Float.parseFloat(bonificacion) / 100));
                    }
                } else if (listaOrdenanza.get(i).getPrecioFijo() != null) {
                    base += Float.parseFloat(listaOrdenanza.get(i).getPrecioFijo()) * (1 - (Float.parseFloat(bonificacion) / 100));
                } else if (listaOrdenanza.get(i).getPorcentajeSobreOtroConcepto() != null && listaOrdenanza.get(i).getSobreQueConcepto() != null) {
                    int conceptoTemp = (int) Float.parseFloat(listaOrdenanza.get(i).getSobreQueConcepto());
                    float[] resultConcepto = conceptosAux(String.valueOf(conceptoTemp), bonificacion, cons, 0, 0, listaOrdenanza, listaInfoConceptos, conceptosVisitados);
                    float porcentajeSobreOtroConcepto = Float.parseFloat(listaOrdenanza.get(i).getPorcentajeSobreOtroConcepto());
                    float baseIncremento = resultConcepto[0] * porcentajeSobreOtroConcepto / 100;
                    if (!conceptosVisitados.contains(listaOrdenanza.get(i).getConcepto())) {
                        datos.add(listaOrdenanza.get(i).getConcepto());
                        datos.add(listaOrdenanza.get(i).getSubconcepto());
                        datos.add(String.valueOf(0.0));
                        datos.add(String.valueOf(baseIncremento));
                        datos.add(listaOrdenanza.get(i).getIVA());
                        datos.add(String.valueOf(baseIncremento * Float.parseFloat(listaOrdenanza.get(i).getIVA()) / 100));
                        datos.add(bonificacion);
                        datos.add(String.valueOf((resultConcepto[0] * 100/(100 - Float.parseFloat(bonificacion)) - resultConcepto[0]) * porcentajeSobreOtroConcepto / 100));
                        reciboInfo(listaInfoConceptos, datos);
                        datos.clear();
                        conceptosVisitados.add(listaOrdenanza.get(i).getConcepto());
                    }
                    base += baseIncremento;
                }
                resultado[0] = base;
                resultado[1] = IVA;
                return resultado;
            }
        }
        return resultado;
    }


    private String getTipoCalculo(String concepto, List<Ordenanza> listaOrdenanza) {
        for (int i = 0; i < listaOrdenanza.size(); i++) {

            if (listaOrdenanza.get(i).getId().equals(String.valueOf(Float.parseFloat(concepto)))) {
                return listaOrdenanza.get(i).getTipoCalculo();
            }
            
    }

        return "";
    }

    
    public List<Element> getListaContrValor() {
        return listaContrValor;
    }

    public void clearListaContrValor() {
        this.listaContrValor.clear();
    }
    
}
