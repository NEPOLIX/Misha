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

package com.nepolix.misha.logger.client;

import com.nepolix.misha.db.cold.client.MishaColdDB;
import com.nepolix.misha.id.client.MishaID;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;
import com.nepolix.misha.logger.LoggerConstants;
import com.nepolix.misha.logger.log.Log;
import com.nepolix.misha.logger.log.LogTag;
import com.nepolix.misha.rest.client.WebClient;

import java.util.MissingResourceException;

/**
 * @author Behrooz Shahriari
 * @since 10/30/17
 */
public
class Logger
{
	 
	 private static Logger LOGGER;
	 
	 private final MishaColdDB mishaColdDB;
	 
	 private static String MY_IP = null;
	 
	 private
	 Logger ( MishaColdDB mishaColdDB )
	 {
			
			this.mishaColdDB = mishaColdDB;
			
			MishaID.getMishaID ( );
			WebClient.getInstance ( );
			MY_IP = WebClient.getInstance ( ).cUrl_ ( "http://checkip.amazonaws.com/" , WebClient.RESTMethod.GET );
			System.out.println ( "MY_IP='" + MY_IP + "'" );
	 }
	 
	 public static
	 Logger getLogger ( MishaColdDB mishaColdDB )
	 {
			
			if ( LOGGER == null ) LOGGER = new Logger ( mishaColdDB );
			return LOGGER;
	 }
	 
	 public static
	 Logger getLogger ( )
	 {
			
			if ( LOGGER != null ) return LOGGER;
			else
			{
				 try
				 {
						throw new MissingResourceException ( "first call 'getLogger ( MishaColdDB mishaColdDB )' for global logger." , Logger.class.getSimpleName ( ) , "loggerIP" );
				 }
				 catch ( Exception e )
				 {
						System.err.println ( "getLogger:" + e.getMessage ( ) );
				 }
				 return new Logger ( null );
			}
	 }
	 
	 public
	 Log buildLog ( String... topics )
	 {
			
			return Log.$build ( mishaColdDB , topics );
	 }
	 
	 public
	 Logger log ( Log log )
	 {
			
			try
			{
				 if ( log.getTopics ( ) != null && !log.getTopics ( ).isEmpty ( ) )
				 {
						if ( log.getMid ( ) == null )
						{
							 log.setMid ( MishaID.getMishaID ( ).nextID ( LoggerConstants.LOGGER_UUID_TAG ) );
						}
						log.setIp ( MY_IP );
						if ( mishaColdDB != null ) mishaColdDB.save ( log );
						else System.out.println ( MJSON.toString ( MJSON.toJSON ( log ) ) );
				 }
			}
			catch ( Exception | NoClassDefFoundError | ExceptionInInitializerError ignored )
			{
			}
			return this;
	 }
	 
	 public
	 void logE ( ExecuteTaskLog$Error taskLog )
	 {
			
			try
			{
				 taskLog.execute ( );
			}
			catch ( Exception e )
			{
				 logE ( e , "EXECUTE_TASK_LOG" , "ERROR" );
			}
	 }
	 
	 public
	 Logger logE ( Exception e ,
								 String... topics )
	 {
			
			Log log = Log.$build ( mishaColdDB , "LOG" , "EXCEPTION" );
			if ( topics != null )
			{
				 log.$addTopics ( topics );
			}
			JSONObject eJSON = JSONException.exceptionToJSON ( e );
			log.$addLogData ( LogTag.ERROR , "ExecuteTaskLog$Error= " + e.getMessage ( ) , eJSON ).$addLogData ( LogTag.ERROR , null , eJSON );
			return log ( log );
	 }
}
