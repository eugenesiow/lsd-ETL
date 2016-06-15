for ((i=$1;i<=$2;i++))
do
	java -cp ETL-0.1.0.jar TestTDBQuery /Users/eugene/Downloads/knoesis_observations_rdf_fix/ /Users/eugene/Dropbox/Private/WORK/LinkedSensorData/queries/ /Users/eugene/Downloads/knoesis_results_gizmo2/ q$i 3 jdbc:jena:remote:query=http://192.168.0.101:8080/
done