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

import com.nepolix.misha.commons.utils.GZIP;
import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.db.cold.ColdDBConstants;
import com.nepolix.misha.db.cold.storage.aws.S3Bucket;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Behrooz Shahriari
 * @since 2/21/18
 */
public
class ColdDBFileWriter
{
	 
	 private final static String ROOT_PATH = System.getProperty ( "user.home" ) + File.separator + ".misha" + File.separator + "coldDB";
	 
	 private final static String RAW_JSON = "raw";
	 
	 private final static String GZIP_JSON = "gzip";
	 
	 public final static String FILE_SEPARATOR = "_$_";
	 
	 static
	 {
			File file = new File ( ROOT_PATH );
			file.mkdirs ( );
	 }
	 
	 private final ReentrantLock lock = new ReentrantLock ( );
	 
	 private final static long FILE_SIZE_LIMIT_OBJECT = 67108864L;//64MB
	 
	 private final static long FACTOR_OBJECT = 7L;
	 
	 private final static long FILE_SIZE_LIMIT_FIELDS = 268435456L;//256MB
	 
	 private final static long FACTOR_FIELDS = 2L;
	 
	 private final static long FILE_TIME_LIMIT = 10800000L;//3hr
	 
	 private final boolean objectPath;
	 
	 private final String accountId;
	 
	 private final String collectionName;
	 
	 private File file;
	 
	 private String path;
	 
	 private int idx = 0;
	 
	 private ByteBuffer[] byteBuffers = new ByteBuffer[ 128 ];
	 
	 private long time_fileOpenning;
	 
	 public
	 ColdDBFileWriter ( boolean objectPath ,
											String accountId ,
											String collectionName )
	 {
			
			this.objectPath = objectPath;
			this.accountId = accountId;
			this.collectionName = collectionName;
			
			generateFile ( );
	 }
	 
	 private
	 void generateFile ( )
	 {
			
			lock.lock ( );
			try
			{
				 Calendar calendar = Utils.getUTCTime ( );
				 int      year     = calendar.get ( Calendar.YEAR );
				 String   month    = calendar.get ( Calendar.MONTH ) < 10 ? "0" + calendar.get ( Calendar.MONTH ) : "" + calendar.get ( Calendar.MONTH );
				 String   day      = calendar.get ( Calendar.DAY_OF_MONTH ) < 10 ? "0" + calendar.get ( Calendar.DAY_OF_MONTH ) : "" + calendar.get ( Calendar.DAY_OF_MONTH );
				 path = ( objectPath ? "objects" : "fields" ) + ColdDBFileWriter.FILE_SEPARATOR + "account=" + accountId + ColdDBFileWriter.FILE_SEPARATOR + "db=" + collectionName
								+ ColdDBFileWriter.FILE_SEPARATOR + "year=" + year + ColdDBFileWriter.FILE_SEPARATOR + "month=" + month + ColdDBFileWriter.FILE_SEPARATOR + "day=" + day;
				 time_fileOpenning = Utils.getCurrentUTCTime ( );
				 file = new File ( ROOT_PATH + File.separator + path + FILE_SEPARATOR + "r-" + Math.abs ( Utils.getRandom ( ).nextLong ( ) ) );
			}
			finally
			{
				 lock.unlock ( );
			}
	 }
	 
	 public synchronized
	 void store ( String x )
	 {
			
			synchronized ( this )
			{
				 byteBuffers[ idx ] = ByteBuffer.wrap ( x.getBytes ( Charset.forName ( "UTF-8" ) ) );
				 idx++;
				 if ( write ( false ) ) sync ( false , true );
			}
	 }
	 
	 private
	 boolean write ( boolean force )
	 {
			
			boolean b = false;
			lock.lock ( );
			try
			{
				 if ( ( force && idx > 0 ) || idx >= byteBuffers.length )
				 {
						try
						{
							 FileChannel fileChannel = new FileOutputStream ( file , true ).getChannel ( );
							 fileChannel.write ( byteBuffers , 0 , idx );
							 fileChannel.close ( );
							 idx = 0;
							 Arrays.setAll ( byteBuffers , x -> null );
							 b = true;
						}
						catch ( Exception e )
						{
							 e.printStackTrace ( );
							 System.err.println ( "write:: idx=" + idx + " file=" + file.getAbsolutePath ( ) + " force=" + force );
						}
				 }
			}
			finally
			{
				 lock.unlock ( );
			}
			return b;
	 }
	 
	 public static
	 void syncOldFiles ( )
	 {
			
			try
			{
				 setupS3AWSCredentials ( );
			}
			catch ( IOException | JSONException e )
			{
				 e.printStackTrace ( );
				 return;
			}
			File root = new File ( ROOT_PATH );
			try
			{
				 File[] files = root.listFiles ( );
				 for ( File f : files )
				 {
						if ( !f.getPath ( ).endsWith ( ".gz" ) )
						{
							 syncLocalCloud ( f , true );
						}
						else
						{
							 f.delete ( );
						}
				 }
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 private static
	 void setupS3AWSCredentials ( )
					 throws
					 IOException,
					 JSONException
	 {
			
			JSONObject credentials = new JSONObject (
							new String ( Files.readAllBytes ( Paths.get ( System.getProperty ( "user.home" ) + File.separator + ".misha" + File.separator + "s3_aws_credentials" ) ) ) );
			ColdDBConstants.setAwsAccessKey ( credentials.optString ( "access_key" ) );
			ColdDBConstants.setAwsPrivateKey ( credentials.optString ( "private_key" ) );
	 }
	 
	 private
	 void sync ( boolean force ,
							 boolean deleteLocal )
	 {
			
			long fileSize = file.length ( );
			long nowTime  = Utils.getCurrentUTCTime ( );
			long fsl      = objectPath ? FILE_SIZE_LIMIT_OBJECT : FILE_SIZE_LIMIT_FIELDS;
			long _fsl     = objectPath ? FILE_SIZE_LIMIT_OBJECT / FACTOR_OBJECT : FILE_SIZE_LIMIT_FIELDS / FACTOR_FIELDS;
			if ( force || fileSize > fsl || ( nowTime - time_fileOpenning > FILE_TIME_LIMIT && fileSize > _fsl ) || ( nowTime - time_fileOpenning >= 8 * FILE_TIME_LIMIT ) )
			{
				 flushBuffer ( );
				 File txtFile = new File ( file.toURI ( ) );
				 if ( deleteLocal ) generateFile ( );
				 syncLocalCloudLock ( txtFile , deleteLocal );
			}
	 }
	 
	 private
	 void syncLocalCloudLock ( File txtFile ,
														 boolean deleteLocal )
	 {
			
			lock.lock ( );
			try
			{
				 syncLocalCloud ( txtFile , deleteLocal );
			}
			finally
			{
				 lock.unlock ( );
			}
	 }
	 
	 private static
	 void syncLocalCloud ( File txtFile ,
												 boolean deleteLocal )
	 {
			
			File zipFile = new File ( txtFile.getPath ( ) + ".gz" );
			GZIP.compressGzipFile ( txtFile.getPath ( ) , zipFile.getPath ( ) );
			String s3KeyPath = zipFile.getPath ( ).replace ( ROOT_PATH + File.separator , "" ).replace ( FILE_SEPARATOR , "/" );
			s3KeyPath = GZIP_JSON + "/" + s3KeyPath;
			S3Bucket.getS3Bucket ( ).uploadDatabaseLogs ( s3KeyPath , zipFile );
//			s3KeyPath = txtFile.getPath ( ).replace ( ROOT_PATH + File.separator , "" ).replace ( FILE_SEPARATOR , "/" );
//			s3KeyPath = RAW_JSON + "/" + s3KeyPath + ".txt";
//			S3Bucket.getS3Bucket ( ).uploadDatabaseLogs ( s3KeyPath , txtFile );
			if ( deleteLocal ) txtFile.delete ( );
			zipFile.delete ( );
	 }
	 
	 void flushBuffer ( )
	 {
			
			write ( true );
	 }
	 
	 public
	 void flushSync ( boolean deleteLocal )
	 {
			
			sync ( true , deleteLocal );
	 }
}
