package info.kwarc.mmt.intellij.Language.psi;

import com.intellij.psi.tree.IElementType;
import info.kwarc.mmt.intellij.Language.MMTLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MMTTokenType extends IElementType {
    public MMTTokenType(@NotNull @NonNls String debugName) {
        super(debugName,MMTLanguage.INSTANCE);
    }
}
