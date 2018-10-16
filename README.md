# pitmutation-jenkins
[![release](http://github-release-version.herokuapp.com/github/jenkinsci/pitmutation-plugin/release.svg?style=flat)](https://github.com/jenkinsci/pitmutation-plugin/releases/latest) [![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/pitmutation-plugin/master)](https://ci.jenkins.io/job/Plugins/pitmutation-plugin/master)

[PIT Mutation](http://pitest.org/) reporting plugin for Jenkins.

Set up as a post-build step, after the PIT mutation tests have been run.

Configure report path, e.g. `target/pit-reports/**/mutations.xml` for a Maven build or `build/reports/pitest/mutations.xml` for a Gradle build.

## Jenkins Pipeline
You can use the following step in pipeline to use this plugin in pipeline:

`step([$class: 'PitPublisher', mutationStatsFile: 'bla/**/mutations.xml', minimumKillRatio: 50.00, killRatioMustImprove: false])`

The plugin needs the XML and HTML output from PIT. Also make sure 
that a clean target is executed before building, otherwise PIT will 
keep all of the old reports and it may not pick up the right one.

The report shows mutation statistics with the change since the last successful build,
and you can drill down to the annotated source code at class level to see what mutations 
happened on each line.

## Releasing
Run `mvn release:prepare release:perform` but ensure that your Maven `settings.xml` has been [set up with your jenkins-ci.org credentials](https://wiki.jenkins.io/display/JENKINS/Hosting+Plugins#HostingPlugins-Releasingtojenkins-ci.org)

## Issues
[Jenkins:pitmutation-plugin](https://issues.jenkins-ci.org/issues/?jql=project+%3D+JENKINS+AND+component+%3D+pitmutation-plugin)
