package net.nekomura.utils.jixiv;

public class Illustration extends Artwork {

    public Illustration(String phpSession) {
        super(phpSession);
    }

    public Illustration(String phpSession, String userAgent) {
        super(phpSession, userAgent);
    }

    /**
     * 獲取插畫作品之資訊物件
     * @param id 插畫id
     * @return 插畫作品之資訊物件
     * @throws Exception 獲取失敗
     */
    public IllustrationInfo get(int id) throws Exception {
        return super.get(id).toIllustrationInfo();
    }

}