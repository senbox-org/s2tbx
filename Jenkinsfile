#!/usr/bin/env groovy

/**
 * Copyright (C) 2019 CS-SI
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

pipeline {
    agent any
    stages {
        stage('Package') {
            agent {
                docker {
                    image 'snap-build-server.tilaa.cloud/maven:3.6.0-jdk-8'
                    // We add the docker group from host (i.e. 999)
                    args ' --group-add 999 -e MAVEN_CONFIG=/var/maven/.m2 -v /var/run/docker.sock:/var/run/docker.sock -v /usr/bin/docker:/bin/docker -v /opt/maven/.m2/settings.xml:/var/maven/.m2/settings.xml'
                }
            }
            steps {
                echo "Build ${env.JOB_NAME} from ${env.GIT_BRANCH} with commit ${env.GIT_COMMIT}"
                // sh 'mvn -Duser.home=/var/maven -Dsnap.userdir=/home/snap clean package install -U -Dsnap.reader.tests.data.dir=/data/ssd/s2tbx/ -Dsnap.reader.tests.execute=false -DskipTests=false'
            }
        }
        stage('Deploy') {
            agent any
            steps {
                echo "Deploy ${env.JOB_NAME} from ${env.GIT_BRANCH} using commit ${env.GIT_COMMIT}"
                script {
                    // Get snap version from .nbm file name
                    snapVersion = sh(returnStdout: true, script: "ls -l *-kit/target/netbeans_site/ | grep kit | tr -s ' ' | cut -d ' ' -f 9 | cut -d'-' -f 3").trim()
                    branchVersion = sh(returnStdout: true, script: "echo ${env.GIT_BRANCH} | cut -d '/' -f 2").trim()
                }
                sh "id"
                sh "ls -l /"
                sh "mkdir -p /local-update-center/${env.JOB_NAME}-${branchVersion}-${snapVersion}-${env.GIT_COMMIT}"
                sh 'cp s2tbx-kit/target/netbeans_site/* /local-update-center/${env.JOB_NAME}-${branchVersion}-${snapVersion}-${env.GIT_COMMIT}'
            }
        }
    }
    post {
        failure {
            step (
                emailext(
                    subject: "[SNAP] JENKINS-NOTIFICATION: ${currentBuild.result ?: 'SUCCESS'} : Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                    body: """Build status : ${currentBuild.result ?: 'SUCCESS'}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':
Check console output at ${env.BUILD_URL}
${env.JOB_NAME} [${env.BUILD_NUMBER}]""",
                    attachLog: true,
                    compressLog: true,
                    recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class:'DevelopersRecipientProvider']]
                )
            )
        }
    }
}
