package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.utils.UserAgentUtils;

public class Jixiv {
    public static String PHPSESSID;
    public static String USER_AGENT;

    /**
     * 儲存PHPSESSID以便之後使用
     * @param phpsessid 瀏覽器登入後會儲存的一個cookie名叫PHPSESSID，使用此cookie的值可以假裝登入帳號
     */
    public static void loginByCookie(String phpsessid) {
        PHPSESSID = phpsessid;
    }

    /**
     * 設定一個固定User Agent
     * @param userAgent User Agent
     */
    public static void setUserAgent(String userAgent) {
        USER_AGENT = userAgent;
    }

    /**
     * 如果未設定User Agent則返回隨機User Agent，有則返回設定的User Agent
     * @return User Agent
     */
    public static String userAgent() {
        if (Jixiv.USER_AGENT == null || Jixiv.USER_AGENT.isEmpty()) {
            return UserAgentUtils.random();
        }else {
            return Jixiv.USER_AGENT;
        }
    }
}
