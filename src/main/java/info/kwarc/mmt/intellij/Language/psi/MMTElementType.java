package info.kwarc.mmt.intellij.Language.psi;

import com.intellij.psi.tree.IElementType;
import info.kwarc.mmt.intellij.Language.MMTLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MMTElementType extends IElementType {
    public MMTElementType(@NotNull @NonNls String debugName) {
        super(debugName,MMTLanguage.INSTANCE);
    }
}
