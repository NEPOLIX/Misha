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

package com.nepolix.misha.db.aurora.driver;

/**
 * @author Behrooz Shahriari
 * @since 7/9/17
 */
public final
class MishaDBConstants
{
	 
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_1MIN = 60000;
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_3MIN = 3 * CACHE_EXPIRATION_DURATION_TIME_1MIN;
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_10MIN = 10 * CACHE_EXPIRATION_DURATION_TIME_1MIN;
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_15MIN = 15 * CACHE_EXPIRATION_DURATION_TIME_1MIN;
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_30MIN = 2 * CACHE_EXPIRATION_DURATION_TIME_15MIN;
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_1HOUR = 2 * CACHE_EXPIRATION_DURATION_TIME_30MIN;
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_3HOUR = 3 * CACHE_EXPIRATION_DURATION_TIME_1HOUR;
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_6HOUR = 2 * CACHE_EXPIRATION_DURATION_TIME_3HOUR;
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_12HOUR = 2 * CACHE_EXPIRATION_DURATION_TIME_6HOUR;
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_24HOUR = 2 * CACHE_EXPIRATION_DURATION_TIME_12HOUR;
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_72HOUR = 3 * CACHE_EXPIRATION_DURATION_TIME_24HOUR;
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_NEO_DB_CACHE = CACHE_EXPIRATION_DURATION_TIME_24HOUR * 3;
	 
	 
	 public final static long CACHE_EXPIRATION_DURATION_TIME_INFINITY = 100000 * CACHE_EXPIRATION_DURATION_TIME_72HOUR;
	 
	 final static String MISHA_DATABASE_MODELS = "MishaDBModels";
	 
	 final static String jdbcDriver = "org.mariadb.jdbc.Driver";
	 
	 public final static String DB_USER = "root";
	 
	 public static String DB_USER_PASS;
	 
	 public static String DB_WRITE_END_POINT_CLUSTER;
	 
	 public static String DB_READ_END_POINT_CLUSTER;
	 
	 final static String COLLECTION_DATABASE_NAME = "OBJECTS";
	 
	 final static String[] COLLECTION_DATABASE_NAME_COLUMNS = new String[] {
					 "object_name"
	 };
	 
	 public final static String TABLE_NAME = "MISHA";
	 
	 final static String[] COLUMNS = new String[] {
					 "mid" ,
//0
					 "hash" ,
//1
					 "type" ,
//2
//OBJECT/FIELD
					 "field_chain" ,
//3
					 "field_type" ,
//4
					 "v_int" ,
//5
					 "v_double" ,
//6
					 "v_string"
//7
	 };
}
