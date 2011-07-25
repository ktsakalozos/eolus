/*
 * Copyright 2008 William Voorsluys
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.opennebula.bridge;

public class CloudConstants {

	public enum VMAction {
		shutdown, suspend, resume, restart, poweroff
	};

	public enum VMState {
		STAGING, SUSPENDED, RUNNING, FAILED, STOPPED
	};

	public enum HostAction {
		disable, enable, delete
	};

	public enum HostState {
		STAGING, MONITORED, DISABLED, FAILED
	};

	public enum VNetAction {
		delete
	};

	public enum VNetState {
		STAGING, READY, FAILED
	};

}