package org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.IntRef;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.cache.AbstractRubyModuleCacheTest;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.impl.RControlFlowBuilder;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 08.04.2008
 */
public class RControlFlowTest extends AbstractRubyModuleCacheTest {
    @NonNls
    protected static final String START_MARKER = "#start#";
    @NonNls
    protected static final String STOP_MARKER = "#stop#";
    @NonNls
    protected static final String RESULT_MARKER = "#result#";

    private void doTest() throws IOException {
        final VirtualFile file = getFile(getTestName(false).toLowerCase() + ".rb", myModule);
        assertNotNull(file);

        String fileText = StringUtil.convertLineSeparators(VfsUtil.loadText(file), "\n");
        final IntRef startMarker = new IntRef(fileText.indexOf(START_MARKER));
        final IntRef stopMarker = new IntRef(fileText.indexOf(STOP_MARKER));
        final IntRef resultMarker = new IntRef(fileText.indexOf(RESULT_MARKER));

        fileText = TestUtil.removeSubstring(fileText, startMarker.get(), START_MARKER.length(), startMarker, stopMarker, resultMarker);
        fileText = TestUtil.removeSubstring(fileText, stopMarker.get(), STOP_MARKER.length(), startMarker, stopMarker, resultMarker);
        fileText = TestUtil.removeSubstring(fileText, resultMarker.get(), RESULT_MARKER.length(), startMarker, stopMarker, resultMarker);

        final String result = fileText.substring(resultMarker.get());
        VfsUtil.saveText(file, fileText.substring(0, resultMarker.get()));

        final RFile myFile = (RFile) PsiManager.getInstance(myProject).findFile(file);
        final PsiElement startElement = myFile.findElementAt(startMarker.get());
        final PsiElement endElement = myFile.findElementAt(stopMarker.get());
        final RPsiElement context = RubyPsiUtil.getCoveringRPsiElement(PsiTreeUtil.findCommonParent(startElement, endElement));
        final StringBuffer buffer = new StringBuffer();
        final Instruction[] instructions = new RControlFlowBuilder().buildControlFlow(myFile.getFileSymbol(), context, null, null);
        for (Instruction instruction : instructions) {
            buffer.append(instruction).append("\n");
        }
        assertEquals(result.trim(), buffer.toString().trim());
    }

    protected String getDataDirPath() {
        return PathUtil.getDataPath(RControlFlowTest.class);
    }


    public void testAssignment() throws Exception {
        doTest();
    }

    public void testBlockParameters() throws Exception {
        doTest();
    }

    public void testBreak() throws Exception {
        doTest();
    }

    public void testCalls() throws Exception {
        doTest();
    }

    public void testCase() throws Exception {
        doTest();
    }

    public void testFor() throws Exception {
        doTest();
    }

    public void testIf() throws Exception {
        doTest();
    }

    public void testIf2() throws Exception {
        doTest();
    }

    public void testIf3() throws Exception {
        doTest();
    }

    public void testIf4() throws Exception {
        doTest();
    }

    public void testIfMod() throws Exception {
        doTest();
    }

    public void testMethodParameters() throws Exception {
        doTest();
    }

    public void testMultiAssignment() throws Exception {
        doTest();
    }

    public void testMultiAssignment2() throws Exception {
        doTest();
    }

    public void testNext() throws Exception {
        doTest();
    }

    public void testRaise() throws Exception {
        doTest();
    }

    public void testRedo() throws Exception {
        doTest();
    }

    public void testReturn() throws Exception {
        doTest();
    }

    public void testUnless() throws Exception {
        doTest();
    }

    public void testUnlessMod() throws Exception {
        doTest();
    }

    public void testUntil() throws Exception {
        doTest();
    }

    public void testUntilMod() throws Exception {
        doTest();
    }

    public void testWhile() throws Exception {
        doTest();
    }

    public void testWhileEmpty() throws Exception {
        doTest();
    }

    public void testWhileEmpty2() throws Exception {
        doTest();
    }

    public void testWhileMod() throws Exception {
        doTest();
    }

    public void testWhileMod2() throws Exception {
        doTest();
    }
}