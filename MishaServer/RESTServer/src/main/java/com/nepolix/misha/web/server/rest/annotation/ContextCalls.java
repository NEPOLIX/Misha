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

package com.nepolix.misha.web.server.rest.annotation;

import java.lang.annotation.*;

/**
 * @author Behrooz Shahriari
 * @since 10/30/16
 */
@Documented
@Target ( ElementType.METHOD )
@Inherited
@Retention ( RetentionPolicy.CLASS )
public
@interface ContextCalls
{
	 
	 String rootPath ( );
	 
	 ApiCall[] apiCalls ( );
}
