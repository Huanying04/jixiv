package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Enums.PixivIllustrationType;
import org.json.JSONObject;

public class RankInfoObject {
    private final JSONObject data;

    public RankInfoObject(JSONObject data) {
        this.data = data;
    }

    private JSONObject getData() {
        return data;
    }

    public int getId() {
        return data.getInt("illust_id");
    }

    public String getTitle() {
        return data.getString("title");
    }

    public String[] getTags() {
        String[] tags = new String[data.getJSONArray("tags").length()];
        for (int i = 0; i < data.getJSONArray("tags").length(); i++) {
            tags[i] = data.getJSONArray("tags").getString(i);
        }
        return tags;
    }

    public int getAuthorId() {
        return data.getInt("user_id");
    }

    public String getAuthorName() {
        return data.getString("user_name");
    }

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

    public int getPageCount() {
        return Integer.parseInt(data.getString("illust_page_count"));
    }

    public int getRank() {
        return data.getInt("rank");
    }

    public int getYesRank() {
        return data.getInt("yes_rank");
    }

    public int getRatingCount() {
        return data.getInt("rating_count");
    }

    public int getViewCount() {
        return data.getInt("view_count");
    }
}
