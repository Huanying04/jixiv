package net.nekomura.utils.jixiv;


public class PixivNovel extends PixivArtwork {

    public PixivNovel(String phpSession) {
        super(phpSession);
    }

    public PixivNovel(String phpSession, String userAgent) {
        super(phpSession, userAgent);
    }

    public PixivNovelInfo get(int id) throws Exception {
        return super.get(id).toNovelInfo();
    }
}