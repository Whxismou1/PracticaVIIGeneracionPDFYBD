package Controllers;

import Entities.Ordenanza;
import Entities.Contribuyente;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author moasin
 */
public class DatabaseManager {

    public void init(List<Contribuyente> listaContribuyenteFiltrado, List<Ordenanza> listaOrdenanza, List<HashMap<String, String>> listaDeMapasElems, String trimestre) {
        Session session = null;
        Transaction transac = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transac = session.beginTransaction();

            for (Ordenanza ordEntidad : listaOrdenanza) {
                POJOS.Ordenanza ordDB = new POJOS.Ordenanza();

                String hqlOrdenanza = "FROM Ordenanza o WHERE o.idOrdenanza = :idOrdenanza AND o.concepto = :concepto AND o.subconcepto = :subconcepto";
                Query<POJOS.Ordenanza> queryOrdenanza = session.createQuery(hqlOrdenanza, POJOS.Ordenanza.class);
                int idOrdenanzaComp = (int) Double.parseDouble(ordEntidad.getId());
                queryOrdenanza.setParameter("idOrdenanza", idOrdenanzaComp);
                queryOrdenanza.setParameter("concepto", ordEntidad.getConcepto());
                queryOrdenanza.setParameter("subconcepto", ordEntidad.getSubconcepto());
                POJOS.Ordenanza ordenanzaExistente = queryOrdenanza.uniqueResult();
                System.out.println("idOrd: " + idOrdenanzaComp);
                if (ordenanzaExistente != null) {
                    System.out.println("Ya existen ordenanzas actualizando");
                    updateOrdenanza(ordenanzaExistente, ordEntidad);
                    session.update(ordenanzaExistente);
                } else {
                    System.out.println("No existen ordenanzas actualizando");
                    ordDB = createNewOrdenanza(ordEntidad);
                    session.save(ordDB);
                }
            }

            for (Contribuyente entidadContri : listaContribuyenteFiltrado) {
                POJOS.Contribuyente pojoContribuyente = null;

                // Buscamos si el contribuyente ya existe
                String hqlContribuyente = "FROM Contribuyente c WHERE c.nifnie = :nifnie AND c.fechaAlta = :fechaAlta";
                Query<POJOS.Contribuyente> queryContribuyente = session.createQuery(hqlContribuyente, POJOS.Contribuyente.class);
                queryContribuyente.setParameter("nifnie", entidadContri.getNIFNIE());
                Date fechaAltaDate = Date.from(LocalDate.parse(entidadContri.getFechaAlta(), formatter).atStartOfDay(ZoneId.systemDefault()).toInstant());
                queryContribuyente.setParameter("fechaAlta", fechaAltaDate);
                POJOS.Contribuyente contribuyenteExistente = queryContribuyente.uniqueResult();

                if (contribuyenteExistente != null) {
                    updateContribuyente(contribuyenteExistente, entidadContri);
                    session.update(contribuyenteExistente);
                    pojoContribuyente = contribuyenteExistente; // Usamos el contribuyente existente
                } else {
                    pojoContribuyente = createNewContribuyente(entidadContri);
                    session.save(pojoContribuyente);
                }

                // Proceso de Lecturas
                POJOS.Lecturas lectContri = new POJOS.Lecturas();
                String hqlLectura = "FROM Lecturas l WHERE l.contribuyente.idContribuyente = :idContribuyente AND l.periodo = :periodo AND l.ejercicio = :ejercicio";
                Query<POJOS.Lecturas> queryLectura = session.createQuery(hqlLectura, POJOS.Lecturas.class);
                queryLectura.setParameter("idContribuyente", pojoContribuyente.getIdContribuyente()); // Aquí se usa el ID del contribuyente creado o existente
                queryLectura.setParameter("periodo", trimestre.substring(0, 2));
                queryLectura.setParameter("ejercicio", trimestre.substring(3));
                POJOS.Lecturas lecturaExistente = queryLectura.uniqueResult();

                if (lecturaExistente != null) {
                    System.out.println("Existe lectura actualizando");
                    updateLectura(lecturaExistente, entidadContri);
                    session.update(lecturaExistente);
                } else {
                    lectContri = createNewLectura(entidadContri, trimestre, pojoContribuyente);
                    session.save(lectContri);
                }

                // Proceso de RelContribuyenteOrdenanza
                String[] conceptos = entidadContri.getConceptosACobrar().split(" ");
                for (String concepto : conceptos) {
                    int conceptAct = Integer.parseInt(concepto);
                    String hql = "FROM Ordenanza o WHERE o.idOrdenanza = :conceptAct";
                    List<POJOS.Ordenanza> ordenanzasUser = session.createQuery(hql, POJOS.Ordenanza.class)
                            .setParameter("conceptAct", conceptAct)
                            .getResultList();

                    for (POJOS.Ordenanza ord : ordenanzasUser) {
                        POJOS.RelContribuyenteOrdenanza relContrOrd = new POJOS.RelContribuyenteOrdenanza();
                        relContrOrd.setContribuyente(pojoContribuyente);
                        relContrOrd.setOrdenanza(ord);
                        session.save(relContrOrd);
                    }
                }
            }
            for (int i = 0; i < listaDeMapasElems.size(); i++) {
                POJOS.Recibos reciboPojo = new POJOS.Recibos();
                HashMap<String, String> actualElemMap = listaDeMapasElems.get(i);
                String hqlRecibo = "FROM Recibos r WHERE r.nifContribuyente = :nifContribuyente AND r.fechaPadron = :fechaPadron";
                Query<POJOS.Recibos> queryRecibo = session.createQuery(hqlRecibo, POJOS.Recibos.class);
                queryRecibo.setParameter("nifContribuyente", actualElemMap.get("NIF"));
                queryRecibo.setParameter("fechaPadron", Date.from(calcularFinTrimestre(Integer.parseInt(trimestre.substring(0, 1)), Integer.parseInt(trimestre.substring(3))).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                POJOS.Recibos reciboExistente = queryRecibo.uniqueResult();

                if (reciboExistente != null) {
                    updateRecibo(reciboExistente, actualElemMap);
                    session.update(reciboExistente);
                } else {
                    POJOS.Contribuyente contribuyente = new POJOS.Contribuyente();
                    String sesHQl = "FROM Contribuyente WHERE nifnie = :nif";
                    Query<POJOS.Contribuyente> contrQu = session.createQuery(sesHQl, POJOS.Contribuyente.class);
                    contrQu.setParameter("nif", actualElemMap.get("NIF"));
                    contribuyente = contrQu.uniqueResult();
                    reciboPojo = createNewRecibo(actualElemMap, trimestre, contribuyente);
                    session.save(reciboPojo);

                    // Process Lineasrecibo
                    String[] conceptos = actualElemMap.get("Concepto").replaceAll("[\\[\\],]", "").split(" ");
                    String[] subconceptos = actualElemMap.get("Subconcepto").replaceAll("[\\[\\]]", "").split(", ");
                    String[] baseImponible = actualElemMap.get("BaseImponible").replaceAll("[\\[\\],]", "").split(" ");
                    String[] porcentajeIVA = actualElemMap.get("PorcentajeIVA").replaceAll("[\\[\\],]", "").split(" ");
                    String[] importeIVA = actualElemMap.get("ImporteIVA").replaceAll("[\\[\\],]", "").split(" ");
                    String[] m3incluidos = actualElemMap.get("m3incluidos").replaceAll("[\\[\\],]", "").split(" ");
                    String[] bonificacion = actualElemMap.get("BonificacionInfo").replaceAll("[\\[\\],]", "").split(" ");
                    String[] importeBonificacion = actualElemMap.get("importeBonificacion").replaceAll("[\\[\\],]", "").split(" ");

                    for (int j = 0; j < conceptos.length; j++) {
                        POJOS.Lineasrecibo lineaReciboPojo = new POJOS.Lineasrecibo();
                        String hqlLineaRecibo = "FROM Lineasrecibo l WHERE l.recibos.contribuyente.nifnie  = :nifnie AND l.concepto = :concepto AND l.subconcepto = :subconcepto AND l.recibos.numeroRecibo = :numeroRecibo";
                        Query<POJOS.Lineasrecibo> queryLineaRecibo = session.createQuery(hqlLineaRecibo, POJOS.Lineasrecibo.class);
                        queryLineaRecibo.setParameter("nifnie", reciboPojo.getContribuyente().getNifnie());
                        queryLineaRecibo.setParameter("concepto", conceptos[j]);
                        queryLineaRecibo.setParameter("subconcepto", subconceptos[j]);
                        queryLineaRecibo.setParameter("numeroRecibo", reciboPojo.getNumeroRecibo());
                        POJOS.Lineasrecibo lineaReciboExistente = queryLineaRecibo.uniqueResult();

                        if (lineaReciboExistente != null) {
                            updateLineaRecibo(lineaReciboExistente, conceptos[j], subconceptos[j], baseImponible[j], porcentajeIVA[j], importeIVA[j], m3incluidos[j], bonificacion[j], importeBonificacion[j]);
                            session.update(lineaReciboExistente);
                        } else {
                            lineaReciboPojo = createNewLineaRecibo(conceptos[j], subconceptos[j], baseImponible[j], porcentajeIVA[j], importeIVA[j], m3incluidos[j], bonificacion[j], importeBonificacion[j], reciboPojo);
                            session.save(lineaReciboPojo);
                        }
                    }
                }
            }

            transac.commit();

        } catch (Exception e) {
            if (transac != null) {
                transac.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
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

    private POJOS.Ordenanza createNewOrdenanza(Ordenanza ordEntidad) {
        POJOS.Ordenanza ordDB = new POJOS.Ordenanza();
        int idOrd = (int) Double.parseDouble(ordEntidad.getId());
        ordDB.setIdOrdenanza(idOrd);
        ordDB.setConcepto(ordEntidad.getConcepto());
        ordDB.setSubconcepto(ordEntidad.getSubconcepto());
        ordDB.setDescripcion(ordEntidad.getDescripcion());
        ordDB.setAcumulable(ordEntidad.getAcumulable());

        String precioFijoString = ordEntidad.getPrecioFijo();
        int precioFijo = 0;
        if (precioFijoString != null) {
            precioFijo = (int) Double.parseDouble(precioFijoString);
        }

        ordDB.setPrecioFijo(precioFijo);

        String M3incluidosStr = ordEntidad.getM3incluidos();
        int m3Incl = 0;
        if (M3incluidosStr != null) {
            m3Incl = (int) Double.parseDouble(M3incluidosStr);
        }

        ordDB.setM3incluidos(m3Incl);

        String precioM3String = ordEntidad.getPreciom3();
        double precioMetroCubico = 0;
        if (precioM3String != null) {
            precioMetroCubico = Double.parseDouble(precioM3String);
        }

        ordDB.setPreciom3(precioMetroCubico);

        String porcentajeSobreOtroConceptoString = ordEntidad.getPorcentajeSobreOtroConcepto();
        double porcentaje = 0.0;
        if (porcentajeSobreOtroConceptoString != null) {
            porcentaje = Double.parseDouble(porcentajeSobreOtroConceptoString);
        }

        ordDB.setPorcentaje(porcentaje);

        String sobreConceptString = ordEntidad.getSobreQueConcepto();
        int sobreConcept = 0;
        if (sobreConceptString != null) {
            sobreConcept = (int) Double.parseDouble(sobreConceptString);
        }

        ordDB.setConceptoRelacionado(sobreConcept);
        ordDB.setIva(Double.parseDouble(ordEntidad.getIVA()));
        ordDB.setPueblo(ordEntidad.getPueblo());
        ordDB.setTipoCalculo(ordEntidad.getTipoCalculo());
        return ordDB;
    }

    private void updateOrdenanza(POJOS.Ordenanza ordenanzaExistente, Ordenanza ordEntidad) {
        ordenanzaExistente.setDescripcion(ordEntidad.getDescripcion());
        ordenanzaExistente.setAcumulable(ordEntidad.getAcumulable());

        String precioFijoString = ordEntidad.getPrecioFijo();
        int precioFijo = 0;
        if (precioFijoString != null) {
            precioFijo = (int) Double.parseDouble(precioFijoString);
        }

        ordenanzaExistente.setPrecioFijo(precioFijo);

        String M3incluidosStr = ordEntidad.getM3incluidos();
        int m3Incl = 0;
        if (M3incluidosStr != null) {
            m3Incl = (int) Double.parseDouble(M3incluidosStr);
        }

        ordenanzaExistente.setM3incluidos(m3Incl);

        String precioM3String = ordEntidad.getPreciom3();
        double precioMetroCubico = 0;
        if (precioM3String != null) {
            precioMetroCubico = Double.parseDouble(precioM3String);
        }

        ordenanzaExistente.setPreciom3(precioMetroCubico);

        String porcentajeSobreOtroConceptoString = ordEntidad.getPorcentajeSobreOtroConcepto();
        double porcentaje = 0.0;
        if (porcentajeSobreOtroConceptoString != null) {
            porcentaje = Double.parseDouble(porcentajeSobreOtroConceptoString);
        }

        ordenanzaExistente.setPorcentaje(porcentaje);

        String sobreConceptString = ordEntidad.getSobreQueConcepto();
        int sobreConcept = 0;
        if (sobreConceptString != null) {
            sobreConcept = (int) Double.parseDouble(sobreConceptString);
        }

        ordenanzaExistente.setConceptoRelacionado(sobreConcept);
        ordenanzaExistente.setIva(Double.parseDouble(ordEntidad.getIVA()));
        ordenanzaExistente.setPueblo(ordEntidad.getPueblo());
        ordenanzaExistente.setTipoCalculo(ordEntidad.getTipoCalculo());
        System.out.println("Controllers.DatabaseManager.updateOrdenanza()asquiiiiii");
    }

    private void updateContribuyente(POJOS.Contribuyente contribuyenteExistente, Contribuyente entidadContri) throws Exception {
        contribuyenteExistente.setNombre(entidadContri.getNombre());
        contribuyenteExistente.setApellido1(entidadContri.getApellido1());
        contribuyenteExistente.setApellido2(entidadContri.getApellido2());
        contribuyenteExistente.setDireccion(entidadContri.getDireccion());
        contribuyenteExistente.setNumero(entidadContri.getNumero());
        contribuyenteExistente.setPaisCcc(entidadContri.getPaisCCC());
        contribuyenteExistente.setCcc(entidadContri.getCCC());
        contribuyenteExistente.setIban(entidadContri.getIBAN());
        contribuyenteExistente.setEemail(entidadContri.getEmail());
        contribuyenteExistente.setExencion(entidadContri.getExencion());
        contribuyenteExistente.setBonificacion(Double.parseDouble(entidadContri.getBonificacion()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Controllers.DatabaseManager.updateContribuyente() + aqui estoy en contri actualizando");
        String fechaAltaString = entidadContri.getFechaAlta();
        Date fechaAlta = null;
        if (fechaAltaString != null) {
            fechaAlta = sdf.parse(fechaAltaString);
        }
        contribuyenteExistente.setFechaAlta(fechaAlta);

        String fechaBajaString = entidadContri.getFechaBaja();
        Date fechaBaja = null;
        if (fechaBajaString != null) {
            fechaBaja = sdf.parse(fechaBajaString);
        }

        contribuyenteExistente.setFechaBaja(fechaBaja);
    }

    private POJOS.Contribuyente createNewContribuyente(Contribuyente entidadContri) throws Exception {
        POJOS.Contribuyente pojoContribuyente = new POJOS.Contribuyente();
        pojoContribuyente.setIdContribuyente(Integer.parseInt(String.valueOf(entidadContri.getId())));
        pojoContribuyente.setNombre(entidadContri.getNombre());
        pojoContribuyente.setApellido1(entidadContri.getApellido1());
        pojoContribuyente.setApellido2(entidadContri.getApellido2());
        pojoContribuyente.setNifnie(entidadContri.getNIFNIE());
        pojoContribuyente.setDireccion(entidadContri.getDireccion());
        pojoContribuyente.setNumero(entidadContri.getNumero());
        pojoContribuyente.setPaisCcc(entidadContri.getPaisCCC());
        pojoContribuyente.setCcc(entidadContri.getCCC());
        pojoContribuyente.setIban(entidadContri.getIBAN());
        pojoContribuyente.setEemail(entidadContri.getEmail());
        pojoContribuyente.setExencion(entidadContri.getExencion());
        pojoContribuyente.setBonificacion(Double.parseDouble(entidadContri.getBonificacion())); // NaN para Double

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String fechaAltaString = entidadContri.getFechaAlta();
        Date fechaAlta = null;
        if (fechaAltaString != null) {
            fechaAlta = sdf.parse(fechaAltaString);
        }
        pojoContribuyente.setFechaAlta(fechaAlta);

        String fechaBajaString = entidadContri.getFechaBaja();
        Date fechaBaja = null;
        if (fechaBajaString != null) {
            fechaBaja = sdf.parse(fechaBajaString);
        }

        pojoContribuyente.setFechaBaja(fechaBaja);
        return pojoContribuyente;
    }

    private POJOS.Lecturas createNewLectura(Contribuyente entidadContri, String trimestre, POJOS.Contribuyente pojoContribuyente) {
        POJOS.Lecturas lectContri = new POJOS.Lecturas();
        String lectActualStr = entidadContri.getLecturaActual();
        int lectActual = 0;
        if (lectActualStr != null) {
            lectActual = (int) Double.parseDouble(lectActualStr);
        }

        lectContri.setLecturaActual(lectActual);

        String lectAnteriorStr = entidadContri.getLecturaAnterior();
        int lectAnterior = 0;
        if (lectAnteriorStr != null) {
            lectAnterior = (int) Double.parseDouble(lectAnteriorStr);
        }

        lectContri.setLecturaAnterior(lectAnterior);
        lectContri.setEjercicio(trimestre.substring(3));
        lectContri.setPeriodo(trimestre.substring(0, 2));
        lectContri.setContribuyente(pojoContribuyente);
        return lectContri;
    }

    private void updateLectura(POJOS.Lecturas lecturaExistente, Contribuyente entidadContri) {
        String lectActualStr = entidadContri.getLecturaActual();
        int lectActual = 0;
        if (lectActualStr != null) {
            lectActual = (int) Double.parseDouble(lectActualStr);
        }

        lecturaExistente.setLecturaActual(lectActual);

        String lectAnteriorStr = entidadContri.getLecturaAnterior();
        int lectAnterior = 0;
        if (lectAnteriorStr != null) {
            lectAnterior = (int) Double.parseDouble(lectAnteriorStr);
        }

        lecturaExistente.setLecturaAnterior(lectAnterior);
    }

    private POJOS.Recibos createNewRecibo(HashMap<String, String> actualElemMap, String trimestre, POJOS.Contribuyente contribuyente) {
        POJOS.Recibos reciboPojo = new POJOS.Recibos();
        reciboPojo.setNifContribuyente(actualElemMap.get("NIF"));
        reciboPojo.setDireccionCompleta(actualElemMap.get("direccion"));
        reciboPojo.setNombre(actualElemMap.get("nombre"));
        reciboPojo.setApellidos(actualElemMap.get("primerApellido") + actualElemMap.get("segundoApellido"));
        reciboPojo.setEmail(actualElemMap.get("email"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formatedDate = LocalDate.now().format(formatter);
        LocalDate locDat = LocalDate.parse(formatedDate, formatter);
        java.sql.Date sqlDate = java.sql.Date.valueOf(locDat);
        reciboPojo.setFechaRecibo(sqlDate);

        int lectAnt = (int) Double.parseDouble(actualElemMap.get("lecturaAnterior"));
        reciboPojo.setLecturaAnterior(lectAnt);

        int lectAct = (int) Double.parseDouble(actualElemMap.get("lecturaActual"));
        reciboPojo.setLecturaActual(lectAct);

        int cons = (int) Double.parseDouble(actualElemMap.get("consumo"));
        reciboPojo.setLecturaActual(cons);

        int numTrimeste = Integer.parseInt(trimestre.substring(0, 1));
        int año = Integer.parseInt(trimestre.substring(3));
        LocalDate fechaPadLoc = calcularFinTrimestre(numTrimeste, año);
        reciboPojo.setFechaPadron(java.sql.Date.valueOf(fechaPadLoc));

        reciboPojo.setTotalBaseImponible(Double.parseDouble(actualElemMap.get("baseImponibleRecibo")));
        reciboPojo.setTotalIva(Double.parseDouble(actualElemMap.get("ivaRecibo")));
        reciboPojo.setTotalRecibo(Double.parseDouble(actualElemMap.get("totalRecibo")));
        reciboPojo.setIban(actualElemMap.get("IBAN"));

        reciboPojo.setExencion(actualElemMap.get("Exencion"));
        reciboPojo.setContribuyente(contribuyente);
        return reciboPojo;
    }

    private void updateRecibo(POJOS.Recibos reciboExistente, HashMap<String, String> actualElemMap) {
        reciboExistente.setDireccionCompleta(actualElemMap.get("direccion"));
        reciboExistente.setNombre(actualElemMap.get("nombre"));
        reciboExistente.setApellidos(actualElemMap.get("primerApellido") + actualElemMap.get("segundoApellido"));
        reciboExistente.setEmail(actualElemMap.get("email"));

        int lectAnt = (int) Double.parseDouble(actualElemMap.get("lecturaAnterior"));
        reciboExistente.setLecturaAnterior(lectAnt);

        int lectAct = (int) Double.parseDouble(actualElemMap.get("lecturaActual"));
        reciboExistente.setLecturaActual(lectAct);

        int cons = (int) Double.parseDouble(actualElemMap.get("consumo"));
        reciboExistente.setLecturaActual(cons);

        reciboExistente.setTotalBaseImponible(Double.parseDouble(actualElemMap.get("baseImponibleRecibo")));
        reciboExistente.setTotalIva(Double.parseDouble(actualElemMap.get("ivaRecibo")));
        reciboExistente.setTotalRecibo(Double.parseDouble(actualElemMap.get("totalRecibo")));
        reciboExistente.setIban(actualElemMap.get("IBAN"));

        reciboExistente.setExencion(actualElemMap.get("Exencion"));
    }

    private POJOS.Lineasrecibo createNewLineaRecibo(String concepto, String subconcepto, String baseImponible, String porcentajeIVA, String importeIVA, String m3incluidos, String bonificacion, String importeBonificacion, POJOS.Recibos reciboPojo) {
        POJOS.Lineasrecibo lineaReciboPojo = new POJOS.Lineasrecibo();
        lineaReciboPojo.setConcepto(concepto);
        lineaReciboPojo.setSubconcepto(subconcepto);
        lineaReciboPojo.setBaseImponible(Double.parseDouble(baseImponible));
        lineaReciboPojo.setPorcentajeIva(Double.parseDouble(porcentajeIVA));
        lineaReciboPojo.setImporteiva(Double.parseDouble(importeIVA));
        lineaReciboPojo.setM3incluidos(Double.parseDouble(m3incluidos));
        lineaReciboPojo.setBonificacion(Double.parseDouble(bonificacion));
        lineaReciboPojo.setImporteBonificacion(Double.parseDouble(importeBonificacion));
        lineaReciboPojo.setRecibos(reciboPojo);
        return lineaReciboPojo;
    }

    private void updateLineaRecibo(POJOS.Lineasrecibo lineaReciboExistente, String concepto, String subconcepto, String baseImponible, String porcentajeIVA, String importeIVA, String m3incluidos, String bonificacion, String importeBonificacion) {
        lineaReciboExistente.setConcepto(concepto);
        lineaReciboExistente.setSubconcepto(subconcepto);
        lineaReciboExistente.setBaseImponible(Double.parseDouble(baseImponible));
        lineaReciboExistente.setPorcentajeIva(Double.parseDouble(porcentajeIVA));
        lineaReciboExistente.setImporteiva(Double.parseDouble(importeIVA));
        lineaReciboExistente.setM3incluidos(Double.parseDouble(m3incluidos));
        lineaReciboExistente.setBonificacion(Double.parseDouble(bonificacion));
        lineaReciboExistente.setImporteBonificacion(Double.parseDouble(importeBonificacion));
    }
}
