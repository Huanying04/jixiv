package net.nekomura.utils.jixiv.enums.artwork;

/**
 * 插畫作品類別
 */
public enum PixivIllustrationType {
    /**
     * 插畫
     */
    ILLUSTRATION(0),

    /**
     * 漫畫
     */
    MANGA(1),

    /**
     * 動圖
     */
    UGOIRA(2);

    private int numberFormat;

    PixivIllustrationType(int type) {
        numberFormat = type;
    }

    public int getNumberFormat() {
        return numberFormat;
    }
}