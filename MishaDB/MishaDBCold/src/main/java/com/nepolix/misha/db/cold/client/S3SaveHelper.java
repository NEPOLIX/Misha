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

package com.nepolix.misha.db.cold.client;

import com.nepolix.misha.db.cold.ColdDBConstants;
import com.nepolix.misha.id.MIDConstants;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.socket.SocketChannel;
import com.nepolix.misha.socket.SocketChannelPool;

/**
 * @author Behrooz Shahriari
 * @since 2/23/18
 */
class S3SaveHelper
{
	 
	 private final static S3SaveHelper SAVE_HELPER = new S3SaveHelper ( );
	 
	 private
	 S3SaveHelper ( )
	 {
		
	 }
	 
	 public static
	 S3SaveHelper getSaveHelper ( )
	 {
			
			return SAVE_HELPER;
	 }
	 
	 public
	 void store ( SocketChannelPool socketChannelPool ,
								String collectionName ,
								JSONArray objects )
	 {
			
			JSONObject message     = new JSONObject ( );
			JSONObject credentials = new JSONObject ( );
			credentials.putOpt ( "access_key" , ColdDBConstants.getAwsAccessKey ( ) ).putOpt ( "private_key" , ColdDBConstants.getAwsPrivateKey ( ) );
			message.putOpt ( "collection_name" , collectionName ).putOpt ( "objects" , objects ).putOpt ( "credentials" , credentials ).putOpt ( "method" , "store" )
						 .putOpt ( "misha_id_address" , MIDConstants.getMishaIdAddress ( ) ).putOpt ( "account" , ColdDBConstants.getAccountId ( ) );
			SocketChannel socketChannel = socketChannelPool.getChannel ( );
			try
			{
				 socketChannel.writeMessage ( message.toString ( ) );
				 socketChannel.readMessage ( );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
			finally
			{
				 socketChannelPool.returnChannel ( socketChannel );
			}
	 }
}
