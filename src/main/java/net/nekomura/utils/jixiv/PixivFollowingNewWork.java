package net.nekomura.utils.jixiv;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.IOException;
import java.util.Arrays;

public class PixivFollowingNewWork {
    private JSONArray data;

    public PixivFollowingNewWork(@NotNull String data) {
        this.data = new JSONArray(data);
    }

    private JSONArray getData() {
        return data;
    }

    public FollowingInfoObject get(int index) {
        return new FollowingInfoObject(data.getJSONObject(index));
    }

    public int[] getArtworkIds() {
        int[] ids = new int[data.length()];
        for (int i = 0; i < data.length(); i++) {
            ids[i] = Integer.parseInt(data.getJSONObject(i).getString("illustId"));
        }
        return ids;
    }

    public String[] getArtworkTitles() {
        String[] titles = new String[data.length()];
        for (int i = 0; i < data.length(); i++) {
            titles[i] = data.getJSONObject(i).getString("illustTitle");
        }
        return titles;
    }

    public boolean containId(int id) {
        return Arrays.stream(getArtworkIds()).anyMatch(x -> x == id);
    }
}