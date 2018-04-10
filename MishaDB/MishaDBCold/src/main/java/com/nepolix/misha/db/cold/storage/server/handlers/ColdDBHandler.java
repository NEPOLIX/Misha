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

package com.nepolix.misha.db.cold.storage.server.handlers;

import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.db.cold.ColdDBConstants;
import com.nepolix.misha.db.cold.storage.listener.ListenerServerBridge;
import com.nepolix.misha.db.cold.storage.server.handlers.files.ColdDBLocalFileCollector;
import com.nepolix.misha.id.client.MishaID;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;
import com.nepolix.misha.socket.server.ISocketServerExchange;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Behrooz Shahriari
 * @since 2/21/18
 */
public
class ColdDBHandler
				implements ISocketServerExchange
{
	 
	 private final ListenerServerBridge listenerServerBridge;
	 
	 public
	 ColdDBHandler ( ListenerServerBridge listenerServerBridge )
	 {
			
			this.listenerServerBridge = listenerServerBridge;
	 }
	 
	 @Override
	 public
	 String handler ( String message ,
										InetAddress socketInetAddress )
	 {
			
			if ( message != null )
			{
				 JSONObject request = MJSON.toJSON ( message );
				 if ( request != null )
				 {
						String method = request.optString ( "method" );
						if ( method.equals ( "store" ) )
						{
							 JSONObject credentials    = request.optJSONObject ( "credentials" );
							 String     mishaIdAddress = request.optString ( "misha_id_address" );
							 MishaID.initInetAddress ( mishaIdAddress );
							 ColdDBConstants.setAwsAccessKey ( credentials.optString ( "access_key" ) );
							 ColdDBConstants.setAwsPrivateKey ( credentials.optString ( "private_key" ) );
							 String accountId = request.optString ( "account" );
							 saveS3AWSCredentials ( credentials );
							 String    collectionName = request.optString ( "collection_name" );
							 JSONArray objects        = request.optJSONArray ( "objects" );
							 if ( objects != null ) listenerServerBridge.routeServerListener ( objects.toString ( ) );
							 ColdDBLocalFileCollector.getColdDbLocalFileCollector ( ).store ( accountId , collectionName , objects );
							 return "OK";
						}
						if ( method.equals ( "sync" ) )
						{
							 String mishaIdAddress = request.optString ( "misha_id_address" );
							 MishaID.initInetAddress ( mishaIdAddress );
							 String accountId = request.optString ( "account" );
							 ColdDBLocalFileCollector.getColdDbLocalFileCollector ( ).sync ( accountId , request.optBoolean ( "delete_local_files" ) );
							 return "OK";
						}
						if ( method.equals ( "health" ) )
						{
							 return "green";
						}
						if ( method.equals ( "restart" ) )
						{
							 return restart ( );
						}
				 }
			}
			return "invalid request";
	 }
	 
	 private
	 String restart ( )
	 {
			
			try
			{
				 ColdDBLocalFileCollector.getColdDbLocalFileCollector ().syncAll();
				 String home = "/home/ubuntu/";
				 Runtime.getRuntime ( ).exec ( "rm " + home + "rc.local.log" );
				 File       file       = new File ( home + "restart.sh" );
				 FileWriter fileWriter = new FileWriter ( file );
				 fileWriter.write ( "#!/bin/sh\nsh " + home + "cold_db-rc.sh >> " + home + "rc.local.log 2>&1 &\n" );
				 fileWriter.close ( );
				 Runtime.getRuntime ( ).exec ( "chmod +x " + home + "restart.sh" );
				 Runtime.getRuntime ( ).exec ( home + "restart.sh" );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
				 return JSONException.exceptionToJSON ( e ).toString ( );
			}
			return "restarting";
	 }
	 
	 private
	 void saveS3AWSCredentials ( JSONObject credentials )
	 {
			
			final String ROOT_PATH = System.getProperty ( "user.home" ) + File.separator + ".misha";
			File         file      = new File ( ROOT_PATH );
			if ( !file.exists ( ) ) file.mkdirs ( );
			file = new File ( ROOT_PATH + File.separator + "s3_aws_credentials" );
			if ( !file.exists ( ) || ( file.exists ( ) && Utils.getRandom ( ).nextDouble ( ) < 0.001 ) )
			{
				 try
				 {
						Files.write ( Paths.get ( file.toURI ( ) ) , credentials.toString ( ).getBytes ( ) );
				 }
				 catch ( IOException e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
	 
}
