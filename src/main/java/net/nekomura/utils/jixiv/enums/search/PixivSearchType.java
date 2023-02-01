package net.nekomura.utils.jixiv.enums.search;

public enum PixivSearchType {
    /**
     * 全部
     */
    ALL,

    /**
     * 插畫及動態插畫
     */
    ILLUST_AND_UGOIRA,

    /**
     * 插畫
     */
    ILLUST,

    /**
     * 動態插畫
     */
    UGOIRA,

    /**
     * 漫畫
     */
    MANGA;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
