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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Behrooz Shahriari
 * @since 3/6/18
 */
class DBFileWriterMap
{
	 
	 private Map< String, WriterMap > writerMap;
	 
	 private boolean objectDB;
	 
	 DBFileWriterMap ( boolean objectDB )
	 {
			
			this.objectDB = objectDB;
			writerMap = new ConcurrentHashMap<> ( );
	 }
	 
	 public
	 Set< String > getAllAccounts ( )
	 {
			
			return writerMap.keySet ( );
	 }
	 
	 public
	 Map< String, ColdDBFileWriter > getMap ( String accountId )
	 {
			
			WriterMap x = writerMap.computeIfAbsent ( accountId , k -> new WriterMap ( ) );
			return x.fileWriterMap;
	 }
	 
	 public synchronized
	 ColdDBFileWriter getColdDBFileWriter ( String accountId ,
																					String collectionName )
	 {
			
			WriterMap x = writerMap.computeIfAbsent ( accountId , k -> new WriterMap ( ) );
			return x.getColdDBFileWriter ( accountId , collectionName );
	 }
	 
	 private
	 class WriterMap
	 {
			
			private Map< String, ColdDBFileWriter > fileWriterMap;
			
			WriterMap ( )
			{
				 
				 fileWriterMap = new ConcurrentHashMap<> ( );
			}
			
			synchronized
			ColdDBFileWriter getColdDBFileWriter ( String accountId ,
																						 String collectionName )
			{
				 
				 return fileWriterMap.computeIfAbsent ( collectionName , k -> new ColdDBFileWriter ( objectDB , accountId , collectionName ) );
			}
	 }
}
