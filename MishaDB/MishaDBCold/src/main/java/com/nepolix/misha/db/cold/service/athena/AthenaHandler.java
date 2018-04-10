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

package com.nepolix.misha.db.cold.service.athena;

import com.nepolix.misha.commons.Constants;
import com.nepolix.misha.commons.security.AES;
import com.nepolix.misha.db.cold.ColdDBConstants;
import com.nepolix.misha.db.exception.MishaSQLFormatException;
import com.nepolix.misha.db.json.FlattenJSONObject;
import com.nepolix.misha.db.json.JSONHelper;
import com.nepolix.misha.db.sql.SQLStatementBuilder;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;

import java.sql.*;
import java.util.*;

import static com.nepolix.misha.db.cold.ColdDBConstants.FIELD_COLUMNS;
import static com.nepolix.misha.db.cold.ColdDBConstants.OBJECT_COLUMNS;
import static com.nepolix.misha.db.json.FlattenJSONObject.*;

/**
 * @author Behrooz Shahriari
 * @since 2/26/18
 */
public
class AthenaHandler
{
	 
	 // private static final String ATHENA_URL = "jdbc:awsathena://athena.us-west-2.amazonaws.com:443";
	 private final static AthenaHandler ATHENA_HANDLER[] = new AthenaHandler[ 1 ];
	 
	 private long lastRefreshPartitionTime = 0;
	 
	 private
	 AthenaHandler ( )
	 {
			
			init ( );
	 }
	 
	 public static
	 AthenaHandler getAthenaHandler ( )
	 {
			
			if ( ATHENA_HANDLER[ 0 ] == null ) ATHENA_HANDLER[ 0 ] = new AthenaHandler ( );
			return ATHENA_HANDLER[ 0 ];
	 }
	 
	 protected
	 void init ( )
	 {
			
			try
			{
				 Class.forName ( "com.amazonaws.athena.jdbc.AthenaDriver" );
			}
			catch ( ClassNotFoundException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 private
	 Connection buildAthenaConnection ( )
	 {
			
			Properties info = new Properties ( );
			info.put ( "s3_staging_dir" , ColdDBConstants.ATHENA_OUTPUT_BUCKET );
			info.put ( "aws_credentials_provider_class" , "com.nepolix.misha.db.cold.service.athena.AthenaCredentialsProvider" );
			info.put ( "aws_credentials_provider_arguments" , "" );
			try
			{
				 return DriverManager.getConnection ( ColdDBConstants.getAthenaUrl ( ) , info );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
			return null;
	 }
	 
	 AthenaHandler createTables ( )
					 throws
					 SQLException
	 {
			
			
			Connection connection = buildAthenaConnection ( );
			Statement  statement  = connection.createStatement ( );
			String q1 = "CREATE EXTERNAL TABLE IF NOT EXISTS `misha`.`fields` (uid string, mid string, field_chain string, field_type string, v_int bigint, v_double double, v_string string, update_time "
									+ "bigint) PARTITIONED BY ( account string, db string, year int, month int, day int ) ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe' "
									+ "LOCATION 's3://cold-db/gzip/fields/' TBLPROPERTIES ('has_encrypted_data'='true');";
			statement.execute ( q1 );
			q1 = "CREATE EXTERNAL TABLE IF NOT EXISTS `misha`.`objects` (uid string, mid string, update_time bigint, object string) PARTITIONED BY ( account string, db string, year int, month int, day int"
					 + " ) ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe' LOCATION 's3://cold-db/gzip/objects/' TBLPROPERTIES ('has_encrypted_data'='true');";
			statement.execute ( q1 );
			statement.close ( );
			connection.close ( );
			return this;
	 }
	 
	 public
	 void refreshPartitions ( )
	 {
			
			if ( System.currentTimeMillis ( ) - lastRefreshPartitionTime > Constants.INTERVAL_12_HOURS )
			{
				 lastRefreshPartitionTime = System.currentTimeMillis ( );
				 try
				 {
						loadPartition ( true ).loadPartition ( false );
				 }
				 catch ( SQLException e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
	 
	 public
	 AthenaHandler loadPartition ( boolean objectTable )
					 throws
					 SQLException
	 {
			
			Connection connection = buildAthenaConnection ( );
			Statement  statement  = connection.createStatement ( );
			String     q1         = objectTable ? "MSCK REPAIR TABLE `misha`.`objects`;" : "MSCK REPAIR TABLE `misha`.`fields`;";
			statement.execute ( q1 );
			statement.close ( );
			connection.close ( );
			return this;
	 }
	 
	 public
	 List< JSONObject > getLastObjects ( final Collection< String > mids ,
																			 String collectionName )
					 throws
					 SQLException
	 {
			
			refreshPartitions ( );
			String             accountId  = ColdDBConstants.getAccountId ( );
			Connection         connection = buildAthenaConnection ( );
			List< JSONObject > models     = new ArrayList<> ( );
			String sql = "SELECT t.mid, t.object FROM \"misha\".\"objects\" AS t INNER JOIN ( SELECT mid, max(update_time) AS md FROM \"misha\".\"objects\" WHERE account = '" + accountId + "' AND db = '"
									 + collectionName + "' GROUP BY mid) AS tm ON t.mid = tm.mid AND t.update_time = tm.md WHERE t.db = '" + collectionName + "' AND t.mid IN ";
			Set< String > midx_ = new LinkedHashSet<> ( mids );
			while ( !midx_.isEmpty ( ) )
			{
				 StringBuilder sb = new StringBuilder ( sql );
				 sb.append ( "(" );
				 int                c        = 0;
				 Iterator< String > iterator = midx_.iterator ( );
				 while ( iterator.hasNext ( ) && c < 1000 )
				 {
						sb.append ( "'" + iterator.next ( ) + "'" );
						if ( iterator.hasNext ( ) ) sb.append ( " , " );
						iterator.remove ( );
						c++;
				 }
				 sb.append ( ");" );
				 
				 Statement statement = connection.createStatement ( );
				 System.out.println ( "getLastObjects SQL STAT= " + sb.toString ( ) );
				 ResultSet resultSet = statement.executeQuery ( sb.toString ( ) );
				 while ( resultSet.next ( ) )
				 {
						String objectJson = resultSet.getString ( OBJECT_COLUMNS[ 3 ] );
						objectJson = AES.decrypt ( objectJson , AES.getGlobalAESKey ( ) );
						models.add ( MJSON.toJSON ( objectJson ) );
				 }
				 resultSet.close ( );
				 statement.close ( );
			}
			connection.close ( );
			return models;
	 }
	 
	 public
	 Map< String, List< JSONObject > > getObjects ( final Collection< String > mids ,
																									String collectionName ,
																									Integer limitPerObject )
					 throws
					 SQLException
	 {
			
			int limit = limitPerObject == null ? 1000 : limitPerObject;
			refreshPartitions ( );
			String                            accountId  = ColdDBConstants.getAccountId ( );
			Connection                        connection = buildAthenaConnection ( );
			Map< String, List< JSONObject > > models     = new LinkedHashMap<> ( );
			Map< String, List< JSONObject > > mm         = new HashMap<> ( );
			Set< String >                     midx_      = new LinkedHashSet<> ( mids );
			while ( !midx_.isEmpty ( ) )
			{
				 StringBuilder      sb       = new StringBuilder ( );
				 int                c        = 0;
				 Iterator< String > iterator = midx_.iterator ( );
				 while ( iterator.hasNext ( ) && c < 1000 )
				 {
						String sql = "(SELECT mid,update_time,object FROM \"misha\".\"objects\" WHERE account = '" + accountId + "' AND db = '" + collectionName + "' AND mid = '" + iterator.next ( )
												 + "' ORDER BY mid, update_time LIMIT " + limit + ")";
						sb.append ( sql );
						if ( iterator.hasNext ( ) ) sb.append ( " UNION " );
						iterator.remove ( );
						c++;
				 }
				 sb.append ( " ORDER BY mid, update_time DESC" );
				 Statement statement = connection.createStatement ( );
				 System.out.println ( "getObjects SQL STAT= " + sb.toString ( ) );
				 ResultSet resultSet = statement.executeQuery ( sb.toString ( ) );
				 while ( resultSet.next ( ) )
				 {
						String mid        = resultSet.getString ( OBJECT_COLUMNS[ 1 ] );
						String objectJson = resultSet.getString ( OBJECT_COLUMNS[ 3 ] );
						objectJson = AES.decrypt ( objectJson , AES.getGlobalAESKey ( ) );
						mm.computeIfAbsent ( mid , k -> new ArrayList<> ( ) ).add ( MJSON.toJSON ( objectJson ) );
				 }
				 resultSet.close ( );
				 statement.close ( );
			}
			connection.close ( );
			mids.forEach ( m -> models.put ( m , mm.get ( m ) ) );
			return models;
	 }
	 
	 public
	 List< JSONObject > find ( JSONObject query ,
														 Integer limit ,
														 String collectionName )
					 throws
					 MishaSQLFormatException
	 {
			
			String limitS = ( ( limit != null && limit > 0 ) ? " LIMIT " + limit : "" );
			refreshPartitions ( );
			String             accountId  = ColdDBConstants.getAccountId ( );
			Connection         connection = buildAthenaConnection ( );
			List< JSONObject > models     = new ArrayList<> ( );
			
			List< JSONObject > mishaSQLQueries = new ArrayList<> ( );
			for ( String fc : query.keys ( ) )
			{
				 JSONObject fcCondition       = query.optJSONObject ( fc );
				 JSONObject mishaSQLCondition = parseGeneralConditionToSQLCondition ( fc , fcCondition );
				 mishaSQLQueries.add ( mishaSQLCondition );
			}
			StringBuilder sb = new StringBuilder ( );
			for ( int i = 0 ; i < mishaSQLQueries.size ( ) ; ++i )
			{
				 JSONObject q = mishaSQLQueries.get ( i );
				 if ( i == 0 )
				 {
						String s = "SELECT mid, update_time FROM \"misha\".\"fields\" WHERE account = '" + accountId + "' AND db = '" + collectionName + "' AND (" + SQLStatementBuilder
																																																																																 .buildSQLConditionStatement ( q )
											 + ")";
						sb.append ( s );
				 }
				 else
				 {
						String s = " AND mid IN (SELECT mid FROM \"misha\".\"fields\" WHERE account = '" + accountId + "' AND db = '" + collectionName + "' AND (" + SQLStatementBuilder
																																																																																 .buildSQLConditionStatement ( q )
											 + "))";
						sb.append ( s );
				 }
			}
			
			String sql = "SELECT t1.mid, t1.update_time, t1.object FROM \"misha\".\"objects\" AS t1 INNER JOIN ((" + sb.toString ( ) + ") AS t2 "
									 + "INNER JOIN (SELECT mid, max(update_time) AS md FROM \"misha\".\"objects\" WHERE account = '" + accountId + "' AND db = '" + collectionName
									 + "' GROUP BY mid) AS tm ON tm.mid = t2.mid AND tm.md = t2.update_time) ON t1.mid = t2.mid AND t1.mid = tm.mid AND t1.update_time = tm.md WHERE t1.db = '" + collectionName
									 + "' AND account = '" + accountId + "'" + limitS;
			Statement statement;
			try
			{
				 statement = connection.createStatement ( );
				 System.out.println ( "find SQL STAT= " + sql );
				 ResultSet resultSet = statement.executeQuery ( sql );
				 while ( resultSet.next ( ) )
				 {
						String objectJson = resultSet.getString ( OBJECT_COLUMNS[ 3 ] );
						objectJson = AES.decrypt ( objectJson , AES.getGlobalAESKey ( ) );
						models.add ( MJSON.toJSON ( objectJson ) );
				 }
				 resultSet.close ( );
				 statement.close ( );
				 connection.close ( );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
			return models;
	 }
	 
	 
	 static
	 JSONObject parseGeneralConditionToSQLCondition ( String fieldChain ,
																										JSONObject fcCondition )
	 {
			
			JSONObject        sqlCondition      = new JSONObject ( );
			JSONObject        subCondition      = new JSONObject ( );
			FlattenJSONObject flattenJSONObject = JSONHelper.flattenObjectFields ( fcCondition );
			Object            value             = null;
			
			List< String > fcs = flattenJSONObject.$getFieldChains ( );
			for ( int i = 0 ; i < fcs.size ( ) && value == null ; ++i )
			{
				 List< Object > values = flattenJSONObject.$getValues ( fcs.get ( i ) );
				 for ( int j = 0 ; j < values.size ( ) ; ++j )
				 {
						if ( values.get ( j ) != null && !values.get ( i ).equals ( null ) )
						{
							 value = values.get ( j );
							 String dBFieldType = FlattenJSONObject.getDBFieldType ( value );
							 if ( dBFieldType.equals ( FIELD_TYPE_STRING ) )
							 {
									String x = value.toString ( );
//									boolean adjust = false;
//									if ( x.startsWith ( "'" ) && x.endsWith ( "'" ) )
//									{
//										 adjust = true;
//										 x = x.substring ( 1 , x.length ( ) - 1 );
//									}
//									x = escapeString$QueryExecute ( x );
//									if ( adjust )
//									{
//										 x = "'" + x + "'";
//									}
									values.set ( j , x );
							 }
						}
				 }
			}
			
			String dBFieldType = getDBFieldType ( value );
			try
			{
//				 subCondition.put ( "$=" , "'" + escapeString$QueryExecute ( fieldChain ) + "'" );
				 subCondition.put ( "$=" , "'" + fieldChain + "'" );
				 sqlCondition.put ( FIELD_COLUMNS[ 2 ] , subCondition.clone ( ) );
				 subCondition.clear ( );
				 subCondition.put ( "$=" , "'" + dBFieldType + "'" );
				 sqlCondition.put ( FIELD_COLUMNS[ 3 ] , subCondition.clone ( ) );
				 subCondition.clear ( );
				 
				 if ( dBFieldType.equals ( FIELD_TYPE_INTEGER ) || dBFieldType.equals ( FIELD_TYPE_LONG ) ) sqlCondition.put ( FIELD_COLUMNS[ 4 ] , fcCondition );
				 if ( dBFieldType.equals ( FIELD_TYPE_BOOLEAN ) )
				 {
						int    v;
						String operator = fcCondition.keys ( ).get ( 0 );
						v = Boolean.parseBoolean ( value.toString ( ).toLowerCase ( ) ) ? 1 : 0;
						fcCondition.remove ( operator );
						fcCondition.put ( operator , v );
						sqlCondition.put ( FIELD_COLUMNS[ 4 ] , fcCondition );
				 }
				 if ( dBFieldType.equals ( FIELD_TYPE_DOUBLE ) ) sqlCondition.put ( FIELD_COLUMNS[ 5 ] , fcCondition );
				 if ( dBFieldType.equals ( FIELD_TYPE_STRING ) ) sqlCondition.put ( FIELD_COLUMNS[ 6 ] , fcCondition );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
			}
			return sqlCondition;
	 }
	 
	 static
	 String escapeString$QueryExecute ( String input )
	 {
			
			if ( input == null ) throw new NullPointerException ( );
			else
			{
				 String inputX = new String ( input );
				 inputX = inputX.replace ( "'" , "''" ).replace ( "\"" , "\\\"" ).replace ( "\\" , "\\\\" );
				 return inputX;
			}
	 }
}
