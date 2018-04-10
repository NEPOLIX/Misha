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

package com.nepolix.misha.db.connection;

import com.nepolix.misha.commons.Constants;
import com.nepolix.misha.commons.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Behrooz Shahriari
 * @since 6/9/17
 */
public
class DBConnectionPool
{
	 
	 private static int MAX_CONNECTION = 2;
	 
	 private static boolean SINGLE_SOCKET_ON_DEMAND = true;
	 
	 private ConcurrentHashMap< String, BlockingQueue< DBConnection > > pool;
	 
	 private boolean writeConnection;
	 
	 private String endPointAddress;
	 
	 public
	 DBConnectionPool ( String endPointAddress ,
											boolean writeConnection )
	 {
			
			this.endPointAddress = endPointAddress;
			this.writeConnection = writeConnection;
			pool = new ConcurrentHashMap<> ( );
	 }
	 
	 public static
	 void setMaxConnection ( int maxConnection )
	 {
			
			MAX_CONNECTION = maxConnection;
	 }
	 
	 public static
	 void setSingleSocketOnDemand ( boolean singleSocketOnDemand )
	 {
			
			SINGLE_SOCKET_ON_DEMAND = singleSocketOnDemand;
	 }
	 
	 public synchronized
	 DBConnection getConnection ( String user ,
																String pass ,
																String collectionName )
	 {
			
			if ( SINGLE_SOCKET_ON_DEMAND )
			{
				 try
				 {
						return generateConnection ( collectionName , user , pass );
				 }
				 catch ( SQLException e )
				 {
						e.printStackTrace ( );
				 }
				 return null;
			}
			else
			{
				 resetConnections ( );
				 BlockingQueue< DBConnection > connections = pool.get ( collectionName );
				 if ( connections == null )
				 {
						connections = new ArrayBlockingQueue<> ( MAX_CONNECTION );
						for ( int i = 0 ; i < MAX_CONNECTION / 2 ; ++i )
						{
							 addDBConnections ( connections , user , pass , collectionName );
						}
						pool.put ( collectionName , connections );
				 }
				 DBConnection             dbConnection = null;
				 Iterator< DBConnection > iterator     = connections.iterator ( );
				 while ( iterator.hasNext ( ) )
				 {
						DBConnection connection = iterator.next ( );
						if ( !DBConnection.validConnection ( connection ) )
						{
							 iterator.remove ( );
						}
						else
						{
							 dbConnection = connection;
							 break;
						}
				 }
				 if ( dbConnection == null )
				 {
						connections = updateDBConnections ( connections , user , pass , collectionName );
				 }
				 try
				 {
						dbConnection = connections.take ( );
				 }
				 catch ( InterruptedException e )
				 {
						e.printStackTrace ( );
				 }
				 return dbConnection;
			}
	 }
	 
	 private
	 BlockingQueue< DBConnection > updateDBConnections ( BlockingQueue< DBConnection > connections ,
																											 String user ,
																											 String pass ,
																											 String collectionName )
	 {
			
			if ( connections.size ( ) < MAX_CONNECTION / 3 )
			{
				 int size = Math.min ( MAX_CONNECTION / 4 , connections.size ( ) + 1 );
				 for ( int i = 0 ; i < size ; ++i )
				 {
						addDBConnections ( connections , user , pass , collectionName );
				 }
			}
			if ( connections.size ( ) < MAX_CONNECTION / 2 && Utils.getRandom ( ).nextBoolean ( ) )
			{
				 addDBConnections ( connections , user , pass , collectionName );
			}
			return connections;
	 }
	 
	 private
	 void addDBConnections ( BlockingQueue< DBConnection > connections ,
													 String user ,
													 String pass ,
													 String collectionName )
	 {
			
			try
			{
				 connections.add ( generateConnection ( collectionName , user , pass ) );
			}
			catch ( SQLException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 private
	 DBConnection generateConnection ( String collectionName ,
																		 String user ,
																		 String pass )
					 throws
					 SQLException
	 {
			
			long       t          = System.currentTimeMillis ( );
			Connection connection = DriverManager.getConnection ( "jdbc:mariadb://" + endPointAddress + ":3306/" + collectionName + "?autoReconnect=true&useUnicode=true" , user , pass );
			Statement  statement  = connection.createStatement ( );
			statement.execute ( "SET NAMES utf8mb4" );
			statement.close ( );
			System.out.println ( "aurora generateConnection   " + ( System.currentTimeMillis ( ) - t ) );
			return new DBConnection ( connection , collectionName , writeConnection );
	 }
	 
	 public
	 void returnConnection ( DBConnection connection )
	 {
			
			if ( SINGLE_SOCKET_ON_DEMAND )
			{
				 try
				 {
						connection.getConnection ( ).close ( );
				 }
				 catch ( SQLException e )
				 {
						e.printStackTrace ( );
				 }
			}
			else
			{
				 BlockingQueue< DBConnection > list = pool.get ( connection.getDbName ( ) );
				 list.offer ( connection );
			}
	 }
	 
	 public
	 boolean isWriteConnection ( )
	 {
			
			return writeConnection;
	 }
	 
	 private long lastResetTime = -1;
	 
	 public
	 void resetConnections ( )
	 {
			
			synchronized ( this )
			{
				 if ( lastResetTime < 0 )
				 {
						lastResetTime = System.currentTimeMillis ( );
						return;
				 }
				 if ( System.currentTimeMillis ( ) - lastResetTime > Constants.INTERVAL_30_MINUTES )
				 {
						if ( pool != null )
						{
							 for ( String key : pool.keySet ( ) )
							 {
									BlockingQueue< DBConnection > queue = pool.get ( key );
									if ( queue != null )
									{
										 for ( DBConnection aQueue : queue )
										 {
												try
												{
													 aQueue.getConnection ( ).close ( );
												}
												catch ( SQLException e )
												{
													 e.printStackTrace ( );
												}
										 }
										 queue.clear ( );
									}
							 }
							 pool.clear ( );
							 lastResetTime = System.currentTimeMillis ( );
							 System.out.println ( "resetConnections  DBConnection" );
						}
				 }
			}
	 }
}
