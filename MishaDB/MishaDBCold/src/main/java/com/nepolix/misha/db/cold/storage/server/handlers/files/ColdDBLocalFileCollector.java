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

package com.nepolix.misha.db.cold.storage.server.handlers.files;

import com.nepolix.misha.commons.security.AES;
import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.db.json.FlattenJSONObject;
import com.nepolix.misha.db.json.JSONHelper;
import com.nepolix.misha.id.client.MishaID;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.nepolix.misha.db.cold.ColdDBConstants.FIELD_COLUMNS;
import static com.nepolix.misha.db.cold.ColdDBConstants.OBJECT_COLUMNS;

/**
 * @author Behrooz Shahriari
 * @since 2/21/18
 */
public
class ColdDBLocalFileCollector
{
	 
	 private final static ColdDBLocalFileCollector COLD_DB_LOCAL_FILE_COLLECTOR = new ColdDBLocalFileCollector ( );
	 
	 private DBFileWriterMap dbFileWriterFieldMap = new DBFileWriterMap ( false );
	 
	 private DBFileWriterMap dbFileWriterObjectMap = new DBFileWriterMap ( true );
	 
	 private
	 ColdDBLocalFileCollector ( )
	 {
		
	 }
	 
	 public static
	 ColdDBLocalFileCollector getColdDbLocalFileCollector ( )
	 {
			
			return COLD_DB_LOCAL_FILE_COLLECTOR;
	 }
	 
	 public
	 void store ( String accountId ,
								String collectionName ,
								JSONArray objects )
	 {
			
			MishaID                   mishaID   = MishaID.getMishaID ( );
			Map< String, JSONObject > objectMap = new LinkedHashMap<> ( );
			objects.forEach ( o -> {
				 JSONObject object = ( JSONObject ) o;
				 object.putOpt ( "updateTime" , Utils.getCurrentUTCTime ( ) );
				 objectMap.put ( mishaID.nextID ( MishaID.MISHA_UUID ) , object );
			} );
			storeObjectRaw ( accountId , collectionName , objectMap );
			storeObjectFields ( accountId , collectionName , objectMap );
	 }
	 
	 private
	 void storeObjectRaw ( String accountId ,
												 String collectionName ,
												 Map< String, JSONObject > objectMap )
	 {
			
			ColdDBFileWriter coldDBFileWriter = dbFileWriterObjectMap.getColdDBFileWriter ( accountId , collectionName );
			objectMap.entrySet ( ).forEach ( z -> {
				 StringBuilder builder    = new StringBuilder ( );
				 String        uid        = z.getKey ( );
				 JSONObject    object     = z.getValue ( );
				 String        mid        = object.optString ( "mid" );
				 JSONObject    row        = new JSONObject ( );
				 String        objectData = AES.encrypt ( object.toString ( ) , AES.getGlobalAESKey ( ) );
				 row.putOpt ( OBJECT_COLUMNS[ 0 ] , uid ).putOpt ( OBJECT_COLUMNS[ 1 ] , mid ).putOpt ( OBJECT_COLUMNS[ 2 ] , object.optLong ( "updateTime" ) ).putOpt ( OBJECT_COLUMNS[ 3 ] , objectData );
				 builder.append ( row.toString ( ) ).append ( "\n" );
				 coldDBFileWriter.store ( builder.toString ( ) );
			} );
	 }
	 
	 private
	 void storeObjectFields ( String accountId ,
														String collectionName ,
														Map< String, JSONObject > objectMap )
	 {
			
			ColdDBFileWriter coldDBFileWriter = dbFileWriterFieldMap.getColdDBFileWriter ( accountId , collectionName );
			objectMap.entrySet ( ).forEach ( z -> {
				 String            uid               = z.getKey ( );
				 JSONObject        object            = z.getValue ( );
				 String            mid               = object.optString ( "mid" );
				 FlattenJSONObject flattenJSONObject = JSONHelper.flattenObjectFields ( object );
				 List< String >    flattenFields     = flattenJSONObject.$getFieldChains ( );
				 StringBuilder     builder           = new StringBuilder ( );
				 flattenFields.forEach ( fc -> {
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
							 if ( type.equals ( "STRING" ) ) valueString = o.toString ( );
							 JSONObject row = new JSONObject ( );
							 row.putOpt ( FIELD_COLUMNS[ 0 ] , uid ).putOpt ( FIELD_COLUMNS[ 1 ] , mid ).putOpt ( FIELD_COLUMNS[ 2 ] , fc ).putOpt ( FIELD_COLUMNS[ 3 ] , type )
									.putOpt ( FIELD_COLUMNS[ 4 ] , valueInt ).putOpt ( FIELD_COLUMNS[ 5 ] , valueDouble ).putOpt ( FIELD_COLUMNS[ 6 ] , valueString )
									.putOpt ( FIELD_COLUMNS[ 7 ] , object.optLong ( "updateTime" ) );
							 builder.append ( row.toString ( ) ).append ( "\n" );
						}
				 } );
				 coldDBFileWriter.store ( builder.toString ( ) );
			} );
	 }
	 
	 public
	 void sync ( String accountId ,
							 boolean deleteLocal )
	 {
			
			dbFileWriterFieldMap.getMap ( accountId ).entrySet ( ).forEach ( x -> x.getValue ( ).flushSync ( deleteLocal ) );
			dbFileWriterObjectMap.getMap ( accountId ).entrySet ( ).forEach ( x -> x.getValue ( ).flushSync ( deleteLocal ) );
	 }
	 
	 public
	 void syncAll ( )
	 {
			
			dbFileWriterObjectMap.getAllAccounts ( ).forEach ( x -> sync ( x , true ) );
	 }
}
