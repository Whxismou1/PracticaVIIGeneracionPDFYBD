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

//            int beginID = 1;
            for (Ordenanza ordEntidad : listaOrdenanza) {
                POJOS.Ordenanza ordDB = new POJOS.Ordenanza();
                
                String hqlOrdenanza = "FROM Ordenanza o WHERE o.idOrdenanza = :idOrdenanza AND o.concepto = :concepto AND o.subconcepto = :subconcepto";
                Query<POJOS.Ordenanza> queryOrdenanza = session.createQuery(hqlOrdenanza, POJOS.Ordenanza.class);
                int idOrdenanzaComp = (int) Double.parseDouble(ordEntidad.getId());
                queryOrdenanza.setParameter("idOrdenanza", idOrdenanzaComp);
                queryOrdenanza.setParameter("concepto", ordEntidad.getConcepto());
                queryOrdenanza.setParameter("subconcepto", ordEntidad.getSubconcepto());
                POJOS.Ordenanza ordenanzaExistente = queryOrdenanza.uniqueResult();
                
                if(ordenanzaExistente != null){
                    System.out.println("Ya existen ordenanzas");
                }else{
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
                    session.save(ordDB);
                }
            }

            for (Contribuyente entidadContri : listaContribuyenteFiltrado) {
                POJOS.Contribuyente pojoContribuyente = new POJOS.Contribuyente();

                String hqlContribuyente = "FROM Contribuyente c WHERE c.nifnie = :nifnie AND c.fechaAlta = :fechaAlta";
                Query<POJOS.Contribuyente> queryContribuyente = session.createQuery(hqlContribuyente, POJOS.Contribuyente.class);
                queryContribuyente.setParameter("nifnie", entidadContri.getNIFNIE());
                Date fechaAltaDate = Date.from(LocalDate.parse(entidadContri.getFechaAlta(), formatter).atStartOfDay(ZoneId.systemDefault()).toInstant());
                queryContribuyente.setParameter("fechaAlta", fechaAltaDate);
                POJOS.Contribuyente contribuyenteExistente = queryContribuyente.uniqueResult();

                if(contribuyenteExistente != null){
                    System.out.println("Ya existen contribuyente");
                }else{
                    String concep[] = entidadContri.getConceptosACobrar().split(" ");

//                
                    POJOS.Lecturas lectContri = new POJOS.Lecturas();

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

                    String lectActualStr = entidadContri.getLecturaActual();
                    int lectActual = 0;
                    if (lectActualStr != null) {
                        lectActual = (int) Double.parseDouble(lectActualStr);
                    }
                    
                    System.out.println("Controllers.DatabaseManager.init()");
                    String hqlLectura = "FROM Lecturas l WHERE l.contribuyente.idContribuyente = :idContribuyente AND l.periodo = :periodo AND l.ejercicio = :ejercicio";
                    Query<POJOS.Lecturas> queryLectura = session.createQuery(hqlLectura, POJOS.Lecturas.class);
                    queryLectura.setParameter("idContribuyente", entidadContri.getId().intValue());
                    queryLectura.setParameter("periodo", trimestre.substring(0, 2));
                    queryLectura.setParameter("ejercicio", trimestre.substring(3, trimestre.length()));
                    POJOS.Lecturas lecturaExistente = queryLectura.uniqueResult();
                    
                    if(lecturaExistente != null){
                        System.out.println("lectura existe");
                    }else{
                        lectContri.setLecturaActual(lectActual);

                        String lectAnteriorStr = entidadContri.getLecturaAnterior();
                        int lectAnterior = 0;
                        if (lectAnteriorStr != null) {
                            lectAnterior = (int) Double.parseDouble(lectAnteriorStr);
                        }

                        lectContri.setLecturaAnterior(lectAnterior);
                        lectContri.setEjercicio(trimestre.substring(3, trimestre.length()));
                        lectContri.setPeriodo(trimestre.substring(0, 2));
                        lectContri.setContribuyente(pojoContribuyente);
                        session.save(lectContri);
                    }
                    
                    session.save(pojoContribuyente);
                    
                    for (int i = 0; i < concep.length; i++) {
                        int conceptAct = Integer.parseInt(concep[i]);
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
            }


            for (int i = 0; i < listaDeMapasElems.size(); i++) {
                POJOS.Recibos reciboPojo = new POJOS.Recibos();
                HashMap<String, String> actualElemMap = listaDeMapasElems.get(i);
                
                String hqlRecibo = "FROM Recibos r WHERE r.nifContribuyente = :nifContribuyente AND r.fechaPadron = :fechaPadron";
                Query<POJOS.Recibos> queryRecibo = session.createQuery(hqlRecibo, POJOS.Recibos.class);
                queryRecibo.setParameter("nifContribuyente", actualElemMap.get("NIF"));

                queryRecibo.setParameter("fechaPadron",  Date.from(calcularFinTrimestre(Integer.parseInt(trimestre.substring(0, 1)), Integer.parseInt(trimestre.substring(3, trimestre.length()))).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                POJOS.Recibos reciboExistente = queryRecibo.uniqueResult();
                
                if(reciboExistente != null){
                    System.out.println("Exite recibo");
                }else{
                    reciboPojo.setNifContribuyente(actualElemMap.get("NIF"));
                    reciboPojo.setDireccionCompleta(actualElemMap.get("direccion"));
                    reciboPojo.setNombre(actualElemMap.get("nombre"));
                    reciboPojo.setApellidos(actualElemMap.get("primerApellido") + actualElemMap.get("segundoApellido"));
                    reciboPojo.setEmail(actualElemMap.get("email"));

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
                    int año = Integer.parseInt(trimestre.substring(3, trimestre.length()));
                    LocalDate fechaPadLoc = calcularFinTrimestre(numTrimeste,año);
                    reciboPojo.setFechaPadron(java.sql.Date.valueOf(fechaPadLoc));

                    reciboPojo.setTotalBaseImponible(Double.parseDouble(actualElemMap.get("baseImponibleRecibo")));
                    reciboPojo.setTotalIva(Double.parseDouble(actualElemMap.get("ivaRecibo")));
                    reciboPojo.setTotalRecibo(Double.parseDouble(actualElemMap.get("totalRecibo")));
                    reciboPojo.setIban(actualElemMap.get("IBAN"));

                    reciboPojo.setExencion(actualElemMap.get("Exencion"));
                    Query<POJOS.Contribuyente> query = session.createQuery("FROM Contribuyente WHERE nifnie = :nif", POJOS.Contribuyente.class);
                    query.setParameter("nif", actualElemMap.get("NIF"));
                    POJOS.Contribuyente contribuyente = query.uniqueResult();
                    reciboPojo.setContribuyente(contribuyente);
                    session.save(reciboPojo);




                    String conceptos[] = actualElemMap.get("Concepto").replaceAll("[\\[\\],]", "").split(" ");
                    String subconceptos[] = actualElemMap.get("Subconcepto").replaceAll("[\\[\\],]", "").split(" ");

                    String baseImponible[] = actualElemMap.get("BaseImponible").replaceAll("[\\[\\],]", "").split(" ");
                    String porcentajeIVA[] = actualElemMap.get("PorcentajeIVA").replaceAll("[\\[\\],]", "").split(" ");
                    String importeIVA[] = actualElemMap.get("ImporteIVA").replaceAll("[\\[\\],]", "").split(" ");
                    String m3incluidos[] = actualElemMap.get("m3incluidos").replaceAll("[\\[\\],]", "").split(" ");
                    String bonificacion[] = actualElemMap.get("BonificacionInfo").replaceAll("[\\[\\],]", "").split(" ");
                    String importeBonificacion[] = actualElemMap.get("BonificacionInfo").replaceAll("[\\[\\],]", "").split(" ");

                    for(int j = 0; j < conceptos.length; j++){
                        POJOS.Lineasrecibo lineaReciboPojo = new POJOS.Lineasrecibo();
    //                  //nif, concepto, subconcepto, numero de recibo
                        String hqlLineaRecibo = "FROM Lineasrecibo l WHERE l.recibos.contribuyente.nifnie  = :nifnie AND l.concepto = :concepto AND l.subconcepto = :subconcepto AND l.recibos.numeroRecibo = :numeroRecibo";
                        Query<POJOS.Lineasrecibo> queryLineaRecibo = session.createQuery(hqlLineaRecibo, POJOS.Lineasrecibo.class);
                        queryLineaRecibo.setParameter("nifnie", reciboPojo.getContribuyente().getNifnie());
                        queryLineaRecibo.setParameter("concepto", conceptos[j]);
                        queryLineaRecibo.setParameter("subconcepto", subconceptos[j]);
                        queryLineaRecibo.setParameter("numeroRecibo", reciboPojo.getNumeroRecibo());
                        POJOS.Lineasrecibo lineaReciboExistente = queryLineaRecibo.uniqueResult();
                        
                        if(lineaReciboExistente != null){
                            System.out.println("Linea recibo existe");
                        }else{
                            lineaReciboPojo.setConcepto(conceptos[j]);
                            lineaReciboPojo.setSubconcepto(subconceptos[j]);
                            lineaReciboPojo.setBaseImponible(Double.parseDouble(baseImponible[j]));
                            lineaReciboPojo.setPorcentajeIva(Double.parseDouble(porcentajeIVA[j]));
                            lineaReciboPojo.setImporteiva(Double.parseDouble(importeIVA[j]));
                            lineaReciboPojo.setM3incluidos(Double.parseDouble(m3incluidos[j]));
                            lineaReciboPojo.setBonificacion(Double.parseDouble(bonificacion[j]));
                            lineaReciboPojo.setImporteBonificacion(Double.parseDouble(importeBonificacion[j]));
                            lineaReciboPojo.setRecibos(reciboPojo);
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
}
