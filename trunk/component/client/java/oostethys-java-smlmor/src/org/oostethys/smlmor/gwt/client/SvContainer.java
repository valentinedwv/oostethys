package org.oostethys.smlmor.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * container used for systems and variables
 * @author Carlos Rueda
 */
abstract class SvContainer {

	static SvContainer createTabPanel() {
		return new TabPanelSvContainer();
	}
	
	
	interface Listener {
		
		void addElement();
		
		void elementRemoved(int idx);
	}
	
	/** interface to an added element */
	interface IElement {
		void setName(String name);
	}
	
	

	protected static PushButton createButton(String name, ClickListener cl) {
		PushButton btn = new PushButton(name);
		DOM.setElementAttribute(btn.getElement(), "id", "my-button-id");
		
		if ( cl != null ) {
			btn.addClickListener(cl);
		}
		
		return btn;
	}
	
	protected PushButton addBtn = createButton("Add", null);
	
	protected Listener _listener;
	
	
	protected SvContainer() {
		addBtn.setTitle("Add a new element");
	}
	
	
	protected void setListener(Listener listener) {
		this._listener = listener;
	}
	
	abstract IElement add(Widget widget, String name) ;
	
	abstract void setElementName(int idx, String name) ;
	
	abstract int getWidgetCount();

	abstract Widget getPanel();

	abstract void remove(int idx) ;




	static class TabPanelSvContainer extends SvContainer {
		TabPanel tabPanel = new TabPanel();
		
		
		class TabWidget extends HorizontalPanel implements IElement {
			final HTML nameLabel = new HTML("");
			final PushButton removeBtn = createButton("x", new ClickListener() {
				public void onClick(Widget sender) {
					removeCalled(TabWidget.this);
				}
			});;
			
			TabWidget(String name) {
				setName(name);
				removeBtn.setTitle("Remove this element");

				setVerticalAlignment(ALIGN_MIDDLE);
				add(nameLabel);
				add(removeBtn);
			}

			public void setName(String name) {
				// &#8209 = non-breaking hyphen
				// &nbsp; = non-breaking space
				name = name.trim().replaceAll("-", "&#8209");
				name = name.replaceAll("\\s", "&nbsp;") + "&nbsp;";
				nameLabel.setHTML(name);
			}
		}
		List<TabWidget> tabWidgets = new ArrayList<TabWidget>();
		
		
		TabPanelSvContainer() {
			addBtn = createButton("+", new ClickListener() {
				public void onClick(Widget sender) {
					addCalled();
				}
			});
			addBtn.setTitle("Add a new element");
			tabPanel.add(new HTML("Click + to add a new element"), addBtn);
		}
		
		private void addCalled() {
			if ( _listener != null ) {
				_listener.addElement();
			}
		}
		
		private void removeCalled(TabWidget tabWidget) {
			if ( _listener != null ) {
				int idx = tabWidgets.indexOf(tabWidget);
				Main.log("removeCalled: idx = " +idx);
				if ( idx >= 0 ) {
					_listener.elementRemoved(idx);
				}
			}

		}

		@Override
		Widget getPanel() {
			return tabPanel;
		}

		@Override
		IElement add(final Widget widget, String name) {
			TabWidget tabWidget = new TabWidget(name);
			tabPanel.insert(widget, tabWidget, tabPanel.getWidgetCount() - 1);
			final int idx = tabPanel.getWidgetIndex(widget);
			tabWidgets.add(tabWidget);
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					tabPanel.selectTab(idx);
				}
			});
			
			return tabWidget;
		}
		
		@Override
		int getWidgetCount() {
			return tabPanel.getWidgetCount() - 1;   // -1: do not include addBtn
		}

		@Override
		void remove(final int idx) {
			tabWidgets.remove(idx);
			tabPanel.remove(idx);
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					int idxSel = idx;
					// we can always select at the same position, but prefer
					// to select a normal element, if any, instead of the "+" tab:
					if ( idxSel == tabPanel.getWidgetCount() - 1  && idxSel > 0 ) {
						idxSel--;
					}
					tabPanel.selectTab(idxSel);
				}
			});
		}

		@Override
		void setElementName(int idx, String name) {
//			tabPanel.getTabBar().setTabText(idx, name);
			
			tabWidgets.get(idx).nameLabel.setText(name);
		}

	}


}
