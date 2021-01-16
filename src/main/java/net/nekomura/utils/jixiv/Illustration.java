package net.nekomura.utils.jixiv;

import java.io.IOException;

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
     * @throws IOException 讀取網路資料失敗
     */
    public IllustrationInfo get(int id) throws IOException {
        return this.getInfo(id);
    }

    /**
     * 獲取插畫作品之資訊物件
     * @param id 插畫id
     * @return 插畫作品之資訊物件
     * @throws IOException 讀取網路資料失敗
     */
    public IllustrationInfo getInfo(int id) throws IOException {
        return super.getInfo(id).toIllustrationInfo();
    }

}