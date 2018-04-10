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

package com.nepolix.misha.db.aurora.pool;


import com.nepolix.misha.db.aurora.driver.MishaDBConstants;
import com.nepolix.misha.db.connection.DBConnection;
import com.nepolix.misha.db.connection.DBConnectionPool;

/**
 * @author Behrooz Shahriari
 * @since 5/22/17
 */
public
class AuroraConnectionPool
{
	 
	 
	 private static AuroraConnectionPool CONNECTION_POOL;
	 
	 private DBConnectionPool DB_CONNECTION_POOL_WRITE;
	 
	 private DBConnectionPool DB_CONNECTION_POOL_READ;
	 
	 
	 private
	 AuroraConnectionPool ( )
	 {
			
			DB_CONNECTION_POOL_WRITE = new DBConnectionPool ( MishaDBConstants.DB_WRITE_END_POINT_CLUSTER , true );
			DB_CONNECTION_POOL_READ = new DBConnectionPool ( MishaDBConstants.DB_READ_END_POINT_CLUSTER , false );
	 }
	 
	 public static
	 AuroraConnectionPool getInstance ( )
	 {
			
			if ( CONNECTION_POOL == null ) CONNECTION_POOL = new AuroraConnectionPool ( );
			return CONNECTION_POOL;
	 }
	 
	 public synchronized
	 DBConnection getConnection ( String collectionName ,
																boolean write )
	 {
			
			DBConnectionPool connectionPool = write ? DB_CONNECTION_POOL_WRITE : DB_CONNECTION_POOL_READ;
			connectionPool.resetConnections ( );
			return connectionPool.getConnection ( MishaDBConstants.DB_USER , MishaDBConstants.DB_USER_PASS , collectionName );
	 }
	 
	 
	 public
	 void returnConnection ( DBConnection connection )
	 {
			
			if ( connection.isWriteConnection ( ) ) DB_CONNECTION_POOL_WRITE.returnConnection ( connection );
			else DB_CONNECTION_POOL_READ.returnConnection ( connection );
	 }
}
