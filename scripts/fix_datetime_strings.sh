sed -i -e 's,\^\^http://www\.w3\.org/2001/XMLSchema#dateTime","\^\^xsd:dateTime,g' *.n3 
sed -i -e 's,\^\^http://www\.w3\.org/2001/XMLSchema#boolean","\^\^xsd:boolean,g' *.n3 
rm *.n3-e