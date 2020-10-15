import Enums.*;
import Utils.Sort;
import Utils.UserAgentUtils;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.net.UrlEscapers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Pixiv {

    private String phpSession;

    public Pixiv(String phpSession) {
        this.phpSession = phpSession;
    }

    public void setPhpSession(String phpSession) {
        this.phpSession = phpSession;
    }

    public JSONObject getUserProfileInfo(int id) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/ajax/user/" + id + "/profile/all");

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", UserAgentUtils.getRandomUserAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return new JSONObject(res.body().string());
    }

    public PixivRank rank(int page, PixivRankMode mode, @NotNull PixivRankContent content, String date) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url;

        if (content.equals(PixivRankContent.Overall)) {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&date=" + date + "&p=" + page + "&format=json";
        }/*else if (content.equals(PixivRankContent.Novel)) {
            url = "https://www.pixiv.net/novel/ranking.php?mode=" + mode.toString().toLowerCase() + "&date=" + date + "&p=" + page + "&format=json";
        }*/else {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&content=" + content.toString().toLowerCase() + "&date=" + date + "&p=" + page + "&format=json";
        }

        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", UserAgentUtils.getRandomUserAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return new PixivRank(res.body().string());
    }

    public PixivRank rank(int page, PixivRankMode mode, @NotNull PixivRankContent content) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url;

        if (content.equals(PixivRankContent.Overall)) {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&p=" + page + "&format=json";
        }/*else if (content.equals(PixivRankContent.Novel)) {
            url = "https://www.pixiv.net/novel/ranking.php?mode=" + mode.toString().toLowerCase() + "&p=" + page + "&format=json";
        }*/else {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&content=" + content.toString().toLowerCase() + "&p=" + page + "&format=json";
        }

        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", UserAgentUtils.getRandomUserAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return new PixivRank(res.body().string());
    }

    public PixivRank rank(int page) throws IOException {
        return rank(page, PixivRankMode.Daily, PixivRankContent.Overall);
    }

    public int[] getUserArtworks(int id, @NotNull PixivUserArtworkType type) throws IOException {
        JSONObject json = getUserProfileInfo(id);
        int keySize = Iterators.size(json.getJSONObject("body").getJSONObject(type.name().toLowerCase()).keys());
        int[] artworks = new int[keySize];

        for (int i = 0; i < keySize; i++) {
            artworks[i] = Integer.parseInt(Iterators.get(json.getJSONObject("body").getJSONObject(type.name().toLowerCase()).keys(), i));
        }

        return Sort.bubbleNegativeWay(artworks);
    }

    public PixivSearchResult search(String keywords, int page, @NotNull PixivSearchArtistType artistType, PixivSearchOrder order, @NotNull PixivSearchMode mode, @NotNull PixivSearchSMode sMode, @NotNull PixivSearchType type) throws IOException {
        String url = String.format("https://www.pixiv.net/ajax/search/%s/%s?word=%s&order=%s&p=%d&s_mode=%s&type=%s&lang=zh_tw",
                artistType.toString().toLowerCase(),
                UrlEscapers.urlFragmentEscaper().escape(keywords),
                UrlEscapers.urlFragmentEscaper().escape(keywords),
                mode.toString().toLowerCase(),
                page,
                sMode.toString().toLowerCase(),
                type.toString().toLowerCase());
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", UserAgentUtils.getRandomUserAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String resultType;
        switch (artistType) {
            case All:
                resultType = "illustManga";
                break;
            case Illustrations:
                resultType = "illust";
                break;
            case Manga:
                resultType = "manga";
                break;
            case Novels: resultType = "novel";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + artistType);
        }


        return new PixivSearchResult(res.body().string(), resultType);
    }

    public PixivSearchResult search(String keywords, int page) throws IOException {
        return search(keywords, page, PixivSearchArtistType.Illustrations, PixivSearchOrder.NEW_TO_OLD, PixivSearchMode.SAFE, PixivSearchSMode.S_TAG, PixivSearchType.Illust);
    }
}