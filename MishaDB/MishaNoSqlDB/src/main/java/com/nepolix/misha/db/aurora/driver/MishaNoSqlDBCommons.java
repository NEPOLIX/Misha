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

import com.nepolix.misha.db.json.FlattenJSONObject;
import com.nepolix.misha.db.json.JSONHelper;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;

import java.util.List;

import static com.nepolix.misha.db.aurora.driver.MishaDBConstants.*;
import static com.nepolix.misha.db.json.FlattenJSONObject.*;

/**
 * @author Behrooz Shahriari
 * @since 7/9/17
 */
public
class MishaNoSqlDBCommons
{
	 
	 public static
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
									String  x      = value.toString ( );
									boolean adjust = false;
									if ( x.startsWith ( "'" ) && x.endsWith ( "'" ) )
									{
										 adjust = true;
										 x = x.substring ( 1 , x.length ( ) - 1 );
									}
									x = escapeString$QueryExecute ( x );
									if ( adjust )
									{
										 x = "'" + x + "'";
									}
									values.set ( j , x );
							 }
						}
				 }
			}
			
			String dBFieldType = getDBFieldType ( value );
			
			try
			{
				 subCondition.put ( "$=" , "'" + escapeString$QueryExecute ( fieldChain ) + "'" );
				 sqlCondition.put ( COLUMNS[ 3 ] , subCondition.clone ( ) );
				 subCondition.clear ( );
				 subCondition.put ( "$=" , "'" + dBFieldType + "'" );
				 sqlCondition.put ( COLUMNS[ 4 ] , subCondition.clone ( ) );
				 subCondition.clear ( );
				 subCondition.put ( "$=" , "'F'" );
				 sqlCondition.put ( COLUMNS[ 2 ] , subCondition.clone ( ) );
				 subCondition.clear ( );
				 if ( dBFieldType.equals ( FIELD_TYPE_INTEGER ) || dBFieldType.equals ( FIELD_TYPE_LONG ) ) sqlCondition.put ( COLUMNS[ 5 ] , fcCondition );
				 if ( dBFieldType.equals ( FIELD_TYPE_BOOLEAN ) )
				 {
						int    v;
						String operator = fcCondition.keys ( ).get ( 0 );
						v = Boolean.parseBoolean ( value.toString ( ).toLowerCase ( ) ) ? 1 : 0;
						fcCondition.remove ( operator );
						fcCondition.put ( operator , v );
						sqlCondition.put ( COLUMNS[ 5 ] , fcCondition );
				 }
				 if ( dBFieldType.equals ( FIELD_TYPE_DOUBLE ) ) sqlCondition.put ( COLUMNS[ 6 ] , fcCondition );
				 if ( dBFieldType.equals ( FIELD_TYPE_STRING ) ) sqlCondition.put ( COLUMNS[ 7 ] , fcCondition );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
			}
			return sqlCondition;
	 }
	 
	 public static
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
