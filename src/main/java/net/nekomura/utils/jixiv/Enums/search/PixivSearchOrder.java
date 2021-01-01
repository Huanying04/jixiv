package net.nekomura.utils.jixiv.Enums.search;

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
    }
}