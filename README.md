# Apache_Camel_Bitronix_file_filtering
There is a folder with files. Using the Apache Camel tools, 
implement the following process: the file is taken from the folder, 
then, depending on the type, some actions are performed. If the file 
has an xml extension, then its contents must be sent to the queue in the 
ActiveMQ broker. If it has a txt extension, then it must be sent to the broker, 
as well as written to a table in the database. If the extension is different, 
an exception is thrown and the file is sent to the invalid-queue in ActiveMQ. 
When processing every hundredth file, send a letter containing the number of txt files, 
the number of xml files, the number of unrecognized files, as well as the processing 
time for a batch of messages. For guaranteed delivery, you must configure and connect 
the distributed transaction manager (Bitronix TM).
