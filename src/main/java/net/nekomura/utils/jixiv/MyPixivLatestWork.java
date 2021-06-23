package net.nekomura.utils.jixiv;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyPixivLatestWork {
    private final JSONObject data;

    public MyPixivLatestWork(JSONObject data) {
        this.data = data;
    }

    public int[] getIds() {
        JSONArray ids = data.getJSONObject("page").getJSONArray("ids");
        int[] idsArray = new int[ids.length()];
        for (int i = 0; i < ids.length(); i++) {
            idsArray[i] = ids.getInt(i);
        }
        return idsArray;
    }
}
