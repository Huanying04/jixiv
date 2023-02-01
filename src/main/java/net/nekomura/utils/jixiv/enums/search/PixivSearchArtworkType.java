package net.nekomura.utils.jixiv.enums.search;

/**
 * 搜尋作品類別
 */
public enum PixivSearchArtworkType {
    /**
     * 插畫和漫畫
     */
    ARTWORKS,

    /**
     * 插畫
     */
    ILLUSTRATIONS,

    /**
     * 漫畫
     */
    MANGA,

    /**
     * 小說
     */
    NOVELS;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
