package net.nekomura.utils.jixiv;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class PixivNovel extends PixivArtwork {

    public PixivNovel(String phpSession) {
        super(phpSession);
    }

    public String getContent(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject("novel").getJSONObject(String.valueOf(id)).getString("content");
    }

    public int getMarkerCount(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject("novel").getJSONObject(String.valueOf(id)).getInt("markerCount");
    }

    public int getTextCount(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject("novel").getJSONObject(String.valueOf(id)).getJSONObject("userNovels").getJSONObject(String.valueOf(id)).getInt("textCount");
    }

    private String getCoverUrl(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject("novel").getJSONObject(String.valueOf(id)).getString("coverUrl");
    }

    public byte[] getCover(int id) throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(getCoverUrl(id));
        rb.addHeader("Referer", "https://www.pixiv.net/artworks");
        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        return res.body().bytes();
    }

    public void downloadCover(String pathname, int id) throws Exception {
        File file = new File(pathname);
        byte[] image = getCover(id);
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        out.write(image);
        out.close();
    }

}