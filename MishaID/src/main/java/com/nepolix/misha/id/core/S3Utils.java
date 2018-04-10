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

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import java.io.File;

/**
 * @author Behrooz Shahriari
 * @since 1/11/18
 */
class S3Utils
{
	 
	 private final static AmazonS3 AMAZON_S3 = AmazonS3ClientBuilder.standard ( ).withRegion ( Regions.US_WEST_2 ).withCredentials ( new AWSStaticCredentialsProvider ( Credentials.AWS_CREDENTIALS ) )
																																	.build ( );
	 
	 private final static String S3_BUCKET_MISHA_ID = "misha.id";
	 
	 private final static TransferManager TRANSFER_MANAGER = TransferManagerBuilder.standard ( ).withS3Client ( AMAZON_S3 ).build ( );
	 
	 private final static S3Utils S_3_UTILS = new S3Utils ( );
	 
	 private final static Object LOCK = new Object ( );
	 
	 private
	 S3Utils ( )
	 {
		
	 }
	 
	 static
	 S3Utils getS3Utils ( )
	 {
			
			return S_3_UTILS;
	 }
	 
	 synchronized
	 void uploadDirectory ( File root )
					 throws
					 AmazonServiceException
	 {
			
			synchronized ( LOCK )
			{
				 try
				 {
						MultipleFileUpload multipleFileUpload = TRANSFER_MANAGER.uploadDirectory ( S3_BUCKET_MISHA_ID , null , root , true );
						multipleFileUpload.waitForCompletion ( );
				 }
				 catch ( Exception e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
	 
	 synchronized
	 void fetchDirectory ( File saveRootPath )
					 throws
					 AmazonServiceException
	 {
			
			try
			{
				 MultipleFileDownload multipleFileDownload = TRANSFER_MANAGER.downloadDirectory ( S3_BUCKET_MISHA_ID , null , saveRootPath );
				 multipleFileDownload.waitForCompletion ( );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
	 }
}
