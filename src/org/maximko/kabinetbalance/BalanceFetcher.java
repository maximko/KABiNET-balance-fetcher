package org.maximko.kabinetbalance;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BalanceFetcher {
    
    public String get(String username, String password) throws IOException {
        Connection.Response res = Jsoup.connect("https://stat.telenet.ru/login")
                                        .data("login", username, "password", password)
                                        .method(Method.POST).execute();
        Document doc = res.parse();
        String balance = doc.select("td.bd[align=right]").get(1).ownText();
        return balance;
    }
    
}
