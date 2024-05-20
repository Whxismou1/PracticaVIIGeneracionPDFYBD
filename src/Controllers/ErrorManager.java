package Controllers;

import Entities.Contribuyente;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author maxim
 */
public class ErrorManager {

    public void errorManagerCCC(List<Contribuyente> lista) {
        try {
            Element cuentas = new Element("Cuentas");
            Document doc = new Document(cuentas);

            for (int i = 0; i < lista.size(); i++) {
                Contribuyente contr = lista.get(i);
                Element cuenta = new Element("Cuenta");

                Attribute attr = new Attribute("id", contr.getId().toString());
                cuenta.setAttribute(attr);

                Element nombre = new Element("Nombre");
                Element apellidos = new Element("Apellidos");
                Element nif = new Element("NIFNIE");
                Element ccc = new Element("CCCErroneo");
                Element iban = new Element("IBANCorrecto");
                nombre.setText(contr.getNombre());
                apellidos.setText(contr.getApellido1() + " " + contr.getApellido2());
                nif.setText(contr.getNIFNIE());
                ccc.setText(contr.getCCC());
                iban.setText(contr.getIBAN());

                cuenta.addContent(nombre);
                cuenta.addContent(apellidos);
                cuenta.addContent(nif);
                cuenta.addContent(ccc);
                cuenta.addContent(iban);
                cuentas.addContent(cuenta);
            }
            XMLOutputter xml = new XMLOutputter();
            xml.setFormat(Format.getPrettyFormat());
            xml.output(doc, new FileWriter("src/resources/ErroresCCC.xml"));
        } catch (IOException ex) {
            Logger.getLogger(ErrorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void errorManagerNIF(List<Contribuyente> lista) {
        try {
            Element contribuyentes = new Element("Contribuyentes");
            Document doc = new Document(contribuyentes);

            for (int i = 0; i < lista.size(); i++) {
                Contribuyente contr = lista.get(i);
                Element contribuyente = new Element("Contribuyente");
                Attribute attr = new Attribute("id", contr.getId().toString());
                contribuyente.setAttribute(attr);
                Element nif = new Element("NIF_NIE");
                Element nombre = new Element("Nombre");
                Element apellido1 = new Element("PrimerApellido");
                Element apellido2 = new Element("SegundoApellido");

                nif.setText(contr.getNIFNIE());
                nombre.setText(contr.getNombre());
                apellido1.setText(contr.getApellido1());
                apellido2.setText(contr.getApellido2());

                contribuyente.addContent(nif);
                contribuyente.addContent(nombre);
                contribuyente.addContent(apellido1);
                contribuyente.addContent(apellido2);
                contribuyentes.addContent(contribuyente);
            }

            XMLOutputter xml = new XMLOutputter();
            xml.setFormat(Format.getPrettyFormat());
            xml.output(doc, new FileWriter("src/resources/ErroresNifNie.xml"));
        } catch (IOException ex) {
            Logger.getLogger(ErrorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}