# Final Exam

### Douglas Rudy

### dmr136@zips.uakron.edu

---

## Overview

The Markturn project provided a great opportunity to test out two of the better known continuous integration and continuous deployment tools in Jenkins and CircleCI. Having become somewhat familiar with both as ways of creating and managing workflows, I can say there are considerations to be made when making the choice of which to work with. Of these considerations, some of the larger ones to note include setting up, support for containerization, built-in features, and overall usage.

## Jenkins

One of the best known means of automating builds and deployment, Jenkins boasts a seamless installation ready to run out-of-the-box. Even so, the way in which Jenkins was utilized for the Markturn project as well as other exercises is a bit different. For our purposes, we were able to get Jenkins working inside of a container and this was crucial as it allowed us to eventually utilize Docker Outside of Docker (DOOD) in order to customize the platform on which we build and test. Perhaps this is a bit ironic, seeing as though Jenkins by itself does not a Docker workflow unlike its counterpart, CircleCI. Nevertheless, apart from how the Markturn project was set up, Jenkins proved quite different from CircleCI in terms of built-in support for many of the utilities needed for the project to build. This is where Jenkins plugins come into the equation. Hosting more than 1,200 plugins, extending the functionality of Jenkins was straightforward and I was able to install a multitude of tools including cmake build, Docker pipeline, and copyArtifact in order to not only get the build going, but establish new features that expedite and facilitate testing across multiple platforms. The medium through which plugins are installed, builds are started and monitored, and projects are managed is the last considerable Jenkins aspect to be noted which sets it apart from CircleCI. This Jenkins UI is an interactive way of configuring builds, and it all starts when your account has been registered. From here, you can create a new project (item) and choose what type of project it is. For the Markturn project, we chose to set up a pipeline project in which a pipeline script could be written using the groovy syntax and aided by Jenkins' "Pipeline Syntax" feature which simplifies what might be complex commands utilizing external tools or plugins.

## CircleCI

The Markturn implementation in CircleCI was quite different from Jenkins in many key ways. Firstly, setting up with CircleCI was not a problem in the slightest due to its direct integration with GitHub as well as other Version Control Systems such as Bitbucket. Indeed, all that was needed to become somewhat familiar with the workflow was to create a "circleci-project-setup" branch which was handled directly by CircleCI with a push of a button. I preferred the setup for CircleCI because I was able to stray away from the longer commands to get the Jenkins container started in addition to cut down the time used for logging in and opening up a Jenkins project. From here, a bit of an adjustment needed to be made as CircleCI pipelines are written in the YAML format rather than the groovy syntax. As such, proper practices needed to be followed to ensure a well-formed YAML document. Following this, something which proved very convenient was CircleCI's support for Docker. This meant that the time needed to implement DOOD and test the build on the desired Ubuntu 20.04 distribution was significantly shortened. The aforementioned testing is another thing which differs from Jenkins as testing a pipeline script is only a matter of performing a commit to the CircleCI GitHub branch. This is so because CircleCI creates a new pipeline for each commit I make and runs an automated test whose results I can see inside of the pull request should I choose to create one. Finally a great feature that was implemented for the Markturn project within CircleCI was the concept of storing build artifacts. This was very useful for the Markturn project as I was able to archive the debian package created as a result of the build and view it on CircleCI under the "Artifacts" tab. What this means is that we can build something for a coworker or colleague using this package link which persists for 30 days after its creation. I found this feature very interesting and thus sought to get it working in the Jenkins pipeline.

## Jenkins Additional Implementations (Archive/Copy Artifact)

Archiving artifacts in CircleCI provided me with the motivation to get the feature working inside my Markturn Jenkins pipeline. In addition, I looked into a "copyArtifact" feature and its potential upsides. To my surprise, implementing "store_artifacts" was much more complex in Jenkins mainly due to how the Jenkins Docker pipeline is configured. As our course page mentions, "Jenkins automatically mounts your Jenkins container workspace into your Docker container in the pipeline." It is for this reason that when I would try to perform 'ls' or 'cd' shell commands inside of what I thought was the Docker container, I would be met with unexpected, confusing output in the form of empty working directories or directories which simply didn't exist which I thought did. Arriving at a feasible solution took much trial-and-error, as one might expect, but basically the goal at hand was to somehow store a build artifact so that it would not be wiped upon completion. Ultimately, this was accomplished by creating a subdirectory inside the Jenkins container and copying the package to this subdirectory using the absolute path established by the Docker container. After this was complete, the `archiveArtifacts` pipeline syntax was utilized to tell Jenkins where to find this package and store it.

```groovy
sh 'cp /Build/*.deb dist/.'
archiveArtifacts artifacts: 'dist/*.deb', followSymlinks: false
```

I am fairly pleased with this solution, as now I can see the built debian package on the Jenkins UI every time a build is successful. According to the documentation on archiveArtifacts, this artifact will persist so long as the build log itself is kept. I did not stop here, however, as I was made aware of a Jenkins plugin which would allow this artifact to be shared amongst other projects. This feature, known as "copyArtifacts" was much easier to implement as it simply involved making a test pipeline named 'markturn-output.' Apart from this, only one stage and step was needed to copy the artifact from the Markturn project into this one. Upon a successful build, if I go into my Jenkins container and change to the appropriate working directory, I can see the package listed under the same 'dist' directory as it was in the Markturn project. As was mentioned earlier, both of these tools are included for the purpose of sharing build artifacts amongst colleagues, coworkers, or anyone else for ease of installation and building.

```groovy
pipeline {
    agent any

    stages {
        stage('Copy Artifact') {
            steps {
                copyArtifacts(projectName: 'markturn');
            }
        }
    }
}
```
