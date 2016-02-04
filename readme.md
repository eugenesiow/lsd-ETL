### Linked Sensor Data RDF to RDBMS

1. Download the set of Linked Observation Data that want to transform from http://wiki.knoesis.org/index.php/LinkedSensorData.
2. Download the Linked Sensor Data with meta data of stations from http://sonicbanana.cs.wright.edu/knoesis_linkedsensordata.tar.gz.
3. Download a release of our tool https://github.com/eugenesiow/lsd-ETL/releases/download/0.1.0/LSD-ETL.zip or clone the repository.
4. Configure the `run.sh` script in the LSD-ETL directory. Change the parameters accordingly. To find a reference of parameters, you can run `java -jar ETL-0.1.0.jar --help`.
5. Run the `run.sh` script in the LSD-ETL directory. You might have to `chmod 775 run.sh` to make it executable.
6. All required files will be produced in the output directory specified. Now you have a `_rdbms` subdirectory and a `_tdb` subdirectory with the database outputs in a RDBMS and triplestore.

### Other Projects
* [sparql2sql](https://github.com/eugenesiow/sparql2sql)
* [sparql2stream](https://github.com/eugenesiow/sparql2stream)
* [sparql2sql-server](https://github.com/eugenesiow/sparql2sql-server)
* [Linked Data Analytics](http://eugenesiow.github.io/iot/)
