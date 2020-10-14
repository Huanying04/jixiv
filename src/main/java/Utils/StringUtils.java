package Utils;

public class StringUtils {

    public static String subString(String string, String from, String to) {
        int fromIndex = string.indexOf(from);
        int toIndex = string.indexOf(to, fromIndex);
        return string.subSequence(fromIndex, toIndex).toString().replace(from, "");
    }

}