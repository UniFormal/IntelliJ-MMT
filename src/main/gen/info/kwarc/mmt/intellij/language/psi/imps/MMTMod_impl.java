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

public class MMTMod_impl extends ASTWrapperPsiElement implements MMTMod {

  public MMTMod_impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitMod(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MMTVisitor) accept((MMTVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public MMTDiagram getDiagram() {
    return findChildByClass(MMTDiagram.class);
  }

  @Override
  @Nullable
  public MMTDocumentScopedComment getDocumentScopedComment() {
    return findChildByClass(MMTDocumentScopedComment.class);
  }

  @Override
  @Nullable
  public MMTError getError() {
    return findChildByClass(MMTError.class);
  }

  @Override
  @Nullable
  public MMTFixmeta getFixmeta() {
    return findChildByClass(MMTFixmeta.class);
  }

  @Override
  @Nullable
  public MMTImport getImport() {
    return findChildByClass(MMTImport.class);
  }

  @Override
  @Nullable
  public MMTMetaDatum getMetaDatum() {
    return findChildByClass(MMTMetaDatum.class);
  }

  @Override
  @Nullable
  public MMTNamespace getNamespace() {
    return findChildByClass(MMTNamespace.class);
  }

  @Override
  @Nullable
  public MMTRule getRule() {
    return findChildByClass(MMTRule.class);
  }

  @Override
  @Nullable
  public MMTTheory getTheory() {
    return findChildByClass(MMTTheory.class);
  }

  @Override
  @Nullable
  public MMTView getView() {
    return findChildByClass(MMTView.class);
  }

}
