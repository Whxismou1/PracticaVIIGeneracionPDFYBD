
package Controllers;

import Entities.Contribuyente;

public class NIFController {

    private static final char[] LETRAS_CONTROL = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
    private boolean isSaneado = false;

    public boolean isNifValid(String nif, boolean isEspañol, Contribuyente actualContribuyente) {
        if (!isValidLength(nif)) {
            return false;
        }

        if (isEspañol) {
            if (isValidStructureNIF(nif)) {
                return checkControlLetter(nif, true, actualContribuyente);
            }
            return false;
        } else {
            if (isValidStructureNIEExtranjero(nif)) {
                return checkControlLetter(nif, false, actualContribuyente);
            }
            return false;
        }

    }

    private boolean isValidLength(String nif) {
        return nif != null && nif.length() == 9;
    }

    private boolean isValidStructureNIF(String nif) {
        if (!Character.isLetter(nif.charAt(nif.length() - 1))) {
            return false;
        }

        for (int i = 0; i < nif.length() - 1; i++) {
            if (!Character.isDigit(nif.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidStructureNIEExtranjero(String nif) {
             
        if (!Character.isLetter(nif.charAt(nif.length() - 1)) || !Character.isLetter(nif.charAt(0))) {
            return false;
        }

        for (int i = 1; i < nif.length() - 1; i++) {
            if (!Character.isDigit(nif.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean checkControlLetter(String nif, boolean español, Contribuyente actualContri) {
        if (español) {
            char letraControlActual = nif.charAt(nif.length() - 1);
            String digits = nif.substring(0, nif.length() - 1);

            char letraControlReal = LETRAS_CONTROL[Integer.parseInt(digits) % 23];

            boolean isSameLetters = (letraControlActual == letraControlReal);

            if (isSameLetters) {
                isSaneado = false;
            } else {
                String newNif = digits + letraControlReal;
                actualContri.setNIFNIE(newNif);
                isSaneado = true;
            }

            return true;
        } else {
            int firstLetterNum = getFirstLetterNIF(nif);
            char letraControlActual = nif.charAt(nif.length() - 1);
            String digits = firstLetterNum + nif.substring(1, nif.length() - 1);
            int index = Integer.parseInt(digits) % 23;
            if (index < 0) {
                index += 23; // Ajustar el índice negativo
            }
            char letraControlReal = LETRAS_CONTROL[index];

            boolean isSameLetters = (letraControlActual == letraControlReal);

            if (isSameLetters) {
                isSaneado = false;
            } else {
                String beginIndex = "";
                if(firstLetterNum == 0){
                    beginIndex = "X";
                }else if(firstLetterNum == 1){
                    beginIndex = "Y";
                }else if(firstLetterNum == 2){
                    beginIndex = "Z";
                }
    
                String newNie = beginIndex + digits.substring(1, digits.length()) + letraControlReal;
                actualContri.setNIFNIE(newNie);
                isSaneado = true;
            }
            return true;

        }

    }

    public boolean getIsSaneado() {
        return this.isSaneado;
    }

    private int getFirstLetterNIF(String nif) {
        char firstLetra = nif.charAt(0);

        switch (firstLetra) {
            case 'X':
            case 'x':
                return 0;
            case 'Y':
            case 'y':
                return 1;
            case 'Z':
            case 'z':
                return 2;

        }
        return -1;

    }

    public boolean isSpanish(String nif) {
        boolean valifNIExtranjero = isValidStructureNIEExtranjero(nif);

        if (valifNIExtranjero) {
            return false;
        }

        return true;
    }

    public void clearSaneado() {
        this.isSaneado = false;
    }

}