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

package com.nepolix.misha.db.connection;

import java.sql.Connection;

/**
 * @author Behrooz Shahriari
 * @since 6/9/17
 */
public
class DBConnection
{
	 
	 private Connection connection;
	 
	 private String dbName;
	 
	 private boolean writeConnection;
	 
	 public
	 DBConnection ( Connection connection ,
									String dbName ,
									boolean writeConnection )
	 {
			
			this.connection = connection;
			this.dbName = dbName;
			this.writeConnection = writeConnection;
	 }
	 
	 public
	 Connection getConnection ( )
	 {
			
			return connection;
	 }
	 
	 public
	 String getDbName ( )
	 {
			
			return dbName;
	 }
	 
	 
	 public static
	 boolean validConnection ( DBConnection dbConnection )
	 {
			
			if ( dbConnection == null ) return false;
			try
			{
				 if ( !dbConnection.getConnection ( ).isValid ( 1 ) )
				 {
						return false;
				 }
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
				 return false;
			}
			return true;
	 }
	 
	 public
	 boolean isWriteConnection ( )
	 {
			
			return writeConnection;
	 }
}
