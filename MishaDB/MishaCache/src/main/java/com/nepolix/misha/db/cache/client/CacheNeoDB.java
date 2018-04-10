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

package com.nepolix.misha.db.cache.client;

import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.db.cache.server.CacheDBConstants;
import com.nepolix.misha.db.model.MModel;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;
import com.nepolix.misha.socket.SocketChannel;
import com.nepolix.misha.socket.SocketChannelPool;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

/**
 * @author Behrooz Shahriari
 * @since 5/24/17
 */
public
class CacheNeoDB
{
	 
	 private static CacheNeoDB CACHE_DB;
	 
	 private SocketChannelPool[] socketChannelPools;
	 
	 private
	 CacheNeoDB ( )
	 {
			
			socketChannelPools = new SocketChannelPool[ CacheDBConstants.getNeoCacheDBURL ( ).length ];
			for ( int i = 0 ; i < socketChannelPools.length ; ++i )
			{
				 socketChannelPools[ i ] = SocketChannelPool.build ( 2 , CacheDBConstants.getNeoCacheDBURL ( )[ i ] , CacheDBConstants.getDBCachePort ( ) );
			}
	 }
	 
	 public static
	 void initInetAddress ( String[][] clusterAddresses ,
													Integer regionIdx )
	 {
			
			CacheDBConstants.initCacheClusterAddresses ( clusterAddresses , regionIdx );
	 }
	 
	 public static
	 CacheNeoDB getInstance ( )
	 {
			
			try
			{
				 CacheDBConstants.getNeoCacheDBURL ( );
			}
			catch ( Exception e )
			{
				 throw new MissingResourceException ( "Please first call 'initInetAddress' to init the address of cluster nodes with region index" , CacheNeoDB.class.getSimpleName ( ) , "cluster address" );
			}
			if ( CACHE_DB == null ) CACHE_DB = new CacheNeoDB ( );
			return CACHE_DB;
	 }
	 
	 private
	 int getCacheIndex ( String key ,
											 String archiveName )
	 {
			
			return Math.abs ( ( key + archiveName ).hashCode ( ) % socketChannelPools.length );
	 }
	 
	 public
	 CacheNeoDB insert ( String key ,
											 List< String > values ,
											 String archiveName ,
											 long expirationDuration )
	 {
			
			JSONObject body = new JSONObject ( );
			body.putOpt ( "METHOD" , "INSERT" ).putOpt ( "KEY" , key ).putOpt ( "ARCHIVE_NAME" , archiveName ).putOpt ( "EXPIRATION_DURATION" , expirationDuration );
			JSONArray array = new JSONArray ( );
			for ( String v : values ) array.put ( v );
			body.putOpt ( "VALUES" , array );
			
			String        x          = body.toString ( );
			int           cacheIndex = getCacheIndex ( key , archiveName );
			SocketChannel channel    = socketChannelPools[ cacheIndex ].getChannel ( );
			try
			{
				 channel.writeMessage ( x );
				 channel.readMessage ( );
				 socketChannelPools[ cacheIndex ].returnChannel ( channel );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
//			System.out.println ( "$$$$$ insert\t\t" + x );
			return this;
	 }
	 
	 public
	 CacheNeoDB save ( String key ,
										 String value ,
										 String archiveName ,
										 long expirationDuration )
	 {

//			LocalCache.getInstance ( )
//								.save ( archiveName , key , value , MishaDBConstants.CACHE_EXPIRATION_DURATION_TIME_1MIN );
			
			JSONObject body = new JSONObject ( );
			body.putOpt ( "METHOD" , "SAVE" ).putOpt ( "KEY" , key ).putOpt ( "ARCHIVE_NAME" , archiveName ).putOpt ( "EXPIRATION_DURATION" , expirationDuration ).putOpt ( "VALUE" , value );
			
			String        x          = body.toString ( );
			int           cacheIndex = getCacheIndex ( key , archiveName );
			SocketChannel channel    = socketChannelPools[ cacheIndex ].getChannel ( );
			try
			{
				 channel.writeMessage ( x );
				 channel.readMessage ( );
				 socketChannelPools[ cacheIndex ].returnChannel ( channel );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
				 System.err.println ( channel );
			}
//			System.out.println ( "$$$$$ save\t\t" + x );
			return this;
	 }
	 
	 public
	 List< String > fetch ( String key ,
													String archiveName ,
													int limit )
	 {
			
			JSONObject body = new JSONObject ( );
			body.putOpt ( "METHOD" , "FETCH" ).putOpt ( "KEY" , key ).putOpt ( "ARCHIVE_NAME" , archiveName );
			if ( limit <= 0 ) limit = Integer.MAX_VALUE;
			body.putOpt ( "LIMIT" , limit );
			String         x          = body.toString ( );
			int            cacheIndex = getCacheIndex ( key , archiveName );
			SocketChannel  channel    = socketChannelPools[ cacheIndex ].getChannel ( );
			List< String > result     = new ArrayList<> ( );
			try
			{
				 channel.writeMessage ( x );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
				 return result;
			}
			
			String json;
			try
			{
				 json = channel.readMessage ( );
				 socketChannelPools[ cacheIndex ].returnChannel ( channel );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
				 return result;
			}
			JSONObject response;
			try
			{
				 response = new JSONObject ( json );
				 JSONArray array = response.optJSONArray ( "array" );
				 for ( int i = 0 ; i < array.length ( ) ; ++i )
				 {
						String data = array.optString ( i );
						if ( data != null ) result.add ( data );
				 }
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
			return result;
	 }
	 
	 public
	 String fetch ( String key ,
									String archiveName )
	 {
			
			String v;
//			LocalCache.getInstance ( )
//													 .fetch ( archiveName , key );
			List< String > list = key == null ? null : fetch ( key , archiveName , 1 );
			if ( list == null || list.isEmpty ( ) ) v = null;
			else v = list.get ( 0 );
			//			if ( v != null )
//			{
//				 LocalCache.getInstance ( )
//									 .save ( archiveName , key , v , MishaDBConstants.CACHE_EXPIRATION_DURATION_TIME_1MIN );
//			}
			return v;
	 }
	 
	 public
	 CacheNeoDB cacheDelete ( String key ,
														String archiveName )
	 {

//			LocalCache.getInstance ( )
//								.delete ( archiveName , key );
			JSONObject body = new JSONObject ( );
			body.putOpt ( "METHOD" , "DELETE" ).putOpt ( "KEY" , key ).putOpt ( "ARCHIVE_NAME" , archiveName );
			int           cacheIndex = getCacheIndex ( key , archiveName );
			SocketChannel channel    = socketChannelPools[ cacheIndex ].getChannel ( );
			try
			{
				 channel.writeMessage ( body.toString ( ) );
				 channel.readMessage ( );
				 socketChannelPools[ cacheIndex ].returnChannel ( channel );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
			return this;
	 }
	 
	 public
	 CacheNeoDB cacheDeleteValues ( String key ,
																	String archiveName ,
																	List< String > values )
	 {
			
			if ( values == null ) return this;
//			LocalCache.getInstance ( )
//								.delete ( archiveName , key );
			JSONArray array = new JSONArray ( );
			for ( String v : values )
			{
				 if ( v != null )
				 {
						array.put ( v );
				 }
			}
			JSONObject body = new JSONObject ( );
			body.putOpt ( "METHOD" , "DELETE-VALUES" ).putOpt ( "KEY" , key ).putOpt ( "VALUES" , array ).putOpt ( "ARCHIVE_NAME" , archiveName );
			int           cacheIndex = getCacheIndex ( key , archiveName );
			SocketChannel channel    = socketChannelPools[ cacheIndex ].getChannel ( );
			String        x          = body.toString ( );
			try
			{
				 channel.writeMessage ( x );
				 channel.readMessage ( );
				 socketChannelPools[ cacheIndex ].returnChannel ( channel );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
//			System.out.println ( "$$$$$ cacheDeleteValues\t\t" + x );
			return this;
	 }
	 
	 public
	 CacheNeoDB deleteModelCacheValue ( String key ,
																			String archiveName ,
																			MModel model )
	 {
			
			if ( model != null )
			{
				 List< String > values = fetch ( key , archiveName , -1 );
				 if ( values != null )
				 {
						for ( String v : values )
						{
							 JSONObject object = MJSON.toJSON ( v );
							 if ( object != null && object.optString ( "mid" ).equals ( model.getMid ( ) ) )
							 {
									cacheDeleteValues ( key , archiveName , Utils.singletonList ( v ) );
									break;
							 }
						}
				 }
			}
			return this;
	 }
	 
	 public
	 void cleanSlate ( )
	 {
			
			System.err.println ( "START CACHE-DB CLEAN SLATE" );
//			LocalCache.getInstance ( )
//								.cleanSlate ( );
			JSONObject body = new JSONObject ( );
			body.putOpt ( "METHOD" , "CLEAN-SLATE" );
			for ( SocketChannelPool socketChannelPool : socketChannelPools )
			{
				 SocketChannel channel = socketChannelPool.getChannel ( );
				 try
				 {
						channel.writeMessage ( body.toString ( ) );
						channel.readMessage ( );
						socketChannelPool.returnChannel ( channel );
				 }
				 catch ( Exception e )
				 {
						e.printStackTrace ( );
				 }
			}
			System.err.println ( "FINISH CACHE-DB CLEAN SLATE" );
	 }
}
