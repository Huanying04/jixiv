package net.nekomura.utils.jixiv.enums.settings;

public enum Language {
    JAPANESE("ja"),
    ENGLISH("en"),
    KOREAN("ko"),
    CHINESE_SIMPLIFIED("zh"),
    CHINESE_TRADITIONAL("zh_tw");

    String languageCode;

    Language(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }
}
