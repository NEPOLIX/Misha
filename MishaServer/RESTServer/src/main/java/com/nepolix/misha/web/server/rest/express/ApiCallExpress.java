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

package com.nepolix.misha.web.server.rest.express;

import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.handler.core.engine.TaskListener;
import com.nepolix.misha.task.handler.core.task.callback.Callback;
import com.nepolix.misha.web.server.Server;
import com.nepolix.misha.web.server.rest.Request;
import com.nepolix.misha.web.server.rest.Response;
import com.nepolix.misha.web.server.rest.calls.*;
import com.nepolix.misha.web.utils.Pair;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.nepolix.misha.logger.log.LogTag.ERROR;
import static com.nepolix.misha.logger.log.LogTag.VERBOSE;

/**
 * @author Behrooz Shahriari
 * @since 9/18/16
 */
public abstract
class ApiCallExpress
				implements HttpHandler
{
	 
	 
	 private String basePath;
	 
	 private SortedSet< PathCall > pathCalls;
	 
	 
	 private boolean redirectToHTTPS;
	 
	 public
	 ApiCallExpress ( String basePath )
	 {
			
			this.basePath = basePath;
			pathCalls = new TreeSet<> ( );
			String apiVersion = getAPIVersion ( );
			if ( apiVersion == null ) apiVersion = "";
			if ( !apiVersion.isEmpty ( ) && !apiVersion.startsWith ( "/" ) ) apiVersion = "/" + apiVersion;
			this.basePath = apiVersion + this.basePath;
			if ( this.basePath.length ( ) > 1 && this.basePath.charAt ( this.basePath.length ( ) - 1 ) == '/' ) this.basePath = this.basePath.substring ( 0 , this.basePath.length ( ) - 1 );
			initContext ( );
			System.out.println ( "\nAPI Calls:" );
			for ( PathCall pathCall : pathCalls )
				 System.out.println ( pathCall.getCallType ( ) + "  " + pathCall.getPath ( ) );
	 }
	 
	 public
	 void setRedirectToHTTPS ( boolean redirectToHTTPS )
	 {
			
			this.redirectToHTTPS = redirectToHTTPS;
	 }
	 
	 /**
		* where you add your apis by calling addGet, addPost, ...
		*/
	 protected abstract
	 void initContext ( );
	 
	 /**
		* @return must starts with '/' example: '/v1.0' OR '' (empty)
		*/
	 protected abstract
	 String getAPIVersion ( );
	 
	 public
	 String getBasePath ( )
	 {
			
			return basePath;
	 }
	 
	 protected
	 ApiCallExpress addAPIs ( ApiCall... apiCalls )
	 {
			
			if ( apiCalls != null )
			{
				 for ( ApiCall ac : apiCalls ) addAPI ( ac );
			}
			return this;
	 }
	 
	 protected
	 ApiCallExpress addAPI ( ApiCall apiCall )
	 {
			
			if ( apiCall instanceof GetCall ) return addGet ( ( GetCall ) apiCall );
			if ( apiCall instanceof PostCall ) return addPost ( ( PostCall ) apiCall );
			if ( apiCall instanceof PutCall ) return addPut ( ( PutCall ) apiCall );
			if ( apiCall instanceof DeleteCall ) return addDelete ( ( DeleteCall ) apiCall );
			return this;
	 }
	 
	 private
	 ApiCallExpress addGet ( GetCall getCall )
	 {
			
			String x = getCall.callPath ( ) == null ? "" : getCall.callPath ( );
			pathCalls.add ( new PathCall ( basePath + x , CallType.GET , getCall ) );
			return this;
	 }
	 
	 private
	 ApiCallExpress addPost ( PostCall postCall )
	 {
			
			String x = postCall.callPath ( ) == null ? "" : postCall.callPath ( );
			pathCalls.add ( new PathCall ( basePath + x , CallType.POST , postCall ) );
			return this;
	 }
	 
	 private
	 ApiCallExpress addPut ( PutCall putCall )
	 {
			
			String x = putCall.callPath ( ) == null ? "" : putCall.callPath ( );
			pathCalls.add ( new PathCall ( basePath + x , CallType.PUT , putCall ) );
			return this;
	 }
	 
	 private
	 ApiCallExpress addDelete ( DeleteCall deleteCall )
	 {
			
			String x = deleteCall.callPath ( ) == null ? "" : deleteCall.callPath ( );
			pathCalls.add ( new PathCall ( basePath + x , CallType.DELETE , deleteCall ) );
			return this;
	 }
	 
	 @Override
	 public
	 void handle ( HttpExchange httpExchange )
					 throws
					 IOException
	 {

//			System.out.println ( "Call Method=" + httpExchange.getRequestMethod ( ) );
			Pair< ApiCall, PathCall > pair       = getApiCall ( httpExchange.getRequestURI ( ).getPath ( ) , CallType.getCallType ( httpExchange.getRequestMethod ( ) ) );
			JSONObject                badRequest = new JSONObject ( );
			
			final Response response = getResponse ( httpExchange );
			if ( pair == null || pair.getE1 ( ) == null )
			{
				 badRequest.putOpt ( "error" , "there is no api for the request" );
				 response.sendResponse ( Response.STATUS_CODE_NOT_FOUND , badRequest );
			}
			else
			{
				 ApiCall apiCall = pair.getE1 ( );
				 Request request = getRequest ( httpExchange , pair.getE2 ( ) );
				 
				 Headers headers   = httpExchange.getRequestHeaders ( );
				 String  path      = headers.getFirst ( "Host" );
				 String  protocol  = headers.getFirst ( "X-forwarded-proto" );
				 String  userAgent = headers.getFirst ( "user-agent" );
				 if ( !userAgent.startsWith ( "ELB-HealthChecker" ) )
				 {
						JSONObject logJSON = new JSONObject ( );
						logJSON.putOpt ( "protocol" , ( ( protocol == null ) ? "http" : protocol ) ).putOpt ( "path" , path ).putOpt ( "request-uri" , httpExchange.getRequestURI ( ) )
									 .putOpt ( "request-method" , httpExchange.getRequestMethod ( ) ).putOpt ( "request-header" , request.headers ( ) ).putOpt ( "request-body" , request.body ( ) )
									 .putOpt ( "request-body-size-B" , request.getBodyString ( ).length ( ) ).putOpt ( "request-body-size-KB" , request.getBodyString ( ).length ( ) / 1024 );
						response.getLog ( ).$addLogData ( VERBOSE , null , logJSON );
				 }
				 if ( redirectToHTTPS )
				 {
						
						if ( protocol != null && !protocol.equalsIgnoreCase ( "https" ) )
						{
							 String uriPath = "https://" + path + httpExchange.getRequestURI ( );
							 System.out.println ( "HTTPS REDIRECT=" + uriPath );
							 try
							 {
									response.sendRedirectAPI ( new URI ( uriPath ) );
							 }
							 catch ( URISyntaxException e )
							 {
									e.printStackTrace ( );
							 }
							 return;
						}
				 }
				 final Callback< JSONObject > callback = new Callback< JSONObject > ( )
				 {
						
						@Override
						public
						void onResult ( JSONObject result )
						{
							 
							 response.sendMessage ( ( response.getResponseCode ( ) == null ? Response.STATUS_CODE_OK : response.getResponseCode ( ) ) , result );
						}
						
						@Override
						public
						void onError ( JSONObject e )
						{
							 
							 try
							 {
									e.put ( "error_status" , response.getResponseCode ( ) == null ? Response.STATUS_CODE_SERVER_ERROR : response.getResponseCode ( ) );
									response.sendMessage ( Response.STATUS_CODE_SERVER_ERROR , e );
							 }
							 catch ( Exception er )
							 {
									er.printStackTrace ( );
									System.err.println ( "response=" + response + "  e=" + e + "  code" + response.getResponseCode ( ) );
							 }
						}
				 };
				 
				 try
				 {
						boolean getBool    = httpExchange.getRequestMethod ( ).equalsIgnoreCase ( "GET" ) && apiCall.getType ( ).equalsIgnoreCase ( "GET" );
						boolean postBool   = httpExchange.getRequestMethod ( ).equalsIgnoreCase ( "POST" ) && apiCall.getType ( ).equalsIgnoreCase ( "POST" );
						boolean putBool    = httpExchange.getRequestMethod ( ).equalsIgnoreCase ( "PUT" ) && apiCall.getType ( ).equalsIgnoreCase ( "PUT" );
						boolean deleteBool = httpExchange.getRequestMethod ( ).equalsIgnoreCase ( "DELETE" ) && apiCall.getType ( ).equalsIgnoreCase ( "DELETE" );
						if ( getBool || postBool || putBool || deleteBool ) apiCall ( request , response , apiCall , callback );
						else
						{
							 badRequest.put ( "reason" , "not a valid REST call method" );
							 badRequest.put ( "REST request call type" , httpExchange.getRequestMethod ( ) );
							 badRequest.put ( "API original call type" , "" + apiCall.getType ( ) );
							 badRequest.put ( "URL Path" , apiCall.callPath ( ) );
							 System.out.println ( MJSON.toString ( badRequest ) );
							 response.send404 ( badRequest );
						}
				 }
				 catch ( JSONException e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
	 
	 private
	 Response getResponse ( HttpExchange httpExchange )
	 {
			
			return new Response ( httpExchange );
	 }
	 
	 private
	 Request getRequest ( final HttpExchange httpExchange ,
												PathCall apiCallPath )
	 {
			
			return new Request ( httpExchange , apiCallPath );
	 }
	 
	 private
	 void apiCall ( Request request ,
									final Response response ,
									ApiCall apiCall ,
									Callback< JSONObject > callback )
	 {
			
			ApiCallTask apiCallTask = new ApiCallTask ( request , callback )
			{
				 
				 @Override
				 protected
				 void callBackExecute ( ITaskEngine iTaskRunner ,
																final TaskListener listener )
				 {
						
						response.setListener ( listener );
						try
						{
							 apiCall.apiCall ( request , response );
						}
						catch ( Exception e )
						{
							 JSONObject error = JSONException.exceptionToJSON ( e );
							 e.printStackTrace ( );
							 response.getLog ( ).$addLogData ( ERROR , null , error );
							 response.sendResponse ( Response.STATUS_CODE_SERVER_ERROR , error );
						}
//						return response.getResult ( );
				 }
			};
			Server.getTaskEngine ( ).add ( apiCallTask );
	 }
	 
	 private
	 Pair< ApiCall, PathCall > getApiCall ( final String path ,
																					final CallType requestMethod )
	 {
			
			for ( PathCall pathCall : pathCalls )
			{
				 if ( pathCall.equalPath ( path ) && pathCall.getCallType ( ).equals ( requestMethod ) )
				 {
						return new Pair<> ( pathCall.getApiCall ( ) , pathCall );
				 }
			}
			return null;
	 }
}
