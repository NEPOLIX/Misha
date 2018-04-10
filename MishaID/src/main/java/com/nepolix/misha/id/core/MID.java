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

package com.nepolix.misha.id.core;

import com.nepolix.misha.commons.Base64;
import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Behrooz Shahriari
 * @since 6/6/17
 */
public
class MID
{
	 
	 private final static SecureRandom RANDOM = new SecureRandom ( );
	 
	 private final static int INIT_ID_SIZE = 5;
	 
	 private String idTag;
	 
	 private byte array[];
	 
	 private byte[] init;
	 
	 private int sequence[];
	 
	 MID ( String idTag )
	 {
			
			init = generateRandomInit ( INIT_ID_SIZE );
			this.idTag = idTag;
			array = Arrays.copyOf ( init , init.length );
			sequence = generateRandomSequence ( INIT_ID_SIZE );
	 }
	 
	 MID ( JSONObject object )
	 {
			
			try
			{
				 idTag = object.getString ( "id_tag" );
				 JSONArray sequence = object.getJSONArray ( "sequence" );
				 int       size     = sequence.length ( );
				 this.sequence = new int[ size ];
				 this.init = new byte[ size ];
				 this.array = new byte[ size ];
				 
				 for ( int i = 0 ; i < size ; ++i )
						this.sequence[ i ] = sequence.getInt ( i );
				 
				 JSONArray init = object.getJSONArray ( "init" );
				 for ( int i = 0 ; i < size ; ++i )
						this.init[ i ] = ( byte ) init.getInt ( i );
				 
				 JSONArray array = object.getJSONArray ( "array" );
				 for ( int i = 0 ; i < size ; ++i )
						this.array[ i ] = ( byte ) array.getInt ( i );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 private static
	 byte[] generateRandomInit ( int size )
	 {
			
			byte b[] = new byte[ size ];
			RANDOM.nextBytes ( b );
			return b;
	 }
	 
	 
	 private static
	 int[] generateRandomSequence ( int size )
	 {
			
			int                seq[] = new int[ size ];
			HashSet< Integer > set   = new HashSet<> ( );
			int                i     = 0;
			while ( set.size ( ) != size )
			{
				 int x = RANDOM.nextInt ( size );
				 if ( set.contains ( x ) ) continue;
				 set.add ( x );
				 seq[ i ] = x;
				 ++i;
			}
			return seq;
	 }
	 
	 public
	 String[] generateNextKIDs ( int k )
	 {
			
			
			String ids[] = new String[ k ];
			for ( int i = 0 ; i < k ; ++i )
			{
				 ids[ i ] = nextID ( );
			}
			saveState ( true );
			return ids;
	 }
	 
	 private
	 String nextID ( )
	 {
			
			String tag   = Base64.toBase64 ( idTag.getBytes ( ) );
			int    carry = 1;
			for ( int idx : sequence )
			{
				 int b = array[ idx ];
				 b = b + carry;
				 if ( b == init[ idx ] && carry == 1 ) carry = 1;
				 else
				 {
						if ( b > 127 ) b = -128;
						carry = 0;
				 }
				 array[ idx ] = ( byte ) b;
			}
			rebase ( );
			long time = Utils.getCurrentUTCTime ( ) + Utils.getRandom ( ).nextInt ( );
			int  idxr = Utils.getRandom ( ).nextInt ( array.length );
			time += array[ idxr ];
			return tag + "#" + Base64.toBase64 ( array ) + "#" + Base64.toBase64 ( ( "" + time ).getBytes ( ) );
	 }
	 
	 private
	 void rebase ( )
	 {
			
			if ( isInit ( ) )
			{
				 int nSize = init.length + idTag.length ( );
				 nSize = nSize == init.length ? nSize + 1 : nSize;
				 init = generateRandomInit ( nSize );
				 array = Arrays.copyOf ( init , init.length );
				 sequence = generateRandomSequence ( nSize );
			}
	 }
	 
	 private
	 boolean isInit ( )
	 {
			
			for ( int i = 0 ; i < init.length ; ++i )
			{
				 if ( init[ i ] != array[ i ] ) return false;
			}
			return true;
	 }
	 
	 synchronized
	 void saveState ( boolean saveS3 )
	 {
			
			String path = System.getProperty ( "user.home" ) + File.separator + ".misha" + File.separator + "mids";
			path = path + File.separator + idTag;
			File file = new File ( path );
			try
			{
				 FileWriter fileWriter = new FileWriter ( file );
				 JSONObject root       = new JSONObject ( );
				 root.put ( "id_tag" , idTag );
				 JSONArray jsonArray;
				 jsonArray = new JSONArray ( );
				 for ( int s : sequence ) jsonArray.put ( s );
				 root.put ( "sequence" , jsonArray );
				 
				 jsonArray = new JSONArray ( );
				 for ( byte b : init ) jsonArray.put ( b );
				 root.put ( "init" , jsonArray );
				 
				 jsonArray = new JSONArray ( );
				 for ( byte b : array ) jsonArray.put ( b );
				 root.put ( "array" , jsonArray );
				 fileWriter.write ( root.toString ( ) );
				 fileWriter.close ( );
				 if ( saveS3 ) S3Utils.getS3Utils ( ).uploadDirectory ( MIDFactory.getRootMishaId ( ) );
			}
			catch ( IOException | JSONException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 public
	 String getIdTag ( )
	 {
			
			return idTag;
	 }
}
