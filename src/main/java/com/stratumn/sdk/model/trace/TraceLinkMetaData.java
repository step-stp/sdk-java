/*
Copyright 2017 Stratumn SAS. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.stratumn.sdk.model.trace;

import java.util.Date;
/**
 * The link metadata
 */
public class TraceLinkMetaData {

	private String ownerId;
	private String groupId;
	private String formId;
	private String lastFormId;
	private Date createdAt;
	private String createdById;
	private String set;
	private String[] inputs;

	 
	public TraceLinkMetaData(  )
    {
      super(); 
    }



   TraceLinkMetaData(String ownerId, String groupId, String formId, String lastFormId, Date createdAt,
			String createdById, String[] inputs) throws IllegalArgumentException {
		if (ownerId == null) {
			throw new IllegalArgumentException("ownerId cannot be null");
		}
		if (groupId == null) {
			throw new IllegalArgumentException("groupId cannot be null");
		}
		if (formId == null) {
			throw new IllegalArgumentException("formId cannot be null");
		}
		if (lastFormId == null) {
			throw new IllegalArgumentException("lastFormId cannot be null");
		}
		if (createdAt == null) {
			throw new IllegalArgumentException("createdAt cannot be null");
		}
		if (createdById == null) {
			throw new IllegalArgumentException("createdById cannot be null");
		}
		if (inputs == null) {
			throw new IllegalArgumentException("inputs cannot be null");
		}
	 
		this.ownerId = ownerId;
		this.groupId = groupId;
		this.formId = formId;
		this.lastFormId = lastFormId;
		this.createdAt = createdAt;
		this.createdById = createdById;
		this.inputs = inputs;
	}

	public String getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getFormId() {
		return this.formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getLastFormId() {
		return this.lastFormId;
	}

	public void setLastFormId(String lastFormId) {
		this.lastFormId = lastFormId;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedById() {
		return this.createdById;
	}

	public void setCreatedById(String createdById) {
		this.createdById = createdById;
	}

	public String[] getInputs() {
		return this.inputs;
	}

	public void setInputs(String[] inputs) {
		this.inputs = inputs;
	}
	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
	}


	 

}
