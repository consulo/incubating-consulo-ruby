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

package org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.tabs;

import com.intellij.openapi.util.Ref;
import com.intellij.util.Function;
import com.intellij.util.ui.AsyncProcessIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 8, 2008
 */
public class EvaluatingComponent<T> extends JPanel {
    private JComponent myOriginalComponent;
    private Runnable myBeforeLoadingHandler;
    private Function<Object,T> myEvaluatingFun;
    private Function<T,Object> myAfterHandler;
    private String myLoadingLabelText;
    private AsyncProcessIcon myProgressIcon;
    private JLabel myLoadingLabel;
    private volatile boolean myIsRunning;

    public EvaluatingComponent(@NotNull final JComponent originalComponent) {
        myOriginalComponent = originalComponent;

        myProgressIcon = new AsyncProcessIcon("");
        myProgressIcon.suspend();
        myProgressIcon.setOpaque(true);
        myProgressIcon.setVisible(false);

        add(myOriginalComponent);
        add(myProgressIcon);

        myLoadingLabel = new JLabel();
        add(myLoadingLabel);
        
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setBorder(BorderFactory.createEmptyBorder());
        getInsets().set(0, 0, 0, 0);
    }

    public JComponent getOriginalComponent() {
        return myOriginalComponent;
    }

    /**
     *
     * @param beforeHandler Setupe before run
     * @param evaluatingFun Function: null -> (T)evaluated value. Asynch evaluates some action in progress
     * @param afterHandler  Function:(T)evaluated value -> null. Performs the result of action in EventDispatch Thread
     * @param loadingLabelText Text during loading
     */
    public void setHanlders(@Nullable final Runnable beforeHandler,
                            @Nullable final Function<Object,T> evaluatingFun,
                            @Nullable final Function<T, Object> afterHandler,
                            @NotNull final String loadingLabelText) {
        myBeforeLoadingHandler = beforeHandler;
        myEvaluatingFun = evaluatingFun;
        myAfterHandler = afterHandler;

        myLoadingLabelText = loadingLabelText;
        setProgressComponentVisibility(false, getCursor());
    }

    public boolean isRunning() {
        return myIsRunning;
    }

    /**
     * Runs in EventDispatch thread "before" handler, then asynch "eval function"
     * then in EventDispatch "after" handler
     */
    public void run() {
        final Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        final Cursor oldCursor = setProgressComponentVisibility(true, waitCursor);
        myProgressIcon.resume();
        myProgressIcon.setVisible(true);
        myBeforeLoadingHandler.run();


        final Ref<T> returnValue = new Ref<T>(null);

        final Thread evalThread = new Thread(new Runnable() {
            public void run() {
                myIsRunning = true;
                // run data
                if (myEvaluatingFun != null) {
                    try {
                        returnValue.set(myEvaluatingFun.fun(null));
                    } finally {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                setProgressComponentVisibility(false, oldCursor);
                            }
                        });
                    }
                }

                // apply data
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        setProgressComponentVisibility(false, oldCursor);

                        if (myAfterHandler != null) {
                            myAfterHandler.fun(returnValue.get());
                        }
                        myIsRunning = false;
                    }
                });
            }
        });
        evalThread.start();
    }

    private Cursor setProgressComponentVisibility(final boolean isVisible, final Cursor newCursor) {
        final Cursor cursor = getCursor();
        setCursor(newCursor);
        myProgressIcon.setCursor(newCursor);
        myOriginalComponent.setCursor(newCursor);

        if (isVisible) {
            myLoadingLabel.setText(myLoadingLabelText);
            myProgressIcon.resume();
        } else {
            myLoadingLabel.setText("");
            myProgressIcon.suspend();
        }

        myProgressIcon.setVisible(isVisible);
        myLoadingLabel.setVisible(isVisible);
        repaint();

        return cursor;
    }
}
