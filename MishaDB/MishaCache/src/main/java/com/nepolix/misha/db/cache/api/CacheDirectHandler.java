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

package com.nepolix.misha.db.cache.api;

import com.nepolix.misha.commons.Base64;
import com.nepolix.misha.db.cache.mem.MemCache;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;
import com.nepolix.misha.socket.server.ISocketServerExchange;

import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Behrooz Shahriari
 * @since 7/3/17
 */
public
class CacheDirectHandler
				implements ISocketServerExchange
{
	 
	 private MemCache memCache;
	 
	 public
	 CacheDirectHandler ( )
	 {
			
			memCache = new MemCache ( );
	 }
	 
	 @Override
	 public
	 String handler ( String message ,
										InetAddress socketInetAddress )
	 {
			
			if ( message != null )
			{
				 JSONObject request            = MJSON.toJSON ( message );
				 String     method             = request.optString ( "METHOD" );
				 String     key                = request.optString ( "KEY" );
				 String     archiveName        = request.optString ( "ARCHIVE_NAME" );
				 long       expirationDuration = request.optLong ( "EXPIRATION_DURATION" , 0 );
				 if ( method.equals ( "SAVE" ) )
				 {
						String value = request.optString ( "VALUE" );
//						System.out.println ( "$LOG SAVE>  KEY=" + key + " Arc=" + archiveName + " V=" + value );
						value = Base64.toBase64 ( value.getBytes ( ) );
						memCache.save ( archiveName , key , value , expirationDuration );
						return "OK";
				 }
				 if ( method.equals ( "INSERT" ) )
				 {
//						System.out.println ( "$LOG INSERT>" );
						List< String > values = buildValues ( request.optJSONArray ( "VALUES" ) );
						memCache.insert ( archiveName , key , values , expirationDuration );
						return "OK";
				 }
				 if ( method.equals ( "FETCH" ) )
				 {
						JSONArray      array = new JSONArray ( );
						int            limit = request.optInt ( "LIMIT" );
						List< String > list  = memCache.fetch ( archiveName , key );
						if ( list != null )
						{
							 for ( int i = 0 ; i < limit && i < list.size ( ) ; ++i )
							 {
									array.put ( new String ( Base64.decodeBase64 ( list.get ( i ) ) ) );
							 }
						}
						JSONObject response = new JSONObject ( );
						response.putOpt ( "array" , array );
						return response.toString ( );
				 }
				 if ( method.equals ( "DELETE" ) )
				 {
						memCache.delete ( archiveName , key );
						return "OK";
				 }
				 if ( method.equals ( "DELETE-VALUES" ) )
				 {
						List< String > values = buildValues ( request.optJSONArray ( "VALUES" ) );
						memCache.delete ( archiveName , key , values );
						return "OK";
				 }
				 if ( method.equals ( "CLEAN-SLATE" ) )
				 {
						memCache.cleanSlate ( );
						return "OK";
				 }
				 if ( method.equals ( "RESTART" ) )
				 {
						return restart ( );
				 }
				 if ( method.equals ( "HEALTH" ) )
				 {
						return "green";
				 }
			}
			return "invalid request";
	 }
	 
	 private
	 String restart ( )
	 {
			
			try
			{
				 String home = "/home/ubuntu/";
				 Runtime.getRuntime ( ).exec ( "rm " + home + "rc.local.log" );
				 File       file       = new File ( home + "restart.sh" );
				 FileWriter fileWriter = new FileWriter ( file );
				 fileWriter.write ( "#!/bin/sh\nsh " + home + "cache-rc.sh >> " + home + "rc.local.log 2>&1 &\n" );
				 fileWriter.close ( );
				 Runtime.getRuntime ( ).exec ( "chmod +x " + home + "restart.sh" );
				 Runtime.getRuntime ( ).exec ( home + "restart.sh" );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
				 return JSONException.exceptionToJSON ( e ).toString ( );
			}
			return "restarting";
	 }
	 
	 private
	 List< String > buildValues ( JSONArray array )
	 {
			
			List< String > values = new ArrayList<> ( );
			if ( array != null )
			{
				 for ( int i = 0 ; i < array.length ( ) ; ++i )
				 {
						String v = array.optString ( i );
						if ( v != null ) values.add ( Base64.toBase64 ( v.getBytes ( ) ) );
				 }
			}
			return values;
	 }
}
