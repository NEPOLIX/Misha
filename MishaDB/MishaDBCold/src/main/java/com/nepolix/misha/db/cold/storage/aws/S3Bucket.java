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

package com.nepolix.misha.db.cold.storage.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import java.io.File;

/**
 * @author Behrooz Shahriari
 * @since 2/19/18
 */
public
class S3Bucket
{
	 
	 private static AmazonS3 AMAZON_S3 = null;
	 
	 private static TransferManager TRANSFER_MANAGER = null;
	 
	 private static S3Bucket S3_BUCKET = null;
	 
	 private static String bucketName = "cold-db";
	 
	 private
	 S3Bucket ( )
	 {
			
			AMAZON_S3 = AmazonS3ClientBuilder.standard ( ).withRegion ( Regions.US_WEST_2 ).withCredentials ( new AWSStaticCredentialsProvider ( AWSCredentials.getAwsCredentials ( ) ) ).build ( );
			TRANSFER_MANAGER = TransferManagerBuilder.standard ( ).withS3Client ( AMAZON_S3 ).build ( );
	 }
	 
	 public static
	 String getBucketName ( )
	 {
			
			return bucketName;
	 }
	 
	 public static
	 void setBucketName ( String bucketName )
	 {
			
			S3Bucket.bucketName = bucketName;
	 }
	 
	 public static
	 S3Bucket getS3Bucket ( )
	 {
			
			if ( S3_BUCKET == null ) S3_BUCKET = new S3Bucket ( );
			return S3_BUCKET;
	 }
	 
	 public
	 void uploadDatabaseLogs ( String databaseKeyName ,
														 File databaseFile )
	 {
			
			try
			{
				 //https://stackoverflow.com/questions/6524041/how-do-you-make-an-s3-object-public-via-the-aws-java-sdk
				 PutObjectRequest putObjectRequest = new PutObjectRequest ( getBucketName ( ) , databaseKeyName , databaseFile ).withCannedAcl ( CannedAccessControlList.PublicRead );
				 ObjectMetadata   objectMetadata   = new ObjectMetadata ( );
				 objectMetadata.setSSEAlgorithm ( ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION );
				 putObjectRequest.setMetadata ( objectMetadata );
				 Upload upload = TRANSFER_MANAGER.upload ( putObjectRequest );
				 
				 upload.waitForUploadResult ( );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
	 }
}
