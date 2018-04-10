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

import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Behrooz Shahriari
 * @since 6/6/17
 */
public
class MIDFactory
{
	 
	 private final static MIDFactory MID_FACTORY = new MIDFactory ( );
	 
	 private HashMap< String, MID > midHashMap;
	 
	 private
	 MIDFactory ( )
	 {
			
			midHashMap = new HashMap<> ( );
			loadFactory ( );
	 }
	 
	 public static
	 MIDFactory getInstance ( )
	 {
			
			return MID_FACTORY;
	 }
	 
	 public
	 MID getMID ( String idTag )
	 {
			
			MID mid = midHashMap.get ( idTag );
			if ( mid == null )
			{
				 mid = new MID ( idTag );
				 midHashMap.put ( idTag , mid );
				 saveFactory ( );
			}
			return mid;
	 }
	 
	 static
	 String getRootMishaIdPath ( )
	 {
			
			return System.getProperty ( "user.home" ) + File.separator + ".misha" + File.separator + "mids";
	 }
	 
	 static
	 File getRootMishaId ( )
	 {
			
			String path = getRootMishaIdPath ( );
			File   file = new File ( path );
			if ( !file.exists ( ) ) System.out.println ( "MIDs PATH IS GENERATED " + file.mkdirs ( ) );
			return file;
	 }
	 
	 private
	 void saveFactory ( )
	 {
			
			for ( String key : midHashMap.keySet ( ) )
				 midHashMap.get ( key ).saveState ( false );
			S3Utils.getS3Utils ( ).uploadDirectory ( getRootMishaId ( ) );
	 }
	 
	 private
	 void loadFactory ( )
	 {
			
			try
			{
				 File file    = getRootMishaId ( );
				 File files[] = file.listFiles ( );
				 S3Utils.getS3Utils ( ).fetchDirectory ( file );
				 files = file.listFiles ( );
				 if ( files == null ) return;
				 for ( File f : files )
				 {
						Scanner       scanner = new Scanner ( f );
						StringBuilder builder = new StringBuilder ( );
						while ( scanner.hasNext ( ) )
						{
							 String line = scanner.nextLine ( );
							 builder.append ( line );
						}
						scanner.close ( );
						JSONObject object = MJSON.toJSON ( builder.toString ( ) );
						MID        mid    = new MID ( object );
						midHashMap.put ( mid.getIdTag ( ) , mid );
				 }
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
	 }
}
