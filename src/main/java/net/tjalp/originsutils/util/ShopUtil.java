package net.tjalp.originsutils.util;

public class ShopUtil {

    public static boolean isInShop(double x, double z) {
        return (x >= -26 && x <= 28) && (z >= -29 && z <= 29);
    }
}
