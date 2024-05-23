/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Entities.Ordenanza;
import Entities.Contribuyente;
import POJOS.Lineasrecibo;
import POJOS.Recibos;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author moasin
 */
public class DatabaseManager {
    
    public void init(List<Contribuyente> listaContribuyenteFiltrado, List<Ordenanza> listaOrdenanza, String trimestre) {
        Session session = null;
        Transaction transac = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transac = session.beginTransaction();
            int beginID = 1;
            for (Ordenanza ordEntidad : listaOrdenanza) {
                POJOS.Ordenanza ordDB = new POJOS.Ordenanza();
                ordDB.setId(beginID);
                int waza = (int) Double.parseDouble(ordEntidad.getId());
                ordDB.setIdOrdenanza(waza);
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
                int precioMetroCubico = 0;
                if (precioM3String != null) {
                    precioMetroCubico = (int) Double.parseDouble(precioM3String);
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
                beginID++;
                
            }
            int beginIDLecturas = 1;
            for (Contribuyente entidadContri : listaContribuyenteFiltrado) {
                POJOS.Contribuyente pojoContribuyente = new POJOS.Contribuyente();
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
                lectContri.setId(beginIDLecturas);
                session.save(pojoContribuyente);
                session.save(lectContri);
                beginIDLecturas++;
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
    
    private boolean isNIFOnBBDD(String nif) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String hql = "SELECT COUNT(*) FROM Contribuyente WHERE NIFNIE = :nif";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("nif", nif);
            Long count = query.uniqueResult();
            
            if (count < 1) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        
    }
    
    private Contribuyente getUserInfo(String nif) {
        Session session = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            String hql = "FROM Contribuyente WHERE NIFNIE = :nif";
            Query<Contribuyente> query = session.createQuery(hql, Contribuyente.class);
            query.setParameter("nif", nif);
            
            return query.uniqueResult();
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        
    }

//    private void printInfoContribuyente(Contribuyente contribuyente) {
//        System.out.println("-----------------------------------------------------");
//        System.out.println("Información del Contribuyente:");
//        System.out.println("ID: " + contribuyente.getIdContribuyente());
//        System.out.println("Nombre: " + contribuyente.getNombre());
//        System.out.println("Apellidos: " + contribuyente.getApellido1() + " " + contribuyente.getApellido2());
//        System.out.println("NIF/NIE: " + contribuyente.getNifnie());
//        System.out.println("Dirección: " + contribuyente.getDireccion());
//        System.out.println("Número: " + contribuyente.getNumero());
//        System.out.println("País CCC: " + contribuyente.getPaisCcc());
//        System.out.println("CCC: " + contribuyente.getCcc());
//        System.out.println("IBAN: " + contribuyente.getIban());
//        System.out.println("E-mail: " + contribuyente.getEemail());
//        System.out.println("Exención: " + contribuyente.getExencion());
//        System.out.println("Bonificación: " + contribuyente.getBonificacion());
//        System.out.println("Fecha de Alta: " + contribuyente.getFechaAlta());
//        System.out.println("Fecha de Baja: " + contribuyente.getFechaBaja());
//        System.out.println("-----------------------------------------------------");
//    }
//    private void actualizarImporteTotalRecibos(Contribuyente contribuyenteActual) {
//        Session session = null;
//        Transaction transact = null;
//        try {
//            session = HibernateUtil.getSessionFactory().openSession();
//            transact = session.beginTransaction();
//
//            String queryHQL = "UPDATE Recibos SET totalRecibo = :nuevoImporte WHERE idContribuyente = :idContribuyente";
//            Query query = session.createQuery(queryHQL);
//            query.setParameter("nuevoImporte", 250.0);
//            query.setParameter("idContribuyente", contribuyenteActual.getIdContribuyente());
//
//            int rowsChanged = query.executeUpdate();
//            System.out.println("Se han actualizado " + rowsChanged + " filas");
//            transact.commit();
//        } catch (Exception e) {
//            if (transact != null) {
//                transact.rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            if (session != null) {
//                session.close();
//            }
//        }
//
//    }
    private void eliminarRecibosConBaseImponibleMenorQueMedia(Contribuyente contribuyenteActual) {
        Session session = null;
        Transaction transac = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transac = session.beginTransaction();
            
            String hqlMedia = "SELECT AVG(r.totalBaseImponible) FROM Recibos r";
            Query<Double> queryMedia = session.createQuery(hqlMedia, Double.class);
            Double mediaBaseImponible = queryMedia.uniqueResult();
            System.out.println("Media base imponible: " + mediaBaseImponible);
            
            String hqlEliminarRecibos = "FROM Recibos r WHERE r.totalBaseImponible < :media";
            Query<Recibos> eliminarRecibos = session.createQuery(hqlEliminarRecibos, Recibos.class);
            eliminarRecibos.setParameter("media", mediaBaseImponible);
            List<Recibos> recibosAEliminar = eliminarRecibos.getResultList();
            int count = recibosAEliminar.size();
            for (Recibos recibo : recibosAEliminar) {
                
                Set<Lineasrecibo> lineasrecibos = recibo.getLineasrecibos();

                // Eliminar cada línea de recibo
                for (Lineasrecibo linea : lineasrecibos) {
                    session.delete(linea);
                }
                
                session.delete(recibo);
            }
            
            session.getTransaction().commit();
            System.out.println("Se han eliminado " + count + " registros ");
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
    
    public void insertOrUpdateSession(List<Contribuyente> contribuyenteList) {
//        this.openSession();
//        this.transaction = this.session.beginTransaction();
//        this.querier = new DatabaseQuerier(this.session);
//        for (contribuyente c in contribuyenteList) {
//            Contribuyente dbItem = this.querier.getFormDDBB(c);
//            if (dbItem == null) {
//                //si no existe se crea
//                c.setIdContribuyente((Integer) this.session.save(c));
//            } else {
//                //si existe lo actualizo
//                c.setIdContribuyente(dbItem.getIdContribuyente());
//                dbItem.update(contribuyente);
//                this.session.update(dbItem);
//            }
//        }
//        this.insertOrUpdate(contribuyenteList);
//        this.transaction.commit();
//        this.closeSession();
//    }
    }
    
}
