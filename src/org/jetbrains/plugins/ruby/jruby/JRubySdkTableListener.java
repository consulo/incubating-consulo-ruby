package org.jetbrains.plugins.ruby.jruby;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacet;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTable;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author: oleg
 * @date: Jul 28, 2008
 */
public class JRubySdkTableListener implements ApplicationComponent {
    private SdkTable.Listener myJdkTableListener;
    protected Project myProject;

    public JRubySdkTableListener(){
        myJdkTableListener = new SdkTable.Listener() {
            public void sdkAdded(final Sdk sdk) {
                if (JRubySdkUtil.isJRubySDK(sdk)) {
                    addLibrary(sdk);
                }
            }
            public void sdkRemoved(final Sdk sdk) {
                if (JRubySdkUtil.isJRubySDK(sdk)) {
                    removeLibrary(sdk);
                }
            }
            public void sdkNameChanged(final Sdk sdk, final String previousName) {
                if (JRubySdkUtil.isJRubySDK(sdk)) {
                    renameLibrary(sdk, previousName);
                }
            }
        };
    }

    private static void renameLibrary(final Sdk sdk, final String previousName) {
        final LibraryTable.ModifiableModel libraryTableModel = LibraryTablesRegistrar.getInstance().getLibraryTable().getModifiableModel();
        final Library library = libraryTableModel.getLibraryByName(JRubyFacet.getFacetLibraryName(previousName));
        if (library!=null){
            final Library.ModifiableModel model = library.getModifiableModel();
            model.setName(JRubyFacet.getFacetLibraryName(sdk.getName()));
            model.commit();
        }
        libraryTableModel.commit();
    }

    private static void removeLibrary(final Sdk sdk) {
        final LibraryTable.ModifiableModel libraryTableModel = LibraryTablesRegistrar.getInstance().getLibraryTable().getModifiableModel();
        final Library library = libraryTableModel.getLibraryByName(JRubyFacet.getFacetLibraryName(sdk.getName()));
        if (library!=null){
            libraryTableModel.removeLibrary(library);
        }
        libraryTableModel.commit();
    }

    public static Library addLibrary(final Sdk sdk) {
        final LibraryTable.ModifiableModel libraryTableModel = LibraryTablesRegistrar.getInstance().getLibraryTable().getModifiableModel();
        final Library library = libraryTableModel.createLibrary(JRubyFacet.getFacetLibraryName(sdk.getName()));
        final Library.ModifiableModel model = library.getModifiableModel();
        for (String url : sdk.getRootProvider().getUrls(OrderRootType.CLASSES)) {
            model.addRoot(url, OrderRootType.CLASSES);
            model.addRoot(url, OrderRootType.SOURCES);
        }
        model.commit();
        libraryTableModel.commit();
        return library;
    }

    public static void updateLibrary(final String name, final VirtualFile[] roots) {
        final LibraryTable.ModifiableModel libraryTableModel = LibraryTablesRegistrar.getInstance().getLibraryTable().getModifiableModel();
        final Library library = libraryTableModel.getLibraryByName(JRubyFacet.getFacetLibraryName(name));
        if (library!=null){
            final Library.ModifiableModel model = library.getModifiableModel();
            for (String url : model.getUrls(OrderRootType.CLASSES)) {
                model.removeRoot(url, OrderRootType.CLASSES);
            }
            for (String url : model.getUrls(OrderRootType.SOURCES)) {
                model.removeRoot(url, OrderRootType.SOURCES);
            }
            for (VirtualFile root : roots) {
                model.addRoot(root, OrderRootType.CLASSES);
                model.addRoot(root, OrderRootType.SOURCES);
            }
            model.commit();
        }
        libraryTableModel.commit();
    }

    @NotNull
    public String getComponentName() {
        return RComponents.JRUBY_SDK_TABLE_LISTENER;
    }

    public void initComponent() {
		ApplicationManager.getApplication().getMessageBus().connect().subscribe(SdkTable.SDK_TABLE_TOPIC, myJdkTableListener);
       // SdkTable.getInstance().addListener(myJdkTableListener);
    }

    public void disposeComponent() {
		//SdkTable.getInstance().removeListener(myJdkTableListener);
    }
}
