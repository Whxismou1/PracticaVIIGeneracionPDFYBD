package mainpkg;

import Controllers.CCCController;
import Controllers.ErrorManager;
import Controllers.ExcelManager;
import Controllers.GeneradorRecibosXML;
import Controllers.IBANController;
import Controllers.NIFController;
import Entities.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Coordinator {

    public void init() {
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

        errorManager.errorManagerNIF(malNie);
        errorManager.errorManagerCCC(malCCC);

        
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce trimestre a calcular Ejemplo: 1T 2024");
        String trimestre = sc.nextLine();
        new GeneradorRecibosXML().generateRecibeXML(listaContribuyenteFiltrado, listaOrdenanza, trimestre);
        excMang.writeExcel(listaContribuyente);
    }

    private boolean isEmptyContribuyente(Contribuyente actual) {

        if (actual.getNIFNIE() == null && actual.getCCC() == null && actual.getIBAN() == null) {
            return true;
        }

        return false;
    }
}
