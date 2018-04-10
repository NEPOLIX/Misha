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
import com.nepolix.misha.db.json.FlattenJSONObject;
import com.nepolix.misha.db.json.JSONHelper;
import com.nepolix.misha.db.connection.DBConnection;
import com.nepolix.misha.db.model.MModel;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.nepolix.misha.db.aurora.driver.MishaDBConstants.COLUMNS;
import static com.nepolix.misha.db.aurora.driver.MishaDBConstants.TABLE_NAME;

/**
 * @author Behrooz Shahriari
 * @since 7/9/17
 */
class SaveHelper
{
	 
	 private MishaDBNoSql mishaAurora;
	 
	 private static String columns;
	 
	 static
	 {
			columns = " (";
			for ( int i = 0 ; i < COLUMNS.length ; ++i )
			{
				 columns += COLUMNS[ i ];
				 if ( i < COLUMNS.length - 1 ) columns += ", ";
			}
			columns += ")";
	 }
	 
	 SaveHelper ( MishaDBNoSql mishaAurora )
	 {
			
			this.mishaAurora = mishaAurora;
	 }
	 
	 void saveObject ( String collectionName ,
										 JSONObject object )
	 {
			
			DBConnection dbConnection = mishaAurora.getCollectionConnection ( collectionName , true );
			Connection   connection   = dbConnection.getConnection ( );
			String       mid          = object.optString ( "mid" );
			Statement    statement    = null;
			try
			{
				 statement = connection.createStatement ( );
				 statement.addBatch ( "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMNS[ 0 ] + "='" + mid + "' AND " + COLUMNS[ 2 ] + "='O'" );
				 String objectData = AES.encrypt ( object.toString ( ) , AES.getGlobalAESKey ( ) );
				 StringBuilder values = new StringBuilder ( ).append ( " VALUES ('" ).append ( mid ).append ( "', " ).append ( mishaAurora.getObjectHash ( object , collectionName ) )
																										 .append ( ", 'O', '#', '#', 0, 0, " ).append ( "'" ).append ( objectData ).append ( "')" );
				 statement.addBatch ( new StringBuilder ( ).append ( "INSERT INTO " ).append ( TABLE_NAME ).append ( columns ).append ( values ).toString ( ) );
				 statement.executeBatch ( );
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
			mishaAurora.returnConnection ( dbConnection );
	 }
	 
	 void saveFields ( String collectionName ,
										 String mid ,
										 FlattenJSONObject flattenJSONObject )
	 {
			
			DBConnection dbConnection = mishaAurora.getCollectionConnection ( collectionName , true );
			Connection   connection   = dbConnection.getConnection ( );
			Statement    statement    = null;
			try
			{
				 statement = connection.createStatement ( );
				 statement.addBatch ( "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMNS[ 0 ] + "='" + mid + "' AND " + COLUMNS[ 2 ] + "='F'" );
				 List< String > fieldChainList = flattenJSONObject.$getFieldChains ( );
				 int            i[]            = { 0 };
				 saveOneField ( fieldChainList , i , statement , mid , collectionName , flattenJSONObject );
				 if ( i[ 0 ] > 0 )
				 {
						statement.executeBatch ( );
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
			mishaAurora.returnConnection ( dbConnection );
	 }
	 
	 private
	 void saveOneField ( List< String > fieldChainList ,
											 int[] i ,
											 Statement statement ,
											 String mid ,
											 String collectionName ,
											 FlattenJSONObject flattenJSONObject )
					 throws
					 SQLException
	 {
			
			for ( String fc : fieldChainList )
			{
				 JSONArray values = flattenJSONObject.$getValuesArray ( fc );
				 for ( int j = 0 ; j < values.length ( ) ; ++j )
				 {
						Object o = values.opt ( j );
						if ( o == null ) continue;
						String type        = FlattenJSONObject.getDBFieldType ( o );
						Long   valueInt    = -1L;
						Double valueDouble = -1d;
						String valueString = null;
						if ( type.equals ( "INTEGER" ) || type.equals ( "LONG" ) ) valueInt = Long.parseLong ( o.toString ( ) );
						if ( type.equals ( "BOOLEAN" ) ) valueInt = ( ( Boolean ) o ? 1L : 0L );
						if ( type.equals ( "DOUBLE" ) ) valueDouble = Double.parseDouble ( o.toString ( ) );
						if ( type.equals ( "STRING" ) ) valueString = MishaNoSqlDBCommons.escapeString$QueryExecute ( o.toString ( ) );
						
						StringBuilder dbValues = new StringBuilder ( ).append ( " VALUES ('" ).append ( mid ).append ( "', " ).append ( mishaAurora.getFieldHash ( collectionName , fc , mid ) )
																													.append ( ", 'F', '" ).append ( fc ).append ( "', '" ).append ( type ).append ( "', " ).append ( valueInt ).append ( ", " )
																													.append ( valueDouble ).append ( ", " ).append ( "'" ).append ( valueString ).append ( "')" );
						statement.addBatch ( new StringBuilder ( ).append ( "INSERT INTO " ).append ( TABLE_NAME ).append ( columns ).append ( dbValues ).toString ( ) );
						i[ 0 ]++;
						if ( i[ 0 ] % 1000 == 0 )
						{
							 statement.executeBatch ( );
							 i[ 0 ] = 0;
						}
				 }
			}
	 }
	 
	 public
	 < T extends MModel > void saveObjects ( Collection< T > objects )
	 {
			
			if ( objects.isEmpty ( ) )
			{
				 System.out.println ( "WARNING: List of object is empty @SaveHelper.saveObjects" );
				 return;
			}
			String             collectionName = objects.iterator ( ).next ( ).modelName ( );
			List< JSONObject > jsonObjects    = new ArrayList<> ( );
			long               time           = Utils.getCurrentUTCTime ( );
			for ( T x : objects )
			{
				 x.setUpdateTime ( time );
				 jsonObjects.add ( MJSON.toJSON ( x ) );
			}
			saveObjects ( jsonObjects , collectionName );
			saveFields ( jsonObjects , collectionName );
	 }
	 
	 private
	 void saveFields ( List< JSONObject > jsonObjects ,
										 String collectionName )
	 {
			
			DBConnection dbConnection = mishaAurora.getCollectionConnection ( collectionName , true );
			Connection   connection   = dbConnection.getConnection ( );
			Statement    statement    = null;
			try
			{
				 statement = connection.createStatement ( );
				 int i[] = { 0 };
				 for ( JSONObject object : jsonObjects )
				 {
						String            mid               = object.optString ( "mid" );
						FlattenJSONObject flattenJSONObject = JSONHelper.flattenObjectFields ( object );
						statement.addBatch ( "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMNS[ 0 ] + "='" + mid + "' AND " + COLUMNS[ 2 ] + "='F'" );
						List< String > fieldChainList = flattenJSONObject.$getFieldChains ( );
						saveOneField ( fieldChainList , i , statement , mid , collectionName , flattenJSONObject );
				 }
				 if ( i[ 0 ] > 0 )
				 {
						statement.executeBatch ( );
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
			mishaAurora.returnConnection ( dbConnection );
	 }
	 
	 private
	 void saveObjects ( List< JSONObject > jsonObjects ,
											String collectionName )
	 {
			
			DBConnection dbConnection = mishaAurora.getCollectionConnection ( collectionName , true );
			Connection   connection   = dbConnection.getConnection ( );
			Statement    statement    = null;
			
			try
			{
				 statement = connection.createStatement ( );
				 //DELETION BATCH
				 int i = 0;
				 for ( JSONObject object : jsonObjects )
				 {
						String mid = object.optString ( "mid" );
						statement.addBatch ( "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMNS[ 0 ] + "='" + mid + "' AND " + COLUMNS[ 2 ] + "='O'" );
						i++;
						if ( i % 1000 == 0 )
						{
							 statement.executeBatch ( );
							 i = 0;
						}
				 }
				 if ( i > 0 ) statement.executeBatch ( );
				 i = 0;
				 //INSERTION BATCH
				 for ( JSONObject object : jsonObjects )
				 {
						String mid        = object.optString ( "mid" );
						String objectData = AES.encrypt ( object.toString ( ) , AES.getGlobalAESKey ( ) );
						StringBuilder values = new StringBuilder ( ).append ( " VALUES ('" ).append ( mid ).append ( "', " ).append ( mishaAurora.getObjectHash ( object , collectionName ) )
																												.append ( ", 'O', '#', '#', 0, 0, " ).append ( "'" ).append ( objectData ).append ( "')" );
						statement.addBatch ( new StringBuilder ( ).append ( "INSERT INTO " ).append ( TABLE_NAME ).append ( columns ).append ( values ).toString ( ) );
						i++;
						if ( i % 1000 == 0 )
						{
							 statement.executeBatch ( );
							 i = 0;
						}
				 }
				 if ( i > 0 ) statement.executeBatch ( );
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
			mishaAurora.returnConnection ( dbConnection );
	 }
}
