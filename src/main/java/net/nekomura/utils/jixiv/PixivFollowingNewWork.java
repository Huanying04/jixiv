package net.nekomura.utils.jixiv;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class PixivFollowingNewWork {
    private JSONArray data;

    public PixivFollowingNewWork(@NotNull String data) {
        this.data = new JSONArray(data);
    }
}