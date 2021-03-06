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

package com.nepolix.misha.db.json;

import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Behrooz Shahriari
 * @since 1/5/17
 */
public
class FlattenJSONObject
{
	 
	 public final static String FIELD_TYPE_STRING = "STRING";
	 
	 public final static String FIELD_TYPE_INTEGER = "INTEGER";
	 
	 public final static String FIELD_TYPE_LONG = "LONG";
	 
	 public final static String FIELD_TYPE_BOOLEAN = "BOOLEAN";
	 
	 public final static String FIELD_TYPE_DOUBLE = "DOUBLE";
	 
	 final static String SEPERATOR = ".";
	 
	 private Map< String, List< Object > > map;
	 
	 public
	 FlattenJSONObject ( )
	 {
			
			map = new LinkedHashMap<> ( );
	 }
	 
	 public
	 Map< String, List< Object > > getMap ( )
	 {
			
			return map;
	 }
	 
	 public
	 void setMap ( Map< String, List< Object > > map )
	 {
			
			this.map = map;
	 }
	 
	 public
	 void $put ( String fieldChain ,
							 Object o )
	 {
			
			List< Object > objects = map.computeIfAbsent ( fieldChain , k -> new ArrayList<> ( ) );
			if ( o != null && !o.equals ( null ) ) objects.add ( o );
	 }
	 
	 
	 public
	 List< Object > $getValues ( String fieldChain )
	 {
			
			List< Object > objects = map.get ( fieldChain );
			if ( objects == null ) objects = new ArrayList<> ( );
			return objects;
	 }
	 
	 public
	 JSONArray $getValuesArray ( String fieldChain )
	 {
			
			List< Object > list      = $getValues ( fieldChain );
			JSONArray      jsonArray = new JSONArray ( );
			for ( Object aList : list ) jsonArray.put ( aList );
			return jsonArray;
	 }
	 
	 public
	 List< String > $getFieldChains ( )
	 {
			
			return new ArrayList<> ( map.keySet ( ) );
	 }
	 
	 @Override
	 public
	 String toString ( )
	 {
			
			StringBuilder builder = new StringBuilder ( );
			for ( String k : map.keySet ( ) )
			{
				 builder.append ( k ).append ( "=" ).append ( map.get ( k ).toString ( ) ).append ( "\n" );
			}
			return builder.toString ( );
	 }
	 
	 static
	 void flattenFields ( FlattenJSONObject flattenJSONObject ,
												JSONObject object ,
												String hierarchyField )
					 throws
					 JSONException
	 {
			
			if ( object == null || object.isEmpty ( ) ) return;
			List< String > keys = object.keys ( );
			for ( String k : keys )
			{
				 Object o = object.get ( k );
				 if ( o == null )
				 {
						String fieldChain = hierarchyField + k;
						flattenJSONObject.$put ( fieldChain , o );
						continue;
				 }
				 if ( o.getClass ( ).equals ( JSONObject.class ) )
				 {
						flattenFields ( flattenJSONObject , ( JSONObject ) o , hierarchyField + k + SEPERATOR );
				 }
				 else
				 {
						if ( o.getClass ( ).equals ( JSONArray.class ) )
						{
							 flattenFields ( flattenJSONObject , ( JSONArray ) o , hierarchyField + k + SEPERATOR );
						}
						else
						{
							 flattenJSONObject.$put ( hierarchyField + k , o );
						}
				 }
			}
	 }
	 
	 static
	 void flattenFields ( FlattenJSONObject flattenJSONObject ,
												JSONArray array ,
												String hierarchyField )
					 throws
					 JSONException
	 {
			
			if ( array == null ) return;
			for ( int i = 0 ; i < array.length ( ) ; ++i )
			{
				 Object o = array.get ( i );
				 if ( o == null || o.equals ( null ) ) continue;
				 if ( o.getClass ( ).equals ( JSONObject.class ) )
				 {
						flattenFields ( flattenJSONObject , ( JSONObject ) o , hierarchyField );
				 }
				 else
				 {
						if ( o.getClass ( ).equals ( JSONArray.class ) )
						{
							 flattenFields ( flattenJSONObject , ( JSONArray ) o , hierarchyField );
						}
						else
						{
							 String k = hierarchyField.substring ( 0 , hierarchyField.length ( ) - 1 );
							 flattenJSONObject.$put ( k , o );
						}
				 }
			}
	 }
	 
	 public static
	 String getDBFieldType ( Object value )
	 {
			
			String valueString = value.toString ( );
			try
			{
				 Long.parseLong ( valueString );
				 if ( value.getClass ( ).equals ( Long.class ) || value.getClass ( ).equals ( long.class ) ) return FIELD_TYPE_LONG;
				 if ( value.getClass ( ).equals ( Integer.class ) || value.getClass ( ).equals ( int.class ) ) return FIELD_TYPE_INTEGER;
			}
			catch ( Exception ignored )
			{
			}
			try
			{
				 if ( valueString.equalsIgnoreCase ( "true" ) || valueString.equalsIgnoreCase ( "false" ) ) return FIELD_TYPE_BOOLEAN;
			}
			catch ( Exception ignored )
			{
			}
			try
			{
				 Double.parseDouble ( valueString );
				 if ( value.getClass ( ).equals ( Double.class ) || value.getClass ( ).equals ( double.class ) ) return FIELD_TYPE_DOUBLE;
			}
			catch ( Exception ignored )
			{
			}
			return FIELD_TYPE_STRING;
	 }
}
