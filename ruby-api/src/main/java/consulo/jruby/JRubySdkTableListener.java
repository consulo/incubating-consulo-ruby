package consulo.jruby;

import consulo.application.ApplicationManager;
import consulo.content.base.BinariesOrderRootType;
import consulo.content.base.SourcesOrderRootType;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.event.SdkTableListener;
import consulo.content.library.Library;
import consulo.content.library.LibraryTable;
import consulo.content.library.LibraryTablesRegistrar;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacet;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkUtil;

/**
 * @author: oleg
 * @date: Jul 28, 2008
 */
public class JRubySdkTableListener
{
	private SdkTableListener myJdkTableListener;
	protected Project myProject;

	public JRubySdkTableListener()
	{
		myJdkTableListener = new SdkTableListener()
		{
			@Override
			public void sdkAdded(final Sdk sdk)
			{
				if(JRubySdkUtil.isJRubySDK(sdk))
				{
					addLibrary(sdk);
				}
			}

			@Override
			public void sdkRemoved(final Sdk sdk)
			{
				if(JRubySdkUtil.isJRubySDK(sdk))
				{
					removeLibrary(sdk);
				}
			}

			@Override
			public void sdkNameChanged(final Sdk sdk, final String previousName)
			{
				if(JRubySdkUtil.isJRubySDK(sdk))
				{
					renameLibrary(sdk, previousName);
				}
			}
		};

		ApplicationManager.getApplication().getMessageBus().connect().subscribe(SdkTableListener.class, myJdkTableListener);
		// SdkTable.getInstance().addListener(myJdkTableListener);
	}

	private static void renameLibrary(final Sdk sdk, final String previousName)
	{
		final LibraryTable.ModifiableModel libraryTableModel = LibraryTablesRegistrar.getInstance().getLibraryTable().getModifiableModel();
		final Library library = libraryTableModel.getLibraryByName(JRubyFacet.getFacetLibraryName(previousName));
		if(library != null)
		{
			final Library.ModifiableModel model = library.getModifiableModel();
			model.setName(JRubyFacet.getFacetLibraryName(sdk.getName()));
			model.commit();
		}
		libraryTableModel.commit();
	}

	private static void removeLibrary(final Sdk sdk)
	{
		final LibraryTable.ModifiableModel libraryTableModel = LibraryTablesRegistrar.getInstance().getLibraryTable().getModifiableModel();
		final Library library = libraryTableModel.getLibraryByName(JRubyFacet.getFacetLibraryName(sdk.getName()));
		if(library != null)
		{
			libraryTableModel.removeLibrary(library);
		}
		libraryTableModel.commit();
	}

	public static Library addLibrary(final Sdk sdk)
	{
		final LibraryTable.ModifiableModel libraryTableModel = LibraryTablesRegistrar.getInstance().getLibraryTable().getModifiableModel();
		final Library library = libraryTableModel.createLibrary(JRubyFacet.getFacetLibraryName(sdk.getName()));
		final Library.ModifiableModel model = library.getModifiableModel();
		for(String url : sdk.getRootProvider().getUrls(BinariesOrderRootType.getInstance()))
		{
			model.addRoot(url, BinariesOrderRootType.getInstance());
			model.addRoot(url, SourcesOrderRootType.getInstance());
		}
		model.commit();
		libraryTableModel.commit();
		return library;
	}

	public static void updateLibrary(final String name, final VirtualFile[] roots)
	{
		final LibraryTable.ModifiableModel libraryTableModel = LibraryTablesRegistrar.getInstance().getLibraryTable().getModifiableModel();
		final Library library = libraryTableModel.getLibraryByName(JRubyFacet.getFacetLibraryName(name));
		if(library != null)
		{
			final Library.ModifiableModel model = library.getModifiableModel();
			for(String url : model.getUrls(BinariesOrderRootType.getInstance()))
			{
				model.removeRoot(url, BinariesOrderRootType.getInstance());
			}
			for(String url : model.getUrls(SourcesOrderRootType.getInstance()))
			{
				model.removeRoot(url, SourcesOrderRootType.getInstance());
			}
			for(VirtualFile root : roots)
			{
				model.addRoot(root, BinariesOrderRootType.getInstance());
				model.addRoot(root, SourcesOrderRootType.getInstance());
			}
			model.commit();
		}
		libraryTableModel.commit();
	}
}
