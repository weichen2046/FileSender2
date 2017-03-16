package com.weichen2046.filesender2.networklib;

import java.util.UUID;

/**
 * Created by chenwei on 2017/3/16.
 */

public class TokenHelper {
    public static String generateTempToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
