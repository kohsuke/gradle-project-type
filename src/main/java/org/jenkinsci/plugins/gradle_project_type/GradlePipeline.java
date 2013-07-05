package org.jenkinsci.plugins.gradle_project_type;

import hudson.Extension;
import hudson.model.AbstractProject.AbstractProjectDescriptor;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.ItemGroupMixIn;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import hudson.model.TopLevelItemDescriptor;
import hudson.util.Function1;
import jenkins.model.AbstractTopLevelItem;
import jenkins.model.Jenkins;
import jenkins.model.ModifiableTopLevelItemGroup;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static hudson.model.ItemGroupMixIn.*;

/**
 * This is a container that doesn't do any build by itself but
 * contains other {@link GradleProject}s that actually do builds.
 *
 * @author Kohsuke Kawaguchi
 */
public class GradlePipeline extends AbstractTopLevelItem implements ModifiableTopLevelItemGroup {
    private transient Map<String,TopLevelItem> items = new TreeMap<String,TopLevelItem>();

    public GradlePipeline(ItemGroup parent, String name) {
        super(parent, name);
    }

    @Override
    public void onCreatedFromScratch() {
        try {
            createProject(GradleProject.class,"build");
            createProject(GradleProject.class,"test");
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Override
    public void onLoad(ItemGroup<? extends Item> parent, String name) throws IOException {
        items = new TreeMap<String, TopLevelItem>();
        super.onLoad(parent, name);
        items = loadChildren(this, jobs(), new Function1<String, Item>() {
            public String call(Item item) {
                return item.getName();
            }
        });
    }

    public Collection<TopLevelItem> getItems() {
        return items.values(); // could be filtered by Item.READ
    }

    public TopLevelItem getItem(String name) {
        return items.get(name);
    }

    @Override
    public Collection<? extends Job> getAllJobs() {
        Set<Job> jobs = new HashSet<Job>();
        for (TopLevelItem i : getItems()) {
            jobs.addAll(i.getAllJobs());
        }
        return jobs;
    }

    private File jobs() {
        return new File(getRootDir(), "jobs");
    }

    private ItemGroupMixIn mixin() {
        return new ItemGroupMixIn(this, this) {
            @Override protected void add(TopLevelItem item) {
                items.put(item.getName(), item);
            }
            @Override protected File getRootDirFor(String name) {
                return new File(jobs(), name);
            }
        };
    }

    @Override
    public <T extends TopLevelItem> T copy(T src, String name) throws IOException {
        return mixin().copy(src, name);
    }

    @Override
    public TopLevelItem createProjectFromXML(String name, InputStream xml) throws IOException {
        return mixin().createProjectFromXML(name, xml);
    }

    @Override public TopLevelItem createProject(TopLevelItemDescriptor type, String name, boolean notify) throws IOException {
        return mixin().createProject(type, name, notify);
    }

    /** Convenience method to create a {@link FreeStyleProject} or similar. */
    public <T extends TopLevelItem> T createProject(Class<T> type, String name) throws IOException {
        return type.cast(createProject((TopLevelItemDescriptor) Jenkins.getInstance().getDescriptor(type), name, true));
    }

    @Override public TopLevelItem doCreateItem(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        return mixin().createTopLevelItem(req, rsp);
    }

    @Override public String getUrlChildPrefix() {
        return "item";
    }

    @Override public File getRootDirFor(TopLevelItem child) {
        return new File(jobs(), child.getName());
    }

    @Override public void onRenamed(TopLevelItem item, String oldName, String newName) throws IOException {
        items.remove(oldName);
        items.put(newName, item);
    }

    @Override public void onDeleted(TopLevelItem item) throws IOException {
        // could call ItemListener.onDeleted
        items.remove(item.getName());
    }

    @Extension
    public static final class DescriptorImpl extends AbstractProjectDescriptor {
        @Override
        public String getDisplayName() {
            return "Gradle Pipeline Container";
        }

        @Override
        public TopLevelItem newInstance(ItemGroup parent, String name) {
            return new GradlePipeline(parent,name);
        }
    }

}
