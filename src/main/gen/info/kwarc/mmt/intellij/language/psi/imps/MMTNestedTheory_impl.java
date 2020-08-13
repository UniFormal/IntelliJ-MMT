// This is a generated file. Not intended for manual editing.
package info.kwarc.mmt.intellij.language.psi.imps;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static info.kwarc.mmt.intellij.language.psi.MMTParserTypes.*;
import info.kwarc.mmt.intellij.language.TheoryElement_impl;
import info.kwarc.mmt.intellij.language.psi.*;

public class MMTNestedTheory_impl extends TheoryElement_impl implements MMTNestedTheory {

  public MMTNestedTheory_impl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitNestedTheory(this);
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
  public MMTTerm getTerm() {
    return findChildByClass(MMTTerm.class);
  }

  @Override
  @Nullable
  public MMTTheoryBody getTheoryBody() {
    return findChildByClass(MMTTheoryBody.class);
  }

  @Override
  @NotNull
  public MMTTheoryHeader getTheoryHeader() {
    return findNotNullChildByClass(MMTTheoryHeader.class);
  }

}
