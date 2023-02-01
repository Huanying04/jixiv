package net.nekomura.utils.jixiv.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PixivUrlBuilder {
    private String path;
    private final List<NameValuePair> params = new ArrayList<>();

    public PixivUrlBuilder() {
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addParameter(String param, Object value) {
        params.add(new NameValuePair(param, value));
    }

    public String build() {
        StringBuilder sb = new StringBuilder("https://www.pixiv.net/").append(path);

        Iterator<NameValuePair> it = params.iterator();

        if (it.hasNext()) {
            sb.append('?');
        }

        for (; it.hasNext();) {
            NameValuePair nvp = it.next();
            sb.append(nvp.getName()).append('=').append(nvp.getValue());
            if (it.hasNext()) {
                sb.append('&');
            }
        }

        return sb.toString();
    }
}
