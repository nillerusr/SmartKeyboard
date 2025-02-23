/*
 * Copyright (C) 2010-2017 Cyril Deguet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dexilog.smartkeyboard;

import android.content.Context;
import android.util.AttributeSet;

import com.dexilog.smartkeyboard.ui.KeyboardView;

public class CalibrationKeyboard extends KeyboardView {

	public int mLastX;
	public int mLastY;


	public CalibrationKeyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void detectAndSendKey(int index, int x, int y, long eventTime, int pointerCount) {
		mLastX = x;
		mLastY = y;
		super.detectAndSendKey(index, x, y, eventTime, pointerCount);
	}

}
