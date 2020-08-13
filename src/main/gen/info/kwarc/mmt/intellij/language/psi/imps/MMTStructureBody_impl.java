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

public class MMTStructureBody_impl extends ASTWrapperPsiElement implements MMTStructureBody {

  public MMTStructureBody_impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitStructureBody(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MMTVisitor) accept((MMTVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<MMTTheoryConstant> getTheoryConstantList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MMTTheoryConstant.class);
  }

  @Override
  @NotNull
  public List<MMTViewDecl> getViewDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MMTViewDecl.class);
  }

}
