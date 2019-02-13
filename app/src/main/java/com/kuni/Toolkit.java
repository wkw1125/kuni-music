package com.kuni;

import java.util.Locale;

/**
 * 工具箱
 *
 * @author Kw
 */
public class Toolkit {
    /**
     * 将秒数转换为0:00格式字符串
     *
     * @param second 秒
     * @return
     */
    public static String displayTime(int second) {
        return String.format(Locale.getDefault(), "%01d:%02d", second / 60,
                second % 60);
    }
}
