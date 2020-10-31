package net.nekomura.utils.jixiv;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class PixivSearchResult {

    private final JSONObject searchResultJson;
    private final String dataType;

    public PixivSearchResult(String searchResultJson, String dataType) {
        this.searchResultJson = new JSONObject(searchResultJson);
        this.dataType = dataType;
    }

    public int getPageResultCount() {
        return searchResultJson.getJSONObject("body").getJSONObject(dataType).getJSONArray("data").length();
    }

    private JSONArray getResultData() {
        return searchResultJson.getJSONObject("body").getJSONObject(dataType).getJSONArray("data");
    }

    public int[] getIds() {
        int[] id = new int[getPageResultCount()];
        for (int i = 0; i < getPageResultCount(); i++) {
            id[i] = Integer.parseInt(getResultData().getJSONObject(i).getString("id"));
        }
        return id;
    }

    public int getResultCount() {
        return searchResultJson.getJSONObject("body").getJSONObject(dataType).getInt("total");
    }

    public int getLastPageIndex() {
        return (int) Math.ceil(getResultCount() / 60f);
    }

    public boolean containId(int id) {
        return Arrays.stream(getIds()).anyMatch(x -> x == id);
    }
}
