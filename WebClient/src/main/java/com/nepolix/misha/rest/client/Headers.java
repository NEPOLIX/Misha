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

package com.nepolix.misha.rest.client;

import java.util.HashMap;

/**
 * @author Behrooz Shahriari
 * @since 9/17/17
 */
public
class Headers
{
	 
	 private HashMap< String, String > headers;
	 
	 public
	 Headers ( )
	 {
			
			headers = new HashMap<> ( );
	 }
	 
	 public
	 Headers add ( String key ,
								 String value )
	 {
			
			headers.put ( key , value );
			return this;
	 }
	 
	 public
	 HashMap< String, String > getHeaders ( )
	 {
			
			return headers;
	 }
}
