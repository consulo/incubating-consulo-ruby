package consulo.ruby.lang.lexer;

import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyRawLexer;
import com.intellij.lexer.FlexAdapter;

/**
 * @author VISTALL
 * @since 2018-09-13
 */
public class RubyLexer extends FlexAdapter
{
	public RubyLexer()
	{
		super(new RubyRawLexer((java.io.Reader) null)
		{
			@Override
			public void reset(CharSequence buffer, int start, int end, int initialState)
			{
				super.reset(buffer, start, end, initialState);
				reset(initialState);
			}
		});
	}
}
