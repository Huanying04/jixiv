package net.nekomura.utils.jixiv;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class FollowingLatestWork {
    private final JSONObject data;
    private final String type;

    public FollowingLatestWork(JSONObject data, String type) {
        this.data = data;
        this.type = type;
    }

    /**
     * 獲取該頁數之所有作品的作品標題
     * @return 該頁數之所有作品的作品標題
     */
    public int[] getIds() {
        JSONArray ids = data.getJSONObject("page").getJSONArray("ids");
        int[] idsArray = new int[ids.length()];
        for (int i = 0; i < ids.length(); i++) {
            idsArray[i] = ids.getInt(i);
        }
        return idsArray;
    }

    /**
     * 獲取該頁數之所有作品的作品標題
     * @return 該頁數之所有作品的作品標題
     */
    public String[] getArtworkTitles() {
        JSONArray artworks = data.getJSONObject("thumbnails").getJSONArray(this.type);
        String[] titles = new String[artworks.length()];
        for (int i = 0; i < artworks.length(); i++) {
            titles[i] = artworks.getJSONObject(i).getString("title");
        }
        return titles;
    }

    /**
     * 該頁是否含有作品id
     * @param id 檢測之作品id
     * @return 是否含有作品id
     */
    public boolean containId(int id) {
        return Arrays.stream(getIds()).anyMatch(x -> x == id);
    }
}
