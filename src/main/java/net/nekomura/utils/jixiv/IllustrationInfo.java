package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.enums.artwork.PixivIllustrationType;
import net.nekomura.utils.jixiv.enums.artwork.PixivImageSize;
import net.nekomura.utils.jixiv.exception.PixivException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class IllustrationInfo extends ArtworkInfo {
    public IllustrationInfo(int id, JSONObject data) {
        super(id, data);
    }

    /**
     * Get Response Count
     * @return Response Count
     */
    public int getResponseCount() {
        return getData().getInt("responseCount");
    }

    private JSONObject getUgoiraMeta() throws IOException {
        String url = "https://www.pixiv.net/ajax/illust/" + getId() + "/ugoira_meta";

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

    private String getImageUrl(int page, @NotNull PixivImageSize type) {
        String pageZero = getData().getJSONObject("urls").getString(type.toString().toLowerCase());
        return pageZero.replace(getId() + "_p0", getId() + "_p" + page);
    }

    private String getUgoiraZipUrl() throws IOException {
        return getUgoiraMeta().getString("originalSrc");
    }

    /**
     * 獲取該插畫作品之第一頁圖片
     * @return 該插畫作品之第一頁圖片的byte array
     * @throws IOException 獲取失敗
     */
    public byte[] getImage() throws IOException {
        return getImage(0);
    }

    /**
     * 獲取該插畫作品之指定頁圖片
     * @param page 指定頁碼
     * @return 該插畫作品之指定頁圖片的byte array
     * @throws IOException 獲取失敗
     */
    public byte[] getImage(int page) throws IOException {
        return getImage(page, PixivImageSize.ORIGINAL);
    }

    /**
     * 獲取該插畫作品之指定頁圖片
     * @param page 指定頁碼
     * @param size 圖片大小
     * @return 該插畫作品之指定頁圖片的byte array
     * @throws IOException 獲取失敗
     */
    public byte[] getImage(int page, PixivImageSize size) throws IOException {
        if (page > getPageCount() - 1)
            throw new IllegalArgumentException("The page is greater than the max page of the artwork .");

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(getImageUrl(page, size));
        rb.addHeader("Referer", "https://www.pixiv.net/artworks");
        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        return Objects.requireNonNull(res.body()).bytes();
    }

    /**
     * 獲取該插畫動圖作品之所有幀之圖片之壓縮檔
     * @return 該插畫動圖作品之所有幀之圖片之壓縮檔的byte array
     * @throws IOException 獲取失敗
     */
    public byte[] getUgoiraZip() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(getUgoiraZipUrl());
        rb.addHeader("Referer", "https://www.pixiv.net/artworks");
        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        return Objects.requireNonNull(res.body()).bytes();
    }

    /**
     * 獲取動圖此一影格距離下張影格的時間差，或為這張影格的fps
     * @param page 第幾張影格
     * @return 此一影格距離下張影格的時間差
     * @throws IOException 獲取失敗
     */
    public int getUgoiraPageDelay(int page) throws IOException {
        return getUgoiraMeta().getJSONArray("frames").getJSONObject(page).getInt("delay");
    }

    /**
     * 獲取插畫作品類別
     * @return 插畫作品類別
     */
    public PixivIllustrationType getIllustrationType() {
        int typeNumber = getData().getInt("illustType");
        switch (typeNumber) {
            case 0:
                return PixivIllustrationType.ILLUSTRATION;
            case 1:
                return PixivIllustrationType.MANGA;
            case 2:
                return PixivIllustrationType.UGOIRA;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 獲取該插畫作品之圖片格式
     * @param page 指定頁碼
     * @return 插畫作品之圖片格式
     */
    public String getImageFileFormat(int page) {
        String[] filename = getImageUrl(page, PixivImageSize.ORIGINAL).split("\\.");
        return filename[filename.length - 1];
    }

    /**
     * 下載插畫圖片指定頁碼
     * @param pathname 儲存位置
     * @param page 指定頁碼
     * @param size 圖片大小
     * @throws IOException 獲取失敗
     */
    public void download(String pathname, int page, PixivImageSize size) throws IOException {
        File file = new File(pathname);
        byte[] image = getImage(page, size);
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        out.write(image);
        out.close();
    }

    /**
     * 下載插畫圖片指定頁碼
     * @param pathname 儲存位置
     * @param page 指定頁碼
     * @throws IOException 獲取網路資料失敗導致下載失敗
     */
    public void download(String pathname, int page) throws IOException {
        download(pathname, page, PixivImageSize.ORIGINAL);
    }

    /**
     * 下載插畫首張圖片
     * @param pathname 儲存位置
     * @throws IOException 獲取網路資料失敗導致下載失敗
     */
    public void download(String pathname) throws IOException {
        download(pathname, 0, PixivImageSize.ORIGINAL);
    }

    /**
     * 下載插畫所有頁圖片
     * @param folder 儲存資料夾
     * @param size 圖片大小
     * @throws IOException 獲取網路資料失敗導致下載失敗
     */
    public void downloadAll(File folder, PixivImageSize size) throws IOException {
        int pageCount = getPageCount();
        folder.mkdirs();

        for (int i = 0; i < pageCount; i++) {
            File file = new File(String.format("%s/%d_p%d.%s", folder, getId(), i, getImageFileFormat(i)));
            byte[] image = getImage(i, size);
            FileOutputStream out = new FileOutputStream(file);
            out.write(image);
            out.close();
        }
    }

    /**
     * 下載插畫所有頁圖片
     * @param folderPath 儲存資料夾位置
     * @param type 圖片大小
     * @throws IOException 獲取網路資料失敗導致下載失敗
     */
    public void downloadAll(String folderPath, PixivImageSize type) throws IOException {
        downloadAll(new File(folderPath), type);
    }

    /**
     * 下載該插畫動圖作品之所有幀之圖片之壓縮檔
     * @param pathname 儲存位置
     * @throws IOException 獲取網路資料失敗導致下載失敗
     */
    public void downloadUgoiraZip(String pathname) throws IOException {
        File file = new File(pathname);
        byte[] bytes = getUgoiraZip();
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();
    }

    /**
     * 使數字永遠保持兩個數字<br>
     * 如1會變成01<br>
     * 而11則保持11
     * @param i 欲保持兩位數字的數字
     * @return 兩位數字
     */
    private static String addZeroChar(int i) {
        String string = String.valueOf(i);
        if (string.length() == 1) {
            return "0" + string;
        }else
            return string;
    }
}