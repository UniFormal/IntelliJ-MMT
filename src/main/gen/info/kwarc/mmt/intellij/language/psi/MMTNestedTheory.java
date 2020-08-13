// This is a generated file. Not intended for manual editing.
package info.kwarc.mmt.intellij.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface MMTNestedTheory extends PsiElement {

  @Nullable
  MMTError getError();

  @Nullable
  MMTTerm getTerm();

  @Nullable
  MMTTheoryBody getTheoryBody();

  @NotNull
  MMTTheoryHeader getTheoryHeader();

}
