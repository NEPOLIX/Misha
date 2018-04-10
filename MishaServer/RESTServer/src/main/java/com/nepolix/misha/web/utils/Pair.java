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

package com.nepolix.misha.web.utils;

/**
 * @author Behrooz Shahriari
 * @since 10/29/16
 */
public
class Pair < E1, E2 >
{
	 
	 private E1 e1;
	 
	 private E2 e2;
	 
	 public
	 Pair ( E1 e1 ,
					E2 e2 )
	 {
			
			this.e1 = e1;
			this.e2 = e2;
	 }
	 
	 public
	 E2 getE2 ( )
	 {
			
			return e2;
	 }
	 
	 public
	 void setE2 ( E2 e2 )
	 {
			
			this.e2 = e2;
	 }
	 
	 public
	 E1 getE1 ( )
	 {
			
			return e1;
	 }
	 
	 public
	 void setE1 ( E1 e1 )
	 {
			
			this.e1 = e1;
	 }
}
