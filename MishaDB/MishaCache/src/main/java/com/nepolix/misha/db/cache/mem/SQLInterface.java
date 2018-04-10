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
import com.nepolix.misha.db.connection.DBConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static com.nepolix.misha.db.cache.server.CacheDBConstants.NEO_CACHE_COLUMNS;
import static com.nepolix.misha.db.cache.server.CacheDBConstants.TABLE_NAME;

/**
 * @author Behrooz Shahriari
 * @since 11/18/16
 */
class SQLInterface
				implements MSQL
{
	 
	 private final Set< String > archiveNames = new HashSet<> ( );
	 
	 private final static SQLInterface SQL_INTERFACE = new SQLInterface ( );
	 
	 
	 final static String jdbcDriver = "org.mariadb.jdbc.Driver";
	 
	 final static String DB_USER = "root";
	 
	 final static String DB_PASS = null;
	 
	 private final static String SUFFIX_DATABASE = "_Cache";
	 
	 private
	 SQLInterface ( )
	 {
			
			init ( );
	 }
	 
	 private
	 void init ( )
	 {
			
			if ( DB_PASS == null ) throw new NullPointerException ( "DB_PASS is not set" );
			try
			{
				 Class.forName ( jdbcDriver );
				 $loadState ( );
			}
			catch ( ClassNotFoundException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 private
	 void $loadState ( )
	 {
			
			String path = System.getProperty ( "user.home" ) + File.separator + ".misha" + File.separator + "dbs" + File.separator + "neo_cache_db_archives";
			File   file = new File ( path );
			System.out.println ( "DEBUG loadState = " + archiveNames );
			if ( file.exists ( ) )
			{
				 try
				 {
						Scanner scanner = new Scanner ( file );
						while ( scanner.hasNext ( ) )
						{
							 String cn = scanner.nextLine ( );
							 getConnection ( cn );
						}
						scanner.close ( );
				 }
				 catch ( FileNotFoundException e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
	 
	 private
	 void $saveState ( )
	 {
			
			String path = System.getProperty ( "user.home" ) + File.separator + ".misha" + File.separator + "dbs" + File.separator;// + "archiveNames";
			
			File file = new File ( path );
			file.mkdirs ( );
			path = path + "neo_cache_db_archives";
			file = new File ( path );
			try
			{
				 FileWriter fileWriter = new FileWriter ( file );
				 for ( String x : archiveNames )
				 {
						fileWriter.write ( x + "\n" );
				 }
				 fileWriter.close ( );
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 static
	 SQLInterface getSqlInterface ( )
	 {
			
			return SQL_INTERFACE;
	 }
	 
	 DBConnection getConnection ( String collectionName )
	 {
			
			if ( collectionName == null || collectionName.equals ( "null" ) ) throw new NullPointerException ( "collection name can't be 'null'" );
			try
			{
				 if ( !archiveNames.contains ( collectionName ) )
				 {
						Connection connection = DriverManager.getConnection ( "jdbc:mariadb://localhost?autoReconnect=true&useSSL=false" , DB_USER , DB_PASS );
						Statement  statement  = connection.createStatement ( );
						statement.executeUpdate ( "CREATE DATABASE IF NOT EXISTS `" + collectionName + SUFFIX_DATABASE + "`" );
						connection.close ( );
						connection = DriverManager.getConnection ( "jdbc:mariadb://localhost/" + collectionName + "_Cache" + "?autoReconnect=true&useSSL=false" , DB_USER , DB_PASS );
						createTable ( connection );
						connection.close ( );
						archiveNames.add ( collectionName );
						$saveState ( );
				 }
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
			return CacheDBConnectionPool.getInstance ( ).getConnection ( collectionName );
	 }
	 
	 private
	 void createTable ( Connection connection )
	 {
			
			try
			{
				 Statement statement = connection.createStatement ( );
				 String tx = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + NEO_CACHE_COLUMNS[ 0 ] + " BIGINT UNSIGNED, " + NEO_CACHE_COLUMNS[ 1 ] + " TEXT, " + NEO_CACHE_COLUMNS[ 2 ] + " LONGTEXT, "
										 + NEO_CACHE_COLUMNS[ 3 ] + " BIGINT UNSIGNED) ENGINE=INNODB";
				 
				 System.out.println ( ">>>createTable= " + tx );
				 statement.execute ( tx );
				 addIndex ( statement );
				 statement.close ( );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 private
	 void addIndex ( Statement statement )
	 {
			
			String sql = "CREATE INDEX " + NEO_CACHE_COLUMNS[ 1 ] + "_idx USING HASH ON " + TABLE_NAME + " (`" + NEO_CACHE_COLUMNS[ 1 ] + "` (190))";
			try
			{
				 statement.execute ( sql );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 @Override
	 public
	 void save ( String collectionName ,
							 String key ,
							 List< String > values ,
							 long expirationDuration )
	 {
			
			try
			{
				 long         expirationTime = expirationDuration + Utils.getCurrentUTCTime ( );
				 DBConnection connection     = getConnection ( collectionName );
				 Statement    statement      = connection.getConnection ( ).createStatement ( );
				 int          i              = 0;
				 for ( String value : values )
				 {
						String sqlStatement = "DELETE FROM " + TABLE_NAME + " WHERE " + NEO_CACHE_COLUMNS[ 1 ] + " = '" + key + "' AND " + NEO_CACHE_COLUMNS[ 2 ] + " = '" + value + "'";
						statement.executeUpdate ( sqlStatement );
						sqlStatement = "INSERT INTO " + TABLE_NAME + " (" + NEO_CACHE_COLUMNS[ 1 ] + ", " + NEO_CACHE_COLUMNS[ 2 ] + ", " + NEO_CACHE_COLUMNS[ 3 ] + ") VALUES " + "('" + key + "', '" + value
													 + "', " + expirationTime + ")";
//						System.out.println ( "save cache sql:" + sqlStatement );
						statement.executeUpdate ( sqlStatement );
				 }
				 statement.close ( );
				 CacheDBConnectionPool.getInstance ( ).returnConnection ( connection );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 @Override
	 public
	 boolean delete ( String collectionName ,
										String key )
	 {
			
			try
			{
				 DBConnection dbConnection = getConnection ( collectionName );
				 Statement    statement    = dbConnection.getConnection ( ).createStatement ( );
				 String       sqlStatement = "DELETE FROM " + TABLE_NAME + " WHERE " + NEO_CACHE_COLUMNS[ 1 ] + " = '" + key + "'";
//				 System.out.println ( ">>>cache-delete" + sqlStatement );
				 statement.executeUpdate ( sqlStatement );
				 statement.close ( );
				 CacheDBConnectionPool.getInstance ( ).returnConnection ( dbConnection );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
				 return false;
			}
			return true;
	 }
	 
	 @Override
	 public
	 boolean delete ( String collectionName ,
										String key ,
										List< String > values )
	 {
			
			try
			{
				 DBConnection dbConnection = getConnection ( collectionName );
				 Statement    statement    = dbConnection.getConnection ( ).createStatement ( );
				 int          i            = 0;
				 for ( String value : values )
				 {
						String sqlStatement = "DELETE FROM " + TABLE_NAME + " WHERE " + NEO_CACHE_COLUMNS[ 1 ] + " = '" + key + "' AND " + NEO_CACHE_COLUMNS[ 2 ] + " = '" + value + "'";
						statement.executeUpdate ( sqlStatement );
//						System.out.println ( ">>>cache-delete-values" + sqlStatement );
				 }
				 statement.close ( );
				 CacheDBConnectionPool.getInstance ( ).returnConnection ( dbConnection );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
				 return false;
			}
			return true;
	 }
	 
	 @Override
	 public
	 boolean update ( String collectionName ,
										String key ,
										String oldValue ,
										String newValue ,
										long expirationDuration )
	 {
			
			Statement statement;
			try
			{
				 long         expirationTime = expirationDuration + Utils.getCurrentUTCTime ( );
				 DBConnection dbConnection   = CacheDBConnectionPool.getInstance ( ).getConnection ( collectionName );
				 statement = dbConnection.getConnection ( ).createStatement ( );
				 String sqlStatement = "UPDATE " + TABLE_NAME + " SET " + NEO_CACHE_COLUMNS[ 2 ] + " = '" + newValue + "', " + NEO_CACHE_COLUMNS[ 3 ] + " = " + expirationTime + " WHERE " + ""
															 + NEO_CACHE_COLUMNS[ 1 ] + " = '" + key + "' AND " + NEO_CACHE_COLUMNS[ 2 ] + " " + "= " + "'" + oldValue + "'";
				 statement.executeUpdate ( sqlStatement );
				 statement.close ( );
				 CacheDBConnectionPool.getInstance ( ).returnConnection ( dbConnection );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
				 return false;
			}
			return true;
	 }
	 
	 @Override
	 public
	 List< CacheObject > fetch ( String collectionName ,
															 String key )
	 {
			
			ResultSet           resultSet = null;
			List< CacheObject > result    = new ArrayList<> ( );
			Statement           statement = null;
			try
			{
				 DBConnection dbConnection = getConnection ( collectionName );
				 statement = dbConnection.getConnection ( ).createStatement ( );
				 String sqlStatement = "SELECT * FROM " + TABLE_NAME + " WHERE " + NEO_CACHE_COLUMNS[ 1 ] + " = '" + key + "'";
//				 System.out.println ( ">>cache-fetch=" + sqlStatement );
				 resultSet = statement.executeQuery ( sqlStatement );
				 while ( resultSet.next ( ) )
				 {
						//Retrieve by column name
						result.add ( new CacheObject ( resultSet.getString ( NEO_CACHE_COLUMNS[ 2 ] ) , resultSet.getLong ( NEO_CACHE_COLUMNS[ 3 ] ) ) );
				 }
				 CacheDBConnectionPool.getInstance ( ).returnConnection ( dbConnection );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
				 return null;
			}
			finally
			{
				 if ( statement != null )
				 {
						try
						{
							 statement.close ( );
						}
						catch ( SQLException e )
						{
							 e.printStackTrace ( );
						}
				 }
				 if ( resultSet != null )
				 {
						try
						{
							 resultSet.close ( );
						}
						catch ( SQLException e )
						{
							 e.printStackTrace ( );
						}
				 }
			}
			return result;
	 }
	 
	 @Override
	 public
	 void reset ( String collectionName ,
								String key ,
								String value ,
								long expirationDuration )
	 {
			
			delete ( collectionName , key );
			save ( collectionName , key , Utils.singletonList ( value ) , expirationDuration );
	 }
	 
	 
	 @Override
	 public
	 void cleanObsoleteCaches ( )
	 {
			
			try
			{
				 System.out.println ( ">>>Cleaning Obsolete Caches" );
				 $loadState ( );
				 long time = Utils.getCurrentUTCTime ( );
				 for ( String ct : archiveNames )
				 {
						System.out.println ( ">>>Cleaning Obsolete Caches-ARCHIVE_NAME=" + ct + "    time:" + time + " " + "  " + System.currentTimeMillis ( ) );
						DBConnection dbConnection = getConnection ( ct );
						Statement    statement    = dbConnection.getConnection ( ).createStatement ( );
						String       sqlStatement = "DELETE FROM " + TABLE_NAME + " WHERE " + NEO_CACHE_COLUMNS[ 3 ] + " < " + time;
						statement.executeUpdate ( sqlStatement );
						statement.close ( );
						CacheDBConnectionPool.getInstance ( ).returnConnection ( dbConnection );
				 }
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 
	 @Override
	 public
	 void cleanSlate ( )
	 {
			
			try
			{
				 System.out.println ( "CLEAN SLATE CACHE" );
				 $loadState ( );
				 for ( String ct : archiveNames )
				 {
						DBConnection dbConnection = getConnection ( ct );
						Statement    statement    = dbConnection.getConnection ( ).createStatement ( );
						String       sqlStatement = "DELETE FROM " + TABLE_NAME;
						statement.executeUpdate ( sqlStatement );
						statement.close ( );
						CacheDBConnectionPool.getInstance ( ).returnConnection ( dbConnection );
				 }
				 Connection connection = DriverManager.getConnection ( "jdbc:mariadb://localhost?autoReconnect=true&useSSL=false" , DB_USER , DB_PASS );
				 Statement  statement  = connection.createStatement ( );
				 for ( String x : archiveNames )
				 {
						statement.execute ( "DROP DATABASE `" + x + SUFFIX_DATABASE + "`" );
				 }
				 statement.close ( );
				 connection.close ( );
				 archiveNames.clear ( );
				 $saveState ( );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
	 }
}
