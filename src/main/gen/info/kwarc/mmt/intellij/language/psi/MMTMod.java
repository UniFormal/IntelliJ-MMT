// This is a generated file. Not intended for manual editing.
package info.kwarc.mmt.intellij.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface MMTMod extends PsiElement {

  @Nullable
  MMTDiagram getDiagram();

  @Nullable
  MMTDocumentScopedComment getDocumentScopedComment();

  @Nullable
  MMTError getError();

  @Nullable
  MMTFixmeta getFixmeta();

  @Nullable
  MMTImport getImport();

  @Nullable
  MMTMetaDatum getMetaDatum();

  @Nullable
  MMTNamespace getNamespace();

  @Nullable
  MMTRule getRule();

  @Nullable
  MMTTheory getTheory();

  @Nullable
  MMTView getView();

}
