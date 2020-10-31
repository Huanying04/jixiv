package net.nekomura.utils.jixiv;

public class PixivIllustration extends PixivArtwork {

    public PixivIllustration(String phpSession) {
        super(phpSession);
    }

    public PixivIllustration(String phpSession, String userAgent) {
        super(phpSession, userAgent);
    }

    /**
     * 獲取插畫作品之資訊物件
     * @param id 插畫id
     * @return 插畫作品之資訊物件
     * @throws Exception 獲取失敗
     */
    public PixivIllustrationInfo get(int id) throws Exception {
        return super.get(id).toIllustrationInfo();
    }

}