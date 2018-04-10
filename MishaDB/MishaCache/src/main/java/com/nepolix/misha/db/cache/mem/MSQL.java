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

import java.util.List;

/**
 * @author Behrooz Shahriari
 * @since 11/18/16
 */
interface MSQL
{
	 
	 void save ( String collectionName ,
							 String key ,
							 List< String > values ,
							 long expirationTime );
	 
	 /**
		* delete all values for the key
		*
		* @param key
		*
		* @return
		*/
	 boolean delete ( String collectionName ,
										String key );
	 
	 boolean delete ( String collectionName ,
										String key ,
										List< String > values );
	 
	 boolean update ( String collectionName ,
										String key ,
										String oldValue ,
										String newValue ,
										long expirationTime );
	 
	 List< CacheObject > fetch ( String collectionName ,
															 String key );
	 
	 /**
		* delete all values for the given key and add the new one
		*
		* @param key
		* @param value
		*/
	 void reset ( String collectionName ,
								String key ,
								String value ,
								long expirationTime );
	 
	 void cleanObsoleteCaches ( );
	 
	 static
	 MSQL getMSQL ( )
	 {
			
			return SQLInterface.getSqlInterface ( );
	 }
	 
	 void cleanSlate ( );
}
