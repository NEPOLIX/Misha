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


import com.nepolix.misha.db.connection.DBConnection;
import com.nepolix.misha.db.connection.DBConnectionPool;

/**
 * @author Behrooz Shahriari
 * @since 5/22/17
 */
class CacheDBConnectionPool
{
	 
	 
	 private final static CacheDBConnectionPool CONNECTION_POOL = new CacheDBConnectionPool ( );
	 
	 private final static DBConnectionPool DB_CONNECTION_POOL = new DBConnectionPool ( "localhost" , true );
	 
	 private
	 CacheDBConnectionPool ( )
	 {
			
	 }
	 
	 public static
	 CacheDBConnectionPool getInstance ( )
	 {
			
			return CONNECTION_POOL;
	 }
	 
	 public synchronized
	 DBConnection getConnection ( String collectionName )
	 {
			
			return DB_CONNECTION_POOL.getConnection ( SQLInterface.DB_USER , SQLInterface.DB_PASS , collectionName + "_Cache" );
	 }
	 
	 public
	 void returnConnection ( DBConnection connection )
	 {
			
			DB_CONNECTION_POOL.returnConnection ( connection );
	 }
}
