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

package com.dexilog.smartkeyboard.input;
import com.dexilog.smartkeyboard.utils.*;
import android.view.inputmethod.*;

public class InputController
{
	WordComposer currentWordComposer;
	int lastSelectionStart, lastSelectionEnd;
	boolean predicting;
	InputConnectionProvider inputConnectionProvider;
	boolean apostropheSeparator;
	
	public InputController(InputConnectionProvider provider, boolean apostropheSeparator)
	{
		currentWordComposer = new WordComposerImpl();
		inputConnectionProvider = provider;
	}
	
	public boolean getPredicting(){
		return predicting;
	}
	public void setPredicting(boolean predicting1)
	{
		predicting = predicting1;
	}
	public WordComposer getCurrentWordComposer()
	{
		return currentWordComposer;
	}
	public void reswapPeriodAndSpace()
	{
		InputConnection ic = inputConnectionProvider.getCurrentInputConnection();
        if( ic == null )
			return;
		CharSequence lastThree = ic.getTextBeforeCursor(3, 0);
        if (lastThree != null && lastThree.length() == 3
			&& lastThree.charAt(0) == KEYCODE_PERIOD
			&& lastThree.charAt(1) == KEYCODE_SPACE
			&& lastThree.charAt(2) == KEYCODE_PERIOD) {
            ic.beginBatchEdit();
            ic.deleteSurroundingText(3, 0);
            ic.commitText(" ..", 1);
            ic.endBatchEdit();
            inputConnectionProvider.updateShiftKeyStateFromEditorInfo();
        }
	}
	public boolean isCursorInsideWord()
	{
		InputConnection ic = inputConnectionProvider.getCurrentInputConnection();
        CharSequence toLeft = ic.getTextBeforeCursor(1, 0);
        CharSequence toRight = ic.getTextAfterCursor(1, 0);
        if (TextUtils.isEmpty(toLeft) || isWordSeparator(toLeft.charAt(0))) {
            return false;
        }
        if (TextUtils.isEmpty(toRight) || isWordSeparator(toRight.charAt(0))) {
            return false;
        }
        return true;
	}
	public boolean isCursorTouchingWord()
	{
		InputConnection ic = inputConnectionProvider.getCurrentInputConnection();
        if( ic == null )
			return false;
		CharSequence toLeft = ic.getTextBeforeCursor(1, 0);
        if (!TextUtils.isEmpty(toLeft) && !isWordSeparator(toLeft.charAt(0))) {
            return true;
        }
        CharSequence toRight = ic.getTextAfterCursor(1, 0);
        if (!TextUtils.isEmpty(toRight) && !isWordSeparator(toRight.charAt(0))) {
            return true;
        }
        return false;
	}
	public boolean isLastSelectionEmpty()
	{
		return lastSelectionEnd == lastSelectionStart;
	}
	public void forceTypedWord(CharSequence word)
	{
		currentWordComposer.forceTypedWord(word);
	}
	public void deleteLastCharacters(InputConnection ic, int toDelete)
	{
		int remainingChars = toDelete;
        CharSequence toTheLeft = ic.getTextBeforeCursor(remainingChars, 0);
        if (toTheLeft != null && toTheLeft.length() > 0
			&& isWordSeparator(toTheLeft.charAt(0))) {
            remainingChars--;
        }
        ic.deleteSurroundingText(remainingChars, 0);
	}
	public void deleteLastPredictingCharacter(InputConnection ic)
	{
		int length = currentWordComposer.size();
        if (length > 0) {
            currentWordComposer.deleteLast();
            // Update the composing now in T9 only if the whole word has been deleted
            boolean forceUpdate = length == 1;
            setComposingText(ic, forceUpdate);
            if (currentWordComposer.size() == 0) {
                predicting = false;
            }
            inputConnectionProvider.postUpdateSuggestions();
        } else {
            ic.deleteSurroundingText(1, 0);
        }
	}
	public void commitPickedSuggestion(CharSequence suggestion, boolean correcting)
	{
		InputConnection ic = inputConnectionProvider.getCurrentInputConnection();
        if (ic != null) {
            // If text is in correction mode and we're not using composing
            // text to underline, then the word at the cursor position needs
            // to be removed before committing the correction
            if (correcting) {
                deleteSelectedWord(ic);
            }

            ic.commitText(suggestion, 1);
        }
	}
	public void deleteSelectedWord(InputConnection ic)
	{

        if (lastSelectionStart < lastSelectionEnd) {
            ic.setSelection(lastSelectionStart, lastSelectionStart);
        }
        EditingUtil.deleteWordAtCursor(ic, getWordSeparators());
	}
	public boolean preferCapitalization()
	{
		return currentWordComposer.isCapitalized();
	}
	public boolean isCursorAtEnd()
	{
		InputConnection ic = inputConnectionProvider.getCurrentInputConnection();
		if( ic == null )
			return false;
        CharSequence toRight = ic.getTextAfterCursor(1, 0);
        return toRight == null || toRight.length() == 0;
	}
	private boolean isSpace(char character)
	{
		return character == KEYCODE_SPACE || character == KEYCODE_NON_BREAKABLE_SPACE;
	}
	public void insertPeriodOnDoubleSpace()
	{

        // if (mCorrectionMode == Suggest.CORRECTION_NONE) return;
        InputConnection ic = inputConnectionProvider.getCurrentInputConnection();
		if( ic == null )
			return;
			CharSequence lastThree = ic.getTextBeforeCursor(3, 0);
        if (lastThree != null && lastThree.length() == 3
			&& Character.isLetterOrDigit(lastThree.charAt(0))
			&& isSpace(lastThree.charAt(1))
			&& isSpace(lastThree.charAt(2))) {
            ic.beginBatchEdit();
            ic.deleteSurroundingText(2, 0);
            ic.commitText(". ", 1);
            ic.endBatchEdit();
            inputConnectionProvider.updateShiftKeyStateFromEditorInfo();
        }
	}
	public void resetWordComposer(WordComposerImpl foundWord)
	{
		if (foundWord != null) {
            currentWordComposer = new WordComposerImpl(foundWord);
        } else {
            currentWordComposer.reset();
        }
	}
	private boolean stringContains(CharSequence string, char code) {
		int len = string.length();
		for (int i = 0; i < len; i++) {
			char c = string.charAt(i);
			if (c == code) {
				return true;
			}
		}
		return false;
	}
	private static final int KEYCODE_SPACE = ' ';
	private static final int KEYCODE_PERIOD = '.';
	private static final int KEYCODE_NON_BREAKABLE_SPACE = '\u00a0';
	private static final int KEYCODE_APOSTROPHE = '\'';
	String wordSeparators = ".\u0020\u00a0,;:!?\n()[]*&@{}/<>_+=|\"\u3002\u3001\u3000\u060c\u061f『』｛｝（）「」：；［］！？～＊※♪♬…＿・•◦【】☆★♥";
	String sentenceSeparators = ".,;:!?\u060c\u061f\u3002\u3001：！？…";
	
	public boolean isSentenceSeparator(int code)
	{
		return stringContains(sentenceSeparators, (char)code);
	}
	public boolean isWordSeparator(int code)
	{
        if (code == KEYCODE_APOSTROPHE) {
            return apostropheSeparator;
        } else {
            return stringContains(wordSeparators, (char)code);
        }
	}
	public String getWordSeparators()
	{
		return wordSeparators;
	}
	private void sendKeyCodePoint(int keyCode)
	{
		if (keyCode <= 0xffff) {
            inputConnectionProvider.sendKeyChar((char)keyCode);
        } else {
			String text = Integer.toBinaryString(keyCode);
            //String text = String(intArrayOf(keyCode), 0, 1)
            inputConnectionProvider.getCurrentInputConnection().commitText(text, text.length());
        }
	}
	public void setComposingText(InputConnection ic, boolean force)
	{
		if( ic == null)
			return;

        // For T9, display the best candidate in updateSuggestions()
        if (!force && inputConnectionProvider.isT9PredictionOn()) {
            return;
        }
        currentWordComposer.convertWord(inputConnectionProvider.getConverter());
        CharSequence convertedWord = currentWordComposer.getConvertedWord();
        inputConnectionProvider.setConvertedComposing(convertedWord);
        ic.setComposingText(convertedWord, 1);
	}
	public void addCharacterWithoutComposing(int primaryCode, boolean replace, boolean hardKbd)
	{
		InputConnection ic = inputConnectionProvider.getCurrentInputConnection();
		if( ic == null )
			return;
		ic.beginBatchEdit();
        if (replace) {
            ic.deleteSurroundingText(1, 0);
        }
        if (hardKbd) {
            // With the hard keyboard, directly send the number, to handle
            // alt+char correctly
            ic.commitText(String.valueOf(((char)primaryCode)), 1);
        } else {
            sendKeyCodePoint(primaryCode);
        }
        ic.endBatchEdit();
	}
	public void addCharacterWithComposing(int primaryCode, int[] keyCodes, boolean replace, boolean isShifted)
	{
		currentWordComposer.addCharacter(primaryCode, keyCodes, replace, isShifted);
        setComposingText( inputConnectionProvider.getCurrentInputConnection(), false);
	}
	public int getLastSelectionEnd()
	{
		return lastSelectionEnd;
	}
	public int getLastSelectionStart()
	{
		return lastSelectionStart;
	}
	public void setLastSelectionEnd(int i)
	{
		lastSelectionEnd = i;
	}
	public void setLastSelectionStart(int i)
	{
		lastSelectionStart = i;
	}
	
}
