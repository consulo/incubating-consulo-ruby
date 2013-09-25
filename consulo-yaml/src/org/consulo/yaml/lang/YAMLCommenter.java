package org.consulo.yaml.lang;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.Commenter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jul 19, 2008
 */
public class YAMLCommenter implements Commenter {

    @NonNls
    private static final String LINE_COMMENT_PREFIX = "#";

    @Override
	public String getLineCommentPrefix() {
        return LINE_COMMENT_PREFIX;
    }

    @Override
	public String getBlockCommentPrefix() {
        // N/A
        return null;
    }

    @Override
	public String getBlockCommentSuffix() {
        // N/A
        return null;
    }

	@Nullable
	@Override
	public String getCommentedBlockCommentPrefix()
	{
		return null;
	}

	@Nullable
	@Override
	public String getCommentedBlockCommentSuffix()
	{
		return null;
	}
}
