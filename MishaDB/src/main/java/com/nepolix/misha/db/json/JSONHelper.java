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

import java.util.List;


/**
 * @author Behrooz Shahriari
 * @since 1/5/17
 */
public
class JSONHelper
{
	 
	 public static
	 FlattenJSONObject flattenObjectFields ( JSONObject object )
	 {
			
			FlattenJSONObject flattenJSONObject = new FlattenJSONObject ( );
			try
			{
				 FlattenJSONObject.flattenFields ( flattenJSONObject , object , "" );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
			}
			return flattenJSONObject;
	 }
	 
	 /**
		* keeps the original json untouched
		*
		* @param jsonObject
		* @param previousFieldNChainName
		*
		* @return
		*/
	 public static
	 JSONObject deleteFieldChain ( JSONObject jsonObject ,
																 String previousFieldNChainName )
	 {
			
			String     fields[] = previousFieldNChainName.split ( "\\." );
			JSONObject object   = jsonObject.clone ( );
			deleteOneFieldInChain$JSONObject ( object , fields , 0 );
			return object;
	 }
	 
	 private static
	 void deleteOneFieldInChain$JSONObject ( JSONObject jsonObject ,
																					 String fieldChainArray[] ,
																					 int idxField )
	 {
			
			List< String > keyFields = jsonObject.keys ( );
			for ( String field : keyFields )
			{
				 if ( field.equals ( fieldChainArray[ idxField ] ) )
				 {
						Object object = jsonObject.opt ( field );
						if ( object != null )
						{
							 if ( object.getClass ( ).equals ( JSONObject.class ) )
							 {
									deleteOneFieldInChain$JSONObject ( ( JSONObject ) object , fieldChainArray , idxField + 1 );
							 }
							 else
							 {
									if ( object.getClass ( ).equals ( JSONArray.class ) )
									{
										 deleteOneFieldInChain$JSONArray ( ( JSONArray ) object , fieldChainArray , idxField );
									}
									else
									{
										 jsonObject.remove ( field );
									}
							 }
						}
				 }
			}
	 }
	 
	 private static
	 void deleteOneFieldInChain$JSONArray ( JSONArray jsonArray ,
																					String fieldChainArray[] ,
																					int idxField )
	 {
			
			for ( int i = 0 ; i < jsonArray.length ( ) ; ++i )
			{
				 Object o = jsonArray.opt ( i );
				 if ( o != null )
				 {
						if ( o.getClass ( ).equals ( JSONObject.class ) )
						{
							 deleteOneFieldInChain$JSONObject ( ( JSONObject ) o , fieldChainArray , idxField + 1 );
						}
						else
						{
							 if ( o.getClass ( ).equals ( JSONArray.class ) )
							 {
									//WE DO NOT SUPPORT
							 }
							 else
							 {
									jsonArray.remove ( i );
									--i;
							 }
						}
				 }
			}
	 }
}
