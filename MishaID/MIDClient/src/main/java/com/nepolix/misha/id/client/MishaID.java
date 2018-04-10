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

package com.nepolix.misha.id.client;

import com.nepolix.misha.id.MIDConstants;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.rest.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;

import static com.nepolix.misha.rest.client.WebClient.RESTMethod.GET;

/**
 * @author Behrooz Shahriari
 * @since 11/3/16
 */
public
class MishaID
{
	 
	 
	 public static final String MISHA_UUID = "MISHA_UNIVERSAL_ID_TAG";
	 
	 public static final String NULL_ID = "NULL_ID";
	 
	 private static MishaID MISHA_ID;
	 
	 private final static int K = 1000;
	 
	 public static
	 MishaID getMishaID ( )
	 {
			
			if ( MIDConstants.getMishaIdAddress ( ) == null )
				 throw new MissingResourceException ( "Please first call 'initInetAddress' with the MishaID server address" , MishaID.class.getSimpleName ( ) , "mishaID address" );
			if ( MISHA_ID == null ) MISHA_ID = new MishaID ( );
			return MISHA_ID;
	 }
	 
	 private HashMap< String/*version*/, IDList > map;
	 
	 private
	 MishaID ( )
	 {
			
			init ( );
	 }
	 
	 public static
	 void initInetAddress ( String mishaID$Address )
	 {
			
			MIDConstants.setMishaIdAddress ( mishaID$Address );
	 }
	 
	 private
	 void init ( )
	 {
			
			map = new HashMap<> ( );
	 }
	 
	 public
	 String nextID ( String idTag )
	 {
			
			return nextKIDs ( idTag , 1 )[ 0 ];
	 }
	 
	 public
	 String[] nextKIDs ( String idTag ,
											 int k )
	 {
			
			String res[]  = new String[ k ];
			IDList idList = map.get ( idTag );
			if ( idList == null || idList.size ( ) < K )
			{
				 if ( idList == null )
				 {
						idList = new IDList ( );
						map.put ( idTag , idList );
				 }
				 getIDs ( idTag );
			}
			int k_ = 0;
			while ( idList.size ( ) != 0 )
			{
				 if ( k_ == k ) break;
				 res[ k_ ] = idList.remove ( );
				 k_++;
			}
			return res;
	 }
	 
	 private
	 void getIDs ( String id_tag )
	 {
			
			HashMap< String, String > headers = new HashMap<> ( );
			headers.put ( "id_tag" , id_tag );
			headers.put ( "k" , "" + K );
			JSONObject result = WebClient.call ( GET , "http://" + MIDConstants.getMishaIdAddress ( ) + ":" + MIDConstants.getMishaIDPort ( ) + "/id_generator" , null , headers , null );
			JSONArray  array  = result.optJSONArray ( "ids" );
			IDList     idList = map.get ( id_tag );
			for ( int i = 0 ; i < array.length ( ) ; ++i ) idList.add ( array.optString ( i ) );
	 }
	 
	 
	 private
	 class IDList
	 {
			
			private ArrayList< String > strings;
			
			IDList ( )
			{
				 
				 strings = new ArrayList<> ( );
			}
			
			
			void add ( String v )
			{
				 
				 strings.add ( v );
			}
			
			String remove ( )
			{
				 
				 return strings.remove ( 0 );
			}
			
			int size ( )
			{
				 
				 return strings.size ( );
			}
	 }
	 
}
