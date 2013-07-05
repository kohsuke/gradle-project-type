package org.jenkinsci.plugins.gradle_project_type;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * This instance maps to individual execution of {@link GradleProject}.
 *
 * @author Kohsuke Kawaguchi
 */
public class GradleBuild extends AbstractBuild<GradleProject,GradleBuild> {
    public GradleBuild(GradleProject job) throws IOException {
        super(job);
    }

    public GradleBuild(GradleProject job, Calendar timestamp) {
        super(job, timestamp);
    }

    public GradleBuild(GradleProject project, File buildDir) throws IOException {
        super(project, buildDir);
    }

    @Override
    public void run() {
        execute(new GradleExecution());
    }

    /**
     * This instance maps to  the in-progress build.
     */
    public class GradleExecution extends AbstractBuildExecution {
        @Override
        protected Result doRun(BuildListener listener) throws Exception, RunnerAbortedException {
            listener.getLogger().println("TODO: do something useful");
            return Result.SUCCESS;
        }

        @Override
        protected void post2(BuildListener listener) throws Exception {
        }

        @Override
        public void cleanUp(@Nonnull BuildListener listener) throws Exception {
        }
    }
}
