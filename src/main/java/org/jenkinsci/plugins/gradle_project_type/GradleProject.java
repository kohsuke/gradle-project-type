package org.jenkinsci.plugins.gradle_project_type;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.DependencyGraph;
import hudson.model.Descriptor;
import hudson.model.ItemGroup;
import hudson.model.TopLevelItem;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;

/**
 * @author Kohsuke Kawaguchi
 */
public class GradleProject extends AbstractProject<GradleProject,GradleBuild> implements TopLevelItem {
    /**
     * List of active {@link Publisher}s configured for this project.
     */
    private DescribableList<Publisher,Descriptor<Publisher>> publishers = new DescribableList<Publisher,Descriptor<Publisher>>(this);

    public GradleProject(ItemGroup parent, String name) {
        super(parent, name);
    }

    @Override
    public DescribableList<Publisher, Descriptor<Publisher>> getPublishersList() {
        return publishers;
    }

    @Override
    protected Class<GradleBuild> getBuildClass() {
        return GradleBuild.class;
    }

    @Override
    public boolean isFingerprintConfigured() {
        return false;
    }

    @Override
    protected void buildDependencyGraph(DependencyGraph graph) {
        // nothing to contribute to the dependency graph

    }

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)Jenkins.getInstance().getDescriptor(GradleProject.class);
    }

    @Extension
    public static final class DescriptorImpl extends AbstractProjectDescriptor {
        @Override
        public String getDisplayName() {
            return "Gradle Project";
        }

        @Override
        public TopLevelItem newInstance(ItemGroup parent, String name) {
            return new GradleProject(parent,name);
        }
    }
}
