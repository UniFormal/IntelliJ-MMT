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

public class MMTStructure_impl extends ASTWrapperPsiElement implements MMTStructure {

  public MMTStructure_impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitStructure(this);
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
  @Nullable
  public MMTStructureBody getStructureBody() {
    return findChildByClass(MMTStructureBody.class);
  }

  @Override
  @Nullable
  public MMTTerm getTerm() {
    return findChildByClass(MMTTerm.class);
  }

  @Override
  @Nullable
  public MMTUri getUri() {
    return findChildByClass(MMTUri.class);
  }

}
