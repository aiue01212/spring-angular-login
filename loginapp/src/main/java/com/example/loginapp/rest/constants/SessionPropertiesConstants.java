package com.example.loginapp.rest.constants;

import java.util.Locale;

public final class SessionPropertiesConstants {

    private SessionPropertiesConstants() {
    }

    /** 正常なタイムアウト値 */
    public static final long TIMEOUT_NORMAL = 5000L;

    /** 負のタイムアウト値 */
    public static final long TIMEOUT_NEGATIVE = -1L;

    /** モックMessageSourceの返却文字列 */
    public static final String MSG_NEGATIVE_TIMEOUT = "セッションタイムアウトは負の値にできません";

    /** モックMessageSource用ロケール */
    public static final Locale LOCALE_JAPANESE = Locale.JAPANESE;
}