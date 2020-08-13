// This is a generated file. Not intended for manual editing.
package info.kwarc.mmt.intellij.language.psi.imps;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static info.kwarc.mmt.intellij.language.psi.MMTParserTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import info.kwarc.mmt.intellij.language.psi.*;

public class MMTTheoryHeader_impl extends ASTWrapperPsiElement implements MMTTheoryHeader {

  public MMTTheoryHeader_impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitTheoryHeader(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MMTVisitor) accept((MMTVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public MMTError getError() {
    return findChildByClass(MMTError.class);
  }

  @Override
  @Nullable
  public MMTPname getPname() {
    return findChildByClass(MMTPname.class);
  }

  @Override
  @Nullable
  public MMTTmlist getTmlist() {
    return findChildByClass(MMTTmlist.class);
  }

  @Override
  @Nullable
  public MMTUri getUri() {
    return findChildByClass(MMTUri.class);
  }

}
