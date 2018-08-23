package info.kwarc.mmt.intellij.Language;

import com.intellij.lang.Language;

public class MMTLanguage extends Language {
    public static final MMTLanguage INSTANCE = new MMTLanguage();

    private MMTLanguage() {
        super("MMT");
    }
}
