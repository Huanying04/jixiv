import net.nekomura.utils.jixiv.Jixiv;
import net.nekomura.utils.jixiv.Pixiv;
import net.nekomura.utils.jixiv.artworks.Illustration;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        Jixiv.loginByCookie("25714086_KjvLu9jO35XZC6MYIjK2KjAduzxGQgCp");
        Jixiv.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36");
        System.out.println(Illustration.getInfo(86410564).isBookmarked());
    }
}