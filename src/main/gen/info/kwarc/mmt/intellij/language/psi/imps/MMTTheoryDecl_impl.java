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

public class MMTTheoryDecl_impl extends ASTWrapperPsiElement implements MMTTheoryDecl {

  public MMTTheoryDecl_impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitTheoryDecl(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MMTVisitor) accept((MMTVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public MMTDerivedDecl getDerivedDecl() {
    return findChildByClass(MMTDerivedDecl.class);
  }

  @Override
  @Nullable
  public MMTError getError() {
    return findChildByClass(MMTError.class);
  }

  @Override
  @Nullable
  public MMTModuleDecl getModuleDecl() {
    return findChildByClass(MMTModuleDecl.class);
  }

  @Override
  @Nullable
  public MMTNestedTheory getNestedTheory() {
    return findChildByClass(MMTNestedTheory.class);
  }

  @Override
  @Nullable
  public MMTNestedView getNestedView() {
    return findChildByClass(MMTNestedView.class);
  }

  @Override
  @Nullable
  public MMTRule getRule() {
    return findChildByClass(MMTRule.class);
  }

  @Override
  @Nullable
  public MMTStructure getStructure() {
    return findChildByClass(MMTStructure.class);
  }

  @Override
  @Nullable
  public MMTTheoryConstant getTheoryConstant() {
    return findChildByClass(MMTTheoryConstant.class);
  }

}
