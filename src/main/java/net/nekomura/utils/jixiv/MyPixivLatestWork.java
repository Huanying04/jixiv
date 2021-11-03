package net.nekomura.utils.jixiv;

import org.json.JSONObject;

/**
 * MyPixiv的最新作品
 */
public class MyPixivLatestWork extends InterestUsersLatestWork {
    public MyPixivLatestWork(JSONObject data, String type) {
        super(data, type);
    }
}
