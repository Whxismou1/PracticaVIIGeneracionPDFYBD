package Controllers;

import Entities.Contribuyente;
import java.math.BigInteger;
import java.util.HashMap;


public class IBANController {

    public void checkIban(Contribuyente actualContri) {
        
        String inputCCC = actualContri.getCCC();
        String country = actualContri.getPaisCCC();
        
        HashMap<Character, Integer> tableCountry = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        char firstLetter = 'A';
        for (int i = 10; i < 36; i++) {
            tableCountry.put(firstLetter, i);
            firstLetter = (char) (firstLetter + 1);

        }

        int numOneCountry = tableCountry.get(country.charAt(0));
        int numTwoCountry = tableCountry.get(country.charAt(1));

        sb.append(inputCCC + numOneCountry + numTwoCountry + "00");

        String iban = sb.toString().trim();
        BigInteger codIban = new BigInteger(iban);

        BigInteger divisor = new BigInteger("97");
        BigInteger resto = codIban.mod(divisor);
        
        int diferencia = 98 - resto.intValue();

        String numDiferencia = String.valueOf(diferencia);
        String newNum = "";

        if (numDiferencia.length() == 2) {
            newNum += numDiferencia;
        } else {
            newNum += "0" + numDiferencia;
        }


        sb.delete(0, sb.length());
        sb.append(country + newNum + inputCCC);

        actualContri.setIBAN( sb.toString());
    }

}