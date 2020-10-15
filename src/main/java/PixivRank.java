import Enums.PixivRankMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class PixivRank {
    private JSONObject rankJson;

    public PixivRank(@NotNull String rankJson) {
        this.rankJson = new JSONObject(rankJson);
    }

    public PixivRankMode getMode() {
        String mode = rankJson.getString("mode");
        switch (mode) {
            case "daily":
                return PixivRankMode.Daily;
            case "weekly":
                return PixivRankMode.Weekly;
            case "monthly":
                return PixivRankMode.Monthly;
            case "rookie":
                return PixivRankMode.Rookie;
            case "original":
                return PixivRankMode.Original;
            case "male":
                return PixivRankMode.Male;
            case "female":
                return PixivRankMode.Female;
            case "daily_r18":
                return PixivRankMode.Daily_R18;
            case "weekly_r18":
                return PixivRankMode.Weekly_R18;
            case "male_r18":
                return PixivRankMode.Male_R18;
            case "female_r18":
                return PixivRankMode.Female_R18;
            default:
                throw new IllegalArgumentException();
        }
    }

    public int getPageResultCount() {
        return rankJson.getJSONArray("contents").length();
    }

    private JSONArray getResultData() {
        return rankJson.getJSONArray("contents");
    }

    public int[] getIDs() {
        int[] id = new int[getPageResultCount()];
        for (int i = 0; i < getPageResultCount(); i++) {
            id[i] = Integer.parseInt(getResultData().getJSONObject(i).getString("id"));
        }
        return id;
    }

    public int getPage() {
        return rankJson.getInt("page");
    }

    public int getTotal() {
        return rankJson.getInt("rank_total");
    }

    public boolean hasNextPage() {
        if (rankJson.get("next") instanceof Boolean && !rankJson.getBoolean("next"))
            return false;
        else
            return true;
    }

    public boolean hasPreviousPage() {
        if (rankJson.get("prev") instanceof Boolean && !rankJson.getBoolean("prev"))
            return false;
        else
            return true;
    }
}
