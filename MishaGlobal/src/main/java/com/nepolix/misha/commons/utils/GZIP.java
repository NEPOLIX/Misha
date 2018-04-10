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

package com.nepolix.misha.commons.utils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Behrooz Shahriari
 * @since 7/21/17
 */
public
class GZIP
{
	 
	 public static
	 byte[] compress ( final String str )
					 throws
					 IOException
	 {
			
			if ( ( str == null ) || ( str.length ( ) == 0 ) )
			{
				 return null;
			}
			ByteArrayOutputStream obj  = new ByteArrayOutputStream ( str.length ( ) );
			GZIPOutputStream      gzip = new GZIPOutputStream ( obj );
			gzip.write ( str.getBytes ( "UTF-8" ) );
			gzip.flush ( );
			gzip.close ( );
			byte compressed[] = obj.toByteArray ( );
			obj.close ( );
			return compressed;
	 }
	 
	 public static
	 String decompress ( final byte[] compressed )
					 throws
					 IOException
	 {
			
			final StringBuilder sb = new StringBuilder ( );
			if ( ( compressed == null ) || ( compressed.length == 0 ) ) return "";
			if ( isCompressed ( compressed ) )
			{
				 ByteArrayInputStream bis = new ByteArrayInputStream ( compressed );
				 GZIPInputStream      gis = new GZIPInputStream ( bis );
				 BufferedReader       br  = new BufferedReader ( new InputStreamReader ( gis , "UTF-8" ) );
				 String               line;
				 while ( ( line = br.readLine ( ) ) != null )
				 {
						sb.append ( line );
				 }
				 br.close ( );
				 gis.close ( );
				 bis.close ( );
			}
			else sb.append ( compressed );
			return sb.toString ( );
	 }
	 
	 static
	 boolean isCompressed ( final byte[] compressed )
	 {
			
			return ( compressed[ 0 ] == ( byte ) ( GZIPInputStream.GZIP_MAGIC ) ) && ( compressed[ 1 ] == ( byte ) ( GZIPInputStream.GZIP_MAGIC >> 8 ) );
	 }
	 
	 public static
	 void decompressGzipFile ( String gzipFile ,
														 String newFile )
	 {
			
			try
			{
				 if ( gzipFile.equals ( newFile ) ) throw new IllegalArgumentException ( "file origin and destination can't be the same" );
				 FileInputStream  fis    = new FileInputStream ( gzipFile );
				 GZIPInputStream  gis    = new GZIPInputStream ( fis );
				 FileOutputStream fos    = new FileOutputStream ( newFile );
				 byte[]           buffer = new byte[ 1024 ];
				 int              len;
				 while ( ( len = gis.read ( buffer ) ) != -1 )
				 {
						fos.write ( buffer , 0 , len );
				 }
				 //close resources
				 fos.close ( );
				 gis.close ( );
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 public static
	 void compressGzipFile ( String file ,
													 String gzipFile )
	 {
			
			try
			{
				 if ( gzipFile.equals ( file ) ) throw new IllegalArgumentException ( "file origin and destination can't be the same" );
				 FileInputStream  fis    = new FileInputStream ( file );
				 FileOutputStream fos    = new FileOutputStream ( gzipFile );
				 GZIPOutputStream gzipOS = new GZIPOutputStream ( fos );
				 byte[]           buffer = new byte[ 1024 ];
				 int              len;
				 while ( ( len = fis.read ( buffer ) ) != -1 )
				 {
						gzipOS.write ( buffer , 0 , len );
				 }
				 //close resources
				 gzipOS.close ( );
				 fos.close ( );
				 fis.close ( );
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
	 }
}
