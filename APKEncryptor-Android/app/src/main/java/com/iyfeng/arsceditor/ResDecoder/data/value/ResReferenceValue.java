/**
 *  Copyright 2014 Ryszard Wiśniewski <brut.alll@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.iyfeng.arsceditor.ResDecoder.data.value;

import java.io.IOException;

import com.iyfeng.arsceditor.ResDecoder.data.ResPackage;

/**
 * @author Ryszard Wiśniewski <brut.alll@gmail.com>
 */
public class ResReferenceValue extends ResIntValue {
	public ResReferenceValue(ResPackage package_, int value, String rawValue) {
		this(package_, value, rawValue, false);
	}

	public ResReferenceValue(ResPackage package_, int value, String rawValue, boolean theme) {
		super(value, rawValue, "reference");
	}

	@Override
	protected String encodeAsResValue() throws IOException {
		return String.valueOf(mValue);
	}

	public boolean isNull() {
		return mValue == 0;
	}
}
