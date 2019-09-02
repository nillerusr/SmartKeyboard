package com.dexilog.smartkeyboard.input;
import android.view.inputmethod.*;

public class InputController
{
	WordComposer composer;
	public InputController(InputConnectionProvider provider, boolean apostropheSeparator)
	{
		composer = new WordComposerImpl();
	}
	
	public boolean getPredicting(){
		return false;
	}
	public void setPredicting(boolean predicting)
	{
		
	}
	public WordComposer getCurrentWordComposer()
	{
		return composer;
	}
	public void reswapPeriodAndSpace()
	{
		
	}
	public boolean isCursorInsideWord()
	{
		return false;
	}
	public boolean isCursorTouchingWord()
	{
		return false;
	}
	public boolean isLastSelectionEmpty()
	{
		return false;
	}
	public void forceTypedWord(CharSequence word)
	{
		
	}
	public void deleteLastCharacters(InputConnection ic, int toDelete)
	{
		
	}
	public void deleteLastPredictingCharacter(InputConnection ic)
	{

	}
	public void commitPickedSuggestion(CharSequence suggection, boolean correcting)
	{
		
	}
	public void deleteSelectedWord(InputConnection ic)
	{
		
	}
	public boolean preferCapitalization()
	{
		return false;
	}
	public boolean isCursorAtEnd()
	{
		return false;
	}
	private boolean isSpace(char character)
	{
		return false;
	}
	public void insertPeriodOnDoubleSpace()
	{
		
	}
	public void resetWordComposer(WordComposerImpl wordFound)
	{
		
	}
	public boolean isSentenceSeparator(int code)
	{
		return false;
	}
	public boolean isWordSeparator(int code)
	{
		return false;
	}
	String wordSeparators;
	public String getWordSeparators()
	{
		return wordSeparators;
	}
	private void sendKeyCodePoint(int keyCode)
	{}
	public void setComposingText(InputConnection ic, boolean force)
	{
		
	}
	public void addCharacterWithoutComposing(int primaryCoDe, boolean replace, boolean hardKbd)
	{
		
	}
	public void addCharacterWithComposing(int primaryCoDe, int[] keyCodes, boolean replace, boolean isShifted)
	{

	}
	public int getLastSelectionEnd()
	{
		return 0;
	}
	public int getLastSelectionStart()
	{
		return 0;
	}
	public void setLastSelectionEnd(int i)
	{
	}
	public void setLastSelectionStart(int i)
	{
	}
	
}
