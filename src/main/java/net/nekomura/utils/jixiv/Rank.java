package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.enums.rank.PixivRankContent;
import net.nekomura.utils.jixiv.enums.rank.PixivRankMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class Rank {
    private final int page;
    private final PixivRankMode mode;
    private final PixivRankContent content;
    private final String date;
    private final JSONObject rankJson;
    private final String phpSession;
    private final String userAgent;

    public Rank(int page,
                PixivRankMode mode,
                PixivRankContent content,
                String date,
                @NotNull String rankJson,
                String phpSession,
                String userAgent) {
        this.page = page;
        this.mode = mode;
        this.content = content;
        this.date = date;
        this.rankJson = new JSONObject(rankJson);
        this.phpSession = phpSession;
        this.userAgent = userAgent;
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
        return !(rankJson.get("next") instanceof Boolean) || rankJson.getBoolean("next");
    }

    /**
     * 是否有上一頁
     * @return 是否有上一頁
     */
    public boolean hasPreviousPage() {
        return !(rankJson.get("prev") instanceof Boolean) || rankJson.getBoolean("prev");
    }

    /**
     * 獲取下一頁的排行榜
     * @return 下一頁的排行榜
     * @throws IOException 獲取失敗
     */
    public Rank nextPage() throws IOException {
        return new Pixiv(phpSession, userAgent).rank(rankJson.getInt("next"), mode, content, date);
    }

    /**
     * 獲取上一頁的排行榜
     * @return 上一頁的排行榜
     * @throws IOException 獲取失敗
     */
    public Rank previousPage() throws IOException {
        return new Pixiv(phpSession, userAgent).rank(rankJson.getInt("prev"), mode, content, date);
    }

    /**
     * 獲取指定作品之資訊物件
     * @param index 第幾項作品
     * @return 排行榜作品資訊物件
     */
    public RankArtwork getInfo(int index) {
        return new RankArtwork(rankJson.getJSONArray("contents").getJSONObject(index));
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