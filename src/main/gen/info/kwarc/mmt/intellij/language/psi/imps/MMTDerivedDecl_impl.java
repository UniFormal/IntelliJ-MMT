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

public class MMTDerivedDecl_impl extends ASTWrapperPsiElement implements MMTDerivedDecl {

  public MMTDerivedDecl_impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitDerivedDecl(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MMTVisitor) accept((MMTVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public MMTDerivedHeader getDerivedHeader() {
    return findChildByClass(MMTDerivedHeader.class);
  }

  @Override
  @Nullable
  public MMTDerivedSimple getDerivedSimple() {
    return findChildByClass(MMTDerivedSimple.class);
  }

  @Override
  @Nullable
  public MMTError getError() {
    return findChildByClass(MMTError.class);
  }

  @Override
  @Nullable
  public MMTTheoryBody getTheoryBody() {
    return findChildByClass(MMTTheoryBody.class);
  }

}
