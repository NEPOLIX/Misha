/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to NOX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.nepolix.misha.web.server;

import com.nepolix.misha.commons.Constants;
import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.logger.client.Logger;
import com.nepolix.misha.logger.log.LogTag;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.scheduler.ScheduleTask;
import com.nepolix.misha.task.scheduler.Scheduler;
import com.nepolix.misha.web.server.rest.express.ApiCallExpress;
import com.nepolix.misha.web.utils.Pair;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;

import static com.nepolix.misha.task.scheduler.Scheduler.DEFAULT_SCHEDULER_WORKER_SIZE;

/**
 * @author Behrooz Shahriari
 * @since 8/8/16
 */
public
class Server
{
	 
	 
	 private boolean serverRunning = false;
	 
	 private final static HashMap< Integer, Server > SERVERS = new HashMap<> ( );
	 
	 private static ITaskEngine TASK_ENGINE;
	 
	 private static Scheduler SCHEDULER;
	 
	 private HttpServer server;
	 
	 private int port;
	 
	 private final JSONObject config;
	 
	 private boolean redirectToHTTPS = false;
	 
	 public static
	 Server buildServerInstance ( int port ,
																boolean https ,
																JSONObject config )
	 {
			
			Server server = SERVERS.computeIfAbsent ( port , k -> new Server ( port , config , https ) );
			return server;
	 }
	 
	 public static
	 Server getServerInstance ( int port )
	 {
			
			Server server = SERVERS.get ( port );
			if ( server == null )
			{
				 throw new NullPointerException ( "no server for port of " + port + " has be setup" );
			}
			return server;
	 }
	 
	 public static
	 Pair< KeyManagerFactory, TrustManagerFactory > loadCertificate ( )
					 throws
					 IOException,
					 KeyStoreException,
					 CertificateException,
					 NoSuchAlgorithmException,
					 UnrecoverableKeyException
	 {
			// load certificate
			String          tmp              = System.getProperty ( "user.home" );//  "/tmp"
			String          certPath         = tmp + File.separator + ".misha" + File.separator + "cert" + File.separator + "misha.jks";
			String          keystoreFilename = certPath;
			char[]          storepass        = "hvkkdUcBMBrRAXGyEJgVW5CBMfCkzufGNbRSWUjk".toCharArray ( );
			char[]          keypass          = "hvkkdUcBMBrRAXGyEJgVW5CBMfCkzufGNbRSWUjk".toCharArray ( );
			String          alias            = "xMISHAx";
			FileInputStream fIn              = new FileInputStream ( keystoreFilename );
			KeyStore        keystore         = KeyStore.getInstance ( "JKS" );
			keystore.load ( fIn , storepass );
// display certificate
			Certificate cert = keystore.getCertificate ( alias );
			/*System.out.println ( cert );*/
// setup the key manager factory
			KeyManagerFactory kmf = KeyManagerFactory.getInstance ( "SunX509" );
			kmf.init ( keystore , keypass );
// setup the trust manager factory
			TrustManagerFactory tmf = TrustManagerFactory.getInstance ( "SunX509" );
			tmf.init ( keystore );
			return new Pair<> ( kmf , tmf );
	 }
	 
	 private
	 Server ( int port ,
						JSONObject config ,
						boolean https )
	 {
			
			this.port = port;
			this.config = config;
			
			try
			{ // setup the socket address
				 InetSocketAddress address = new InetSocketAddress ( port );
				 System.out.println ( "PORT= " + port );
				 System.out.println ( "SECURE SERVER=" + https );
				 if ( !https ) server = HttpServer.create ( address , 0 );
				 else
				 {
						// initialise the HTTPS server
						server = HttpsServer.create ( address , 0 );
						SSLContext sslContext = SSLContext.getInstance ( "TLS" );
						
						// initialise the keystore
						char[]          password = "hvkkdUcBMBrRAXGyEJgVW5CBMfCkzufGNbRSWUjk".toCharArray ( );
						KeyStore        ks       = KeyStore.getInstance ( "JKS" );
						String          tmp      = System.getProperty ( "user.home" );//  "/tmp"
						String          certPath = tmp + File.separator + ".misha" + File.separator + "cert" + File.separator + "misha" + ".jks";
						FileInputStream fis      = new FileInputStream ( certPath );
						ks.load ( fis , password );
						
						// setup the key manager factory
						KeyManagerFactory kmf = KeyManagerFactory.getInstance ( "SunX509" );
						kmf.init ( ks , password );
						
						// setup the trust manager factory
						TrustManagerFactory tmf = TrustManagerFactory.getInstance ( "SunX509" );
						tmf.init ( ks );
						
						// setup the HTTPS context and parameters
						sslContext.init ( kmf.getKeyManagers ( ) , tmf.getTrustManagers ( ) , null );
//						server.setHttpsConfigurator ( new HttpsConfigurator ( sslContext ) );
						( ( HttpsServer ) server ).setHttpsConfigurator ( new HttpsConfigurator ( sslContext )
						{
							 
							 public
							 void configure ( HttpsParameters params )
							 {
									
									try
									{
										 // initialise the SSL context
										 SSLContext c      = SSLContext.getDefault ( );
										 SSLEngine  engine = c.createSSLEngine ( );
										 params.setNeedClientAuth ( false );
										 params.setCipherSuites ( engine.getEnabledCipherSuites ( ) );
										 params.setProtocols ( engine.getEnabledProtocols ( ) );
										 
										 // get the default parameters
										 SSLParameters defaultSSLParameters = c.getDefaultSSLParameters ( );
										 params.setSSLParameters ( defaultSSLParameters );
										 
									}
									catch ( Exception ex )
									{
										 System.out.println ( "Failed to create HTTPS port" );
									}
							 }
						} );
				 }
			}
			catch ( IOException | CertificateException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException e )
			{
				 e.printStackTrace ( );
			}
			if ( TASK_ENGINE == null )
			{
				 if ( config != null ) System.out.println ( "CONFIG=" + config.toString ( ) );
				 if ( config != null && config.has ( Config.CONCURRENCY ) )
				 {
						TASK_ENGINE = ITaskEngine.buildTaskEngine ( false );
				 }
				 else
				 {
						TASK_ENGINE = ITaskEngine.buildTaskEngine ( true );
				 }
				 if ( config != null && config.has ( Config.SCHEDULER_CONCURRENCY ) )
				 {
						{
							 try
							 {
									SCHEDULER = Scheduler.getScheduler ( config.getInt ( Config.SCHEDULER_CONCURRENCY ) );
							 }
							 catch ( JSONException e )
							 {
									e.printStackTrace ( );
									SCHEDULER = Scheduler.getScheduler ( DEFAULT_SCHEDULER_WORKER_SIZE );
							 }
						}
				 }
				 else
				 {
						SCHEDULER = Scheduler.getScheduler ( DEFAULT_SCHEDULER_WORKER_SIZE );
				 }
				 
				 class GCTask
								 extends ScheduleTask
				 {
						
						@Override
						public
						int interval ( )
						{
							 
							 return Constants.INTERVAL_5_MINUTES;
						}
						
						@Override
						protected
						void execute ( )
						{
							 
							 Utils.causeGC ( );
						}
				 }
				 SCHEDULER.schedule ( new GCTask ( ) );
			}
	 }
	 
	 public
	 void setRedirectToHTTPS ( boolean redirectToHTTPS )
	 {
			
			this.redirectToHTTPS = redirectToHTTPS;
	 }
	 
	 public static
	 ITaskEngine getTaskEngine ( )
	 {
			
			return TASK_ENGINE;
	 }
	 
	 public static
	 void setTaskEngine ( ITaskEngine taskEngine )
	 {
			
			TASK_ENGINE = taskEngine;
	 }
	 
	 public static
	 Scheduler getScheduler ( )
	 {
			
			return SCHEDULER;
	 }
	 
	 public static
	 void setScheduler ( Scheduler scheduler )
	 {
			
			Server.SCHEDULER = scheduler;
	 }
	 
	 public
	 Server addAPIHandler ( ApiCallExpress apiCallExpress )
	 {
			
			apiCallExpress.setRedirectToHTTPS ( redirectToHTTPS );
			server.createContext ( apiCallExpress.getBasePath ( ) , apiCallExpress );
			return this;
	 }
	 
	 public final
	 Server start ( )
	 {
			
			if ( serverRunning ) return this;
			server.setExecutor ( null ); // creates a default executor
			server.start ( );
			System.out.println ( "Server is running on port=" + port );
			System.out.println ( "Server Start Time= " + Utils.getCurrentUTCTime ( ) + "   " + Utils.getCurrentFormattedUTCTime ( ) );
			serverRunning = true;
			try
			{
				 Logger.getLogger (  ).log ( Logger.getLogger (  ).buildLog ( "SERVER" , "LOG" , "MISHA" ).$addLogData ( LogTag.INFO ,
																																														"Server is running on port=" + port + "\n" + "Server Start Time= " + Utils.getCurrentUTCTime ( ) + "   "
																																														+ Utils.getCurrentFormattedUTCTime ( ) , null ) );
			}
			catch ( Exception e )
			{
				 System.out.println ( "WARNING: "+e.getMessage () );
			}
			return this;
	 }
	 
	 public
	 JSONObject getConfig ( )
	 {
			
			return config;
	 }
	 
	 public static
	 void addAPIHandler ( ApiCallExpress apiCallExpressList[] ,
												Server... servers )
	 {
			
			for ( Server server : servers )
				 for ( ApiCallExpress apiCallExpress : apiCallExpressList )
						server.addAPIHandler ( apiCallExpress );
	 }

//	 private
//	 void addContexts ( HttpServer httpServer )
//	 {
//
//			httpServer.createContext ( "/register/", new TestAPIHandler ( ) );
//			httpServer.createContext ( "/register/name", new HttpHandler ( )
//			{
//
//				 @Override
//				 public
//				 void handle ( HttpExchange httpExchange )
//								 throws
//								 IOException
//				 {
//
//						sendMessage ( "EMPTY", 200, httpExchange );
//				 }
//			} );
//	 }
//
//	 public static
//	 void sendEmptyResponse ( final HttpExchange httpExchange )
//	 {
//
//			String response = "{\"status\":\"404\"}\n";
//			try
//			{
//				 httpExchange.sendResponseHeaders ( 200, response.length ( ) );
//				 OutputStream os = httpExchange.getResponseBody ( );
//				 os.write ( response.getBytes ( ) );
//				 os.close ( );
//			}
//			catch ( IOException e )
//			{
//				 e.printStackTrace ( );
//			}
//	 }
//
//	 public static
//	 void sendMessage ( final String message,
//											final int responseCode,
//											final HttpExchange httpExchange )
//	 {
//
//			try
//			{
//				 String string = message + "\n";
//				 httpExchange.sendResponseHeaders ( responseCode, string.length ( ) );
//				 OutputStream os = httpExchange.getResponseBody ( );
//				 os.write ( string.getBytes ( ) );
//				 os.close ( );
//			}
//			catch ( IOException e )
//			{
//				 e.printStackTrace ( );
//			}
//	 }
//
//	 static
//	 HashMap< String, String > getHeaders ( Headers headers )
//	 {
//
//			HashMap< String, String > map = new HashMap<> ( );
//			for ( String k : headers.keySet ( ) )
//				 map.put ( k.toLowerCase ( ), headers.getFirst ( k ) );
//			return map;
//
//	 }
}
