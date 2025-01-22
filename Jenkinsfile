// This Jenkinsfile is used by Jenkins to run the DiagramConverter step of Reactome's release.
// It requires that the 'GenerateGraphDatabaseAndAnalysisCore' step has been run successfully before it can be run.
// This is the FINAL step that makes updates to any database.

import org.reactome.release.jenkins.utilities.Utilities

// Shared library maintained at 'release-jenkins-utils' repository.
def utils = new Utilities()

pipeline{
	agent any

	// Set output folder name where files generated by step will be output.
    	environment {
            OUTPUT_FOLDER = "diagram"
            ECR_URL = 'public.ecr.aws/reactome/diagram-converter'
            CONT_NAME = 'diagram_converter_container'
            CONT_ROOT = '/opt/diagram-converter'
    	}

	stages{
		// This stage checks that upstream project 'GenerateGraphDatabaseAndAnalysisCore' was run successfully.
		stage('Check Graph DB build succeeded'){
			steps{
				script{
                                    utils.checkUpstreamBuildsSucceeded("GenerateGraphDatabase")
				}
			}
		}

		stage('Setup: Pull and clean docker environment'){
			steps{
				sh "docker pull ${ECR_URL}:latest"
				sh """
					if docker ps -a --format '{{.Names}}' | grep -Eq '${CONT_NAME}'; then
						docker rm -f ${CONT_NAME}
					fi
				"""
			}
		}

		// Execute the jar file, producing the diagram JSON files.
		stage('Main: Run Diagram-Converter'){
			steps{
				script{
				    def releaseVersion = utils.getReleaseVersion()
					sh "mkdir -p ${env.OUTPUT_FOLDER}"
					sh "rm -rf ${env.OUTPUT_FOLDER}/*"
					sh "mkdir -p reports"
					sh "rm -rf reports/*"
					withCredentials([usernamePassword(credentialsId: 'mySQLUsernamePassword', passwordVariable: 'mysqlPass', usernameVariable: 'mysqlUser')]){
						withCredentials([usernamePassword(credentialsId: 'neo4jUsernamePassword', passwordVariable: 'neo4jPass', usernameVariable: 'neo4jUser')]){
				 			sh """\
							    docker run -v \$(pwd)/reports:${CONT_ROOT}/reports -v \$(pwd)/${env.OUTPUT_FOLDER}:${CONT_ROOT}/${env.OUTPUT_FOLDER} --net=host --name ${CONT_NAME} ${ECR_URL}:latest /bin/bash -c 'java -Dlogback.configurationFile=src/main/resources/logback.xml -jar target/diagram-converter-exec.jar --graph_user $neo4jUser --graph_password $neo4jPass --rel_user $mysqlUser --rel_password $mysqlPass --rel_database ${env.REACTOME_DB} --output ./${env.OUTPUT_FOLDER}'
							"""
					        	// Create archive that will be stored on S3.
							sh "tar -zcf diagrams-v${releaseVersion}.tgz ${env.OUTPUT_FOLDER}/"
							// Restart tomcat9 and neo4j services after updates were made to graph db.
					        	sh "sudo service tomcat9 stop"
							sh "sudo service neo4j stop"
							sh "sudo service neo4j start"
							sh "sudo service tomcat9 start"
						}
					}
				}
			}
		}
		
		// There are generally over 30k JSON diagram files produced in a typical release.
		// This stage gets the file counts between the current and previous release, which allows for quick review.
		stage('Post: Compare previous release file number') {
		    steps{
		        script{
				def releaseVersion = utils.getReleaseVersion()
				def previousReleaseVersion = utils.getPreviousReleaseVersion()
				def previousDiagramsArchive = "diagrams-v${previousReleaseVersion}.tgz"
				sh "mkdir -p ${previousReleaseVersion}"
				// Download previous diagram-converter output files and extract them.
				sh "aws s3 --no-progress cp s3://reactome/private/releases/${previousReleaseVersion}/diagram_converter/data/${previousDiagramsArchive} ${previousReleaseVersion}/"
				dir("${previousReleaseVersion}"){
					sh "tar -xf ${previousDiagramsArchive}"
				}
				// Output number of JSON diagram files between releases.
				def currentDiagramsFileCount = findFiles(glob: "${env.OUTPUT_FOLDER}/*").size()
				def previousDiagramsFileCount = findFiles(glob: "${previousReleaseVersion}/${env.OUTPUT_FOLDER}/*").size()
				echo("Total diagram files for v${releaseVersion}: ${currentDiagramsFileCount}")
				echo("Total diagram files for v${previousReleaseVersion}: ${previousDiagramsFileCount}")
				sh "rm -r ${previousReleaseVersion}*"
		        }
		    }
		}
		// Archive FINAL graph database file for release.
		stage('Post: Back up final graph database') {
		    steps{
		        script{
		            sh "cp -r /var/lib/neo4j/data/databases/graph.db/ ."
		            utils.createGraphDatabaseTarFile("graph.db/", "diagram_converter")
		        }
		    }
		}
		// Move diagram files and FINAL graph database to downloads folder.
		stage('Post: Move FINAL graph db and diagrams to download folder') {
		    steps{
		        script{
		            def finalGraphDbArchive = "reactome.graphdb.tgz"
		            def releaseVersion = utils.getReleaseVersion()
		            def downloadPath = "${env.ABS_DOWNLOAD_PATH}/${releaseVersion}"
		            sh "cp diagram_converter_graph_database.dump*tgz ${finalGraphDbArchive}"
		            sh "cp ${finalGraphDbArchive} ${downloadPath}/"
			    sh "if [ -d ${downloadPath}/diagram/ ]; then sudo rm -r ${downloadPath}/diagram/; mkdir ${downloadPath}/diagram/; fi"
		            sh "mv ${env.OUTPUT_FOLDER} ${downloadPath}/ "
		        }
		    }
		}
		// Archive everything on S3, and move the 'diagram' folder to the download/XX folder.
		stage('Post: Archive Outputs'){
			steps{
				script{
				    def releaseVersion = utils.getReleaseVersion()
				    def dataFiles = ["diagrams-v${releaseVersion}.tgz", "reactome.graphdb.tgz"]
					def logFiles = ["reports/*"]
					// Note at time of writing diagram-converter does not output log files (but makes very, very verbose stdout)
					def foldersToDelete = []
					utils.cleanUpAndArchiveBuildFiles("diagram_converter", dataFiles, logFiles, foldersToDelete)
				}
			}
		}
	}
}
