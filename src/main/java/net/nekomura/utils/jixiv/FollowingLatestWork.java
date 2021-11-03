package net.nekomura.utils.jixiv;

import org.json.JSONObject;

/**
 * 關注用戶的最新作品
 */
public class FollowingLatestWork extends InterestUsersLatestWork {
    public FollowingLatestWork(JSONObject data, String type) {
        super(data, type);
    }
}
