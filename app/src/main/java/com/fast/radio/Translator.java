package com.fast.radio;

/* Online translation foundation. No offline language models are included. */
public class Translator {
    public interface Callback {
        void onTranslated(String text);
        void onError(String message);
    }

    public void translate(String text, String sourceLanguage,
                          String targetLanguage, Callback callback) {
        if (text == null || text.trim().isEmpty()) {
            callback.onError("متنی برای ترجمه وجود ندارد");
            return;
        }
        callback.onError("سرویس ترجمه آنلاین هنوز تنظیم نشده است");
    }
}
