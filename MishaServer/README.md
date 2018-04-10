# Misha Server
### Socket Server module
To create socket server and its client.
Example of this module is [MishaDb.MishaCache](https://github.com/NEPOLIX/Misha/tree/master/MishaDB/MishaCache) module
### RESTServer module
To build your own REST server.
This module filter AWS load-balancer pings from logger.

##### Example:

_TestServer.class_
```
public
class TestServer
{
	 
	 private static int PORT = -1;
	 
	 static
	 {
			Init$MID$CacheClusters$Logger$InetAddressConfig.config$Init ( );
	 }
	 
	 public static
	 ITaskEngine getITaskRunner ( )
	 {
			
			return Server.getTaskEngine ( );
	 }
	 
	 public static
	 JSONObject getConfig ( )
	 {
			
			return Server.getServerInstance ( getPort ( ) ).getConfig ( );
	 }
	 
	 public static
	 int getPort ( )
	 {
			
			if ( PORT == -1 )
			{
				 JSONObject config = Config.getConfig ( "test" );
				 try
				 {
						PORT = config.getInt ( "port" );
				 }
				 catch ( JSONException e )
				 {
						PORT = 23572;
				 }
			}
			return PORT;
	 }
	 
	 public static
	 void main ( String[] args )
					 throws
					 JSONException
	 {
			
			TaskExceptionListener exceptionListenerServer = e -> {
				 JSONObject error = JSONException.exceptionToJSON ( e );
				 Log        log   = Logger.getLogger ( ).buildLog ( "TEST-SERVER" , "LOG" , "ERROR" , "SERVER" , "TASK" );
				 log.$addLogData ( LogTag.ERROR , "Task Exception\n" + MJSON.toString ( error ) , null );
				 Logger.getLogger ( ).log ( log );
			};
			WebClient.getInstance ( ).getTaskRunner ( ).setExceptionListener ( exceptionListenerServer );
			JSONObject config = Config.getConfig ( "test" );
			Server     server = Server.buildServerInstance ( getPort ( ) , false , config );
			Server.getTaskEngine ( ).setExceptionListener ( exceptionListenerServer );
			if ( config.getBoolean ( "init_secure_session_id" ) )
			{
				 MishaID mishaID = MishaID.getMishaID ( );
				 mishaID.nextID ( MISHA_SESSION_ID_NAME );
			}
			server.setRedirectToHTTPS ( true );
			server.start ( );
			ApiCallExpress[] callExpressList = new ApiCallExpress[] {
							EmptyAPI.getInstance ( )
			};
			Server.addAPIHandler ( callExpressList , server );
			System.out.println ( "Server Start Time= " + Utils.getCurrentUTCTime ( ) + "   " + Utils.getCurrentFormattedUTCTime ( ) );
	 }
}
```

_Init$MID$CacheClusters$Logger$InetAddressConfig.class_
Initializing MishaID, Cache Cluster, Logger, MishaDB.
```
public abstract
class Init$MID$CacheClusters$Logger$InetAddressConfig
{
	 
	 private final static String AURORA_DB_USER_PASS = "<AWS_AURORA_pASSWORD>";
	 
	 private final static String AURORA_DB_WRITE_ADDRESS = "testcluster01.cluster-csvhg0ylmvrt.us-west-2.rds.amazonaws.com";
	 
	 private final static String AURORA_DB_READ_ADDRESS = "testcluster01.cluster-ro-csvhg0ylmvrt.us-west-2.rds.amazonaws.com";
	 
	 private final static String MISHA_ID_ADDRESS = "mishaid.testsite.com";
	 
	 private final static String[][] NEO_CACHE_CLUSTER_ADDRESS = new String[][] {
					 {//us-west
                     				<IPs of  Misha cachecluster of west>
					 },
					 {//us-east
                     				<IPs of  Misha cachecluster of east>
					 }
	 };
	 
	 private final static String[] MISHA_DB_COLD_ADDRESS = new String[] { "colddb-west.testsite.com" };
	 
	 private final static String   ATHENA_DB_URL         = "jdbc:awsathena://athena.us-west-2.amazonaws.com:443";
	 
	 public static
	 void config$Init ( )
	 {
			
			WebClient.getInstance ( );
			CacheNeoDB.initInetAddress ( NEO_CACHE_CLUSTER_ADDRESS , 0 );
			MishaID.initInetAddress ( MISHA_ID_ADDRESS );
			MishaDBNoSql.init ( MishaID.getMishaID ( ) , AURORA_DB_WRITE_ADDRESS , AURORA_DB_READ_ADDRESS , AURORA_DB_USER_PASS );
			MishaColdDB.init ( MishaID.getMishaID ( ) , MISHA_DB_COLD_ADDRESS , 0 , Credentials.AWS_ACCESS_KEY , Credentials.AWS_PRIVATE_KEY , ATHENA_DB_URL , "test" );
			
			Logger.getLogger ( MishaColdDB.getInstance ( ) );
	 }
}
```

_EmptyAPI.class_
Sample APIs, health API for AWS load-balancer health checker.
```
public
class EmptyAPI
				extends ApiCallExpress
{
	 
	 private final static ApiCallExpress $X = new EmptyAPI ( );
	 
	 EmptyAPI ( )
	 {
			
			super ( "/" );
	 }
	 
	 public static
	 ApiCallExpress getInstance ( )
	 {
			
			return $X;
	 }
	 
	 GetCall getCall ( )
	 {
			
			return new GetCall ( )
			{
				 
				 @Override
				 public
				 String callPath ( )
				 {
						
						return null;
				 }
				 
				 @Override
				 public
				 void apiCall ( Request request ,
												Response response )
								 throws
								 Exception
				 {

						response.sendRedirectAPI ( new URI ( "http://testsite.com" ) );
				 }
			};
	 }
	 
	 
	 GetCall healthChecker ( )
	 {
			
			return new GetCall ( )
			{
				 
				 @Override
				 public
				 String callPath ( )
				 {
						
						return "/health";
				 }
				 
				 @Override
				 public
				 void apiCall ( Request request ,
												Response response )
								 throws
								 Exception
				 {
						
						APICallResult apiCallResult = new APICallResult ( );
						apiCallResult.setStatusCode ( Response.STATUS_CODE_OK );
						apiCallResult.getResult ( ).putOpt ( "status" , "green" );
						response.sendResponse ( apiCallResult );
				 }
			};
	 }
	 
	 GetCall throwExceptionAPI ( )
	 {
			
			return new GetCall ( )
			{
				 
				 @Override
				 public
				 String callPath ( )
				 {
						
						return "/throwExceptionAPI";
				 }
				 
				 @Override
				 public
				 void apiCall ( Request request ,
												Response response )
								 throws
								 Exception
				 {
						
						APICallResult apiCallResult = new APICallResult ( );
						String        x             = null;
						x.contains ( "jh" );
						response.sendResponse ( apiCallResult );
				 }
			};
	 }
	 
	 @Override
	 protected
	 void initContext ( )
	 {
			
			addAPIs ( getCall ( ) , healthChecker ( ) , throwExceptionAPI ( ) );
	 }
	 
	 @Override
	 protected
	 String getAPIVersion ( )
	 {
			
			return null;
	 }
}
```