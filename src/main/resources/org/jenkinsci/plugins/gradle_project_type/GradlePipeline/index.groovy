package org.jenkinsci.plugins.gradle_project_type.GradlePipeline;

def l = namespace(lib.LayoutTagLib)
def f = namespace(lib.FormTagLib)
def p = namespace(lib.JenkinsTagLib)

l.layout {
    l.main_panel {
        p.projectView(jobs:my.items)
    }
}