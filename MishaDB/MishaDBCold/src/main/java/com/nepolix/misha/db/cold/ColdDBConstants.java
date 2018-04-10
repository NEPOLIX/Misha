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

package com.nepolix.misha.db.cold;

/**
 * @author Behrooz Shahriari
 * @since 2/22/18
 */
public abstract
class ColdDBConstants
{
	 
	 public final static String[] FIELD_COLUMNS = new String[] {
					 "uid" ,
//0
					 "mid" ,
//1
					 "field_chain" ,
//2
					 "field_type" ,
//3
					 "v_int" ,
//4
					 "v_double" ,
//5
					 "v_string" ,
//6
					 "update_time"
	 };
	 
	 public final static String[] OBJECT_COLUMNS = new String[] {
					 "uid" ,
					 "mid" ,
					 "update_time" ,
					 "object"
	 };
	 
	 public final static String FIELD_TYPE_STRING = "STRING";
	 
	 public final static String FIELD_TYPE_INTEGER = "INTEGER";
	 
	 public final static String FIELD_TYPE_LONG = "LONG";
	 
	 public final static String FIELD_TYPE_BOOLEAN = "BOOLEAN";
	 
	 public final static String FIELD_TYPE_DOUBLE = "DOUBLE";
	 
	 /**
		* default should be, 0:west, 1:east, 2:europe
		*/
	 private static Integer regionIndex;
	 
	 private static String[] storeClusterAddress;
	 
	 
	 public static
	 void initClusterAddresses ( String[] storeClusterAddress ,
															 Integer regionIndex )
	 {
			
			ColdDBConstants.storeClusterAddress = storeClusterAddress;
			ColdDBConstants.regionIndex = regionIndex;
	 }
	 
	 public static
	 String getStoreClusterAddress ( )
	 {
			
			return storeClusterAddress[ regionIndex ];
	 }
	 
	 public static
	 int getPort ( )
	 {
			
			return 23576;
	 }
	 
	 private static String AWS_ACCESS_KEY;
	 
	 private static String AWS_PRIVATE_KEY;
	 
	 public static
	 String getAwsAccessKey ( )
	 {
			
			return AWS_ACCESS_KEY;
	 }
	 
	 public static
	 void setAwsAccessKey ( String awsAccessKey )
	 {
			
			AWS_ACCESS_KEY = awsAccessKey;
	 }
	 
	 public static
	 String getAwsPrivateKey ( )
	 {
			
			return AWS_PRIVATE_KEY;
	 }
	 
	 public static
	 void setAwsPrivateKey ( String awsPrivateKey )
	 {
			
			AWS_PRIVATE_KEY = awsPrivateKey;
	 }
	 
	 private static String ATHENA_URL;
	 
	 public static
	 String getAthenaUrl ( )
	 {
			
			return ATHENA_URL;
	 }
	 
	 public static
	 void setAthenaUrl ( String athenaUrl )
	 {
			
			ATHENA_URL = athenaUrl;
	 }
	 
	 public final static String ATHENA_OUTPUT_BUCKET = "s3://athena-cold-db/query-results/";
	 
	 public static String ACCOUNT_ID = "misha";
	 
	 public static
	 String getAccountId ( )
	 {
			
			return ACCOUNT_ID;
	 }
	 
	 public static
	 void setAccountId ( String accountId )
	 {
			
			ACCOUNT_ID = accountId;
	 }
	 
	 public static
	 int getLoggerListenerServerPort ( )
	 {
			
			return 23573;
	 }
}
