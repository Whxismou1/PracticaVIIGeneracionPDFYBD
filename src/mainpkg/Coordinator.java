package mainpkg;

import Controllers.CCCController;
import Controllers.DatabaseManager;
import Controllers.ErrorManager;
import Controllers.ExcelManager;
import Controllers.GeneradorRecibosXML;
import Controllers.IBANController;
import Controllers.NIFController;
import Entities.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.jdom2.Element;

public class Coordinator {

    public void init() {
        DatabaseManager dbm = new DatabaseManager();
        List<String> emailsList = new ArrayList<>();
        ExcelManager excMang = new ExcelManager();
        CCCController cccController = new CCCController();
        IBANController ibanCont = new IBANController();
        NIFController nifControler = new NIFController();
        ErrorManager errorManager = new ErrorManager();

        List<Contribuyente> malNie = new LinkedList<>();
        List<Contribuyente> malCCC = new LinkedList<>();
        List<String> nifNiesApariciones = new LinkedList<>();

        List<Ordenanza> listaOrdenanza = excMang.readExcelOrdenanza();
        List<Contribuyente> listaContribuyente = excMang.readExcelContribuyente();
        List<Contribuyente> listaContribuyenteFiltrado = new ArrayList<>();

        for (int i = 0; i < listaContribuyente.size(); i++) {
            Contribuyente contribuyenteActual = listaContribuyente.get(i);
            if (isEmptyContribuyente(contribuyenteActual)) {
                continue;
            }

            String nifActual = contribuyenteActual.getNIFNIE();

            if (nifActual == null) {
                malNie.add(contribuyenteActual);
            } else {
                if (nifNiesApariciones.contains(nifActual)) {
                    malNie.add(contribuyenteActual);
                } else {
                    boolean isSpanish = nifControler.isSpanish(nifActual);

                    if (nifControler.isNifValid(nifActual, isSpanish, contribuyenteActual)) {
                        if (nifControler.getIsSaneado()) {
                            malNie.add(contribuyenteActual);
                            nifControler.clearSaneado();
                        }

                        nifNiesApariciones.add(nifActual);

                        String actualCCC = contribuyenteActual.getCCC();
                        cccController.checkCCC(actualCCC, malCCC, contribuyenteActual);

                        ibanCont.checkIban(contribuyenteActual);
                        if (!nifControler.getIsSaneado()) {
                            listaContribuyenteFiltrado.add(contribuyenteActual);
                        }
                    } else {
                        malNie.add(contribuyenteActual);
                    }
                }

            }
        }

        for (Contribuyente con : listaContribuyenteFiltrado) {
            emailsList.add(con.getEmail());
        }

        errorManager.errorManagerNIF(malNie);
        errorManager.errorManagerCCC(malCCC);

        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce trimestre a calcular Ejemplo: 1T 2024");
        String trimestre = sc.nextLine();

        GeneradorRecibosXML xmlGen = new GeneradorRecibosXML();
        xmlGen.generateRecibeXML(listaContribuyenteFiltrado, listaOrdenanza, trimestre);

        excMang.writeExcel(listaContribuyente);

        List<Element> listElemContr = xmlGen.getListaContrValor();

        List<HashMap<String, String>> listaDeMapas = convertXML2Map(listElemContr, emailsList);
//        
//        for(String a: aa){
//            System.out.println(a);
//            
//        }

//        for (int i = 0; i < listaDeMapas.size(); i++) {
//            HashMap<String, String> map = listaDeMapas.get(i);
//            System.out.println("Mapa " + (i + 1) + ":");
//            for (String clave : map.keySet()) {
//                System.out.println("  " + clave + ": " + map.get(clave));
//            }
//        }

//        
        dbm.init(listaContribuyenteFiltrado, listaOrdenanza, listaDeMapas, trimestre);
        System.exit(0);
    }

    private boolean isEmptyContribuyente(Contribuyente actual) {

        if (actual.getNIFNIE() == null && actual.getCCC() == null && actual.getIBAN() == null) {
            return true;
        }

        return false;
    }

    private static List<HashMap<String, String>> convertXML2Map(List<Element> listaElementos, List<String> emailsList) {
        List<HashMap<String, String>> listaDeMapas = new ArrayList<>();

        for (int i = 0; i < listaElementos.size(); i++) {
            Element elementoPrincipal = listaElementos.get(i);
            HashMap<String, String> map = new HashMap<>();
            List<Element> elementos = elementoPrincipal.getChildren();

            for (Element elemento : elementos) {
                String nombreElemento = elemento.getName();
                String valorElemento = elemento.getText();
                map.put(nombreElemento, valorElemento);
            }
            map.put("email", emailsList.get(i));
            listaDeMapas.add(map);
        }

        return listaDeMapas;
    }

}
