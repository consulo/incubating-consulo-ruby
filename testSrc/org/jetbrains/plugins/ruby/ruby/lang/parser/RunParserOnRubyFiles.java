/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.ruby.ruby.lang.parser;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.ruby.lang.RubySupportLoader;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 26.06.2006
 */
public class RunParserOnRubyFiles {
    // Ruby installation directory
    @NonNls static final String RUBY_HOME = RubySdkUtil.suggestRubyHomePath();

    // TIMEOUT per each test in miliseconds
    static final int TIMEOUT = 1500;

    static PrintStream out;
    static int doneCounter=0;
    private static int correctCounter=0;
    private static int counter=0;
    private static PsiManager psiManager;
    private static ArrayList<String> correctFiles = new ArrayList<String>();
    private static ArrayList<String> incorrectFiles = new ArrayList<String>();
    private static ArrayList<String> timeoutFiles = new ArrayList<String>();
    @NonNls
    private static final String TEMP_FILE = "temp.rb";
    @NonNls
    private static final String LOG_FILE = "RubyParserOnRubyFiles.log";

    enum TestResult {CORRECT, INCORRECT, TIMEOUT}

    public static TestResult runTest(final File file) throws Exception {
        final char[] fileText = FileUtil.loadFileText(file);

        final PsiElementFactory psiElementFactory = psiManager.getElementFactory();
        final Ref<PsiFile> psiFile = new Ref<PsiFile>();
        Runnable runTest =  new Runnable(){
            public void run(){
                psiFile.set(psiElementFactory.createFileFromText(TEMP_FILE, new String(fileText)));
            }
        };
        Thread testThread;
        try{
            testThread = new Thread(runTest);
            testThread.start();
            testThread.join(TIMEOUT);
        } catch (Exception e){
            return TestResult.INCORRECT;
        }
        if (testThread.isAlive()){
            testThread.interrupt();
            return TestResult.TIMEOUT;
        } else {
            final Ref<Boolean> correct = new Ref<Boolean>(true);
            PsiRecursiveElementVisitor visitor = new PsiRecursiveElementVisitor() {
                public void visitErrorElement(PsiErrorElement element) {
                    System.err.println(element);
//                    out.println(element);
                    correct.set(false);
                    super.visitErrorElement(element);
                }
            };
            psiFile.get().accept(visitor);
            if (correct.get()){
                return TestResult.CORRECT;
            } else {
                return TestResult.INCORRECT;
            }
        }
    }

    /**
     * If all the children - correct, all directory is correct - correct
     * @param root path
     * @throws Exception if something goes wrong. For example no file found at location root.
     */
    private static void runRecursiveTester(String root) throws Exception {
        runRecTests(new File(root));
    }


    private static TestResult runRecTests(File f) throws Exception {
// recursively scan for all subdirectories
        if (f.isDirectory()) {
            boolean dirCorrect = true;
            ArrayList<String> correctInDir = new ArrayList<String>();

            for(File file: f.listFiles()){
                if (file.isDirectory() || file.getAbsolutePath().toLowerCase().endsWith("rb")){
                    TestResult result = runRecTests(file);
                    if (result== TestResult.CORRECT){
                        correctInDir.add(file.getAbsolutePath());
                    } else {
                        dirCorrect=false;
                    }

                }
            }

            if (dirCorrect){
                return TestResult.CORRECT;
            } else {
                for (String s: correctInDir){
                    correctFiles.add(s);
                }
                return TestResult.INCORRECT;
            }

        } else {
            counter++;
            System.out.println("Parsing: "+f.getAbsolutePath());
//            out.println("Parsing: "+f.getAbsolutePath());

            TestResult result = runTest(f);

            if (result== TestResult.CORRECT){
                System.out.println("Correct");
//                out.println("Correct");
            } else
            if (result== TestResult.INCORRECT){
                System.out.println("Incorrect");
//                out.println("Incorrect");
            } else
            if (result== TestResult.TIMEOUT){
                System.out.println("Timeout");
//                out.println("Timeout");
            }

            if (result== TestResult.CORRECT){
                correctCounter++;
                doneCounter++;
            } else
            if (result== TestResult.INCORRECT){
                incorrectFiles.add(f.getAbsolutePath());
                doneCounter++;
            }
            if (result== TestResult.TIMEOUT){
                timeoutFiles.add(f.getAbsolutePath());
            }

            System.out.println(correctCounter+" : "+doneCounter+" : "+counter);
//            out.println(correctCounter+" : "+doneCounter+" : "+counter);
            return result;
        }
    }


    public static void main(String[] args) throws Exception {
        TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = IdeaTestFixtureFactory.getFixtureFactory().createLightFixtureBuilder();
        IdeaProjectTestFixture fixture = fixtureBuilder.getFixture();

        fixture.setUp();

        Project project = fixture.getProject();
        RubySupportLoader.loadRuby();
        psiManager = PsiManager.getInstance(project);

        out = new PrintStream(new File(LOG_FILE));

        String root = RUBY_HOME + "/lib";
        long startTime = System.currentTimeMillis();
        runRecursiveTester(root);
        long endTime = System.currentTimeMillis();
        System.out.println("Correct files:");
//        out.println("Correct files:");
        for (String str: correctFiles){
            System.out.println(str);
 //           out.println(str);
        }

        System.out.println("Incorrect files:");
        out.println("Incorrect files:");
        for (String str: incorrectFiles){
            System.out.println(str);
            out.println(str);
        }

        System.out.println("Timeout files:");
        out.println("Timeout files:");
        for (String str: timeoutFiles){
            System.out.println(str);
            out.println(str);
        }

        long totalTime = endTime - startTime;
        System.out.println("TOTAL: "+ totalTime+"ms");
        System.out.println("Average: "+totalTime/counter+"ms");
        System.out.println("TIMEOUT: "+TIMEOUT);
        System.out.println("Total files: "+counter);
        System.out.println("Done files: "+doneCounter);
        System.out.println("Correct files: "+correctCounter);

        out.println("TOTAL: "+ totalTime+"mx");
        out.println("Average: "+totalTime/counter+"ms");
        out.println("TIMEOUT: "+TIMEOUT);
        out.println("Total files: "+counter);
        out.println("Done files: "+doneCounter);
        out.println("Correct files: "+correctCounter);

        fixture.tearDown();
        System.exit(0);
    }

}
