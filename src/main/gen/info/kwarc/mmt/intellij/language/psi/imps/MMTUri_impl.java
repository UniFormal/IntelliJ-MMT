// This is a generated file. Not intended for manual editing.
package info.kwarc.mmt.intellij.language.psi.imps;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static info.kwarc.mmt.intellij.language.psi.MMTParserTypes.*;
import info.kwarc.mmt.intellij.language.URIElement_impl;
import info.kwarc.mmt.intellij.language.psi.*;

public class MMTUri_impl extends URIElement_impl implements MMTUri {

  public MMTUri_impl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitUri(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MMTVisitor) accept((MMTVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<MMTUritoken> getUritokenList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MMTUritoken.class);
  }

}
