package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Enums.PixivIllustrationType;
import org.json.JSONArray;
import org.json.JSONObject;

public class FollowingInfoObject {
    private final JSONObject data;

    public FollowingInfoObject(JSONObject data) {
        this.data = data;
    }

    private JSONObject getData() {
        return data;
    }

    public int getID() {
        return Integer.parseInt(data.getString("illustId"));
    }

    public String getTitle() {
        return data.getString("illustTitle");
    }

    public PixivIllustrationType getIllustrationType() throws IllegalArgumentException{
        int typeNumber = data.getInt("illustType");
        switch (typeNumber) {
            case 0:
                return PixivIllustrationType.Illustration;
            case 1:
                return PixivIllustrationType.Manga;
            case 2:
                return PixivIllustrationType.Ugoira;
            default:
                throw new IllegalArgumentException();
        }
    }

    public String[] getTags() {
        JSONArray tags = data.getJSONArray("tags");
        String[] tagsArray = new String[tags.length()];
        for (int i = 0; i < tags.length(); i++) {
            tagsArray[i] = tags.getString(i);
        }

        return tagsArray;
    }

    public int getAuthorId() {
        return Integer.parseInt(data.getString("userId"));
    }

    public String getAuthorName() {
        return data.getString("userName");
    }

    public int getWidth() {
        return data.getInt("width");
    }

    public int getHeight() {
        return data.getInt("height");
    }

    public int getPageCount() {
        return data.getInt("pageCount");
    }
}