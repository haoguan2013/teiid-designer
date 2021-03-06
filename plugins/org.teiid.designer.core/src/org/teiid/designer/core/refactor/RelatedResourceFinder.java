/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.refactor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResourceFilter;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;

/**
 * Find resources related to the given resource 
 */
public class RelatedResourceFinder {
    
    private static final ModelResourceFilter RESOURCE_FILTER = new ModelResourceFilter();
    
    /**
     * Types of relationship used in this finder
     */
    public enum Relationship {
        /**
         * The relationship where resource A is imported by resource B
         * hence B is dependent on A
         */
        DEPENDENT,

        /**
         * The relationship where resource A imports resource B
         * hence A is dependent on B
         */
        DEPENDENCY,

        /**
         * Encompasses all types of relationship
         */
        ALL;
    }

    private final IResource sourceResource;
    
    /**
     * @param sourceResource
     */
    public RelatedResourceFinder(IResource sourceResource) {
        this.sourceResource = sourceResource;
    }

    private boolean isClosedProject(IResource resource) {
        if (resource instanceof IProject && !((IProject) resource).isOpen()) {
            return true;
        }

        return false;
    }

    /**
     * Recursive dependent find method on the given resource
     * 
     * @param resource
     * @return Collection<IFile>
     */
    private Collection<IFile> findDependentResources(IResource resource) {

        Collection<IFile> dependentResources = Collections.emptyList();

        try {
            if (isClosedProject(resource)) {
                /*
                 * If the project is closed then it has no dependents since the members
                 * are impossible to reach.
                 */
                return dependentResources;
            } else if (resource instanceof IContainer) {
                // sometimes this is getting called with a nonexistent resource...
                // see defect 18558 for more details
                if (resource.exists()) {
                    IContainer folder = (IContainer) resource;
                    IResource[] resources = folder.members();
                    dependentResources = new HashSet<IFile>();

                    for (int idx = 0; idx < resources.length; idx++) {
                        IResource depResource = resources[idx];

                        /*
                         * Add this resource (if a model file) since it is a dependency
                         * of the container.
                         */
                        if (ModelUtil.isModelFile(depResource.getFullPath()))
                            dependentResources.add((IFile) depResource);

                        /*
                         * Add the dependent resource's dependencies too
                         */
                        dependentResources.addAll(findDependentResources(depResource));
                    }
                } // endif
            } else {
                dependentResources = WorkspaceResourceFinderUtil.getResourcesThatUse(resource, RESOURCE_FILTER, IResource.DEPTH_INFINITE);
            }
        } catch (CoreException ce) {
            ModelerCore.Util.log(ce);
        }

        return dependentResources;
    }
    
    /**
     * find the dependent resources of the source resource
     * 
     * @return Collection<IFile>
     */
    private Collection<IFile> findDependentResources() {
        return findDependentResources(sourceResource);
    }

    /**
     * Recursive dependency find method on the given resource
     * 
     * @param resource
     * @return Collection<IFile>
     */
    private Collection<IFile> findDependencyResources(IResource resource) {

        Collection<IFile> dependencyResources = new HashSet<IFile>();

        try {
            if (isClosedProject(resource)) {
                /*
                 * If the project is closed then it has no dependencies since the members
                 * are impossible to reach.
                 */
                return dependencyResources;
            } else if (resource instanceof IContainer) {
                IContainer folder = (IContainer)resource;
                IResource[] resources = folder.members();

                for (int idx = 0; idx < resources.length; idx++) {
                    /*
                     * Don't add this resource to the collection since the members of the folder
                     * are dependent on the container and NOT dependencies of the container,
                     * ie. member contained by (dependent) on parent container rather than
                     *     container contains (association not dependency) on members.
                     */
                    dependencyResources.addAll(findDependencyResources(resources[idx]));
                }

            } else {
                dependencyResources.addAll(WorkspaceResourceFinderUtil.getDependentResources(resource));
            }
        } catch (CoreException ce) {
            ModelerCore.Util.log(ce);
        }

        return dependencyResources;
    }

    /**
     * calculateDependencyResources
     * 
     * @return Collection
     * @since 4.3
     */
    private Collection<IFile> findDependencyResources() {
        return findDependencyResources(sourceResource);
    }
    
    /**
     * Find related resources associated by the given type of relationship
     *
     * @param typeOfRelationship
     * @return collection of related resources
     */
    public Collection<IFile> findRelatedResources(Relationship typeOfRelationship) {
        switch (typeOfRelationship) {
            case DEPENDENT:
                return findDependentResources();
            case DEPENDENCY:
                return findDependencyResources();
            case ALL:
                Set<IFile> files = new HashSet<IFile>();
                files.addAll(findDependentResources());
                files.addAll(findDependencyResources());
                return files;
        }
        
        throw new IllegalStateException();
    }
   
}