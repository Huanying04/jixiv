package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Enums.artwork.PixivIllustrationType;
import org.json.JSONObject;

public class RankArtwork {
    private final JSONObject data;

    public RankArtwork(JSONObject data) {
        this.data = data;
    }

    private JSONObject getData() {
        return data;
    }

    /**
     * 獲取作品id
     * @return 作品id
     */
    public int getId() {
        return data.getInt("illust_id");
    }

    /**
     * 獲取作品標題
     * @return 作品標題
     */
    public String getTitle() {
        return data.getString("title");
    }

    /**
     * 獲取作品所有標籤
     * @return 作品所有標籤
     */
    public String[] getTags() {
        String[] tags = new String[data.getJSONArray("tags").length()];
        for (int i = 0; i < data.getJSONArray("tags").length(); i++) {
            tags[i] = data.getJSONArray("tags").getString(i);
        }
        return tags;
    }

    /**
     * 獲取作品作者id
     * @return 作品作者id
     */
    public int getAuthorId() {
        return data.getInt("user_id");
    }

    /**
     * 獲取作品作者名稱
     * @return 作品作者名稱
     */
    public String getAuthorName() {
        return data.getString("user_name");
    }

    /**
     * 獲取插畫作品類別
     * @return 插畫作品類別
     */
    public PixivIllustrationType getIllustrationType() {
        String typeNumber = data.getString("illust_type");
        switch (typeNumber) {
            case "0":
                return PixivIllustrationType.Illustration;
            case "1":
                return PixivIllustrationType.Manga;
            case "2":
                return PixivIllustrationType.Ugoira;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 獲取作品總頁數
     * @return 作品總頁數
     */
    public int getPageCount() {
        return Integer.parseInt(data.getString("illust_page_count"));
    }

    /**
     * 獲取作品排名
     * @return 作品排名
     */
    public int getRank() {
        return data.getInt("rank");
    }

    /**
     * 獲取作品排名
     * @return 作品排名
     */
    public int getYesRank() {
        return data.getInt("yes_rank");
    }

    /**
     * Get Artwork Rating Count
     * @return Artwork Rating Count
     */
    public int getRatingCount() {
        return data.getInt("rating_count");
    }

    /**
     * 獲取作品閱覽次數
     * @return 作品閱覽次數
     */
    public int getViewCount() {
        return data.getInt("view_count");
    }
}
