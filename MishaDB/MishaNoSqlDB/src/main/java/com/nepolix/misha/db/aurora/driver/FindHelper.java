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

import com.nepolix.misha.db.exception.MishaSQLFormatException;
import com.nepolix.misha.db.connection.DBConnection;
import com.nepolix.misha.db.sql.SQLStatementBuilder;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static com.nepolix.misha.db.aurora.driver.MishaDBConstants.COLUMNS;
import static com.nepolix.misha.db.aurora.driver.MishaDBConstants.TABLE_NAME;

/**
 * @author Behrooz Shahriari
 * @since 7/9/17
 */
class FindHelper
{
	 
	 private MishaDBNoSql mishaAurora;
	 
	 FindHelper ( MishaDBNoSql mishaAurora )
	 {
			
			this.mishaAurora = mishaAurora;
	 }
	 
	 TreeSet< String > findMIDs ( JSONObject query ,
																String collectionName )
	 {
			
			List< JSONObject > mishaSQLQueries = new ArrayList<> ( );
			for ( String fc : query.keys ( ) )
			{
				 JSONObject fcCondition       = query.optJSONObject ( fc );
				 JSONObject mishaSQLCondition = MishaNoSqlDBCommons.parseGeneralConditionToSQLCondition ( fc , fcCondition );
				 mishaSQLQueries.add ( mishaSQLCondition );
			}
			return queryFields ( mishaSQLQueries , collectionName );
	 }
	 
	 private
	 TreeSet< String > queryFields ( List< JSONObject > mishaSQLQueries ,
																	 String collectionName )
	 {
			
			
			TreeSet< String > ids          = new TreeSet<> ( );
			DBConnection      dbConnection = mishaAurora.getCollectionConnection ( collectionName , false );
			long              t            = System.currentTimeMillis ( );
			Connection        connection   = dbConnection.getConnection ( );
			Statement         statement    = null;
			ResultSet         resultSet    = null;
			try
			{
				 JSONArray projections = new JSONArray ( );
				 projections.put ( COLUMNS[ 0 ] );
				 statement = connection.createStatement ( );
				 StringBuilder largeQuery = new StringBuilder ( );
				 
				 for ( int i = 0 ; i < mishaSQLQueries.size ( ) ; ++i )
				 {
						long       tt           = System.currentTimeMillis ( );
						JSONObject q            = mishaSQLQueries.get ( i );
						JSONObject sqlStatement = new JSONObject ( );
						sqlStatement.putOpt ( "type" , "FIND" );
						sqlStatement.putOpt ( "CE" , q );
//						JSONArray group = new JSONArray ( );
//						group.put ( COLUMNS[ 0 ] );
						sqlStatement.putOpt ( "projection" , projections );
						String query = SQLStatementBuilder.buildSQLStatement ( sqlStatement , TABLE_NAME );
						if ( i == 0 ) largeQuery.append ( query );
						else largeQuery.append ( " AND " + COLUMNS[ 0 ] + " IN (" + query + ")" );
						System.out.println ( "*&tt  " + ( System.currentTimeMillis ( ) - tt ) );
				 }
//				 largeQuery.append ( " GROUP BY " + COLUMNS[ 0 ] );
				 resultSet = statement.executeQuery ( largeQuery.toString ( ) );
				 while ( resultSet.next ( ) )
				 {
						ids.add ( resultSet.getString ( COLUMNS[ 0 ] ) );
				 }
				 System.out.println ( "DEBUG db name:" + dbConnection.getDbName ( ) + " query: " + largeQuery.toString ( ) );
				 System.out.println ( "DEBUG queryFields  " + ( System.currentTimeMillis ( ) - t ) );
			}
			catch ( SQLException | MishaSQLFormatException e )
			{
				 e.printStackTrace ( );
			}
			finally
			{
				 if ( resultSet != null )
				 {
						try
						{
							 resultSet.close ( );
						}
						catch ( SQLException e1 )
						{
							 e1.printStackTrace ( );
						}
				 }
				 if ( statement != null )
				 {
						try
						{
							 statement.close ( );
						}
						catch ( SQLException e1 )
						{
							 e1.printStackTrace ( );
						}
				 }
			}
			mishaAurora.returnConnection ( dbConnection );
			return ids;
	 }
}
