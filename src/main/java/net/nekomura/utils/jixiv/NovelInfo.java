package net.nekomura.utils.jixiv;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class NovelInfo extends ArtworkInfo {
    public NovelInfo(int id, JSONObject preloadData) {
        super(id, preloadData);
    }

    /**
     * 獲取小說內容
     * @return 小說內容
     */
    public String getContent() {
        return getData().getString("content");
    }

    /**
     * Get Marker Count
     * @return Marker Count
     */
    public int getMarkerCount() {
        return getData().getInt("markerCount");
    }

    /**
     * 獲取總字數
     * @return 總字數
     */
    public int getTextCount() {
        return getData().getJSONObject("userNovels").getJSONObject(String.valueOf(getId())).getInt("textCount");
    }

    private String getCoverUrl() {
        return getData().getString("coverUrl");
    }

    /**
     * 獲取封面圖片
     * @return 封面圖片的byte array
     * @throws IOException 獲取網路資料失敗
     */
    public byte[] getCover() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(getCoverUrl());
        rb.addHeader("Referer", "https://www.pixiv.net/artworks");
        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        return Objects.requireNonNull(res.body()).bytes();
    }

    /**
     * 下載封面圖片
     * @param pathname 儲存位置
     * @throws IOException 獲取網路資料失敗或文件處理錯誤
     */
    public void downloadCover(String pathname) throws IOException {
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