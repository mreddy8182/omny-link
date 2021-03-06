/*******************************************************************************
 *Copyright 2011-2018 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.knowprocess.core.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Consts {

    public static final String PERSISTENCE_UNIT = "bbc-pu";
    public static final String PT_CASE_MGMT = "CaseMgmtProcess";
    public static final String KEY_PRINCIPAL = "com.bigbpmcloud.Principal";
    public static final String KEY_ID = "id";
    public static final String USER_ADMIN = "admin@bigbpmcloud.com";
    public static final String ACT_DONE = "done";
	public static final String ACT_UPDATE = "update";
	public static final String KEY_MARKUP = "markup";
	public static final int DEFAULT_PAGED_LIST_SIZE = 250;
	public static final String KEY_PROJECT = "project";
	public static final String KEY_LOCATION = "location";
	public static final String KEY_ESTIMATE = "estimate";

	public static final DateFormat isoDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	/**
	 * Support for ISO 8601 date and time serialization to and from clients.
	 * 
	 * <p>
	 * Note that this expects:
	 * <ul>
	 * <li>hours in 24 hour clock format ;
	 * <li>3 digits for fractions of second ;
	 * <li>Z for timezone (GMT or UTC).
	 */
	public static final DateFormat isoDateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd\'T\'hh:mm:ss.SSSZ");
	public static final String KEY_META_DATA = "_meta";
	public static final String ACT_CLAIM = "claim";
	public static final String COMMITTED = "committed";
	public static final String ACT_REOFFER = "reoffer";

}
