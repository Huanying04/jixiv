import net.nekomura.utils.jixiv.IllustrationInfo;
import net.nekomura.utils.jixiv.Jixiv;
import net.nekomura.utils.jixiv.artworks.Illustration;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        Jixiv.loginByCookie("25714086_s2VHnqDrrm9UK29z1sz3pnLiahdQ1eOh");
        Jixiv.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36");
        IllustrationInfo i = Illustration.getInfo(91219596);
        System.out.println(i.getDescription());
        System.out.println(i.getRawDescription());
    }
}
