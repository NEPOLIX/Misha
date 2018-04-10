# Misha Cold DB
store data in bucket name *_cold-db_*

set:
* `HealthChecker.COLD_DB_NODES_ADDRESS` to IPs of coldDB servers behind TCP load-balancer
*  `ColdDBConstants.ATHENA_OUTPUT_BUCKET` default is _"s3://athena-cold-db/query-results/"_


`ACCOUNT_ID` is the name of organization that is storing the data. As Cold DB can be extended to support multi-organization.


### Tests
Check [AthenaQuery]() and [TestStore]()
 
