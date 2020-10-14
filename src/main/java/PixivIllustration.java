import Enums.PixivImageUrlType;
import Enums.PixivUserArtworkType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class PixivIllustration extends PixivArtwork {

    public PixivIllustration(String phpSession) {
        super(phpSession);
    }

    public int getResponseCount(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject("illust").getJSONObject(String.valueOf(id)).getInt("responseCount");
    }

    @NotNull
    private String getImageUrl(int id, int page, @NotNull PixivImageUrlType type) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        String pageZero = json.getJSONObject("illust").getJSONObject(String.valueOf(id)).getJSONObject("urls").getString(type.toString().toLowerCase());
        return pageZero.replace(id + "_p0", id + "_p" + page);
    }

    public byte[] getImage(int id) throws Exception {
        return getImage(id, 0);
    }

    public byte[] getImage(int id, int page) throws Exception {
        return getImage(id, page, PixivImageUrlType.Original);
    }

    public byte[] getImage(int id, int page, PixivImageUrlType type) throws Exception {

        if (page > getPageCount(id) - 1)
            throw new IllegalArgumentException("The page is greater than the max page of the artwork .");

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(getImageUrl(id, page, type));
        rb.addHeader("Referer", "https://www.pixiv.net/artworks");
        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        return res.body().bytes();
    }

    public String getImageFileFormat(int id, int page) throws Exception {
        String[] filename = getImageUrl(id, page, PixivImageUrlType.Original).split("\\.");
        return filename[filename.length - 1];
    }

    public void download(@NotNull File file, int id, int page, PixivImageUrlType type) throws Exception {
        byte[] image = getImage(id, page, type);
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        out.write(image);
        out.close();
    }

    public void download(File file, int id, int page) throws Exception {
        download(file, id, page, PixivImageUrlType.Original);
    }

    public void download(File file, int id) throws Exception {
        download(file, id, 0, PixivImageUrlType.Original);
    }

    public void download(String filePath, int id, int page, PixivImageUrlType type) throws Exception {
        download(new File(filePath), id, page, type);
    }

    public void download(String filePath, int id, int page) throws Exception {
        download(new File(filePath), id, page, PixivImageUrlType.Original);
    }

    public void download(String filePath, int id) throws Exception {
        download(new File(filePath), id, 0, PixivImageUrlType.Original);
    }

    public void downloadAll(File folder, int id, PixivImageUrlType type) throws Exception {
        int pageCount = getPageCount(id);
        folder.mkdirs();

        for (int i = 0; i < pageCount; i++) {

            File file = new File(String.format("%s/%d_p%d.%s", folder, id, i, getImageFileFormat(id, i)));
            byte[] image = getImage(id, i, type);
            FileOutputStream out = new FileOutputStream(file);
            out.write(image);
            out.close();
        }
    }

    public void downloadAll(String folderPath, int id, PixivImageUrlType type) throws Exception {
        downloadAll(new File(folderPath), id, type);
    }

    public void downloadUserAll(File folder, int userId, PixivImageUrlType type) throws Exception {
        int[] artworks = new Pixiv(getPhpSession()).getUserArtworks(userId, PixivUserArtworkType.Illusts);
        for (int id: artworks) {
            downloadAll(folder, id, type);
        }
    }

    public void downloadUserAll(String folderPath, int userId, PixivImageUrlType type) throws Exception {
        int[] artworks = new Pixiv(getPhpSession()).getUserArtworks(userId, PixivUserArtworkType.Illusts);
        for (int id: artworks) {
            downloadAll(folderPath, id, type);
        }
    }

}