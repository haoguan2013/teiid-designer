/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.widget;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.TreeItem;

/**<p>
 * </p>
 * @since 8.0
 */
public interface ITreeViewerController {
    //============================================================================================================================
	// MVC Controller Methods

    /**<p>
	 * </p>
	 * @since 4.0
	 */
	void checkedStateToggled(TreeItem item);
    
    /**<p>
     * </p>
     * @since 4.0
     */
    boolean isItemCheckable(TreeItem item);

    /**<p>
     * </p>
     * @since 4.0
     */
    void itemCollapsed(TreeExpansionEvent event);

    /**<p>
     * </p>
     * @since 4.0
     */
    void itemDoubleClicked(DoubleClickEvent event);

    /**<p>
     * </p>
     * @since 4.0
     */
    void itemExpanded(TreeExpansionEvent event);

    /**<p>
	 * </p>
	 * @since 4.0
	 */
    void itemSelected(SelectionChangedEvent event);
    
    /**<p>
	 * </p>
	 * @since 4.0
	 */
	void update(TreeItem item, boolean selected);
}
