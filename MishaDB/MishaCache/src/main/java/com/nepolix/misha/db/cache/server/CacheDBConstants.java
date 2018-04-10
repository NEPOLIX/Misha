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

package com.nepolix.misha.db.cache.server;

/**
 * @author Behrooz Shahriari
 * @since 7/10/17
 */
public abstract
class CacheDBConstants
{
	 
	 public final static String TABLE_NAME = "MISHA";
	 
	 public final static String[] NEO_CACHE_COLUMNS = new String[] {
					 "ID" ,
					 "cache_key" ,
					 "cache_value" ,
					 "cache_expiration_time"
	 };
	 
	 /**
		* default should be, 0:west, 1:east, 2:europe
		*/
	 private static Integer regionCacheIndex;
	 
	 private static String[][] neoCacheDBClusterIPs;
	 
	 public static
	 void initCacheClusterAddresses ( String[][] neoCacheDBClusterIPs ,
																		Integer regionCacheIndex )
	 {
			
			CacheDBConstants.neoCacheDBClusterIPs = neoCacheDBClusterIPs;
			CacheDBConstants.regionCacheIndex = regionCacheIndex;
	 }
	 
	 public static
	 String[] getNeoCacheDBURL ( )
	 {
			
			return neoCacheDBClusterIPs[ regionCacheIndex ];
	 }
	 
	 public static
	 int getDBCachePort ( )
	 {
			
			return 23576;
	 }
}
