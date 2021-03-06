package net.nekomura.utils.jixiv.enums.search;

/**
 * 搜尋排序
 */
public enum PixivSearchOrder {
    /**
     * 按最新排序
     */
    NEW_TO_OLD {
        @Override
        public String toString() {
            return "date_d";
        }
    },

    /**
     * 按舊排序
     */
    OLD_TO_NEW{
        @Override
        public String toString() {
            return "date";
        }
    },

    POPULAR{
        @Override
        public String toString() {
            return "popular_d";
        }
    },

    POPULAR_MALE{
        @Override
        public String toString() {
            return "popular_male_d";
        }
    },

    POPULAR_FEMALE{
        @Override
        public String toString() {
            return "popular_female_d";
        }
    }
}