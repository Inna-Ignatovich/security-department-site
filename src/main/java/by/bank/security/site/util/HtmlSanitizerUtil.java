package by.bank.security.site.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizerUtil {


    public static String sanitize(String incomingHtml) {
        return Jsoup.clean(incomingHtml, Safelist.basic());
    }
}
