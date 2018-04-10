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
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.web.server.rest.express.PathCall;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Behrooz Shahriari
 * @since 9/18/16
 */
public final
class Request
{
	 
	 private HttpExchange httpExchange;
	 
	 private Map< String, String > headers;
	 
	 private String bodyString;
	 
	 private JSONObject body;
	 
	 private Map< String, String > queryParams;
	 
	 private Map< String, String > params;
	 
	 private String requestMethod;
	 
	 private String remoteHost;
	 
	 private String remoteHostAddress;
	 
	 private PathCall apiCallPath;
	 
	 private String fullPath;
	 
	 public
	 Request ( HttpExchange httpExchange ,
						 PathCall apiCallPath )
	 {
			
			this.httpExchange = httpExchange;
			requestMethod = httpExchange.getRequestMethod ( );
			
			remoteHost = httpExchange.getRemoteAddress ( ).getHostString ( );
			remoteHostAddress = httpExchange.getRemoteAddress ( ).getAddress ( ).getHostAddress ( );
			this.apiCallPath = apiCallPath;
			
			HttpContext           httpContext = httpExchange.getHttpContext ( );
			Map< String, Object > attributes  = httpContext.getAttributes ( );
//			System.out.println ( "attributes: " + attributes.toString ( ) );
			
			try
			{
				 buildHeaders ( );
				 buildQueryParams ( );
				 buildParams ( );
				 buildBody ( );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 private
	 void buildParams ( )
	 {
			
			params = apiCallPath.getPathParams ( httpExchange.getRequestURI ( ).getPath ( ) );
	 }
	 
	 private
	 void buildBody ( )
	 {
			
			InputStream         inputStream         = httpExchange.getRequestBody ( );
			BufferedInputStream bufferedInputStream = new BufferedInputStream ( inputStream );
			StringBuilder       stringBuilder       = new StringBuilder ( );
			byte[]              contents            = new byte[ 1024 * 8 ];
			
			int bytesRead;
			try
			{
				 while ( ( bytesRead = bufferedInputStream.read ( contents ) ) != -1 )
				 {
						String x = new String ( contents , 0 , bytesRead );
						stringBuilder.append ( x );
				 }
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
			String bodyS = stringBuilder.toString ( );
			bodyS = Utils.convertToUTF8 ( bodyS );
			if ( !bodyS.isEmpty ( ) )
			{
				 try
				 {
						this.body = fixJSONObjectFieldNames ( new JSONObject ( bodyS ) );
				 }
				 catch ( JSONException e )
				 {
						this.body = new JSONObject ( );
						this.body.putOpt ( "invalid_formatted_body" , bodyS );
				 }
			}
			try
			{
				 bufferedInputStream.close ( );
			}
			catch ( IOException ignored )
			{
			}
			try
			{
				 inputStream.close ( );
			}
			catch ( IOException ignored )
			{
			}
			bodyString = bodyS;
	 }
	 
	 private
	 JSONObject fixJSONObjectFieldNames ( JSONObject object )
					 throws
					 JSONException
	 {
			
			if ( object == null ) return object;
			List< String > keys = object.keys ( );
			for ( String k : keys )
			{
				 Object o = object.remove ( k );
				 k = k.replace ( "“" , "" );
				 k = k.replace ( "”" , "" );
				 if ( o.getClass ( ).equals ( JSONObject.class ) )
				 {
						o = fixJSONObjectFieldNames ( ( JSONObject ) o );
						
				 }
				 if ( o.getClass ( ).equals ( JSONArray.class ) )
				 {
						o = fixJSONArray ( ( JSONArray ) o );
				 }
				 object.put ( k , o );
			}
			return object;
	 }
	 
	 private
	 JSONArray fixJSONArray ( JSONArray jsonArray )
					 throws
					 JSONException
	 {
			
			JSONArray array = new JSONArray ( );
			for ( int i = 0 ; i < jsonArray.length ( ) ; ++i )
			{
				 Object o = jsonArray.get ( i );
				 if ( o.getClass ( ).equals ( JSONObject.class ) )
				 {
						o = fixJSONObjectFieldNames ( ( JSONObject ) o );
				 }
				 if ( o.getClass ( ).equals ( JSONArray.class ) )
				 {
						o = fixJSONArray ( ( JSONArray ) o );
				 }
				 array.put ( o );
			}
			return array;
	 }
	 
	 public
	 JSONObject body ( )
	 {
			
			if ( body == null ) body = new JSONObject ( );
			return body;
	 }
	 
	 public
	 String getBodyString ( )
	 {
			
			return bodyString;
	 }
	 
	 private
	 void buildHeaders ( )
	 {
			
			headers = new HashMap<> ( );
			Headers headers_ = httpExchange.getRequestHeaders ( );
			for ( String key : headers_.keySet ( ) )
			{
				 if ( key != null )
				 {
						headers.put ( key.toLowerCase ( ).trim ( ) , Utils.convertToUTF8 ( headers_.get ( key ).get ( 0 ).trim ( ) ) );
				 }
			}
			if ( !headers.containsKey ( "remote_host_ip" ) )
			{
				 if ( headers.containsKey ( "x-forwarded-for" ) ) headers.put ( "remote_host_ip" , headers.get ( "x-forwarded-for" ) );
				 else headers.put ( "remote_host_ip" , httpExchange.getRemoteAddress ( ).getAddress ( ).getHostAddress ( ) );
			}
			headers.put ( "remote_host_port" , "" + httpExchange.getRemoteAddress ( ).getPort ( ) );
	 }
	 
	 public
	 String header ( String key )
	 {
			
			return headers.get ( key.toLowerCase ( ) );
	 }
	 
	 public
	 JSONObject headers ( )
	 {
			
			return new JSONObject ( headers );
	 }
	 
	 public static
	 boolean validHeader ( String value )
	 {
			
			return value != null && !value.isEmpty ( );
	 }
	 
	 private
	 void buildQueryParams ( )
	 {
			
			queryParams = new HashMap<> ( );
			URI uri = httpExchange.getRequestURI ( );
			fullPath = uri.getPath ( );
			String query = uri.getQuery ( );
			if ( query != null && !query.isEmpty ( ) )
			{
				 
				 String parts[] = query.split ( "&" );
				 for ( String p : parts )
				 {
						int i = p.indexOf ( '=' );
						queryParams.put ( p.substring ( 0 , i ) , p.substring ( i + 1 ) );
				 }
//				 System.out.println ( uri.getRawQuery () + "  " + queryParams );
			}
	 }
	 
	 public
	 String queryParam ( String key )
	 {
			
			return queryParams.get ( key.toLowerCase ( ) );
	 }
	 
	 public
	 JSONObject queryParams ( )
	 {
			
			return new JSONObject ( queryParams );
	 }
	 
	 public
	 boolean validQueryParams ( Response.APICallResult apiCallResult ,
															String... names )
	 {
			
			for ( String n : names )
			{
				 if ( !queryParams.containsKey ( n ) )
				 {
						apiCallResult.getResult ( ).putOpt ( "error" , "Invalid query" );
						apiCallResult.setStatusCode ( Response.STATUS_CODE_BAD_REQUEST );
						return false;
				 }
			}
			return true;
	 }
	 
	 public
	 JSONObject params ( )
	 {
			
			return new JSONObject ( params );
	 }
	 
	 public
	 String param ( String key )
	 {
			
			return params.get ( key.toLowerCase ( ) );
	 }
	 
	 public
	 boolean validHeaders ( String... names )
	 {
			
			for ( String n : names )
			{
				 if ( !validHeader ( header ( n ) ) ) return false;
			}
			return true;
	 }
	 
	 public
	 boolean validHeaders ( Response.APICallResult apiCallResult ,
													String... names )
	 {
			
			for ( String n : names )
			{
				 if ( !validHeader ( header ( n ) ) )
				 {
						apiCallResult.getResult ( ).putOpt ( "error" , "Invalid headers" );
						apiCallResult.setStatusCode ( Response.STATUS_CODE_BAD_REQUEST );
						return false;
				 }
			}
			return true;
	 }
	 
	 public
	 boolean validBody ( Response.APICallResult apiCallResult ,
											 String... flattenKeys )
	 {
			
			if ( flattenKeys == null )
			{
				 apiCallResult.setStatusCode ( Response.STATUS_CODE_BAD_REQUEST );
				 apiCallResult.getResult ( ).putOpt ( "error" , "Invalid body" );
				 return false;
			}
			for ( String fk : flattenKeys )
			{
				 if ( !body.has ( fk ) )
				 {
						apiCallResult.setStatusCode ( Response.STATUS_CODE_BAD_REQUEST );
						apiCallResult.getResult ( ).putOpt ( "error" , "Invalid body" );
						return false;
				 }
				 else
				 {
						if ( body.opt ( fk ) == null )
						{
							 apiCallResult.setStatusCode ( Response.STATUS_CODE_BAD_REQUEST );
							 apiCallResult.getResult ( ).putOpt ( "error" , "Invalid body" );
						}
				 }
			}
			return true;
	 }
}
