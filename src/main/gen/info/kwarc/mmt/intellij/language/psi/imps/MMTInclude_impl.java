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

public class MMTInclude_impl extends ASTWrapperPsiElement implements MMTInclude {

  public MMTInclude_impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitInclude(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MMTVisitor) accept((MMTVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public MMTTmlist getTmlist() {
    return findChildByClass(MMTTmlist.class);
  }

  @Override
  @NotNull
  public List<MMTUri> getUriList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MMTUri.class);
  }

  @Override
  @NotNull
  public List<MMTUrilit> getUrilitList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MMTUrilit.class);
  }

}
