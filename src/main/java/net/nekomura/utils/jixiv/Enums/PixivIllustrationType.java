package net.nekomura.utils.jixiv.Enums;

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