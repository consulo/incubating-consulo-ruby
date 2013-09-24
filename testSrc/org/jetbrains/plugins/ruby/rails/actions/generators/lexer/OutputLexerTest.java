package org.jetbrains.plugins.ruby.rails.actions.generators.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.text.CharArrayCharSequence;
import com.intellij.util.text.CharArrayUtil;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.testCases.LexerTestCase;

import java.io.Reader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 22, 2008
 */
public class OutputLexerTest extends LexerTestCase {
    @NonNls
    private static final String DATA_PATH = PathUtil.getDataPath(OutputLexerTest.class);

    public OutputLexerTest() {
        super(DATA_PATH);
    }

    protected void setUp() {
        super.setUp();
        setLexer(new MyLexer());
    }

    public static Test suite() {
        return new OutputLexerTest();
    }

    class MyLexer extends LexerBase {
        private OutputLexer myLex = null;
        private String myTokenType = null;
        private CharSequence myText;

        private int myEnd;
        private int myState;

        MyLexer() {
            myLex = new OutputLexer((Reader) null);
        }

        public void start(char[] buffer, int startOffset, int endOffset, int initialState) {
            final CharArrayCharSequence arrayCharSequence = new CharArrayCharSequence(buffer);
            start(arrayCharSequence, startOffset, endOffset, initialState);
        }

        public void start(final CharSequence buffer, int startOffset, int endOffset, final int initialState) {
            myText = buffer;
            myEnd = endOffset;
            try {
                myLex.reset(myText, startOffset, endOffset, initialState);
            } catch (AbstractMethodError ame) {
                // Demetra compatibility
                myLex.reset(myText.subSequence(startOffset, endOffset), initialState);
            }
            myTokenType = null;
        }

        public int getState() {
            locateToken();
            return myState;
        }

        public IElementType getTokenType() {
            locateToken();
            return myTokenType == null ? null : new IElementType(myTokenType, null);
        }

        public int getTokenStart() {
            locateToken();
            return myLex.getTokenStart();
        }

        public int getTokenEnd() {
            locateToken();
            return myLex.getTokenEnd();
        }

        public void advance() {
            locateToken();
            myTokenType = null;
        }

        public char[] getBuffer() {
            return CharArrayUtil.fromSequence(myText);
        }

        public CharSequence getBufferSequence() {
            return myText;
        }

        public int getBufferEnd() {
            return myEnd;
        }

        private void locateToken() {
            if (myTokenType != null) return;
            try {
                myState = myLex.yystate();
                myTokenType = myLex.advance();
            }
            catch (IOException e) { /*Can't happen*/ }
        }

    }
}
