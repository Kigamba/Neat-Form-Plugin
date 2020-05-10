package org.shan;

import com.intellij.lang.Language;

public class SimpleLanguage extends Language {

    public static final SimpleLanguage INSTANCE = new SimpleLanguage();

    private SimpleLanguage() {
        super(Constants.FILE_TYPE_NAME);
    }
}
