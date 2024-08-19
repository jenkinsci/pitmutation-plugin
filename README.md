# pitmutation-jenkins
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/jenkinsci/pitmutation-plugin?label=release) [![Build Status](https://ci.jenkins.io/job/Plugins/job/pitmutation-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/pitmutation-plugin/job/master/)

[PIT Mutation](http://pitest.org/) reporting plugin for Jenkins.

Set up as a post-build step, after the PIT mutation tests have been run.

Configure report path, e.g. `target/pit-reports/**/mutations.xml` for a Maven build or `build/reports/pitest/mutations.xml` for a Gradle build.

## Jenkins Pipeline
You can use the following step in pipeline to use this plugin in pipeline:

`pitmutation ignoreMissingReports: false, killRatioMustImprove: false, minimumKillRatio: 50.0, mutationStatsFile: '**/target/pit-reports/**/mutations.xml'`

The plugin needs the XML and HTML output from PIT. Also make sure
that a clean target is executed before building, otherwise PIT will
keep all of the old reports and it may not pick up the right one.

The report shows mutation statistics with the change since the last successful build,
and you can drill down to the annotated source code at class level to see what mutations
happened on each line.

## Development
Ensure that your IDE is [setup correctly](https://wiki.jenkins.io/display/JENKINS/Plugin+tutorial#Plugintutorial-SettingupaproductiveenvironmentwithyourIDE).

## Releasing
Run `mvn release:prepare release:perform` but ensure that your Maven `settings.xml` has been [set up with your jenkins-ci.org credentials](https://wiki.jenkins.io/display/JENKINS/Hosting+Plugins#HostingPlugins-Releasingtojenkins-ci.org)
