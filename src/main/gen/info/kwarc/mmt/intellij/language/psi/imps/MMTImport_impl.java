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

public class MMTImport_impl extends ASTWrapperPsiElement implements MMTImport {

  public MMTImport_impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitImport(this);
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
  @NotNull
  public MMTPname getPname() {
    return findNotNullChildByClass(MMTPname.class);
  }

  @Override
  @NotNull
  public MMTUri getUri() {
    return findNotNullChildByClass(MMTUri.class);
  }

}
