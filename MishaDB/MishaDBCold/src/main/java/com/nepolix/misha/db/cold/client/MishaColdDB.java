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

import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.commons.xstructures.ModelIDList;
import com.nepolix.misha.db.MishaDB;
import com.nepolix.misha.db.MishaDB$A;
import com.nepolix.misha.db.cold.ColdDBConstants;
import com.nepolix.misha.db.cold.service.athena.AthenaHandler;
import com.nepolix.misha.db.exception.MishaSQLFormatException;
import com.nepolix.misha.db.model.MModel;
import com.nepolix.misha.id.MIDConstants;
import com.nepolix.misha.id.client.MishaID;
import com.nepolix.misha.id.exception.MishaIDException;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;
import com.nepolix.misha.socket.SocketChannel;
import com.nepolix.misha.socket.SocketChannelPool;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Behrooz Shahriari
 * @since 2/22/18
 */
public
class MishaColdDB
				extends MishaDB$A
{
	 
	 private static MishaColdDB mishaColdDB = null;
	 
	 private SocketChannelPool socketChannelPool;
	 
	 public
	 MishaColdDB ( MishaID mishaID )
	 {
			
			super ( mishaID );
			socketChannelPool = SocketChannelPool.build ( 2 , ColdDBConstants.getStoreClusterAddress ( ) , ColdDBConstants.getPort ( ) , 120000 );
	 }
	 
	 public static
	 void initInetAddress ( String[] storeClusterAddress ,
													Integer regionIdx )
	 {
			
			ColdDBConstants.initClusterAddresses ( storeClusterAddress , regionIdx );
	 }
	 
	 public static
	 MishaColdDB init ( MishaID mishaID ,
											String[] storeClusterAddress ,
											Integer regionIdx ,
											String AWS_ACCESS_KEY ,
											String AWS_PRIVATE_KEY ,
											String athenaURL ,
											String accountId )
	 {
			
			ColdDBConstants.setAwsAccessKey ( AWS_ACCESS_KEY );
			ColdDBConstants.setAwsPrivateKey ( AWS_PRIVATE_KEY );
			ColdDBConstants.setAthenaUrl ( athenaURL );
			ColdDBConstants.setAccountId ( accountId );
			initInetAddress ( storeClusterAddress , regionIdx );
			if ( mishaColdDB == null ) mishaColdDB = new MishaColdDB ( mishaID );
			return getInstance ( );
	 }
	 
	 public static
	 MishaColdDB getInstance ( )
	 {
			
			try
			{
				 ColdDBConstants.getStoreClusterAddress ( );
			}
			catch ( Exception e )
			{
				 throw new MissingResourceException ( "Please first call 'initInetAddress' to init the address of cluster nodes with region index" , MishaColdDB.class.getSimpleName ( ) , "cluster address" );
			}
			if ( mishaColdDB == null ) throw new NullPointerException ( "first initialize the misha-cold-db" );
			else return mishaColdDB;
	 }
	 
	 @Override
	 protected
	 void init ( )
	 {
			
	 }
	 
	 public
	 void sync ( boolean deleteLocal )
	 {
			
			JSONObject message = new JSONObject ( );
			message.putOpt ( "method" , "sync" ).putOpt ( "misha_id_address" , MIDConstants.getMishaIdAddress ( ) ).putOpt ( "delete_local_files" , deleteLocal )
						 .putOpt ( "account" , ColdDBConstants.getAccountId ( ) );
			SocketChannel socketChannel = socketChannelPool.getChannel ( );
			try
			{
				 socketChannel.writeMessage ( message.toString ( ) );
				 socketChannel.readMessage ( );
				 socketChannelPool.returnChannel ( socketChannel );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 @Override
	 public
	 < T extends MModel > T findOne ( JSONObject query ,
																		Class< T > clazz )
	 {
			
			List< T > list = find ( query , 1 , 0 , clazz );
			if ( list != null && !list.isEmpty ( ) ) return list.get ( 0 );
			return null;
	 }
	 
	 @Override
	 public
	 < T extends MModel > T findOne ( String mid ,
																		Class< T > clazz )
	 {
			
			Set< String > set = new LinkedHashSet<> ( );
			set.add ( mid );
			Set< T > result = findObjects ( set , clazz );
			if ( result != null && !result.isEmpty ( ) ) return result.iterator ( ).next ( );
			else return null;
	 }
	 
	 
	 @Override
	 public
	 JSONObject findOne ( String mid ,
												String collectionName )
	 {
			
			Set< String > mids = new LinkedHashSet<> ( );
			mids.add ( mid );
			List< JSONObject > list;
			try
			{
				 list = AthenaHandler.getAthenaHandler ( ).getLastObjects ( mids , collectionName );
				 if ( !list.isEmpty ( ) ) return list.get ( 0 );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
			return null;
	 }
	 
	 public
	 Map< String, List< JSONObject > > findObjects ( JSONObject query ,
																									 int limit ,
																									 Integer limitPerObject ,
																									 String collectionName )
	 {
			
			
			List< JSONObject > list = find ( query , limit , 0 , collectionName );
			if ( list != null || !list.isEmpty ( ) )
			{
				 Set< String > mids = new LinkedHashSet<> ( );
				 list.forEach ( v -> mids.add ( v.optString ( "mid" ) ) );
				 return fetchObjects ( mids , collectionName , limitPerObject );
			}
			return null;
	 }
	 
	 public
	 < T extends MModel > Map< String, List< T > > findObjects ( JSONObject query ,
																															 int limit ,
																															 Integer limitPerObject ,
																															 Class< T > clazz )
	 {
			
			String                            collectionName = MModel.getModelName ( clazz );
			Map< String, List< JSONObject > > map            = findObjects ( query , limit , limitPerObject , collectionName );
			if ( map != null )
			{
				 Map< String, List< T > > models = new LinkedHashMap<> ( );
				 map.keySet ( ).forEach ( k -> {
						List< T > list = new ArrayList<> ( );
						map.get ( k ).forEach ( v -> list.add ( MJSON.toObject ( v , clazz ) ) );
						models.put ( k , list );
				 } );
				 return models;
			}
			return null;
	 }
	 
	 @Override
	 public
	 < T extends MModel > List< T > find ( JSONObject query ,
																				 int limit ,
																				 int offset ,
																				 Class< T > clazz )
	 {
			
			List< T >          models         = new ArrayList<> ( );
			String             collectionName = MModel.getModelName ( clazz );
			List< JSONObject > list           = find ( query , limit , offset , collectionName );
			if ( list != null || !list.isEmpty ( ) )
			{
				 list.forEach ( v -> models.add ( MJSON.toObject ( v , clazz ) ) );
			}
			return models;
	 }
	 
	 @Override
	 public
	 List< JSONObject > find ( JSONObject query ,
														 int limit ,
														 int offset ,
														 String collectionName )
	 {
			
			try
			{
				 return AthenaHandler.getAthenaHandler ( ).find ( query , limit , collectionName );
			}
			catch ( MishaSQLFormatException e )
			{
				 e.printStackTrace ( );
			}
			return null;
	 }
	 
	 @Override
	 public
	 < T extends MModel > Set< T > findObjects ( Collection< String > mids ,
																							 Class< T > clazz )
	 {
			
			try
			{
				 String   collectionName = MModel.getModelName ( clazz );
				 Set< T > set            = new LinkedHashSet<> ( );
				 AthenaHandler.getAthenaHandler ( ).getLastObjects ( mids , collectionName ).forEach ( v -> set.add ( MJSON.toObject ( v , clazz ) ) );
				 return set;
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
			return null;
	 }
	 
	 public
	 < T extends MModel > Map< String, List< JSONObject > > fetchObjects ( final Collection< String > mids ,
																																				 String collectionName ,
																																				 Integer limitPerObject )
	 {
			
			try
			{
				 return AthenaHandler.getAthenaHandler ( ).getObjects ( mids , collectionName , limitPerObject );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
			return null;
	 }
	 
	 public
	 < T extends MModel > Map< String, List< T > > fetchObjects ( final Collection< String > mids ,
																																Class< T > clazz ,
																																Integer limitPerObject )
	 {
			
			try
			{
				 String                            collectionName = MModel.getModelName ( clazz );
				 Map< String, List< JSONObject > > mapModelsX     = AthenaHandler.getAthenaHandler ( ).getObjects ( mids , collectionName , limitPerObject );
				 Map< String, List< T > >          models         = new LinkedHashMap<> ( );
				 mapModelsX.keySet ( ).forEach ( k -> {
						List< T > list = new ArrayList<> ( );
						mapModelsX.get ( k ).forEach ( v -> list.add ( MJSON.toObject ( v , clazz ) ) );
						models.put ( k , list );
				 } );
				 return models;
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
			return null;
	 }
	 
	 @Override
	 public
	 < T extends MModel > MishaDB save ( Collection< T > objects )
	 {
			
			if ( objects != null && !objects.isEmpty ( ) )
			{
				 JSONArray                 objectArray    = new JSONArray ( );
				 AtomicReference< String > collectionName = new AtomicReference<> ( );
				 objects.forEach ( o -> {
						objectArray.put ( MJSON.toJSON ( o ) );
						o.setUpdateTime ( Utils.getCurrentUTCTime ( ) );
						collectionName.set ( o.modelName ( ) );
				 } );
				 S3SaveHelper.getSaveHelper ( ).store ( socketChannelPool , collectionName.get ( ) , objectArray );
			}
			return this;
	 }
	 
	 @Override
	 public
	 void save ( String collectionName ,
							 JSONObject object )
	 {
			
			JSONArray objectArray = new JSONArray ( );
			objectArray.put ( object );
			S3SaveHelper.getSaveHelper ( ).store ( socketChannelPool , collectionName , objectArray );
	 }
	 
	 @Override
	 public
	 void save ( MModel object )
					 throws
					 MishaIDException,
					 NullPointerException
	 {
			
			if ( object != null )
			{
				 if ( object.getMid ( ) == null || object.getMid ( ).isEmpty ( ) ) throw new MishaIDException ( "object must contain 'mid::String' field " + "is a unique " + "identifier" );
				 object.setUpdateTime ( Utils.getCurrentUTCTime ( ) );
				 save ( object.modelName ( ) , MJSON.toJSON ( object ) );
			}
	 }
	 
	 @Override
	 public
	 < T extends MModel > int delete ( JSONObject query ,
																		 Class< T > clazz )
	 {
			
			throw new UnsupportedOperationException ( "delete not supported for coldDB" );
	 }
	 
	 @Override
	 public
	 int deleteObjects ( Collection< String > mids ,
											 String collectionName )
	 {
			
			throw new UnsupportedOperationException ( "deleteObjects not supported for coldDB" );
	 }
	 
	 @Override
	 public
	 < T extends MModel > ModelIDList getCollectionIDs ( Class< T > modelClass )
	 {
			
			return null;
	 }
}
