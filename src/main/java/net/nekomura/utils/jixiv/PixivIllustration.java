package net.nekomura.utils.jixiv;

public class PixivIllustration extends PixivArtwork {

    public PixivIllustration(String phpSession) {
        super(phpSession);
    }

    public PixivIllustration(String phpSession, String userAgent) {
        super(phpSession, userAgent);
    }

    public PixivIllustrationInfo get(int id) throws Exception {
        return super.get(id).toIllustrationInfo();
    }

}