// This is a generated file. Not intended for manual editing.
package info.kwarc.mmt.intellij.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface MMTDerivedDecl extends PsiElement {

  @Nullable
  MMTDerivedHeader getDerivedHeader();

  @Nullable
  MMTDerivedSimple getDerivedSimple();

  @Nullable
  MMTError getError();

  @Nullable
  MMTTheoryBody getTheoryBody();

}
