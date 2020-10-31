package net.nekomura.utils.jixiv;

import com.google.common.collect.Iterators;
import net.nekomura.utils.jixiv.Enums.PixivArtworkType;
import net.nekomura.utils.jixiv.Utils.Sort;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

public class PixivUserProfileInfo {
    private final int id;
    private final JSONObject profile;
    private final JSONObject preloadData;

    public PixivUserProfileInfo(int id, JSONObject profile, JSONObject preloadData) {
        this.id = id;
        this.profile = profile;
        this.preloadData = preloadData;
    }

    public int[] getUserArtworks(@NotNull PixivArtworkType type) throws IOException {
        if (profile.getJSONObject("body").get(type.name().toLowerCase()) instanceof JSONObject) {
        int keySize = Iterators.size(profile.getJSONObject("body").getJSONObject(type.name().toLowerCase()).keys());
        int[] artworks = new int[keySize];

        for (int i = 0; i < keySize; i++) {
            artworks[i] = Integer.parseInt(Iterators.get(profile.getJSONObject("body").getJSONObject(type.name().toLowerCase()).keys(), i));
        }

        return Sort.bubbleNegativeWay(artworks);
        }else {
            return new int[0];
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("name");
    }

    public String getAvatarSmallUrl() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("image");
    }

    public String getAvatarBigUrl() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("imageBig");
    }

    public boolean isPremium() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("premium");
    }

    public boolean isFollowed() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("isFollowed");
    }

    public boolean isMyPixiv() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("isMypixiv");
    }

    public boolean isBlocking() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("isBlocking");
    }

    public String getBackgroundUrl() {
        if (preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).get("background") != null) {
            return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getJSONObject("background").getString("url");
        }else {
            return null;
        }
    }

    public int getFollowingCount() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getInt("following");
    }

    public boolean followedBack() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("followedBack");
    }

    public String getComment() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("comment");
    }

    public String getCommentHtml() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("commentHtml");
    }

    public String getWebPage() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("webpage");
    }

    public String getWorkspace() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("workspace");
    }

    public String getGroup() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("group");
    }

    public boolean isOfficial() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("official");
    }
}