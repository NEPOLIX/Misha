/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to HEX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.nepolix.misha.rest.client;

import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.handler.core.engine.TaskListener;
import com.nepolix.misha.task.handler.core.task.Task;
import com.nepolix.misha.task.handler.core.task.callback.Callback;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Behrooz Shahriari
 * @since 10/10/16
 */
public final
class WebClient
{
	 
	 static
	 {
			
			// Initialize configuration
			String tmp  = System.getProperty ( "user.home" );//  "/tmp"
			String path = tmp + File.separator + ".misha" + File.separator + "cert" + File.separator + "misha.jks";
//			System.setProperty ( "javax.net.ssl.trustStore", path );
//			System.setProperty ( "javax.net.ssl.trustStoreType", "jks" );
			//for localhost testing only
			javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier ( new javax.net.ssl.HostnameVerifier ( )
			{
				 
				 public
				 boolean verify ( String hostname ,
													javax.net.ssl.SSLSession sslSession )
				 {
						
						if ( hostname.equals ( "localhost" ) )
						{
							 return false;
						}
						return true;
				 }
			} );
	 }
	 
	 
	 public
	 enum RESTMethod
	 {
			GET ( "GET" ),
			POST ( "POST" ),
			PUT ( "PUT" ),
			DELETE ( "DELETE" );
			
			private String name;
			
			RESTMethod ( String name )
			{
				 
				 this.name = name;
			}
			
			@Override
			public
			String toString ( )
			{
				 
				 return name;
			}
	 }
	 
	 private final static String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36 [NEPOLIX-MISHA]";
	 
	 private static WebClient WEB_CLIENT = null;
	 
	 private static ITaskEngine TASK_RUNNER;
	 
	 private
	 WebClient ( )
	 {
			
			System.out.println ( "init WebClient" );
			TASK_RUNNER = ITaskEngine.buildTaskEngine ( true );
	 }
	 
	 private
	 WebClient ( ITaskEngine iTaskEngine )
	 {
			
			TASK_RUNNER = iTaskEngine;
	 }
	 
	 public static
	 WebClient getInstance ( )
	 {
			
			if ( WEB_CLIENT == null ) WEB_CLIENT = new WebClient ( );
			return WEB_CLIENT;
	 }
	 
	 public static
	 WebClient getInstance ( ITaskEngine iTaskEngine )
	 {
			
			if ( WEB_CLIENT == null ) WEB_CLIENT = new WebClient ( iTaskEngine );
			return WEB_CLIENT;
	 }
	 
	 public
	 void cUrl ( String url ,
							 RESTMethod method ,
							 Callback< JSONObject > callback ,
							 CUrlParameter... cUrlArgs )
	 {
			
			TASK_RUNNER.add ( new Task ( )
			{
				 
				 @Override
				 public
				 void execute ( ITaskEngine iTaskEngine ,
												TaskListener listener )
				 {
						
						JSONObject result = cUrl ( url , method , cUrlArgs );
						if ( callback != null )
						{
							 callback.onResult ( result );
						}
						listener.setResult ( result );
						listener.finish ( );
				 }
			} );
	 }
	 
	 public
	 String cUrl_ ( String url ,
									RESTMethod method ,
									CUrlParameter... cUrlArgs )
	 {
			
			String args[] = new String[ ( cUrlArgs != null ? 2 * cUrlArgs.length : 0 ) + 5 ];
			args[ 0 ] = "curl";
			args[ 1 ] = "-s";
			args[ 2 ] = "-X";
			args[ 3 ] = method.toString ( );
			args[ 4 ] = url;
			if ( cUrlArgs != null )
			{
				 for ( int i = 0 ; i < cUrlArgs.length ; i++ )
				 {
						args[ 5 + 2 * i ] = cUrlArgs[ i ].getFlag ( );
						args[ 5 + ( 2 * i + 1 ) ] = cUrlArgs[ i ].getParameter ( );
				 }
			}
			ProcessBuilder p = new ProcessBuilder ( args );
			p.redirectErrorStream ( true );
			String fv = null;
			try
			{
				 final Process shell       = p.start ( );
				 InputStream   errorStream = shell.getErrorStream ( );
				 InputStream   shellIn     = shell.getInputStream ( );
				 Scanner       scanner     = new Scanner ( shellIn );
				 StringBuilder builder     = new StringBuilder ( );
				 while ( scanner.hasNext ( ) )
				 {
						builder.append ( scanner.nextLine ( ) );
						if ( scanner.hasNext ( ) ) builder.append ( "\n" );
				 }
				 scanner.close ( );
				 fv = builder.toString ( );
				 return fv;
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
				 System.err.println ( "response value='" + fv + "'" );
				 return JSONException.exceptionToJSON ( e ).toString ( );
			}
	 }
	 
	 public
	 JSONObject cUrl ( String url ,
										 RESTMethod method ,
										 CUrlParameter... cUrlArgs )
	 {
			
			try
			{
				 String        lines   = cUrl_ ( url , method , cUrlArgs );
				 Scanner       scanner = new Scanner ( lines );
				 StringBuilder builder = new StringBuilder ( );
				 while ( scanner.hasNext ( ) )
				 {
						String line = scanner.nextLine ( );
						if ( line.startsWith ( "{" ) )
						{
							 builder.append ( line );
							 break;
						}
				 }
				 while ( scanner.hasNext ( ) )
				 {
						builder.append ( scanner.nextLine ( ) );
				 }
				 scanner.close ( );
				 String fv = builder.toString ( );
				 return new JSONObject ( fv );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
				 return JSONException.exceptionToJSON ( e );
			}
	 }
	 
	 public
	 WebClient call ( RESTMethod method ,
										String url ,
										HashMap< String, String > urlParams ,
										HashMap< String, String > headers ,
										JSONObject body ,
										Callback< JSONObject > callback )
	 {
			
			
			boolean https   = url.contains ( "https" );
			String  _method = "";
			if ( method == RESTMethod.GET ) _method = "GET";
			if ( method == RESTMethod.POST ) _method = "POST";
			if ( method == RESTMethod.PUT ) _method = "PUT";
			if ( method == RESTMethod.DELETE ) _method = "DELETE";
			
			final String final_method = _method;
			final String finalUrl     = buildURL ( url , urlParams );
			if ( https ) TASK_RUNNER.add ( getSecureRESTTask ( headers , body , final_method , finalUrl , callback ) );
			else TASK_RUNNER.add ( getRESTTask ( headers , body , final_method , finalUrl , callback ) );
			return this;
	 }
	 
	 public static
	 void callNoCallback ( RESTMethod method ,
												 String url ,
												 HashMap< String, String > urlParams ,
												 HashMap< String, String > headers ,
												 JSONObject body )
	 {
			
			boolean https   = url.contains ( "https" );
			String  _method = "";
			if ( method == RESTMethod.GET ) _method = "GET";
			if ( method == RESTMethod.POST ) _method = "POST";
			if ( method == RESTMethod.PUT ) _method = "PUT";
			if ( method == RESTMethod.DELETE ) _method = "DELETE";
			
			final String final_method = _method;
			final String finalUrl     = buildURL ( url , urlParams );
			if ( https ) TASK_RUNNER.add ( getSecureRESTTask ( headers , body , final_method , finalUrl , null ) );
			else TASK_RUNNER.add ( getRESTTask ( headers , body , final_method , finalUrl , null ) );
	 }
	 
	 public static
	 JSONObject call ( RESTMethod method ,
										 String url ,
										 HashMap< String, String > urlParams ,
										 HashMap< String, String > headers ,
										 JSONObject body )
	 {
			
			BlockingQueue< JSONObject > resultObject = new ArrayBlockingQueue<> ( 1 );
			
			boolean https   = url.contains ( "https" );
			String  _method = "";
			if ( method == RESTMethod.GET ) _method = "GET";
			if ( method == RESTMethod.POST ) _method = "POST";
			if ( method == RESTMethod.PUT ) _method = "PUT";
			if ( method == RESTMethod.DELETE ) _method = "DELETE";
			
			final String final_method = _method;
			final String finalUrl     = buildURL ( url , urlParams );
			
			Callback< JSONObject > callback = new Callback< JSONObject > ( )
			{
				 
				 @Override
				 public
				 void onResult ( JSONObject result )
				 {
						
						resultObject.offer ( result );
				 }
				 
				 @Override
				 public
				 void onError ( JSONObject e )
				 {
						
						resultObject.offer ( e );
				 }
			};
			if ( https ) TASK_RUNNER.add ( getSecureRESTTask ( headers , body , final_method , finalUrl , callback ) );
			else TASK_RUNNER.add ( getRESTTask ( headers , body , final_method , finalUrl , callback ) );
			try
			{
				 return resultObject.take ( );
			}
			catch ( InterruptedException e )
			{
				 e.printStackTrace ( );
			}
			return new JSONObject ( );
	 }
	 
	 private static
	 String buildURL ( String url ,
										 HashMap< String, String > urlParams )
	 {
			
			if ( url == null || url.isEmpty ( ) ) throw new NullPointerException ( "url can't be null" );
			if ( urlParams != null && !urlParams.isEmpty ( ) )
			{
				 StringBuffer builder = new StringBuffer ( url );
				 builder.append ( "?" );
				 for ( String key : urlParams.keySet ( ) )
				 {
						builder.append ( key );
						builder.append ( "=" );
						builder.append ( urlParams.get ( key ) );
						builder.append ( "&" );
				 }
				 builder.deleteCharAt ( builder.length ( ) - 1 );
				 url = builder.toString ( );
				 url = url.replaceAll ( " " , "%20" );
			}
			return url;
	 }
	 
	 private static
	 RESTTask getRESTTask ( final HashMap< String, String > headers ,
													final JSONObject body ,
													final String final_method ,
													final String finalUrl ,
													final Callback< JSONObject > callback )
	 {
			
			RESTTask task = new RESTTask ( callback )
			{
				 
				 @Override
				 protected
				 void callBackExecute ( ITaskEngine iTaskRunner ,
																TaskListener listener )
				 {
						
						JSONObject result = new JSONObject ( );
						try
						{
							 
							 URL               url        = new URL ( finalUrl );
							 HttpURLConnection connection = ( HttpURLConnection ) url.openConnection ( );
							 connection.setRequestMethod ( final_method );
							 connection.setRequestProperty ( "User-Agent" , USER_AGENT );
							 connection.setRequestProperty ( "Accept-Language" , "en-US,en;q=0.5" );
							 connection.setConnectTimeout ( 25000 );
							 if ( headers != null && !headers.isEmpty ( ) )
							 {
									for ( String key : headers.keySet ( ) )
										 connection.setRequestProperty ( key , headers.get ( key ) );
							 }
							 System.out.println ( "<x>\t" + finalUrl + "   " + final_method );
							 writeBody ( body , final_method , connection );
							 int         responseCode = connection.getResponseCode ( );
							 InputStream inputStream  = null;
							 try
							 {
									inputStream = connection.getInputStream ( );
							 }
							 catch ( Exception e )
							 {
							 }
							 if ( inputStream == null ) inputStream = connection.getErrorStream ( );
							 BufferedReader in       = new BufferedReader ( new InputStreamReader ( inputStream ) );
							 String         inputLine;
							 StringBuffer   response = new StringBuffer ( );
							 while ( ( inputLine = in.readLine ( ) ) != null ) response.append ( inputLine );
							 in.close ( );
							 result = new JSONObject ( response.toString ( ) );
						}
						catch ( Exception e )
						{
//							 e.printStackTrace ( );
							 result.putOpt ( "fatal" , "0x0FAC" );
//							 result.putOpt ( "error", e.getMessage ( ) );
						}
						finally
						{
							 listener.setResult ( result );
							 listener.finish ( );
						}
				 }
			};
			return task;
	 }
	 
	 private static
	 void writeBody ( JSONObject body ,
										String final_method ,
										HttpURLConnection connection )
					 throws
					 IOException
	 {
			
			if ( body != null && !final_method.equals ( "GET" ) )
			{
				 connection.setDoOutput ( true );
				 DataOutputStream wr     = new DataOutputStream ( connection.getOutputStream ( ) );
				 BufferedWriter   writer = new BufferedWriter ( new OutputStreamWriter ( wr , "UTF-8" ) );
				 writer.write ( body.toString ( ) );
				 writer.close ( );
				 wr.close ( );
			}
	 }
	 
	 private static
	 RESTTask getSecureRESTTask ( final HashMap< String, String > headers ,
																final JSONObject body ,
																final String final_method ,
																final String finalUrl ,
																final Callback< JSONObject > callback )
	 {
			
			RESTTask task = new RESTTask ( callback )
			{
				 
				 @Override
				 protected
				 void callBackExecute ( ITaskEngine iTaskRunner ,
																TaskListener listener )
				 {
						
						JSONObject result = new JSONObject ( );
						try
						{
							 URL                url        = new URL ( finalUrl );
							 HttpsURLConnection connection = ( HttpsURLConnection ) url.openConnection ( );
							 connection.setRequestMethod ( final_method );
							 connection.setRequestProperty ( "User-Agent" , USER_AGENT );
							 connection.setRequestProperty ( "Accept-Language" , "en-US,en;q=0.5" );
							 connection.setConnectTimeout ( 25000 );
							 if ( headers != null && !headers.isEmpty ( ) )
							 {
									for ( String key : headers.keySet ( ) )
										 connection.setRequestProperty ( key , headers.get ( key ) );
							 }
							 writeBody ( body , final_method , connection );
							 int         responseCode = connection.getResponseCode ( );
							 InputStream inputStream;
							 if ( connection.getResponseCode ( ) >= 400 ) inputStream = connection.getErrorStream ( );
							 else inputStream = connection.getInputStream ( );
							 BufferedReader in       = new BufferedReader ( new InputStreamReader ( inputStream ) );
							 String         inputLine;
							 StringBuffer   response = new StringBuffer ( );
							 while ( ( inputLine = in.readLine ( ) ) != null ) response.append ( inputLine );
							 in.close ( );
							 result = new JSONObject ( response.toString ( ) );
						}
						catch ( Exception e )
						{
							 result.putOpt ( "fatal" , "0x0FAC" );
						}
						finally
						{
							 listener.setResult ( result );
							 listener.finish ( );
						}
				 }
			};
			return task;
	 }
	 
	 public
	 void terminate ( )
	 {
			
			TASK_RUNNER.stop ( );
	 }
	 
	 public synchronized
	 void cancel ( )
	 {
			
			synchronized ( this )
			{
				 TASK_RUNNER.clearTasks ( );
			}
	 }


//	 public
//	 void restart ( )
//	 {
//
//			TASK_RUNNER = ITaskEngine.buildTaskEngine ( -1 );
//	 }
	 
	 public
	 ITaskEngine getTaskRunner ( )
	 {
			
			return TASK_RUNNER;
	 }
}
