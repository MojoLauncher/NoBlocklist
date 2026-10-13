package git.artdeell.noblocklist;

import java.io.IOException;
import java.net.URL;

public class TestMain {
    public static void main(String[] args) throws IOException {
        new URL("https://example.com/blockedservers").openConnection();
        System.out.println("https://example.com/blockedservers OK");
        new URL("https://sessionserver.mojang.com/abcdef").openConnection();
        System.out.println("https://sessionserver.mojang.com/abcdef OK");
        try {
            new URL("https://sessionserver.mojang.com/blockedservers").openConnection();
            System.out.println("3 OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
