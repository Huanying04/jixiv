package net.nekomura.utils.jixiv.Enums.artwork;

/**
 * 插畫作品類別
 */
public enum PixivIllustrationType {
    Illustration(0),
    Manga(1),
    Ugoira(2);

    private int numberFormat;

    PixivIllustrationType(int type) {
        numberFormat = type;
    }

    public int getNumberFormat() {
        return numberFormat;
    }
}