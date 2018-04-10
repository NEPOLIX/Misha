/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to HEX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.nepolix.misha.json.bson;

/**
 * @author Behrooz Shahriari
 * @since 11/24/16
 */
public final
class ObjectId
				extends MBSON
{
	 
	 private String $oid;
	 
	 
	 public
	 String get$oid ( )
	 {
			
			return $oid;
	 }
	 
	 public
	 void set$oid ( String $oid )
	 {
			
			this.$oid = $oid;
	 }
	 
	 @Override
	 public
	 String $value ( )
	 {
			
			return $oid;
	 }
}
