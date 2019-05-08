package com.lunamint.wallet;

public class ApiUtils {
    private static final String LUNA_BASE_URL = "YOUR_CONTROL_SERVER_URL";

    public static final LcdService getLcdService() {
        return HttpClient.getInstanceLcd(Blockchain.getInstance().getLcd()).create(LcdService.class);
    }

    public static final LcdService getLcdService(String baseUrl) {
        return HttpClient.getInstanceLcd(baseUrl).create(LcdService.class);
    }

    public static final LunaService getLunaService() {
        return HttpClient.getInstanceLuna(LUNA_BASE_URL).create(LunaService.class);
    }

    public static final LcdService getCallbackService(String url) {
        return HttpClient.getInstanceCallback(url).create(LcdService.class);
    }
}
