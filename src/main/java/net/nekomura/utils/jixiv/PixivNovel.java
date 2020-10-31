package net.nekomura.utils.jixiv;


public class PixivNovel extends PixivArtwork {

    public PixivNovel(String phpSession) {
        super(phpSession);
    }

    public PixivNovel(String phpSession, String userAgent) {
        super(phpSession, userAgent);
    }

    /**
     * 獲取小說作品之資訊物件
     * @param id 小說id
     * @return 小說作品之資訊物件
     * @throws Exception 獲取失敗
     */
    public PixivNovelInfo get(int id) throws Exception {
        return super.get(id).toNovelInfo();
    }
}