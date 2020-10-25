package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Enums.PixivIllustrationType;
import net.nekomura.utils.jixiv.Enums.PixivImageUrlType;
import net.nekomura.utils.jixiv.Enums.PixivUserArtworkType;
import net.nekomura.utils.jixiv.Utils.StringUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.*;
import java.util.Calendar;

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

    private String getUgoiraZipUrl(int id) throws Exception {
        Calendar calendar = getCreateDateCalendar(id);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String monthString = StringUtils.addZeroChar(month + 1);
        String dayString = StringUtils.addZeroChar(day);
        String hourString = StringUtils.addZeroChar(hour);
        String minuteString = StringUtils.addZeroChar(minute);
        String secondString = StringUtils.addZeroChar(second);

        return String.format("https://i.pximg.net/img-zip-ugoira/img/%d/%s/%s/%s/%s/%s/%d_ugoira1920x1080.zip",
                year,
                monthString,
                dayString,
                hourString,
                minuteString,
                secondString,
                id);
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

    public byte[] getUgoiraZip(int id) throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(getUgoiraZipUrl(id));
        rb.addHeader("Referer", "https://www.pixiv.net/artworks");
        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        return res.body().bytes();
    }

    public PixivIllustrationType getIllustrationType(int id) throws Exception {
        int typeNumber = getArtworkPreloadData(id).getJSONObject("illust").getJSONObject(String.valueOf(id)).getInt("illustType");
        switch (typeNumber) {
            case 0:
                return PixivIllustrationType.Illustration;
            case 1:
                return PixivIllustrationType.Manga;
            case 2:
                return PixivIllustrationType.Ugoira;
            default:
                throw new IllegalArgumentException();
        }
    }

    public String getImageFileFormat(int id, int page) throws Exception {
        String[] filename = getImageUrl(id, page, PixivImageUrlType.Original).split("\\.");
        return filename[filename.length - 1];
    }

    public void download(String pathname, int id, int page, PixivImageUrlType type) throws Exception {
        File file = new File(pathname);
        byte[] image = getImage(id, page, type);
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        out.write(image);
        out.close();
    }

    public void download(String pathname, int id, int page) throws Exception {
        download(pathname, id, page, PixivImageUrlType.Original);
    }

    public void download(String pathname, int id) throws Exception {
        download(pathname, id, 0, PixivImageUrlType.Original);
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

    public void downloadUgoiraZip(String pathname, int id) throws Exception {
        if (!getIllustrationType(id).equals(PixivIllustrationType.Ugoira)) {
            throw new IllegalArgumentException("The Illustration is not an ugoira.");
        }

        File file = new File(pathname);
        byte[] bytes = getUgoiraZip(id);
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();
    }

}