/*
 * This is the source code of Lunagram for Android v. 1.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright {company_name}, 2019.
 */

package com.lunamint.lunagram;

import java.util.Locale;

public class BuildVars {

    private final static String INSTALL_URL = "https://play.google.com/store/apps/details?id=com.lunamint.lunagram";

    //Todo: translation
    private static String lunagramSupportMessages[] = {
            "This message includes features that are available only in lunagram.\n\nPlease install or update the app in the Google Play Store.\n\n" + INSTALL_URL,
            "이 메시지에는 lunagram 에서만 사용할 수 있는 기능이 포함되어 있습니다.\n\n구글플레이스토어에서 앱을 설치 또는 업데이트하고 이용해주세요.\n\n" + INSTALL_URL
    };

    public static String lunagramUnsupportedMessages[] = {
            "This message contains a request that is not supported by the current version of Lunagram.",
            "이 메시지는 현재 버전의 Lunagram에서 지원하지 않는 요청을 포함하고 있습니다."
    };

    public static String getLunagramSupportedMessage() {
        return getLunagramSupportMessage();
    }

    public static String getLunagramSupportedMessage(String msg) {
        for (String lunagramSupportMessage : lunagramSupportMessages) {
            if (msg.contains(lunagramSupportMessage)) return lunagramSupportMessage;
        }
        return "";
    }

    public static boolean containLunagramSupportedMessage(String msg) {
        for (String lunagramSupportMessage : lunagramSupportMessages) {
            if (msg.contains(lunagramSupportMessage)) return true;
        }
        return false;
    }

    private static String getLunagramSupportMessage() {
        String msg;
        switch (getLanguage()) {
            case "en":
                msg = lunagramSupportMessages[0];
                break;
            case "ko":
                msg = lunagramSupportMessages[1];
                break;
            default:
                msg = lunagramSupportMessages[0];
        }
        return msg;
    }

    public static String getLunagramUnsupportedMessage() {
        return getLunagramUnsupportMessage();
    }

    private static String getLunagramUnsupportMessage() {
        String msg;
        switch (getLanguage()) {
            case "en":
                msg = lunagramUnsupportedMessages[0];
                break;
            case "ko":
                msg = lunagramUnsupportedMessages[1];
                break;
            default:
                msg = lunagramUnsupportedMessages[0];
        }
        return msg;
    }

    private static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
