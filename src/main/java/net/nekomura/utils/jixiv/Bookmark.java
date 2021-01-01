package net.nekomura.utils.jixiv;

import org.json.JSONObject;

public class Bookmark {
    private final int page;
    private final JSONObject data;

    public Bookmark(int page, JSONObject data) {
        this.page = page;
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    private JSONObject getData() {
        return data;
    }

    private void showError() throws Exception {
        if (data.getBoolean("error")) {
            throw new Exception(data.getString("message"));
        }
    }

    public int[] getIDs() throws Exception {
        showError();

        int[] ids = new int[48];
        for (int i = 0; i < 48; i++) {
            ids[i] = Integer.parseInt(data.getJSONObject("body").getJSONArray("works").getJSONObject(i).getString("id"));
        }
        return ids;
    }
}
