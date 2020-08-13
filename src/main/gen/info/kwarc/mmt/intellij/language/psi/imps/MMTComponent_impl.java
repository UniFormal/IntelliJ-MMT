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

public class MMTComponent_impl extends ASTWrapperPsiElement implements MMTComponent {

  public MMTComponent_impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MMTVisitor visitor) {
    visitor.visitComponent(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MMTVisitor) accept((MMTVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public MMTAliasComp getAliasComp() {
    return findChildByClass(MMTAliasComp.class);
  }

  @Override
  @Nullable
  public MMTDefComp getDefComp() {
    return findChildByClass(MMTDefComp.class);
  }

  @Override
  @Nullable
  public MMTMetaComp getMetaComp() {
    return findChildByClass(MMTMetaComp.class);
  }

  @Override
  @Nullable
  public MMTNotComp getNotComp() {
    return findChildByClass(MMTNotComp.class);
  }

  @Override
  @Nullable
  public MMTObjComment getObjComment() {
    return findChildByClass(MMTObjComment.class);
  }

  @Override
  @Nullable
  public MMTRoleComp getRoleComp() {
    return findChildByClass(MMTRoleComp.class);
  }

  @Override
  @Nullable
  public MMTTypeComp getTypeComp() {
    return findChildByClass(MMTTypeComp.class);
  }

}
