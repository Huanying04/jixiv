package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Enums.PixivRankContent;
import net.nekomura.utils.jixiv.Enums.PixivRankMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class PixivRank {
    private final int page;
    private final PixivRankMode mode;
    private final PixivRankContent content;
    private final String date;
    private final JSONObject rankJson;

    public PixivRank(int page, PixivRankMode mode, PixivRankContent content, String date, @NotNull String rankJson) {
        this.page = page;
        this.mode = mode;
        this.content = content;
        this.date = date;
        this.rankJson = new JSONObject(rankJson);
    }

    /**
     * 獲取頁碼
     * @return 頁碼
     */
    public int getPage() {
        return page;
    }

    /**
     * 獲取排行榜篩選模式
     * @return 排行榜篩選模式
     */
    public PixivRankMode getMode() {
        return mode;
    }

    /**
     * 獲取排行榜作品類別
     * @return 排行榜作品類別
     */
    public PixivRankContent getContent() {
        return content;
    }

    /**
     * 獲取排行榜日期
     * @return 排行榜日期
     */
    public String getDate() {
        return date;
    }

    /**
     * 獲取該頁之顯示作品數量
     * @return 該頁之顯示作品數量
     */
    public int getPageResultCount() {
        return rankJson.getJSONArray("contents").length();
    }

    private JSONArray getResultData() {
        return rankJson.getJSONArray("contents");
    }

    /**
     * 獲取該頁所有作品之id
     * @return 該頁所有作品之id
     */
    public int[] getIds() {
        int[] id = new int[getPageResultCount()];
        for (int i = 0; i < getPageResultCount(); i++) {
            id[i] = Integer.parseInt(getResultData().getJSONObject(i).getString("id"));
        }
        return id;
    }

    /**
     * 獲取總數
     * @return 總數
     */
    public int getTotal() {
        return rankJson.getInt("rank_total");
    }

    /**
     * 是否有下一頁
     * @return 是否有下一頁
     */
    public boolean hasNextPage() {
        if (rankJson.get("next") instanceof Boolean && !rankJson.getBoolean("next"))
            return false;
        else
            return true;
    }

    /**
     * 是否有上一頁
     * @return 是否有上一頁
     */
    public boolean hasPreviousPage() {
        if (rankJson.get("prev") instanceof Boolean && !rankJson.getBoolean("prev"))
            return false;
        else
            return true;
    }

    /**
     * 獲取指定作品之資訊物件
     * @param index 第幾項作品
     * @return 排行榜作品資訊物件
     */
    public RankInfoObject getInfo(int index) {
        return new RankInfoObject(rankJson.getJSONArray("contents").getJSONObject(index));
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