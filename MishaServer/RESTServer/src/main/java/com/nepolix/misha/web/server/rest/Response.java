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

package com.nepolix.misha.web.server.rest;

import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.logger.client.Logger;
import com.nepolix.misha.logger.log.Log;
import com.nepolix.misha.logger.log.LogTag;
import com.nepolix.misha.task.handler.core.engine.TaskListener;
import com.nepolix.misha.web.server.Server;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * @author Behrooz Shahriari
 * @since 9/18/16
 */
public final
class Response
{
	 
	 private HttpExchange httpExchange;
	 
	 public final static int STATUS_CODE_OK = 200;
	 
	 public final static int STATUS_CODE_CREATED = 201;
	 
	 public final static int STATUS_CODE_ACCEPTED = 202;
	 
	 public final static int STATUS_CODE_NO_CONTENT = 204;
	 
	 public final static int STATUS_CODE_RESET_CONTENT = 205;
	 
	 public final static int STATUS_CODE_TEMPORARY_REDIRECT = 307;
	 
	 public final static int STATUS_CODE_BAD_REQUEST = 400;
	 
	 public final static int STATUS_CODE_UNAUTHORIZED = 401;
	 
	 public final static int STATUS_CODE_PAYMENT_REQUIRED = 402;
	 
	 public final static int STATUS_CODE_FORBIDDEN = 403;
	 
	 public final static int STATUS_CODE_NOT_FOUND = 404;
	 
	 public final static int STATUS_CODE_METHOD_NOT_ALLOWED = 405;
	 
	 public final static int STATUS_CODE_SERVER_ERROR = 500;
	 
	 private final static Latency LATENCY = new Latency ( );
	 
	 private Integer code;
	 
	 private JSONObject result;
	 
	 private TaskListener listener;
	 
	 private long timeCreated;
	 
	 private long timeResponded;
	 
	 private Log log;
	 
	 public
	 Response ( HttpExchange httpExchange )
	 {
			
			this.httpExchange = httpExchange;
			result = new JSONObject ( );
			timeCreated = Utils.getCurrentUTCTime ( );
	 }
	 
	 public
	 Log getLog ( )
	 {
			
			if ( log == null )
			{
				 log = Logger.getLogger ( ).buildLog ( "MISHA" , "LOG" , "API" );
			}
			return log;
	 }
	 
	 public
	 void sendMessage ( int statusCode ,
											String message ,
											boolean logB )
	 {
			
			byte byteArray[] = message.getBytes ( );
			try
			{
				 httpExchange.sendResponseHeaders ( statusCode , byteArray.length );
			}
			catch ( Exception ignored )
			{
			}
			OutputStream os = httpExchange.getResponseBody ( );
			try
			{
				 os.write ( byteArray );
				 os.close ( );
				 if ( logB )
				 {
						timeResponded = Utils.getCurrentUTCTime ( );
						//it will be null only if user-agent is ELB-HealthChecker
						if ( log != null )
						{
							 Server.getTaskEngine ( ).add ( ( ) -> {
									long       lTime       = timeResponded - timeCreated;
									JSONObject responseLog = new JSONObject ( );
									responseLog.putOpt ( "response" , message ).putOpt ( "response-size-B" , message.length ( ) ).putOpt ( "response-size-KB" , ( message.length ( ) / 1024 ) )
														 .putOpt ( "status-code" , statusCode ).putOpt ( "response-time-ms" , lTime ).putOpt ( "response-time-sec" , ( lTime / 1000 ) )
														 .putOpt ( "average-latency" , LATENCY.getAverageLatency ( lTime ) );
									log.$addLogData ( LogTag.VERBOSE , null , responseLog );
									try
									{
										 Logger.getLogger ( ).log ( log );
									}
									catch ( Exception e )
									{
										 System.out.println ( "WARNING: " + e.getMessage ( ) );
									}
							 } );
						}
				 }
			}
			catch ( Exception e )
			{
				 try
				 {
						os.close ( );
				 }
				 catch ( Exception ignored )
				 {
				 }
			}
			finally
			{
				 httpExchange.close ( );
			}
	 }
	 
	 public
	 void sendMessage ( int statusCode ,
											JSONObject json )
	 {
			
			String message = json.toString ( );
			timeResponded = Utils.getCurrentUTCTime ( );
			//it will be null only if user-agent is ELB-HealthChecker
			if ( log != null )
			{
				 Server.getTaskEngine ( ).add ( ( ) -> {
						long       lTime       = timeResponded - timeCreated;
						JSONObject responseLog = new JSONObject ( );
						responseLog.putOpt ( "response" , json ).putOpt ( "response-size-B" , message.length ( ) ).putOpt ( "response-size-KB" , ( message.length ( ) / 1024 ) )
											 .putOpt ( "status-code" , statusCode ).putOpt ( "response-time-ms" , lTime ).putOpt ( "response-time-sec" , ( lTime / 1000 ) )
											 .putOpt ( "average-latency" , LATENCY.getAverageLatency ( lTime ) );
						log.$addLogData ( LogTag.VERBOSE , null , responseLog );
						try
						{
							 Logger.getLogger ( ).log ( log );
						}
						catch ( Exception e )
						{
							 System.out.println ( "WARNING: " + e.getMessage ( ) );
						}
				 } );
			}
			sendMessage ( statusCode , message , false );
	 }
	 
	 public
	 void send404 ( JSONObject reason )
	 {
			
			if ( reason == null ) reason = new JSONObject ( );
			try
			{
				 reason.put ( "status" , "bad_request" );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
			}
			sendMessage ( STATUS_CODE_BAD_REQUEST , reason );
	 }
	 
	 public
	 Integer getResponseCode ( )
	 {
			
			return code;
	 }
	 
	 /**
		* https://github.com/googlegsa/library/blob/master/src/com/google/enterprise/adaptor/HttpExchanges.java
		*
		* @param uri
		*/
	 public
	 void sendRedirectAPI ( URI uri )
	 {
			
			String redirectUrl = uri.toString ( );
			httpExchange.getResponseHeaders ( ).set ( "Location" , redirectUrl );
			int code = HttpURLConnection.HTTP_SEE_OTHER;
			try
			{
				 respond ( httpExchange , code , null , null );
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
			result = new JSONObject ( );
			result.putOpt ( "redirect" , uri.toString ( ) );
			listener.setResult ( result );
			listener.finish ( );
	 }
	 
	 static
	 void startResponse ( HttpExchange ex ,
												int code ,
												String contentType ,
												boolean hasBody )
					 throws
					 IOException
	 {
			
			if ( contentType != null )
			{
				 ex.getResponseHeaders ( ).set ( "Content-Type" , contentType );
			}
			if ( !hasBody )
			{
				 // No body. Required for HEAD requests
				 ex.sendResponseHeaders ( code , -1 );
			}
			else
			{
				 // Chuncked encoding
				 ex.sendResponseHeaders ( code , 0 );
			}
	 }
	 
	 /**
		* Sends response to GSA. Should not be used directly if the request method is HEAD.
		*/
	 static
	 void respond ( HttpExchange ex ,
									int code ,
									String contentType ,
									byte response[] )
					 throws
					 IOException
	 {
			
			startResponse ( ex , code , contentType , response != null );
			if ( response != null )
			{
				 OutputStream responseBody = ex.getResponseBody ( );
				 responseBody.write ( response );
				 responseBody.flush ( );
				 // This shouldn't be needed, but without it one developer had trouble
				 responseBody.close ( );
			}
			ex.close ( );
	 }
	 
	 
	 public
	 void sendResponse ( Integer statusCode ,
											 JSONObject result )
	 {
			
			code = statusCode == null ? STATUS_CODE_OK : statusCode;
			if ( result == null )
			{
				 result = new JSONObject ( );
			}
			this.result = result;
			listener.setResult ( this.result );
			listener.finish ( );
//			try
//			{
//				 this.setResult.put ( "statusCode", code );
//			}
//			catch ( JSONException e )
//			{
//				 e.printStackTrace ( );
//			}
	 }
	 
	 public
	 void sendResponse ( APICallResult apiCallResult )
	 {
			
			if ( apiCallResult != null )
			{
				 Integer    statusCode = apiCallResult.getStatusCode ( );
				 JSONObject result     = apiCallResult.getResult ( );
				 code = statusCode == null ? STATUS_CODE_OK : statusCode;
				 if ( result == null )
				 {
						result = new JSONObject ( );
				 }
				 this.result = result;
			}
			listener.setResult ( this.result );
			listener.finish ( );
	 }
	 
	 public
	 JSONObject getResult ( )
	 {
			
			return result;
	 }
	 
	 public
	 void setListener ( TaskListener listener )
	 {
			
			this.listener = listener;
	 }
	 
	 public static
	 class APICallResult
	 {
			
			private Integer statusCode;
			
			private JSONObject result;
			
			public
			APICallResult ( Integer statusCode ,
											JSONObject result )
			{
				 
				 this.statusCode = statusCode;
				 this.result = result;
			}
			
			public
			APICallResult ( )
			{
				 
				 result = new JSONObject ( );
			}
			
			public
			Integer getStatusCode ( )
			{
				 
				 if ( statusCode == null ) statusCode = STATUS_CODE_ACCEPTED;
				 return statusCode;
			}
			
			public
			JSONObject getResult ( )
			{
				 
				 if ( result == null ) result = new JSONObject ( );
				 return result;
			}
			
			public
			void setStatusCode ( Integer statusCode )
			{
				 
				 this.statusCode = statusCode;
			}
			
			public
			void setResult ( JSONObject result )
			{
				 
				 this.result = result;
			}
	 }
	 
	 private static
	 class Latency
	 {
			
			private long count = 0;
			
			private long lTime = 0;
			
			long getAverageLatency ( long lTime )
			{
				 
				 this.lTime += lTime;
				 count++;
				 return this.lTime / count;
			}
	 }
}
