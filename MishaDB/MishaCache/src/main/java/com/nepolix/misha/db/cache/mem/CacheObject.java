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

/**
 * @author Behrooz Shahriari
 * @since 11/28/16
 */
public
class CacheObject
{
	 
	 private String value;
	 
	 private long expirationTime;
	 
	 public
	 CacheObject ( )
	 {
			
	 }
	 
	 public
	 CacheObject ( String value ,
								 long expirationTime )
	 {
			
			this.value = value;
			this.expirationTime = expirationTime;
	 }
	 
	 public
	 String getValue ( )
	 {
			
			return value;
	 }
	 
	 public
	 void setValue ( String value )
	 {
			
			this.value = value;
	 }
	 
	 public
	 long getExpirationTime ( )
	 {
			
			return expirationTime;
	 }
	 
	 public
	 void setExpirationTime ( long expirationTime )
	 {
			
			this.expirationTime = expirationTime;
	 }
	 
	 
	 @Override
	 public
	 boolean equals ( Object obj )
	 {
			
			if ( obj == null ) return false;
			if ( obj.getClass ( ).equals ( CacheObject.class ) )
			{
				 CacheObject cacheObject = ( CacheObject ) obj;
				 return cacheObject.getValue ( ).equals ( value );
			}
			else
			{
				 return false;
			}
	 }
	 
	 @Override
	 public
	 int hashCode ( )
	 {
			
			return value.hashCode ( );
	 }
	 
	 @Override
	 public
	 String toString ( )
	 {
			
			return "{\"$value\":'" + value + "',\"$exp\":" + expirationTime + "}";
	 }
}
