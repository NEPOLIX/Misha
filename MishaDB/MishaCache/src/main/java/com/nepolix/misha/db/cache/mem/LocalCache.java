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

package com.nepolix.misha.db.cache.mem;

import com.nepolix.misha.commons.utils.Utils;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * @author Behrooz Shahriari
 * @since 7/22/17
 */
public
class LocalCache
{
	 
	 private LinkedHashMap< String, CacheObject > localCache;
	 
	 private final static LocalCache LOCAL_CACHE = new LocalCache ( );
	 
	 private static long MAXIMUM_LOCAL_CACHE_SIZE_BYTE = 500 * 1024 * 1024;//500 MB
	 
	 static
	 {
			com.sun.management.OperatingSystemMXBean os = ( com.sun.management.OperatingSystemMXBean ) java.lang.management.ManagementFactory.getOperatingSystemMXBean ( );
			MAXIMUM_LOCAL_CACHE_SIZE_BYTE = ( long ) ( os.getTotalPhysicalMemorySize ( ) * 0.3 );
			System.out.println ( "LocalCache >> MAXIMUM_LOCAL_CACHE_SIZE_BYTE= " + MAXIMUM_LOCAL_CACHE_SIZE_BYTE );
	 }
	 
	 private long currentCacheSize = 0;
	 
	 private
	 LocalCache ( )
	 {
			
			localCache = new LinkedHashMap<> ( );
	 }
	 
	 public static
	 LocalCache getInstance ( )
	 {
			
			return LOCAL_CACHE;
	 }
	 
	 public
	 void save ( String archiveName ,
							 String key ,
							 String value ,
							 long expirationDuration )
	 {
			
			long        insertionTime  = Utils.getCurrentUTCTime ( );
			long        expirationTime = insertionTime + expirationDuration;
			CacheObject cacheObject    = new CacheObject ( );
			cacheObject.setExpirationTime ( expirationTime );
			cacheObject.setValue ( value );
			CacheObject x = localCache.put ( archiveName + key , cacheObject );
			if ( x != null && x.getValue ( ) != null ) currentCacheSize -= x.getValue ( ).length ( );
			currentCacheSize += value != null ? value.length ( ) : 0;
			updateCacheLimit ( );
	 }
	 
	 private
	 void updateCacheLimit ( )
	 {
			
			while ( currentCacheSize > MAXIMUM_LOCAL_CACHE_SIZE_BYTE )
			{
				 Iterator< CacheObject > iterator = localCache.values ( ).iterator ( );
				 while ( iterator.hasNext ( ) )
				 {
						CacheObject cacheObject = iterator.next ( );
						currentCacheSize -= cacheObject.getValue ( ) != null ? cacheObject.getValue ( ).length ( ) : 0;
						iterator.remove ( );
				 }
				 if ( localCache.isEmpty ( ) ) currentCacheSize = 0;
			}
	 }
	 
	 public
	 String fetch ( String archiveName ,
									String key )
	 {
			
			CacheObject cacheObject = localCache.get ( archiveName + key );
			String      v           = null;
			if ( cacheObject != null && cacheObject.getExpirationTime ( ) < Utils.getCurrentUTCTime ( ) )
			{
				 v = cacheObject.getValue ( );
			}
			else
			{
				 CacheObject x = localCache.remove ( archiveName + key );
				 if ( x != null && x.getValue ( ) != null ) currentCacheSize -= x.getValue ( ).length ( );
			}
			return v;
	 }
	 
	 public
	 void delete ( String archiveName ,
								 String key )
	 {
			
			CacheObject x = localCache.remove ( archiveName + key );
			if ( x != null && x.getValue ( ) != null ) currentCacheSize -= x.getValue ( ).length ( );
	 }
	 
	 public
	 void cleanSlate ( )
	 {
			
			localCache.clear ( );
			currentCacheSize = 0;
			Utils.causeGC ( );
	 }
}
