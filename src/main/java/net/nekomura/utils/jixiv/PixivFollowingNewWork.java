package net.nekomura.utils.jixiv;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.Arrays;

public class PixivFollowingNewWork {
    private final int page;
    private final JSONArray data;

    public PixivFollowingNewWork(int page, @NotNull String data) {
        this.page = page;
        this.data = new JSONArray(data);
    }

    /**
     * 獲取頁碼
     * @return 頁碼
     */
    public int getPage() {
        return page;
    }

    private JSONArray getData() {
        return data;
    }

    /**
     * 獲取已關注用戶的最新作品該頁數第幾個作品之資訊物件
     * @param index 第幾個作品
     * @return 關注作品資訊物件
     */
    public FollowingInfoObject get(int index) {
        return new FollowingInfoObject(data.getJSONObject(index));
    }

    /**
     * 獲取該頁數之所有作品的作品id
     * @return 該頁數之所有作品的作品id
     */
    public int[] getArtworkIds() {
        int[] ids = new int[data.length()];
        for (int i = 0; i < data.length(); i++) {
            ids[i] = Integer.parseInt(data.getJSONObject(i).getString("illustId"));
        }
        return ids;
    }

    /**
     * 獲取該頁數之所有作品的作品標題
     * @return 該頁數之所有作品的作品標題
     */
    public String[] getArtworkTitles() {
        String[] titles = new String[data.length()];
        for (int i = 0; i < data.length(); i++) {
            titles[i] = data.getJSONObject(i).getString("illustTitle");
        }
        return titles;
    }

    /**
     * 該頁是否含有作品id
     * @param id 檢測之作品id
     * @return 是否含有作品id
     */
    public boolean containId(int id) {
        return Arrays.stream(getArtworkIds()).anyMatch(x -> x == id);
    }
}