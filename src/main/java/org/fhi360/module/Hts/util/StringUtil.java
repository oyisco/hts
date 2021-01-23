package org.fhi360.module.Hts.util;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.commons.lang3.StringUtils;

public class StringUtil {
    private static final String[] SUFFIX = {"y"};
    private static final String[] REPLACEMENTS = {"s", "ies"};

    public static String pluralize(String str) {
        if (str.endsWith(SUFFIX[0])) {
            str = str.replace(str.substring(str.lastIndexOf(SUFFIX[0])), REPLACEMENTS[1]);
        } else {
            str += "s";
        }
        return str;
    }

    public static String setCountryCode(String phone, String countryCode) {
        return phone.replaceFirst("\\d", countryCode);
    }
    
    public static String zerorize(String str, int max) {
        String zeros = "";
        if(str.length() < max) {
            for(int i = 0; i < max - str.length(); i++) {
               zeros = zeros + "0"; 
            }
            str = zeros + str;
        }
        return str;
    }
    
    public static boolean isInteger(String s) {
        return StringUtils.isNumeric(s);
    }

    public static boolean isDouble(String variable){
        try {
            Double.parseDouble(variable);
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLong(String variable){
        return StringUtils.isNumeric(variable);
    }

    public static boolean isInt(String variable){
        return StringUtils.isNumeric(variable);
    }
    
    public static String stripCommas(String s) {      
        return s.replace(",", "").replace(";", "").replace("'", "");
        
    }

    public static boolean isDateParceable(String variable, String format){
        boolean dateParseable = true;
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            if(!variable.equals("") && !variable.isEmpty()) {
                dateFormat.parse(variable);
            }
        }catch (ParseException e) {
            dateParseable = false;
        }
        return dateParseable;
    }
    
    public static boolean found(String searchFor, String[] strings) {
        boolean found = false;
        for(String string : strings) {
            if (string.equalsIgnoreCase(searchFor.toLowerCase())) {
                found = true;
            }           
        }       
        return found;
    }
        
    public static String toCamelCase(String s){
   String[] parts = s.split("_");
   String camelCaseString = "";
    
   for (String part : parts){
      camelCaseString = camelCaseString + toProperCase(part);
    
   }
   return camelCaseString.substring(0,1).toLowerCase()+ camelCaseString.substring(1);
}

public static String toProperCase(String s) {
    return s.substring(0, 1).toUpperCase() +
               s.substring(1).toLowerCase();
}
}
