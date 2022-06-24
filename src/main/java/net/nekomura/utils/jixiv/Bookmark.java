package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.exception.PixivException;
import org.json.JSONObject;

public class Bookmark {
    private final JSONObject data;

    public Bookmark(JSONObject data) {
        this.data = data;
    }

    private JSONObject getData() {
        return data;
    }

    /**
     * 顯示錯誤
     */
    private void showError() {
        if (data.getBoolean("error")) {
            throw new PixivException(data.getString("message"));
        }
    }

    /**
     * 獲取此頁中所有作品的id
     * @return 此頁中所有作品的id
     */
    public int[] getIDs() {
        showError();

        int[] ids = new int[48];
        for (int i = 0; i < 48; i++) {
            ids[i] = Integer.parseInt(data.getJSONObject("body").getJSONArray("works").getJSONObject(i).getString("id"));
        }
        return ids;
    }
}
