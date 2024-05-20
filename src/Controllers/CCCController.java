/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Entities.Contribuyente;
import java.util.List;


public class CCCController {

    private static int[] arrayPos = {1, 2, 4, 8, 5, 10, 9, 7, 3, 6};
    private boolean isCorregido = false;

    public void checkCCC(String ccc, List<Contribuyente> malCCC, Contribuyente actualContri) {
        if (!isValidLengthCCC(ccc)) {
            malCCC.add(actualContri);
            return;
        }

        int firstDigitOfControl = Integer.parseInt(String.valueOf(ccc.charAt(8)));
        int secondDigitOfControl = Integer.parseInt(String.valueOf(ccc.charAt(9)));
        StringBuilder sb = new StringBuilder("00");
        StringBuilder newCCC = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            sb.append(ccc.charAt(i));
            newCCC.append(ccc.charAt(i));
        }
        int sum = 0;
        for (int i = 0; i < sb.length(); i++) {
            int num = Integer.parseInt(String.valueOf(sb.charAt(i)));
            sum += num * arrayPos[i];
        }
        int tempOne = 11 - (sum % 11);
        int realFirstDigitOfControl;
        if (tempOne >= 11) {
            realFirstDigitOfControl = 0;
        } else if (tempOne >= 10) {
            realFirstDigitOfControl = 1;
        } else {
            realFirstDigitOfControl = tempOne;
        }
        
        newCCC.append(realFirstDigitOfControl);
        
        sb.delete(0, sb.length());
        
        String secondPart = "";
        for (int i = 10; i < ccc.length(); i++) {
            sb.append(ccc.charAt(i));
            secondPart += ccc.charAt(i);
        }

        sum = 0;
        for (int i = 0; i < sb.length(); i++) {
            int num = Integer.parseInt(String.valueOf(sb.charAt(i)));
            sum += num * arrayPos[i];
        }

        int temp = 11 - (sum % 11);
        int realSecondDigitOfControl;

        if (temp >= 11) {
            realSecondDigitOfControl = 0;
        } else if (temp >= 10) {
            realSecondDigitOfControl = 1;
        } else {
            realSecondDigitOfControl = temp;
        }

        
        newCCC.append(realSecondDigitOfControl+secondPart);
        
        if (realFirstDigitOfControl != firstDigitOfControl || realSecondDigitOfControl != secondDigitOfControl) {
            //check xml genereta
            isCorregido = true;
            actualContri.setCCC(newCCC.toString());
            malCCC.add(actualContri);
        }else{
            isCorregido = false;
        }

    }

    private boolean isValidLengthCCC(String ccc) {
        if (ccc.length() > 20 || ccc.length() < 20) {
            return false;
        }

        return true;
    }

    public boolean getIsCorregido() {
        return isCorregido;
    }
    
}