import Enums.PixivRankMode;
import org.jetbrains.annotations.NotNull;
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
}
