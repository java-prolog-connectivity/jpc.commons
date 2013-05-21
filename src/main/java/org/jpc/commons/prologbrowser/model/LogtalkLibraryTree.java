package org.jpc.commons.prologbrowser.model;

import javafx.scene.image.Image;

import org.jpc.commons.prologbrowser.ui.BrowserImage;
import org.jpc.engine.logtalk.LogtalkLibrary;
import org.jpc.engine.logtalk.LogtalkLibraryItem;

public interface LogtalkLibraryTree {
	public Image getIcon();
	public String getName();
	
	
	public static class LogtalkLibraryTreeRoot implements LogtalkLibraryTree {
		@Override
		public Image getIcon() {
			return null;
		}

		@Override
		public String getName() {
			return "Logtalk Libraries";
		}
	}

	
	public static class LogtalkLibraryDirNode implements LogtalkLibraryTree {
		private LogtalkLibrary logtalkLibrary;
		
		public LogtalkLibraryDirNode(LogtalkLibrary logtalkLibrary) {
			this.logtalkLibrary = logtalkLibrary;
		}

		@Override
		public Image getIcon() {
			return null;
		}

		@Override
		public String getName() {
			return logtalkLibrary.getAlias();
		}
	}
	
	
	public static class LogtalkLibraryFileLeaf implements LogtalkLibraryTree {
		private LogtalkLibraryItem logtalkLibraryItem;
		
		public LogtalkLibraryFileLeaf(LogtalkLibraryItem logtalkLibraryItem) {
			this.logtalkLibraryItem = logtalkLibraryItem;
		}

		public LogtalkLibraryItem getLogtalkLibraryItem() {
			return logtalkLibraryItem;
		}

		@Override
		public Image getIcon() {
			if(logtalkLibraryItem.isProtocol())
				return BrowserImage.logtalkProtocolImage();
			else
				return BrowserImage.logtalkObjectImage();
		}

		@Override
		public String getName() {
			return logtalkLibraryItem.getName();
		}
	}

}
