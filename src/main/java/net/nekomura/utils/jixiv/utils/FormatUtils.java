package net.nekomura.utils.jixiv.utils;

public class FormatUtils {
    /**
     * 使數字永遠保持兩個數字<br>
     * 如1會變成01<br>
     * 而11則保持11
     * @param i 欲保持兩位數字的數字
     * @return 兩位數字
     */
    @Deprecated
    public static String addZeroChar(int i) {
        String string = String.valueOf(i);
        if (string.length() == 1) {
            return "0" + string;
        }else
            return string;
    }
}