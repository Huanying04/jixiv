package net.nekomura.utils.jixiv;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class SearchResult {

    private final JSONObject searchResultJson;
    private final String dataType;

    public SearchResult(String searchResultJson, String dataType) {
        this.searchResultJson = new JSONObject(searchResultJson);
        this.dataType = dataType;
    }

    /**
     * 獲取該頁之作品總數
     * @return 該頁之作品總數
     */
    public int getPageResultCount() {
        String ignore_true = "{\"isAdContainer\":true}";
        String ignore_false = "{\"isAdContainer\":false}";
        if (getResultData().toString().contains(ignore_true) || getResultData().toString().contains(ignore_false)) {
            return getResultData().length() - 1;
        }else {
            return getResultData().length();
        }
    }

    private JSONArray getResultData() {
        return searchResultJson.getJSONObject("body").getJSONObject(dataType).getJSONArray("data");
    }

    /**
     * 獲取該頁所有作品的id
     * @return 該頁所有作品的id
     */
    public int[] getIds() {
        int[] id = new int[getPageResultCount()];
        for (int i = 0; i < getPageResultCount(); i++) {
            if (getResultData().getJSONObject(i).has("isAdContainer")) {
                continue;
            }
            id[i] = Integer.parseInt(getResultData().getJSONObject(i).getString("id"));
        }
        return id;
    }

    /**
     * 獲取搜尋結果總數
     * @return 搜尋結果總數
     */
    public int getResultCount() {
        return searchResultJson.getJSONObject("body").getJSONObject(dataType).getInt("total");
    }

    /**
     * 獲取最後一頁頁碼
     * @return 最後一頁頁碼
     */
    public int getLastPageIndex() {
        return (int) Math.ceil(getResultCount() / 60f);
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
