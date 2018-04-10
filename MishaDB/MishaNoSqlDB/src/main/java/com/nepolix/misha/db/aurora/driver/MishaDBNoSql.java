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

package com.nepolix.misha.db.aurora.driver;

import com.nepolix.misha.commons.security.AES;
import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.commons.xstructures.ModelIDList;
import com.nepolix.misha.db.MishaDB$A;
import com.nepolix.misha.db.json.FlattenJSONObject;
import com.nepolix.misha.db.json.JSONHelper;
import com.nepolix.misha.db.aurora.pool.AuroraConnectionPool;
import com.nepolix.misha.db.connection.DBConnection;
import com.nepolix.misha.db.model.MModel;
import com.nepolix.misha.id.client.MishaID;
import com.nepolix.misha.id.exception.MishaIDException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;

import java.sql.*;
import java.util.*;

import static com.nepolix.misha.db.aurora.driver.MishaDBConstants.*;

/**
 * @author Behrooz Shahriari
 * @since 7/9/17
 */
public
class MishaDBNoSql
				extends MishaDB$A
{
	 
	 
	 private static MishaDBNoSql MISHA_AURORA = null;
	 
	 private static Set< String > collectionNames;
	 
	 private SaveHelper saveHelper;
	 
	 private FindHelper findHelper;
	 
	 MishaDBNoSql ( MishaID mishaID )
	 {
			
			super ( mishaID );
	 }
	 
	 private static
	 void initInetAddress ( String dbWriteEndPoint$GenioAuroraCluster ,
													String dbReadEndPoint$GenioAuroraCluster ,
													String dbUserPass )
	 {
			
			MishaDBConstants.DB_USER_PASS = dbUserPass;
			MishaDBConstants.DB_WRITE_END_POINT_CLUSTER = dbWriteEndPoint$GenioAuroraCluster;
			MishaDBConstants.DB_READ_END_POINT_CLUSTER = dbReadEndPoint$GenioAuroraCluster;
	 }
	 
	 public static
	 MishaDBNoSql getInstance ( )
	 {
			
			if ( MISHA_AURORA == null ) throw new NullPointerException ( "first initialize the misha-db" );
			else return MISHA_AURORA;
	 }
	 
	 public static
	 MishaDBNoSql init ( MishaID mishaID ,
											 String dbWriteEndPoint$GenioAuroraCluster ,
											 String dbReadEndPoint$GenioAuroraCluster ,
											 String dbUserPass )
	 {
			
			initInetAddress ( dbWriteEndPoint$GenioAuroraCluster , dbReadEndPoint$GenioAuroraCluster , dbUserPass );
			if ( MishaDBConstants.DB_USER_PASS == null )
			{
				 throw new MissingResourceException ( "Please first call 'initInetAddress' to set the DB cluster and password" , MishaDBNoSql.class.getSimpleName ( ) , "address/pass" );
			}
			if ( MISHA_AURORA == null ) MISHA_AURORA = new MishaDBNoSql ( mishaID );
			return getInstance ( );
	 }
	 
	 @Override
	 protected
	 void init ( )
	 {
			
			try
			{
				 Class.forName ( /*"org.sqlite.JDBC" */MishaDBConstants.jdbcDriver );
			}
			catch ( ClassNotFoundException e )
			{
				 e.printStackTrace ( );
			}
			collectionNames = new HashSet<> ( );
			fetchAllCollectionNames ( );
			saveHelper = new SaveHelper ( this );
			findHelper = new FindHelper ( this );
	 }
	 
	 private
	 void fetchAllCollectionNames ( )
	 {
			
			if ( collectionNames.isEmpty ( ) )
			{
				 try
				 {
						Connection connection = DriverManager.getConnection ( "jdbc:mariadb://" + DB_WRITE_END_POINT_CLUSTER + ":3306/" + MISHA_DATABASE_MODELS + "?autoReconnect=true&useUnicode=true" , DB_USER ,
																																	DB_USER_PASS );
						Statement statement = connection.createStatement ( );
						statement.execute ( "CREATE TABLE IF NOT EXISTS " + COLLECTION_DATABASE_NAME + " (" + COLLECTION_DATABASE_NAME_COLUMNS[ 0 ] + " VARCHAR" + "(767) NOT NULL PRIMARY KEY) ENGINE=INNODB" );
						ResultSet resultSet = statement.executeQuery ( "SELECT * FROM " + COLLECTION_DATABASE_NAME );
						while ( resultSet.next ( ) )
						{
							 String name = resultSet.getString ( COLLECTION_DATABASE_NAME_COLUMNS[ 0 ] );
							 collectionNames.add ( name );
						}
						
						resultSet.close ( );
						statement.close ( );
						connection.close ( );
						System.out.println ( "init_collection_names=" + collectionNames.toString ( ) );
//						call0Index ( );
				 }
				 catch ( SQLException e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
	 
	 private
	 void addIndexes ( Statement statement )
					 throws
					 SQLException
	 {
			
			String sql = "CREATE INDEX " + COLUMNS[ 0 ] + "_idx USING HASH ON MISHA (`" + COLUMNS[ 0 ] + "` (128))";
			System.out.println ( sql + " = " + statement.execute ( sql ) );
			sql = "CREATE INDEX " + COLUMNS[ 3 ] + "_idx USING HASH ON MISHA (" + COLUMNS[ 3 ] + "(190))";
			System.out.println ( sql + " = " + statement.execute ( sql ) );
			sql = "CREATE INDEX " + COLUMNS[ 5 ] + "_idx USING BTREE ON MISHA (" + COLUMNS[ 5 ] + ")";
			System.out.println ( sql + " = " + statement.execute ( sql ) );
			sql = "CREATE INDEX " + COLUMNS[ 6 ] + "_idx USING BTREE ON MISHA (" + COLUMNS[ 6 ] + ")";
			System.out.println ( sql + " = " + statement.execute ( sql ) );
			sql = "CREATE FULLTEXT INDEX " + COLUMNS[ 7 ] + "_idx ON MISHA (" + COLUMNS[ 7 ] + ")";
			System.out.println ( sql + " = " + statement.execute ( sql ) );
	 }
	 
	 private
	 void call0Index ( )
	 {
			
			collectionNames.forEach ( x -> {
				 DBConnection dbConnection = getCollectionConnection ( x , true );
				 Connection   connection   = dbConnection.getConnection ( );
				 String       sql          = "";
				 System.out.println ( "collection  " + x );
				 try
				 {
						Statement statement = connection.createStatement ( );
//						addIndexes ( statement );
//						statement.close ();


//						sql = "CREATE INDEX " + COLUMNS[ 0 ] + "_idx USING HASH ON MISHA (`" + COLUMNS[ 0 ] + "` (128))";
//						System.out.println ( sql + " = " + statement.execute ( sql ) );
//						sql = "CREATE INDEX " + COLUMNS[ 3 ] + "_idx USING HASH ON MISHA (" + COLUMNS[ 3 ] + "(190))";
//						System.out.println ( sql + " = " + statement.execute ( sql ) );
//						sql = "CREATE INDEX " + COLUMNS[ 5 ] + "_idx USING BTREE ON MISHA (" + COLUMNS[ 5 ] + ")";
//						System.out.println ( sql + " = " + statement.execute ( sql ) );
//						sql = "CREATE INDEX " + COLUMNS[ 6 ] + "_idx USING BTREE ON MISHA (" + COLUMNS[ 6 ] + ")";
//						System.out.println ( sql + " = " + statement.execute ( sql ) );
//						sql = "CREATE FULLTEXT INDEX " + COLUMNS[ 7 ] + "_idx ON MISHA (" + COLUMNS[ 7 ] + ")";
//						System.out.println ( sql + " = " + statement.execute ( sql ) );
						
						
						//DROPS
//						sql = "DROP INDEX " + COLUMNS[ 0 ] + "_idx ON MISHA";
//						System.out.println ( sql + " = " + statement.execute ( sql ) );
//						sql = "DROP INDEX " + COLUMNS[ 3 ] + "_idx ON MISHA";
//						System.out.println ( sql + " = " + statement.execute ( sql ) );
//						sql = "DROP INDEX " + COLUMNS[ 5 ] + "_idx ON MISHA";
//						System.out.println ( sql + " = " + statement.execute ( sql ) );
//						sql = "DROP INDEX " + COLUMNS[ 6 ] + "_idx ON MISHA";
//						System.out.println ( sql + " = " + statement.execute ( sql ) );
//						sql = "DROP INDEX " + COLUMNS[ 7 ] + "_idx ON MISHA";
//						System.out.println ( sql + " = " + statement.execute ( sql ) );
						
				 }
				 catch ( SQLException e )
				 {
						System.err.println ( e.toString ( ) + "\n" + sql );
				 }
			} );
	 }
	 
	 
	 DBConnection getCollectionConnection ( String collectionName ,
																					boolean write )
	 {
			
			if ( !collectionNames.contains ( collectionName ) )
			{
				 System.out.println ( "collection_names=" + collectionNames.toString ( ) );
				 Connection connection;
				 try
				 {
						connection = DriverManager.getConnection ( "jdbc:mariadb://" + DB_WRITE_END_POINT_CLUSTER + ":3306?autoReconnect=true&useUnicode=true" , DB_USER , DB_USER_PASS );
						Statement statement = connection.createStatement ( );
						statement.executeUpdate ( "CREATE DATABASE IF NOT EXISTS `" + collectionName + "` DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci" );
						statement.close ( );
						connection.close ( );
						connection = DriverManager.getConnection ( "jdbc:mariadb://" + DB_WRITE_END_POINT_CLUSTER + ":3306/" + collectionName + "?autoReconnect=true&useUnicode=true" , DB_USER , DB_USER_PASS );
						statement = connection.createStatement ( );
						statement.execute (
										"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + COLUMNS[ 0 ] + " " + "TEXT, " + COLUMNS[ 1 ] + " INT UNSIGNED, " + COLUMNS[ 2 ] + " CHAR (2) NOT NULL, " + COLUMNS[ 3 ]
										+ " TEXT NOT NULL, " + COLUMNS[ 4 ] + " TEXT NOT NULL, " + COLUMNS[ 5 ] + " BIGINT, " + COLUMNS[ 6 ] + " DOUBLE PRECISION, " + COLUMNS[ 7 ]
										+ " LONGTEXT) ENGINE=INNODB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci" );
						addIndexes ( statement );
						statement.close ( );
						connection.close ( );
						saveCollectionName ( collectionName );
				 }
				 catch ( SQLException e )
				 {
						e.printStackTrace ( );
				 }
			}
			return AuroraConnectionPool.getInstance ( ).getConnection ( collectionName , write );
	 }
	 
	 void returnConnection ( DBConnection dbConnection )
	 {
			
			AuroraConnectionPool.getInstance ( ).returnConnection ( dbConnection );
	 }
	 
	 private
	 void saveCollectionName ( String collectionName )
	 {
			
			collectionNames.add ( collectionName );
			Connection connection;
			try
			{
				 connection = DriverManager
															.getConnection ( "jdbc:mariadb://" + DB_WRITE_END_POINT_CLUSTER + ":3306/" + MISHA_DATABASE_MODELS + "?autoReconnect=true&useUnicode=true&characterEncoding=utf8mb4" ,
																							 DB_USER , DB_USER_PASS );
				 Statement statement = connection.createStatement ( );
				 statement.execute ( "INSERT IGNORE INTO " + COLLECTION_DATABASE_NAME + " (" + COLLECTION_DATABASE_NAME_COLUMNS[ 0 ] + ") VALUES" + " ('" + collectionName + "')" );
				 statement.close ( );
				 connection.close ( );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 @Override
	 public
	 < T extends MModel > MishaDBNoSql save ( Collection< T > objects )
	 {
			
			if ( objects != null && !objects.isEmpty ( ) ) saveHelper.saveObjects ( objects );
			return this;
	 }
	 
	 @Override
	 public
	 void save ( MModel object )
					 throws
					 MishaIDException,
					 NullPointerException
	 {
			
			if ( object == null ) throw new NullPointerException ( "'object' cannot be 'null'" );
			object.setUpdateTime ( Utils.getCurrentUTCTime ( ) );
			JSONObject jsonObject = MJSON.toJSON ( object );
			if ( !jsonObject.has ( "mid" ) ) throw new MishaIDException ( "object must contain 'mid::String' field " + "is a unique " + "identifier" );
			save ( object.modelName ( ) , jsonObject );
	 }
	 
	 @Override
	 public
	 void save ( String collectionName ,
							 JSONObject object )
	 {
			
			if ( object == null )
			{
				 throw new NullPointerException ( "object is null" );
			}
			String mid = object.optString ( "mid" );
			if ( mid == null )
			{
				 throw new NullPointerException ( "object is not a misha object" );
			}
			FlattenJSONObject flattenJSONObject = JSONHelper.flattenObjectFields ( object );
			saveHelper.saveObject ( collectionName , object );
			saveHelper.saveFields ( collectionName , mid , flattenJSONObject );
	 }
	 
	 @Override
	 public
	 < T extends MModel > ModelIDList getCollectionIDs ( Class< T > modelClass )
	 {
			
			String collectionName = null;
			try
			{
				 collectionName = modelClass.newInstance ( ).modelName ( );
			}
			catch ( InstantiationException | IllegalAccessException e )
			{
				 throw new IllegalArgumentException ( e.getMessage ( ) );
			}
			return getCollectionIDs ( collectionName );
	 }
	 
	 
	 public
	 ModelIDList getCollectionIDs ( String collectionName )
	 {
			
			ModelIDList idList = new ModelIDList ( );
			if ( collectionName == null ) return idList;
			DBConnection dbConnection = getCollectionConnection ( collectionName , true );
			Connection   connection   = dbConnection.getConnection ( );
			try
			{
				 Statement statement = connection.createStatement ( );
				 ResultSet resultSet = statement.executeQuery ( "SELECT " + COLUMNS[ 0 ] + " FROM " + TABLE_NAME + " WHERE " + COLUMNS[ 2 ] + "='O'" );
				 while ( resultSet.next ( ) )
				 {
						idList.$addMID ( resultSet.getString ( COLUMNS[ 0 ] ) );
				 }
				 resultSet.close ( );
				 statement.close ( );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
			returnConnection ( dbConnection );
			return idList;
	 }
	 
	 public
	 < T extends MModel > T findOne ( JSONObject query ,
																		Class< T > clazz )
	 {
			
			List< T > list = find ( query , 1 , -1 , clazz );
			if ( list == null || list.isEmpty ( ) ) return null;
			else return list.get ( 0 );
	 }
	 
	 public
	 JSONObject findOne ( JSONObject query ,
												String collectionName )
	 {
			
			List< JSONObject > list = find ( query , -1 , -1 , collectionName );
			if ( list.isEmpty ( ) ) return null;
			else
			{
				 return list.get ( 0 );
			}
	 }
	 
	 @Override
	 public
	 < T extends MModel > List< T > find ( JSONObject query ,
																				 int limit ,
																				 int offset ,
																				 Class< T > objectClass )
					 throws
					 IllegalArgumentException
	 {
			
			String collectionName = MModel.getModelName ( objectClass );
			if ( collectionName == null || collectionName.isEmpty ( ) ) throw new IllegalArgumentException ( "invalid collection name" );
			List< T >          objects    = new ArrayList<> ( );
			List< JSONObject > rawObjects = find ( query , limit , offset , collectionName );
			for ( JSONObject rawObject : rawObjects )
			{
				 objects.add ( MJSON.toObject ( rawObject , objectClass ) );
			}
			return objects;
	 }
	 
	 @Override
	 public
	 List< JSONObject > find ( JSONObject query ,
														 int limit ,
														 int offset ,
														 String collectionName )
	 {
			
			ArrayList< JSONObject > objects   = new ArrayList<> ( );
			boolean                 oneObject = false;
			if ( query != null && query.keys ( ).contains ( "mid" ) )
			{
				 JSONObject midCondition = query.optJSONObject ( "mid" );
				 String     operator     = midCondition.keys ( ).get ( 0 );
				 if ( operator.equals ( "$=" ) )
				 {
						String mid = midCondition.optString ( operator );
						objects.addAll ( findObjects ( Utils.singletonList ( mid ) , collectionName ) );
						oneObject = true;
				 }
			}
			if ( query != null && !oneObject )
			{
				 long          t      = System.currentTimeMillis ( );
				 Set< String > setIds = findHelper.findMIDs ( query , collectionName );
				 System.out.println ( "$@find DB internal  1 " + ( System.currentTimeMillis ( ) - t ) );
				 t = System.currentTimeMillis ( );
				 List< String > mids = new ArrayList<> ( setIds );
				 System.out.println ( "$@find DB internal  2 " + ( System.currentTimeMillis ( ) - t ) );
				 if ( limit < 0 ) limit = mids.size ( );
				 if ( offset < 0 ) offset = 0;
				 int size = limit + offset;
				 if ( size > mids.size ( ) ) size = mids.size ( );
				 List< String > mids_ = new ArrayList<> ( );
				 for ( int i = offset ; i < size ; ++i )
				 {
						String mid = mids.get ( i );
						mids_.add ( mid );
				 }
				 objects.addAll ( findObjects ( mids_ , collectionName ) );
			}
			return objects;
	 }
	 
	 public
	 int count ( JSONObject query ,
							 String collectionName )
	 {
			
			return findHelper.findMIDs ( query , collectionName ).size ( );
	 }
	 
	 @Override
	 public
	 < T extends MModel > Set< T > findObjects ( Collection< String > mids ,
																							 Class< T > clazz )
	 {
			
			Set< T > objects = new LinkedHashSet<> ( );
			if ( mids == null || mids.isEmpty ( ) ) return objects;
			mids.removeIf ( Objects:: isNull );
			String             collectionName = MModel.getModelName ( clazz );
			List< JSONObject > jsons          = findObjects ( mids , collectionName );
			if ( jsons != null )
			{
				 for ( JSONObject x : jsons )
				 {
						if ( x != null ) objects.add ( MJSON.toObject ( x , clazz ) );
				 }
			}
			return objects;
	 }
	 
	 
	 public
	 List< JSONObject > findObjects ( Collection< String > mids ,
																		String collectionName )
	 {
			
			DBConnection          dbConnection = getCollectionConnection ( collectionName , true );
			Connection            connection   = dbConnection.getConnection ( );
			List< JSONObject >    objects      = new ArrayList<> ( );
			int                   i            = 0;
			List< StringBuilder > builders     = new ArrayList<> ( );
			StringBuilder         builder      = new StringBuilder ( );
			for ( String mid : mids )
			{
				 mid = mid.replace ( "'" , "" );
				 builder.append ( "('" ).append ( mid ).append ( "' , 'O')" );
				 i++;
				 if ( i % 1000 == 0 )
				 {
						builders.add ( builder );
						builder = new StringBuilder ( );
						i = 0;
				 }
				 else
				 {
						builder.append ( ", " );
				 }
			}
			if ( i > 0 )
			{
				 int idx = builder.lastIndexOf ( "," );
				 if ( idx > 0 ) builder.replace ( idx , builder.length ( ) , "" );
				 builders.add ( builder );
			}
			try
			{
				 Statement statement = connection.createStatement ( );
				 for ( StringBuilder builder_ : builders )
				 {
						StringBuilder statementBuilder = new StringBuilder ( );
						statementBuilder.append ( "SELECT * FROM " + TABLE_NAME + " WHERE (" ).append ( COLUMNS[ 0 ] ).append ( ", " ).append ( COLUMNS[ 2 ] ).append ( ") IN (" ).append ( builder_ )
														.append ( ")" );
						ResultSet resultSet = statement.executeQuery ( statementBuilder.toString ( ) );
						while ( resultSet.next ( ) )
						{
							 try
							 {
									String objectJson = resultSet.getString ( COLUMNS[ 7 ] );
									objectJson = AES.decrypt ( objectJson , AES.getGlobalAESKey ( ) );
									JSONObject object = MJSON.toJSON ( objectJson );
									objects.add ( object );
							 }
							 catch ( Exception e )
							 {
									e.printStackTrace ( );
							 }
						}
						resultSet.close ( );
				 }
				 statement.close ( );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
			returnConnection ( dbConnection );
			return objects;
	 }
	 
	 @Override
	 public
	 < T extends MModel > T findOne ( String mid ,
																		Class< T > clazz )
	 {
			
			String     collectionName = MModel.getModelName ( clazz );
			JSONObject x              = findOne ( mid , collectionName );
			if ( x != null ) return MJSON.toObject ( x , clazz );
			else return null;
	 }
	 
	 @Override
	 public
	 JSONObject findOne ( String mid ,
												String collectionName )
	 {
			
			if ( mid == null ) return null;
			mid = mid.replace ( "'" , "" );
			DBConnection dbConnection = getCollectionConnection ( collectionName , true );
			Connection   connection   = dbConnection.getConnection ( );
			JSONObject   object       = null;
			try
			{
				 Statement statement = connection.createStatement ( );
				 ResultSet resultSet = statement.executeQuery ( "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMNS[ 0 ] + "='" + mid + "' AND " + COLUMNS[ 2 ] + "='O'" );
				 while ( resultSet.next ( ) )
				 {
						String objectJson = resultSet.getString ( COLUMNS[ 7 ] );
						objectJson = AES.decrypt ( objectJson , AES.getGlobalAESKey ( ) );
						object = MJSON.toJSON ( objectJson );
				 }
				 resultSet.close ( );
				 statement.close ( );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
			returnConnection ( dbConnection );
			return object;
	 }
	 
	 public
	 < T extends MModel > void deleteCollection ( Class< T > clazz )
	 {
			
			String       collectionName = MModel.getModelName ( clazz );
			DBConnection dbConnection   = getCollectionConnection ( collectionName , true );
			Connection   connection     = dbConnection.getConnection ( );
			try
			{
				 Statement statement = connection.createStatement ( );
				 statement.execute ( "DELETE FROM " + TABLE_NAME );
				 statement.close ( );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
			returnConnection ( dbConnection );
	 }
	 
	 public
	 int delete ( JSONObject query ,
								String collectionName )
	 {
			
			List< String > mids      = new ArrayList<> ( );
			boolean        oneObject = false;
			if ( query.keys ( ).contains ( "mid" ) )
			{
				 JSONObject midCondition = query.optJSONObject ( "mid" );
				 String     operator     = midCondition.keys ( ).get ( 0 );
				 if ( operator.equals ( "$=" ) )
				 {
						oneObject = true;
						String mid = midCondition.optString ( operator );
						mids.add ( mid );
				 }
			}
			if ( !oneObject )
			{
				 mids.addAll ( findHelper.findMIDs ( query , collectionName ) );
			}
			return deleteObjects ( mids , collectionName );
	 }
	 
	 @Override
	 public
	 < T extends MModel > int delete ( JSONObject query ,
																		 Class< T > clazz )
	 {
			
			String collectionName = MModel.getModelName ( clazz );
			return delete ( query , collectionName );
	 }
	 
	 public
	 < T extends MModel > int deleteObjects ( Collection< T > objects )
	 {
			
			if ( objects.isEmpty ( ) ) return 0;
			String        collectionName = MModel.getModelName ( objects.iterator ( ).next ( ).getClass ( ) );
			Set< String > mids           = new LinkedHashSet<> ( );
			objects.forEach ( o -> mids.add ( o.getMid ( ) ) );
			return deleteObjects ( mids , collectionName );
	 }
	 
	 public
	 < T extends MModel > int deleteObjects ( Collection< String > mids ,
																						Class< T > clazz )
	 {
			
			String collectionName = MModel.getModelName ( clazz );
			return deleteObjects ( mids , collectionName );
	 }
	 
	 @Override
	 public
	 int deleteObjects ( Collection< String > mids ,
											 String collectionName )
	 {
			
			if ( mids == null ) throw new NullPointerException ( "mid list is null" );
			if ( mids.isEmpty ( ) ) return 0;
			DBConnection dbConnection = getCollectionConnection ( collectionName , true );
			Connection   connection   = dbConnection.getConnection ( );
			Statement    statement    = null;
			int          c            = 0;
			try
			{
				 statement = connection.createStatement ( );
				 int i = 0;
				 for ( String id : mids )
				 {
						statement.addBatch ( "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMNS[ 0 ] + "='" + id + "'" );
						i++;
						if ( i % 1000 == 0 )
						{
							 int z[] = statement.executeBatch ( );
							 for ( int zz : z )
							 {
									if ( zz == Statement.SUCCESS_NO_INFO || zz >= 0 ) c++;
							 }
							 i = 0;
						}
				 }
				 if ( i > 0 )
				 {
						int z[] = statement.executeBatch ( );
						for ( int zz : z )
						{
							 if ( zz == Statement.SUCCESS_NO_INFO || zz >= 0 ) c++;
						}
				 }
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
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
			}
			returnConnection ( dbConnection );
			return c;
	 }
	 
	 int getObjectHash ( JSONObject object ,
											 String collectionName )
	 {
			
			return 0;
	 }
	 
	 int getFieldHash ( String collectionName ,
											String fc ,
											String mid )
	 {
			
			return 0;
	 }
	 
	 public
	 void cleanSlate ( )
	 {
			
			System.err.println ( "START DB CLEAN SLATE" );
			fetchAllCollectionNames ( );
			for ( String x : collectionNames )
			{
				 DBConnection dbConnection = getCollectionConnection ( x , true );
				 Connection   connection   = dbConnection.getConnection ( );
				 try
				 {
						Statement statement = connection.createStatement ( );
						statement.execute ( "DELETE FROM " + TABLE_NAME );
						statement.close ( );
						connection.close ( );
						
						connection = DriverManager
																 .getConnection ( "jdbc:mariadb://" + DB_WRITE_END_POINT_CLUSTER + ":3306/" + MISHA_DATABASE_MODELS + "?autoReconnect=true&useUnicode=true" , DB_USER , DB_USER_PASS );
						statement = connection.createStatement ( );
						statement.execute ( "DELETE FROM " + COLLECTION_DATABASE_NAME );
						statement.close ( );
						connection.close ( );
				 }
				 catch ( SQLException e )
				 {
						e.printStackTrace ( );
				 }
			}
			try
			{
				 Connection connection = DriverManager.getConnection ( "jdbc:mariadb://" + DB_WRITE_END_POINT_CLUSTER + ":3306/?autoReconnect=true&useUnicode=true&characterEncoding=utf8mb4" , DB_USER ,
																															 DB_USER_PASS );
				 Statement statement = connection.createStatement ( );
				 int       i         = 0;
				 for ( String x : collectionNames )
				 {
						statement.addBatch ( "DROP DATABASE `" + x + "`" );
						i++;
						if ( i % 500 == 0 )
						{
							 i = 0;
							 statement.executeBatch ( );
						}
				 }
				 if ( i > 0 )
				 {
						statement.executeBatch ( );
				 }
				 statement.close ( );
				 connection.close ( );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
			collectionNames.clear ( );
			System.err.println ( "FINISH DB CLEAN SLATE" );
	 }
	 
	 
}
