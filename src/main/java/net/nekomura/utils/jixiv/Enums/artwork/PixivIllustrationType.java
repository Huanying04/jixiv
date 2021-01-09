package net.nekomura.utils.jixiv.Enums.artwork;

/**
 * 插畫作品類別
 */
public enum PixivIllustrationType {
    /**
     * 插畫
     */
    Illustration(0),

    /**
     * 漫畫
     */
    Manga(1),

    /**
     * 動圖
     */
    Ugoira(2);

    private int numberFormat;

    PixivIllustrationType(int type) {
        numberFormat = type;
    }

    public int getNumberFormat() {
        return numberFormat;
    }
}