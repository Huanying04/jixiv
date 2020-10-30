package net.nekomura.utils.jixiv;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class PixivNovelInfo extends PixivArtworkInfo {
    public PixivNovelInfo(int id, JSONObject preloadData) {
        super(id, preloadData);
    }

    public String getContent() {
        return getPreloadData().getJSONObject("novel").getJSONObject(String.valueOf(getId())).getString("content");
    }

    public int getMarkerCount() {
        return getPreloadData().getJSONObject("novel").getJSONObject(String.valueOf(getId())).getInt("markerCount");
    }

    public int getTextCount() {
        return getPreloadData().getJSONObject("novel").getJSONObject(String.valueOf(getId())).getJSONObject("userNovels").getJSONObject(String.valueOf(getId())).getInt("textCount");
    }

    private String getCoverUrl() {
        return getPreloadData().getJSONObject("novel").getJSONObject(String.valueOf(getId())).getString("coverUrl");
    }

    public byte[] getCover() throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(getCoverUrl());
        rb.addHeader("Referer", "https://www.pixiv.net/artworks");
        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        return Objects.requireNonNull(res.body()).bytes();
    }

    public void downloadCover(String pathname) throws Exception {
        File file = new File(pathname);
        byte[] image = getCover();
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        out.write(image);
        out.close();
    }

}