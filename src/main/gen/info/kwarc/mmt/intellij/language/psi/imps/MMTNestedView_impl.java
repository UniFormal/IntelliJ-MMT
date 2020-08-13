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

public class MMTNestedView_impl extends ASTWrapperPsiElement implements MMTNestedView {

  public MMTNestedView_impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitNestedView(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MMTVisitor) accept((MMTVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public MMTTerm getTerm() {
    return findChildByClass(MMTTerm.class);
  }

  @Override
  @Nullable
  public MMTViewBody getViewBody() {
    return findChildByClass(MMTViewBody.class);
  }

  @Override
  @NotNull
  public MMTViewHeader getViewHeader() {
    return findNotNullChildByClass(MMTViewHeader.class);
  }

}
