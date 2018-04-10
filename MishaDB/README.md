#MishaDB

##Misha NoSQL - AWS Aurora
MishaNoSQL utilize AWS Aurora Relational DB as storage to provide NoSQL database for any object extended from `MModel.class` or json with valid `mid` field.

##Misha Cache
Key-Value in-memory cache library that is backed up by MariaDB database to avoid cache-miss. Also provide TTL for each cached item.

Value can be singleton or a list. Value type must be string.

##Misha Cold DB
Almost the same architecture as MishaNoSqlDB to store any MModel object or valid json in S3. Thus, converting S3 to cold NoSQL database.
Unlike normal database operations such as delete or update are not supported. As this storage acts as a continuous log storage. 
