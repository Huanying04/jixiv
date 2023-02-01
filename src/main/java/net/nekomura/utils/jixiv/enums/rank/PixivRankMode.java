package net.nekomura.utils.jixiv.enums.rank;

/**
 * 排行棒評分類別
 */
public enum PixivRankMode {
    /**
     * 今日
     */
    DAILY,

    /**
     * 本周
     */
    WEEKLY,

    /**
     * 本月
     */
    MONTHLY,

    /**
     * 新人
     */
    ROOKIE,

    /**
     * 原創
     */
    ORIGINAL,

    /**
     * 受男性歡迎
     */
    MALE,

    /**
     * 受女性歡迎
     */
    FEMALE,

    /**
     * 今日R18
     */
    DAILY_R18,

    /**
     * 本周R18
     */
    WEEKLY_R18,

    /**
     * 受男性歡迎(R18)
     */
    MALE_R18,

    /**
     * 受女性歡迎(R18)
     */
    FEMALE_R18;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
