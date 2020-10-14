import org.json.JSONArray;
import org.json.JSONObject;

public class PixivSearchResult {

    private JSONObject searchResultJson;
    private String dataType;

    public PixivSearchResult(String searchResultJson, String dataType) {
        this.searchResultJson = new JSONObject(searchResultJson);
        this.dataType = dataType;
    }

    private int getPageResultCount() {
        return searchResultJson.getJSONObject("body").getJSONObject(dataType).getJSONArray("data").length();
    }

    private JSONArray getResultData() {
        return searchResultJson.getJSONObject("body").getJSONObject(dataType).getJSONArray("data");
    }

    public int[] getIDs() {
        int[] id = new int[getPageResultCount()];
        for (int i = 0; i < getPageResultCount(); i++) {
            id[i] = Integer.parseInt(getResultData().getJSONObject(i).getString("id"));
        }
        return id;
    }

    public int getResultCount() {
        return searchResultJson.getJSONObject("body").getJSONObject(dataType).getInt("total");
    }

    public int getMaxPage() {
        return (int) Math.ceil(getResultCount() / 60f);
    }

}
