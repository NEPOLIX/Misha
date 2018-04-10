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

package com.nepolix.misha.db.cache.mem;

import com.nepolix.misha.commons.utils.Utils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author Behrooz Shahriari
 * @since 8/23/17
 */
public
class CacheSet
{
	 
	 private LinkedHashSet< CacheObject > cacheObjects;
	 
	 private int cacheSize;
	 
	 CacheSet ( )
	 {
			
			cacheObjects = new LinkedHashSet<> ( );
	 }
	 
	 
	 public
	 long save ( String value ,
							 long expirationDuration )
	 {
			
			int size = 0;
			if ( value != null )
			{
				 CacheObject cacheObject = new CacheObject ( value , expirationDuration + Utils.getCurrentUTCTime ( ) );
				 for ( CacheObject co : cacheObjects )
				 {
						size -= co.getValue ( ).length ( );
				 }
				 cacheObjects.clear ( );
				 cacheObjects.add ( cacheObject );
				 size += value.length ( );
			}
			cacheSize += size;
			return size;
	 }
	 
	 public
	 long insert ( List< String > values ,
								 long expirationDuration )
	 {
			
			long size = 0;
			for ( String v : values )
			{
				 CacheObject tmp = new CacheObject ( v , expirationDuration + Utils.getCurrentUTCTime ( ) );
				 if ( cacheObjects.add ( tmp ) )
				 {
						size += v.length ( );
				 }
				 else
				 {
						cacheObjects.remove ( tmp );
						cacheObjects.add ( tmp );
				 }
			}
			cacheSize += size;
			return size;
	 }
	 
	 public
	 long delete ( )
	 {
			
			long size = 0;
			for ( CacheObject object : cacheObjects )
			{
				 size += object.getValue ( ).length ( );
			}
			cacheObjects.clear ( );
			cacheSize -= size;
			return size;
	 }
	 
	 public
	 long delete ( List< String > values )
	 {
			
			int size = 0;
			for ( String v : values )
			{
				 CacheObject tmp = new CacheObject ( v , 0 );
				 if ( cacheObjects.remove ( tmp ) )
				 {
						size += v.length ( );
				 }
			}
			cacheSize -= size;
			return size;
	 }
	 
	 public
	 long fetch ( final List< String > valuesOut )
	 {
			
			long                    size     = 0;
			long                    nowTime  = Utils.getCurrentUTCTime ( );
			Iterator< CacheObject > iterator = cacheObjects.iterator ( );
			while ( iterator.hasNext ( ) )
			{
				 CacheObject object = iterator.next ( );
				 if ( object.getExpirationTime ( ) < nowTime )
				 {
						size += object.getValue ( ).length ( );
						iterator.remove ( );
				 }
				 else
				 {
						valuesOut.add ( object.getValue ( ) );
				 }
			}
			size = -size;
			cacheSize += size;
			return size;
	 }
	 
	 public
	 int getCacheSize ( )
	 {
			
			return cacheSize;
	 }
}
