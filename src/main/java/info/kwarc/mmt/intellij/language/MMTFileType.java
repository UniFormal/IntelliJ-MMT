package info.kwarc.mmt.intellij.language;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import info.kwarc.mmt.intellij.MMT;
import info.kwarc.mmt.intellij.language.MMTLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * The implementing class of the file type extension point.
 *
 * This has to be a true Java class because the extension point (see plugin.xml)
 * expects a) a fully qualified path to a class and b) a field name of the instance
 * (here INSTANCE). Even though we could do this in Scala with class and companion
 * object, we could not reference it without errors in plugin.xml.
 */
public class MMTFileType extends LanguageFileType {
    public static final FileType INSTANCE = new MMTFileType();

    public MMTFileType() {
        super(MMTLanguage$.MODULE$.INSTANCE());
    }

    protected MMTFileType(@NotNull Language language) {
        super(language);
    }

    @NotNull
    @Override
    public String getName() {
        return "MMT";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "MMT Document";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "mmt";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MMT.icon();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
