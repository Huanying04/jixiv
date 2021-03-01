package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.enums.artwork.PixivIllustrationType;
import org.json.JSONArray;
import org.json.JSONObject;

public class FollowingArtworkInfo {
    private final JSONObject data;

    public FollowingArtworkInfo(JSONObject data) {
        this.data = data;
    }

    private JSONObject getData() {
        return data;
    }

    /**
     * 獲取作品id
     * @return 作品id
     */
    public int getID() {
        return Integer.parseInt(data.getString("illustId"));
    }

    /**
     * 獲取作品標題
     * @return 作品標題
     */
    public String getTitle() {
        return data.getString("illustTitle");
    }

    /**
     * 獲取插畫作品類別
     * @return 插畫作品類別
     * @throws IllegalArgumentException 無此作品類別
     */
    public PixivIllustrationType getIllustrationType() throws IllegalArgumentException{
        int typeNumber = data.getInt("illustType");
        switch (typeNumber) {
            case 0:
                return PixivIllustrationType.ILLUSTRATION;
            case 1:
                return PixivIllustrationType.MANGA;
            case 2:
                return PixivIllustrationType.UGOIRA;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 獲取作品所有標籤
     * @return 作品所有標籤
     */
    public String[] getTags() {
        JSONArray tags = data.getJSONArray("tags");
        String[] tagsArray = new String[tags.length()];
        for (int i = 0; i < tags.length(); i++) {
            tagsArray[i] = tags.getString(i);
        }

        return tagsArray;
    }

    /**
     * 獲取作品作者id
     * @return 作品作者id
     */
    public int getAuthorId() {
        return Integer.parseInt(data.getString("userId"));
    }

    /**
     * 獲取作品作者名稱
     * @return 作品作者名稱
     */
    public String getAuthorName() {
        return data.getString("userName");
    }

    /**
     * 獲取作品圖片寬
     * @return 作品圖片寬
     */
    public int getWidth() {
        return data.getInt("width");
    }

    /**
     * 獲取作品圖片高
     * @return 作品圖片高
     */
    public int getHeight() {
        return data.getInt("height");
    }

    /**
     * 獲取作品總頁數
     * @return 作品總頁數
     */
    public int getPageCount() {
        return data.getInt("pageCount");
    }
}