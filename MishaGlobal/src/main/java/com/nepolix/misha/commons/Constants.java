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

package com.nepolix.misha.commons;

/**
 * @author Behrooz Shahriari
 * @since 12/22/16
 */
public
interface Constants
{
	 
	 int INTERVAL_1_MINUTE = 60000;
	 
	 int INTERVAL_5_MINUTES = INTERVAL_1_MINUTE * 5;
	 
	 int INTERVAL_10_MINUTES = 2 * INTERVAL_5_MINUTES;
	 
	 int INTERVAL_15_MINUTES = INTERVAL_5_MINUTES * 3;
	 
	 int INTERVAL_30_MINUTES = INTERVAL_15_MINUTES * 2;
	 
	 int INTERVAL_1_HOUR = INTERVAL_30_MINUTES * 2;
	 
	 int INTERVAL_6_HOURS = INTERVAL_1_HOUR * 6;
	 
	 int INTERVAL_12_HOURS = INTERVAL_6_HOURS * 2;
	 
	 int INTERVAL_1_DAY = INTERVAL_12_HOURS * 2;
	 
	 int INTERVAL_1_WEEK = INTERVAL_1_DAY * 7;
}
