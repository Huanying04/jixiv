package net.nekomura.utils.jixiv.Utils;

public class StringUtils {

    public static String subString(String string, String from, String to) {
        int fromIndex = string.indexOf(from);
        int toIndex = string.indexOf(to, fromIndex);
        return string.subSequence(fromIndex, toIndex).toString().replace(from, "");
    }

    public static String addZeroChar(int i) {
        String string = String.valueOf(i);
        if (string.length() == 1) {
            return "0" + string;
        }else
            return string;
    }
}