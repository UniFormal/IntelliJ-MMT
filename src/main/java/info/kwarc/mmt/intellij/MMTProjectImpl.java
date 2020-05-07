package info.kwarc.mmt.intellij;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

@Service
public final class MMTProjectImpl extends MMTProject {
    public MMTProjectImpl(Project pr) {
        super(pr);
    }
}
