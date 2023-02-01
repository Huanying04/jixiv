package net.nekomura.utils.jixiv.artworks;

import net.nekomura.utils.jixiv.Jixiv;
import net.nekomura.utils.jixiv.enums.artwork.PixivArtworkType;
import net.nekomura.utils.jixiv.exception.PixivException;
import net.nekomura.utils.jixiv.utils.PixivUrlBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

public class Artwork {
    /**
     * 獲取作品資訊
     * @param id 作品id
     * @param type 作品類別
     * @return 作品資訊的JSON Object
     * @throws IOException GET失敗
     */
    public static JSONObject getArtworkData(int id, PixivArtworkType type) throws IOException {
        String url;
        PixivUrlBuilder pub = new PixivUrlBuilder();
        if (type.equals(PixivArtworkType.ILLUSTS)) {
            pub.setPath("ajax/illust/" + id);
            url = pub.build();
        }else if (type.equals(PixivArtworkType.NOVELS)) {
            pub.setPath("ajax/novel/" + id);
            url = pub.build();
        }else {
            throw new IllegalArgumentException("The type must be PixivArtworkType.Illusts or PixivArtworkType.Novels");
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        JSONObject json = new JSONObject(res.body().string());

        res.close();

        if (json.getBoolean("error")) {
            throw new PixivException(json.getString("message"));
        }
        return json.getJSONObject("body");
    }
}
