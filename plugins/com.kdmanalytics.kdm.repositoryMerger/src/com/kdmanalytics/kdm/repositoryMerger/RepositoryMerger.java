
package com.kdmanalytics.kdm.repositoryMerger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdmanalytics.kdm.repositoryMerger.Utilities.IntUtils;
import com.kdmanalytics.kdm.repositoryMerger.Utilities.KdmConstants;
import com.kdmanalytics.kdm.repositoryMerger.Utilities.KdmConstants.KdmPredicate;
import com.kdmanalytics.kdm.repositoryMerger.Utilities.KdmConstants.KdmType;
import com.kdmanalytics.kdm.repositoryMerger.Utilities.KdmConstants.WorkbenchPredicate;
import com.kdmanalytics.kdm.repositoryMerger.Utilities.KdmConstants.WorkbenchStereotype;
import com.kdmanalytics.kdm.repositoryMerger.Utilities.KdmLiteral;
import com.kdmanalytics.kdm.repositoryMerger.Utilities.MergerURI;
import com.kdmanalytics.kdm.repositoryMerger.Utilities.MergerUtilities;
import com.kdmanalytics.kdm.repositoryMerger.linkconfig.MergeConfig;
import com.kdmanalytics.kdm.repositoryMerger.ranges.Range;
import com.kdmanalytics.kdm.repositoryMerger.ranges.RangeSet;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import info.aduna.iteration.CloseableIteration;

/**
 * 
 * @author Ken Duck
 *         
 */
class MethodOverrides {
  
  private final String link;
  
  private MethodState state;
  
  public MethodOverrides(String link) {
    this.link = link;
    state = MethodState.DECLARATION;
  }
  
  /**
   * 
   * @return
   */
  public final String getLink() {
    return link;
  }
  
  /**
   * 
   * @return
   */
  public final MethodState getState() {
    return state;
  }
  
  /**
   * 
   * @param state
   */
  public final void setState(MethodState state) {
    this.state = state;
  }
  
}

/**
 * Used to indicate whether the IMPLEMENTATION of a method has been committed yet
 * 
 * @author Ken Duck
 *         
 */
enum MethodState {
  DECLARATION,
  IMPLEMENTATION
};

/**
 * Merge multiple repositories into a common repository. The destination repository is actually a
 * file target, not a memory or disk repository, which means all merging needs to be done on the
 * fly, with a minimum of data kept in memory.
 * 
 * The individual repositories being loaded are provided as KdmRepository instances.
 * 
 * Output may be performed in two ways: * NTRIPLES format * MODIFIED_NTRIPLES which is a more
 * compressed representation
 * 
 */
public class RepositoryMerger {
  
  public static final int ACTION_ELEMENT_NAMES = 0x10;
  
  private static final String LINK_ID = "link:id";
  
  /**
   * log4j logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(RepositoryMerger.class);
  
  public static final int LOWMEM = 0x0C;
  
  public static final int MODIFIED_NTRIPLES = 0x02;
  
  public static final int NO_FLOWS = 0x04;
  
  public static final int NO_LOCALS = 0x08;
  
  // 0x20
  // 0x40
  // 0x80
  
  public static final int NTRIPLES = 0x01;
  
  /**
   * A switch for testing purposes. Should be false for non test runs.
   */
  private static final boolean preserveIDs = false;
  
  /**
   * This switch is used to ensure an error message is written only once.
   */
  private static boolean preservingURIs = true;
  
  public static final String UID_RANGES_ATTRIBUTE = "__KNT_UIDs";
  
  /**
   * The codeAssembly that all codeModel elements from the source repository will be written into.
   */
  private final URI assembly;
  
  /**
   * Case Sensitive KdmTypes
   */
  final EnumSet<KdmType> caseSensitive = EnumSet.of(KdmType.PACKAGE, KdmType.SHARED_UNIT, KdmType.BINARY_FILE,
                                                    KdmType.CONFIGURATION, KdmType.DEPENDS_ON, KdmType.DIRECTORY,
                                                    KdmType.EXECUTABLE_FILE, KdmType.IMAGE, KdmType.INVENTORY_CONTAINER,
                                                    KdmType.INVENTORY_ELEMENT, KdmType.INVENTORY_ITEM, KdmType.PROJECT,
                                                    KdmType.RESOURCE_DESCRIPTION, KdmType.SOURCE_FILE);
                                                    
  /**
   * The codeModel that is created.
   */
  private final URI codeModel;
  
  private final Pattern commaSplitter;
  
  // // Disabled for C. May re-enable for C++
  // private static final String CLASS_MODEL_NAME = "Class Structure";
  
  /**
   * Configuration file that defines how various elements are merged together.
   */
  private final MergeConfig config;
  
  /**
   * Reusable URI for the "contains" relationship
   */
  private final URI contains = new MergerURI(MergerURI.KdmNS, "contains");
  
  /**
   * Map of new ranges by URI
   */
  private final Map<URI, RangeSet> deferredRanges;
  
  /**
   * List of statements whose output is deferred until the URI "object" of the statement can be
   * resolved to the "newURI" that will be assigned to it. This list is reset every .kdmo object
   * file that is processed.
   */
  private List<Statement> deferredStatements;
  
  /**
   * Factory with which to generate URIs
   */
  private ValueFactory factory;
  
  private int format = 1;
  
  /**
   * This is a double key map, where the first key is the destination URI, and the second is a
   * unique link:id within that destination. It is used in the merging process to locate existing
   * elements in the target repository with which to merge. An example would be finding a SharedUnit
   * that is already within the repository which we are merging the same SharedUnit from another
   * object with.
   * 
   */
  private final GlobalMap globals;
  
  /**
   * Use this to mark elements that should remain hidden. This includes at least:
   * 
   * * LanguageUnit * InventoryModel
   * 
   */
  private URI hiddenStereotype;
  
  /**
   * The new ID numbers are assigned from here. Start counting at 1 (one) because the root is id 0.
   * 
   */
  private long id = GlobalMap.ROOTID;
  
  /**
   * The localIdMap is a map of the URI contained within the kdmo file to the URI that it is
   * translated to in the new linked file.
   * 
   * The map is refreshed for every kdmo file being loaded.
   */
  private Map<URI, URI> localIdMap;
  
  /**
   * Use this to mark the StructureModel as being updateable by Workbench probes.
   * 
   */
  private URI managedStereotype;
  
  /**
   * On a per-merge basis, what is the maximum UID found.
   */
  private long maxUID = 0;
  
  /**
   * Utilities for the repository (per .kdmo object)
   */
  private MergerUtilities mergerUtils;
  
  /**
   * A map of models by their type names to allow us to identify the models that need to be merged
   * together.
   * 
   */
  private final Map<String, URI> modelByType;
  
  /**
   * Set of nodes that did not get link IDs (used for error messaging)
   */
  private Set<URI> noLinkId;
  
  /**
   * The StructureAssembly is a class which gathers the information required for generating a
   * StructureModel for the binary. It then dumps the new Structure on shutdown.
   */
  // // Disabled for C. May re-enable for C++
  // private StructureAssembly structure;
  
  /**
   */
  private final StatementWriter output;
  
  /**
   * Path of the file being analysed
   */
  private String path;
  
  /**
   * Repository being merged FROM
   */
  private Repository repository;
  
  // // Disabled for C. May re-enable for C++
  // /** Set to true to generate an object oriented model
  // */
  // private boolean ooModel = true;
  //
  // /** Set to true to generate a file based model
  // */
  // private boolean fileModel = false;
  
  private Map<URI, URI> reverseLocalIdMap;
  
  private URI segmentURI;
  
  private final Pattern semiColonSplitter;
  
  /**
   * Indicate how much to offset UID values during integration.
   */
  private long uidOffset = 0;
  
  /**
   * Set to true to do extra data validation.
   */
  private boolean validate = false;
  
  /**
   * Store data used in validation stages
   */
  private ValidationData validationData;
  
  /**
   * Instantiate the merger. This causes some basic top level elements to be created, including the
   * Segment, a CodeModel, and the CodeAssembly for the linked code (binary/library/etc.)
   * 
   * Various lists and maps are instantiated that are to contain the minimal amount of data required
   * to ensure linking is successful.
   * 
   * @param mergeConfig
   * @param out
   * @param assemblyName
   *          The name of the CodeAssembly to "link" everything into
   */
  public RepositoryMerger(MergeConfig mergeConfig, PrintWriter out, int format, String assemblyName) {
    commaSplitter = Pattern.compile(",");
    semiColonSplitter = Pattern.compile(";");
    
    config = mergeConfig;
    
    globals = new GlobalMap();
    modelByType = new THashMap<String, URI>();
    deferredRanges = new THashMap<URI, RangeSet>();
    output = new StatementWriter(out, format);
    this.format = format;
    
    segmentURI = new MergerURI(MergerURI.KdmModelNS, 0 + "");
    // Write the segment
    ++id;
    output.print(segmentURI, KdmPredicate.KDM_TYPE.toURI(), KdmType.SEGMENT.toLiteral());
    output.print(segmentURI, KdmPredicate.NAME.toURI(), new KdmLiteral("root"));
    // Set the UID for the node
    output.print(segmentURI, WorkbenchPredicate.UID.toURI(), new KdmLiteral("" + uidOffset));
    deferredRanges.put(segmentURI, new RangeSet((int) uidOffset));
    ++uidOffset;
    
    codeModel = getDestinationModel("code/CodeModel");
    
    // Initialize the stereotypes required by the workbench
    initializeWorkbenchStereotypes(segmentURI);
    
    // Write the CodeAssembly
    assembly = new MergerURI(MergerURI.KdmModelNS, "" + id);
    ++id;
    output.print(assembly, KdmPredicate.KDM_TYPE.toURI(), KdmType.CODE_ASSEMBLY.toLiteral());
    output.print(assembly, KdmPredicate.NAME.toURI(), new KdmLiteral(assemblyName));
    output.print(codeModel, KdmPredicate.CONTAINS.toURI(), assembly);
    // Set the UID for the node
    output.print(assembly, WorkbenchPredicate.UID.toURI(), new KdmLiteral("" + uidOffset));
    deferredRanges.put(assembly, new RangeSet((int) uidOffset));
    ++uidOffset;
    
    // Substitute the CodeAssembly for the CodeModel
    modelByType.put("code/CodeModel", assembly);
  }
  
  /**
   * Add the specified range to all of the ancestors in the current ancestor path, which should go
   * straight up to the segment.
   * 
   * @param ancestors
   * @param range
   */
  private void addDeferredRanges(Stack<URI> ancestors, Range range) {
    for (int i = ancestors.size() - 1; i >= 0; --i) {
      final URI parent = ancestors.get(i);
      RangeSet ranges = null;
      if (deferredRanges.containsKey(parent)) {
        ranges = deferredRanges.get(parent);
      } else {
        ranges = new RangeSet();
        deferredRanges.put(parent, ranges);
      }
      ranges.add(range);
    }
  }
  
  /**
   * Close the merge session. This will output some key information that was built from information
   * from all merged objects, including:
   * 
   * 1. Segment and CodeModel ranges. 2. Class model for c++ // Currently disabled 2. Link
   * information
   * 
   * This MUST be called to complete the output
   */
  public void close() {
    // Update the CodeModel ranges
    final RangeSet cmRanges = deferredRanges.get(codeModel);
    final RangeSet assRanges = deferredRanges.get(assembly);
    cmRanges.addAll(assRanges);
    
    // Update segment ranges
    final RangeSet sRanges = deferredRanges.get(segmentURI);
    sRanges.add(new Range(0, uidOffset));
    
    // Write the deferred ranges
    for (final Entry<URI, RangeSet> entry : deferredRanges.entrySet()) {
      final URI element = entry.getKey();
      final RangeSet ranges = entry.getValue();
      // // Disabled for C. May re-enable for C++
      // structure.addRanges(element, ranges);
      output.print(element, WorkbenchPredicate.WORKBENCH_RANGES.toURI(), new KdmLiteral(ranges.toString()));
    }
    // // Disabled for C. May re-enable for C++
    // // Write the structure
    // // FIXME: Can only currently create one type of model. This should be
    // updated
    // // to allow generation of multiple model types.
    // URI structureModel = null;
    //
    // // Override the default name for the structure model if it is object
    // oriented
    // if(ooModel) structureModel =
    // getDestinationModel(KdmType.STRUCTURE_MODEL.toString(),
    // CLASS_MODEL_NAME);
    // else structureModel =
    // getDestinationModel(KdmType.STRUCTURE_MODEL.toString());
    //
    // id = structure.generateStructure(structureModel, id);
    output.close();
  }
  
  /**
   * Copy the specified node and its children. Any non-containment implicit "relation" (where the
   * object is a URI) should be deferred till all nodes have been transfered.
   * 
   * Part of the copy method is assigning a new id to the node and note the translation so that
   * "relations" may be redirected at the end of the merge.
   * 
   * Also involved is applying the UID offset to all contained UIDs to ensure there is no collision
   * with the UIDs of other elements.
   * 
   * @param dst
   * @param node
   * @param remember
   *          is set to true if these nodes should be added to the globals (copy as a result of a
   *          merge). Remember is not set to true if we are writing a CompilationUnit, for example,
   *          but will be for a SharedUnit.
   * @throws RepositoryException
   */
  private void copy(URI dst, URI node, boolean remember) throws RepositoryException {
    // If the element is already in the local map, then we have
    // already handled the node. This will only happen if there
    // is a problem with the input data.
    if (localIdMap.containsKey(node)) {
      LOG.error("Attempting to re-copy element " + node + " into parent " + dst);
      debugPathToRoot(node);
      throw new UnsupportedOperationException("Attempting to re-copy element " + node + " into parent " + dst);
    }
    
    // In order to successfully merge methods/functions, we need to track
    // some particular
    // information about the method.
    final KdmType type = KdmType.valueOfKdmString(mergerUtils.getRDFAttribute(node, KdmPredicate.KDM_TYPE.toString()));
    
    try {
      // Generate a new URI for the element. All elements get new URIs
      // to ensure there is no overlap with existing URIs.
      final URI newURI = generateLinkerURI(node);
      
      // Add the mapping to the localIdMap. This data is only required
      // in the local map, since different objects CANNOT directly
      // reference each other. References between objects are resolved
      // by the RelationshipLinker.
      localIdMap.put(node, newURI);
      reverseLocalIdMap.put(newURI, node);
      
      // Record a set of all contained children.
      final Set<URI> children = new THashSet<URI>();
      
      // Remember the kdmType of this node. It will help determine how
      // the data is handled.
      String kdmType = null;
      
      // Keep a list of statements found.
      final List<Statement> stmts = new ArrayList<Statement>();
      
      // // Disabled for C. May re-enable for C++
      // // By default we hide a class in the StructureModel. If it has a
      // UID then we know it
      // // is not hidden for the purposes of the structure model.
      // boolean hiddenClass = true;
      
      // Get all triples with the node as the subject
      final RepositoryConnection con = repository.getConnection();
      final CloseableIteration<Statement, RepositoryException> statements = con.getStatements(node, null, null, true);
      try {
        long validateOriginalUid = 0;
        long validateOriginalLastUid = 0;
        long validateUid = 0;
        long validateLastUid = 0;
        
        // Get all statements and handle them in the appropriate manner
        while (statements.hasNext()) {
          final Statement st = statements.next();
          final URI predicate = st.getPredicate();
          final Value object = st.getObject();
          
          // If the KDMType is not expected to be linked, then
          // return from here.
          if (skip(predicate, object)) {
            return;
          }
          
          final String predicateName = predicate.getLocalName();
          // If the object is a URI and is not a contains then we
          // want to defer the writing until we know what the
          // object ID is being rewritten to.
          //
          // Contains are written at the beginning of "copy"
          if (object instanceof URI) {
            // Several predicates actually indicate a contains.
            if ("contains".equals(predicateName)) {
              if (!skipChild((URI) object)) {
                children.add((URI) object);
              }
            } else if ("__item".equals(predicateName)) {
              children.add((URI) object);
            } else if ("__index".equals(predicateName)) {
              children.add((URI) object);
            } else {
              // LOG.debug("Defer " + node + "(" + newURI + ") " +
              // predicate + " " + object);
              deferredStatements.add(new StatementImpl(newURI, predicate, object));
              
              // Run this code for advanced data validation
              if (validate) {
                if (KdmPredicate.FROM.toString().equals(predicateName)) {
                  validationData.setRelationshipFrom(node, (URI) object);
                } else if (KdmPredicate.TO.toString().equals(predicateName)) {
                  validationData.setRelationshipTo(node, (URI) object);
                }
              }
              
            }
            continue;
          }
          
          // Handle compressed representation data
          if ("SourceRef".equals(predicateName)) {
            deferredStatements.add(new StatementImpl(newURI, predicate, object));
            continue;
          }
          
          // Only print ActionElement names if required
          if (((format & ACTION_ELEMENT_NAMES) == 0) && KdmPredicate.NAME.toString().equals(predicateName)
              && KdmConstants.KdmType.ACTION_ELEMENT.toString().equals(type)) {
            continue;
          }
          
          // When merging, in most cases we are merely visiting
          // children
          // to see if they match up or new ones need copying.
          
          // If there is a link:id try to add this to the global map.
          // Don't write it to the file, though. This link:id
          // will be used for future merges, so only needs to be
          // done if we are going to merge with this node (check the
          // remember boolean).
          if (remember && LINK_ID.equals(predicateName)) {
            globals.add(dst, getLinkId(node), newURI);
            continue;
          }
          
          // Some element outputs depend on KDM type. Remember the
          // type.
          if (KdmPredicate.KDM_TYPE.toString().equals(predicateName)) {
            kdmType = object.stringValue();
          }
          
          // If this is a UID related triple, add the offset
          if (WorkbenchPredicate.UID.toString().equals(predicateName) || WorkbenchPredicate.LAST_UID.toString().equals(
                                                                                                                       predicateName)) {
            // If this is the lastUID and it is redundant, then skip
            // it
            if (WorkbenchPredicate.LAST_UID.toString().equals(predicateName)) {
              final long myLastUid = Long.parseLong(object.stringValue());
              final long myUid = Long.parseLong(mergerUtils.getRDFAttribute((URI) st.getSubject(),
                                                                            WorkbenchPredicate.UID.toString()));
              if (myUid == myLastUid) {
                continue;
              }
              if (myLastUid < myUid) {
                LOG.error("Invalid range for " + node + " (" + myUid + "-" + myLastUid + ")");
              }
            }
            
            // // Disabled for C. May re-enable for C++
            // hiddenClass = false; // There us a UID. If this is a
            // class do not hide it.
            
            // Get the uid/lastUid
            long uid = Long.parseLong(object.stringValue());
            
            // Remember the original values for validation
            if (WorkbenchPredicate.UID.toString().equals(predicateName)) validateOriginalUid = uid;
            else validateOriginalLastUid = uid;
            
            // otherwise get the appropriate UID
            if (uid <= 0) {
              LOG.error("Linked object contains node with an invalid UID <= 0");
            }
            uid += uidOffset;
            if (uid > maxUID) {
              maxUID = uid;
            }
            
            // Remember the converted UIDs for validation purposes
            if (WorkbenchPredicate.UID.toString().equals(predicateName)) validateUid = uid;
            else validateLastUid = uid;
            
            final Literal newUid = new KdmLiteral(Long.toString(uid));
            
            stmts.add(new StatementImpl(newURI, predicate, newUid));
            
            // If we are performing validation, we want to remember
            // UIDs
            if (validate) {
              validationData.setUid(node, object);
            }
            continue;
          }
          
          // Buffer the remaining statements for printing
          stmts.add(new StatementImpl(newURI, predicate, object));
        }
        
        // Validate the UID, since they appear to be having troubles
        if (validate) {
          if (validateLastUid > 0 && validateLastUid < validateUid) {
            if (validateOriginalLastUid < validateOriginalUid) {
              LOG.error("Invalid original range for " + node + " (" + validateOriginalUid + "-"
                        + validateOriginalLastUid + ")");
            } else {
              LOG.error("Invalid converted range for " + node + " (" + validateUid + "-" + validateLastUid + ")");
            }
          }
        }
        
      } finally {
        statements.close(); // make sure the result object is closed
        // properly
        con.close();
      }
      
      // At this point all statements have been loaded. We know the
      // element type. Some triples are being deferred (implicit
      // relationships).
      
      // Set to hidden if required
      if ("code/LanguageUnit".equals(kdmType)) {
        output.print(newURI, KdmPredicate.STEREOTYPE.toURI(), hiddenStereotype);
      }
      
      // // Disabled for C. May re-enable for C++
      // // Some elements need to be passed to the StructureAssembly
      // // for successful generation of the StructureModel.
      //
      // // FIXME: Can only currently create one type of model. This
      // should be updated
      // // to allow generation of multiple model types.
      //
      // // OO models are based on classes
      // if(ooModel)
      // {
      // if("code/ClassUnit".equals(kdmType))
      // {
      // if(!hiddenClass)
      // {
      // structure.add(newURI, stmts);
      // }
      // }
      // }
      //
      // // File models are based on SharedUnits and CompilationUnits
      // else if(fileModel)
      // {
      // if("code/CompilationUnit".equals(kdmType))
      // structure.add(newURI, stmts);
      // if("code/SharedUnit".equals(kdmType))
      // structure.add(newURI, stmts);
      // }
      //
      // // Both model types require the source path
      // if("source/SourceFile".equals(kdmType))
      // structure.add(newURI, stmts);
      
      // Write the stored statements.
      for (final Statement stmt : stmts) {
        output.print((URI) stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
      }
      
      // Setup the contains. This call is here because we may have
      // bailed out earlier if this is a node that is not being
      // added to the linked repository.
      //
      // Also, we want the contains to be after anything else about
      // the node.
      output.print(dst, contains, newURI);
      
      // Recursively copy the child nodes.
      for (final URI child : children) {
        copy(newURI, child, remember);
      }
    } finally {}
  }
  
  /**
   * Copy the specified child into the target node, and merge the UID ranges. This is only done for
   * the top level of a copy, since the range spread for these elements are already known
   * absolutely.
   * 
   * @param dst
   *          Destination URI the element is being added to
   * @param node
   *          Source URI being added
   * @param remember
   *          is set to true if these nodes should be added to the globals (copy as a result of a
   *          merge). Remember is not set to true if we are writing a CompilationUnit, for example,
   *          but will be for a SharedUnit.
   *          
   * @throws RepositoryException
   */
  private void copyAndMergeRange(URI dst, URI node, boolean remember) throws RepositoryException {
    // First copy all of the elements. All of the ranges in a copy
    // are offset by the same amount, which preserves the containment
    // information provided by the ranges.
    copy(dst, node, remember);
    
    // Now get the ranges for the top element copied. Offset the ranges
    // and add them to the destination (parent) ranges. This is a
    // simple translation process, since the previous copy offset the
    // copied ranges by the same amount.
    try {
      final RangeSet childRanges = new RangeSet(repository, node);
      childRanges.offsetAll(uidOffset);
      if (validate) {
        if (!childRanges.isValid()) {
          LOG.error("Invalid range for " + node + " (" + childRanges + ")");
        }
      }
      final RangeSet parentRanges = deferredRanges.get(dst);
      parentRanges.addAll(childRanges);
      if (!parentRanges.isValid()) {
        LOG.error("Invalid range for " + node + " (" + parentRanges + ")");
      }
    }
    // If there is an exception, there are no ranges to merge. This may
    // happen in the case of InventoryModel elements, at the very
    // least.
    catch (final RepositoryException e) {}
  }
  
  /**
   * This code is only invoked on an error, and is used to provide a path to the root from a given
   * URI.
   * 
   * @param node
   * @throws RepositoryException
   */
  private String debugPathToRoot(URI node) throws RepositoryException {
    final URI parent = mergerUtils.getOwner(node);
    String indent = "";
    if (parent != null) {
      indent = debugPathToRoot(parent);
    }
    final String linkId = mergerUtils.getRDFAttribute(node, LINK_ID);
    LOG.warn(indent + node + " " + linkId);
    return indent + "  ";
  }
  
  /**
   * Dump context information for the specified element id. This information is pulled from the
   * *.kdmo repository, so the ID must be valid for there.
   * 
   * If the element is a relationship, also dump information about the "from" side of the
   * relationship.
   * 
   * @param id
   */
  private void dumpContextLocation(String indent, URI id) {
    RepositoryConnection con = null;
    CloseableIteration<Statement, RepositoryException> statements = null;
    try {
      LOG.error(indent + "Subject: " + id);
      if (id != null) {
        boolean found = false;
        con = repository.getConnection();
        statements = con.getStatements(id, null, null, true);
        // Get all statements and handle them in the appropriate manner
        while (statements.hasNext()) {
          Statement stmt = statements.next();
          if (stmt != null) {
            Value object = stmt.getObject();
            URI predicate = stmt.getPredicate();
            
            LOG.error(indent + "  " + predicate + " " + object);
            found = true;
            
            // If this is a relationship, then get the "from" side
            // for great justice.
            if (object != null && predicate != null) {
              String pstring = predicate.getLocalName();
              if (KdmPredicate.FROM.toString().equals(pstring) && (object instanceof URI)) {
                dumpContextLocation(indent + "    ", (URI) stmt.getObject());
              }
              if (KdmPredicate.SOURCEREF.toString().equals(pstring)) {
                String ref = object.stringValue();
                if (ref != null) {
                  String[] tokens = ref.split(";");
                  final URI fid = new MergerURI(MergerURI.KdmModelNS, tokens[0]);
                  dumpContextLocation(indent + "    ", fid);
                }
              }
            }
          }
        }
        // If we found no good statements, then this is a "dangling to"
        // type problem+
        
        if (!found) {
          LOG.error(indent + "  No statements found");
        }
      }
    } catch (RepositoryException e) {
      LOG.error("Exception gathering context information for " + id, e);
    } finally {
      if (statements != null) {
        try {
          statements.close();
        } catch (RepositoryException e) {
          LOG.error("Exception closing statement", e);
        }
      }
      if (con != null) {
        try {
          con.close();
        } catch (RepositoryException e) {
          LOG.error("Exception closing connection", e);
        }
      }
    }
  }
  
  /**
   * Dump as an error contextual location useful for debugging purposes.
   * 
   * @param src
   * @param dst
   */
  private void dumpContextLocation(URI src, URI dst) {
    LOG.error("  Path: " + path);
    // Need to use a reverse map to get the source ID for the kdmo
    // repository
    URI srcId = reverseLocalIdMap.get(src);
    // LOG.error(" Source context (" + getSourceRef(src) + ")");
    dumpContextLocation("      ", srcId);
    
    // Dump information for the target if it exists
    LOG.error("    Target context");
    dumpContextLocation("      ", dst);
  }
  
  /**
   * Generate a new URI for the linker. In production use this ID should be generated to ensure
   * there is no overlap, but for testing purposes I may preserve existing values.
   * 
   */
  private URI generateLinkerURI(URI node) {
    // This should only run in debug situations
    if (preserveIDs) {
      if (preservingURIs) {
        LOG.error("Preserving compiled URIs -- use only for debug purposes");
        preservingURIs = false;
      }
      return node;
    } else {
      final URI newURI = factory.createURI(MergerURI.KdmModelNS + id);
      ++id;
      return newURI;
    }
  }
  
  /**
   * Get the destination model with the specified type. If it does not yet exist, then create it.
   * 
   * @param type
   * @return
   */
  private URI getDestinationModel(String type) {
    // Get the name. Fancy it up a little by adding spaces before capitol
    // letters
    final int start = type.indexOf('/') + 1;
    final StringBuilder name = new StringBuilder();
    for (int i = start; i < type.length(); i++) {
      final char c = type.charAt(i);
      if ((c >= 'A') && (c <= 'Z') && (i > start)) {
        name.append(' ');
      }
      name.append(c);
    }
    
    return getDestinationModel(type, name.toString());
  }
  
  /**
   * 
   * @param type
   * @param name
   * @return
   */
  private URI getDestinationModel(String type, String name) {
    if (modelByType.containsKey(type)) {
      return modelByType.get(type);
    }
    
    final URI model = new MergerURI(MergerURI.KdmModelNS, "" + id);
    ++id;
    
    // Write the Model
    output.print(model, KdmPredicate.KDM_TYPE.toURI(), new KdmLiteral(type));
    
    output.print(model, KdmPredicate.NAME.toURI(), new KdmLiteral(name));
    
    output.print(segmentURI, KdmPredicate.CONTAINS.toURI(), model);
    
    // Set to hidden if required
    if (KdmConstants.KdmType.INVENTORY_MODEL.toString().equals(type)) {
      output.print(model, KdmPredicate.STEREOTYPE.toURI(), hiddenStereotype);
    }
    
    // The StructureModel may be managed by Workbench probes
    if (KdmType.STRUCTURE_MODEL.toString().equals(type)) {
      output.print(model, KdmPredicate.STEREOTYPE.toURI(), managedStereotype);
    }
    
    // Set the UID for the node
    output.print(model, WorkbenchPredicate.UID.toURI(), new KdmLiteral("" + uidOffset));
    deferredRanges.put(model, new RangeSet((int) uidOffset));
    ++uidOffset;
    
    // Reuse other models
    modelByType.put(type, model);
    return model;
  }
  
  /**
   * 
   * @param node
   * @return
   * @throws RepositoryException
   */
  private Value getLinkId(URI node) throws RepositoryException {
    Value id = mergerUtils.getRDFAttributeValue(node, LINK_ID);
    if (id != null) {
      if (SystemUtils.IS_OS_WINDOWS) {
        KdmType kdmType = KdmType.valueOfKdmString(mergerUtils.getRDFAttribute(node, KdmPredicate.KDM_TYPE.toString()));
        if (caseSensitive.contains(kdmType)) {
          id = factory.createLiteral(id.stringValue().toLowerCase());
        }
      }
      // if(id.stringValue().trim().isEmpty())
      // {
      // LOG.error("Invalid link:id " + id + " for element " + node);
      // }
    }
    return id;
  }
  
  /**
   * Return the maximum assigned subject ID at this point.
   * 
   * @return
   */
  public long getMaxId() {
    return id;
  }
  
  /**
   * Return the number of statements processed by the merger.
   * 
   * @return
   */
  public long getStatementCount() {
    return output.getCount();
  }
  
  /**
   * Used to determine whether the code has a UID or not.
   * 
   * @param element
   *          element to check
   * @return true if the element has a UID defined
   * @throws RepositoryException
   */
  private boolean hasUID(URI element) throws RepositoryException {
    String result = mergerUtils.getRDFAttribute(element, "UID");
    if (result == null || result.trim().isEmpty()) return false;
    return true;
  }
  
  /**
   * Initialize the stereotypes expected by workbench
   * 
   * @param segment
   *          The segment to which this data is being written.
   */
  private void initializeWorkbenchStereotypes(URI segment) {
    final URI extensionFamily = new MergerURI(MergerURI.KdmModelNS, "" + id);
    ++id;
    output.print(extensionFamily, KdmPredicate.KDM_TYPE.toURI(), new KdmLiteral("kdm/ExtensionFamily"));
    output.print(extensionFamily, KdmPredicate.NAME.toURI(), MergerURI.WORKBENCH_EXTENSION_LITERAL);
    output.print(segment, KdmPredicate.CONTAINS.toURI(), extensionFamily);
    
    hiddenStereotype = new MergerURI(MergerURI.KdmModelNS, "" + id);
    ++id;
    output.print(hiddenStereotype, KdmPredicate.KDM_TYPE.toURI(), new KdmLiteral("kdm/Stereotype"));
    output.print(hiddenStereotype, KdmPredicate.NAME.toURI(), WorkbenchStereotype.HIDDEN.toLiteral());
    output.print(extensionFamily, KdmPredicate.CONTAINS.toURI(), hiddenStereotype);
    
    managedStereotype = new MergerURI(MergerURI.KdmModelNS, "" + id);
    ++id;
    output.print(managedStereotype, KdmPredicate.KDM_TYPE.toURI(), new KdmLiteral("kdm/Stereotype"));
    output.print(managedStereotype, KdmPredicate.NAME.toURI(), WorkbenchStereotype.MANAGED_STRUCTURE_MODEL.toLiteral());
    output.print(extensionFamily, KdmPredicate.CONTAINS.toURI(), managedStereotype);
  }
  
  /**
   * Returns true if the subject is a local variable
   * 
   * @param subject
   * @return
   * @throws RepositoryException
   */
  private boolean isLocalVariable(URI subject) throws RepositoryException {
    final String type = mergerUtils.getRDFAttribute(subject, KdmPredicate.KDM_TYPE.toString());
    if (type == null) {
      return false;
    }
    if (type.equals(KdmType.STORABLE_UNIT.toString())) {
      final String kind = mergerUtils.getRDFAttribute(subject, KdmPredicate.KIND.toString());
      if ("local".equals(kind) || "register".equals(kind)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Top level merge of the repository. This instantiates some utilities so that they know of the
   * KdmRepository for the object being merged. It kicks off the model merge and performs final
   * closing operations (writing deferred values and clearing utilities).
   * 
   * @deprecated Use merge(String path, KdmRepository repository)
   *             
   * @param repository
   * @throws RepositoryException
   */
  @Deprecated
  public void merge(Repository repository) throws RepositoryException {
    try {
      factory = repository.getValueFactory();
      mergerUtils = new MergerUtilities(repository);
      this.repository = repository;
      deferredStatements = new ArrayList<Statement>();
      localIdMap = new THashMap<URI, URI>();
      reverseLocalIdMap = new THashMap<URI, URI>();
      noLinkId = new THashSet<URI>();
      
      // Initialise the maximum uid
      maxUID = uidOffset;
      
      // This data is only initialised if we are performing advanced data
      // validation
      if (validate) {
        validationData = new ValidationData();
      }
      
      mergeModels();
      
      // Identify and merge "bad" data with the invalid UID "-1". This is
      // only
      // intended
      // as a temporary handling of some bad data.
      if (validate) {
        mergeInvalid();
      }
      
      // Perform the merge/copy
      // recursiveMerge(globals.getRoot(), segment, "kdm/Segment");
      
      // Write any deferred data
      writeDeferredStatements();
      
      // Perform validation
      if (validate) {
        validationData.validate(noLinkId);
      }
      
      // Clear some variables to help ensure the code is running correctly
      factory = null;
      mergerUtils = null;
      this.repository = null;
      deferredStatements = null;
      localIdMap = null;
      reverseLocalIdMap = null;
      noLinkId = null;
      
      // Set the new UID offset for next linked node.
      uidOffset = maxUID;
    } finally {
      // Ensure we have cleared the file name
      this.path = null;
    }
  }
  
  /**
   * 
   * @param file
   * @param repository
   * @throws RepositoryException
   */
  public void merge(String filePath, Repository repository) throws RepositoryException {
    this.path = filePath;
    merge(repository);
    this.path = null; // ensure the path is reset.
  }
  
  /**
   * Identify and merge "bad" data with the invalid UID "-1". This is only intended as a temporary
   * handling of some bad data.
   * 
   * @throws RepositoryException
   */
  private void mergeInvalid() throws RepositoryException {
    final URI invalid = new MergerURI(MergerURI.KdmNS, "-1");
    // Get the invalid children.
    final List<URI> children = mergerUtils.getRelated(invalid, "contains");
    for (final URI child : children) {
      final String typeString = mergerUtils.getRDFAttribute(child, KdmConstants.KdmPredicate.KDM_TYPE.toString());
      final KdmType type = KdmType.valueOfKdmString(typeString);
      switch (type) {
        case COMPILATION_UNIT:
        case SHARED_UNIT:
          LOG.error("Element has invalid parent " + child + " (" + type + ")");
          LOG.error("  - Added to CodeAssembly");
          // output.print(assembly, KdmPredicate.CONTAINS.toURI(),
          // child);
          mergeInvalid(assembly, child, config.getMergeType(type.toString()));
          break;
        case SOURCE_FILE:
          LOG.error("Element has invalid parent " + child + " (" + type + ")");
          final URI dstModel = getDestinationModel("source/InventoryModel");
          // output.print(dstModel, KdmPredicate.CONTAINS.toURI(),
          // child);
          mergeInvalid(dstModel, child, config.getMergeType(type.toString()));
          LOG.error("  - Added to InventoryModel");
          break;
        case CODE_MODEL:
        case EXTENSION_FAMILY:
        case INVENTORY_MODEL:
          LOG.warn("Element has invalid parent " + child + " (" + type + ")");
          break;
        default:
          LOG.error("Element has invalid parent " + child + " (" + type + ")");
          break;
      }
    }
  }
  
  private void mergeInvalid(URI dst, URI child, int defaultMergeType) throws RepositoryException {
    final String type = mergerUtils.getRDFAttribute(child, "kdmType");
    // String name = rdfUtils.getRDFAttribute(child, "name");
    
    // If we are explicitly trying to copy, then copy
    if ((defaultMergeType == MergeConfig.COPY) || (config.getMergeType(type) == MergeConfig.COPY)) {
      // Copy, but do not "remember" the node IDs in the globals
      // map, since we will not be merging with this data.
      copyAndMergeRange(dst, child, false);
    }
    // Fallback is to merge if possible
    else {
      // If the node exists in the target, then merge, otherwise
      // copy it. This is done so we can error out on a merge
      // that does not match.
      // final Value linkID = rdfUtils.getRDFAttributeValue(child,
      // "link:id");
      final Value linkID = getLinkId(child);
      if (linkID == null) {
        return;
      }
      
      // Is this id already within the global map?
      // If it is then attempt to merge with it.
      if (globals.containsId(dst, linkID)) {
        final Stack<URI> ancestors = new Stack<URI>();
        ancestors.push(dst);
        mergeNode(ancestors, child);
      }
      // Otherwise just copy it in, updating the UID ranges of the
      // destination.
      else {
        // Copy, and "remember" the node IDs in the globals
        // map since we may be merging with this data later.
        copyAndMergeRange(dst, child, true);
      }
    }
  }
  
  /**
   * We are provided the destination URI (dst), the source model URI (src), and a default merge
   * type. For each of the children in the source, determine if the desired operation is to COPY the
   * data in, or to attempt to MERGE it with some existing data.
   * 
   * Some things we might want to always copy are CompilationUnits. There should be no overlap
   * between these in objects. SharedUnits are likely overlapping between files.
   * 
   * In the case of elements that are intended to be merged. If the element has NOT been added yet
   * (first time the SharedUnit is encountered, for example) then it will be copied.
   * 
   * The identity of mergable elements is determined by the "link:id" attribute, which needs to be
   * added by this point in the operations. The "link:id" is unique within a particular parent, but
   * should always be the same across different objects. An example might be a file name within a
   * particular directory.
   * 
   * When either merging or copying, the UID range of the target may need to be updated (a certainty
   * in copying).
   * 
   * @param dst
   * @param child
   * @param i
   * @throws RepositoryException
   */
  private void mergeModelContents(URI dst, URI src, int defaultMergeType) throws RepositoryException {
    // Get the contained nodes
    final List<URI> children = mergerUtils.getRelated(src, "contains");
    for (final URI child : children) {
      final String type = mergerUtils.getRDFAttribute(child, "kdmType");
      // String name = rdfUtils.getRDFAttribute(child, "name");
      
      // If we are explicitly trying to copy, then copy
      if ((defaultMergeType == MergeConfig.COPY) || (config.getMergeType(type) == MergeConfig.COPY)) {
        // Copy, but do not "remember" the node IDs in the globals
        // map, since we will not be merging with this data.
        copyAndMergeRange(dst, child, false);
      }
      // Fallback is to merge if possible
      else {
        // If the node exists in the target, then merge, otherwise
        // copy it. This is done so we can error out on a merge
        // that does not match.
        // final Value linkID = rdfUtils.getRDFAttributeValue(child,
        // "link:id");
        final Value linkID = getLinkId(child);
        
        if (linkID == null) {
          continue;
        }
        
        // Is this id already within the global map?
        // If it is then attempt to merge with it.
        if (globals.containsId(dst, linkID)) {
          final Stack<URI> ancestors = new Stack<URI>();
          ancestors.push(dst);
          mergeNode(ancestors, child);
        }
        // Otherwise just copy it in, updating the UID ranges of the
        // destination.
        else {
          // Copy, and "remember" the node IDs in the globals
          // map since we may be merging with this data later.
          copyAndMergeRange(dst, child, true);
        }
      }
    }
  }
  
  /**
   * Merge the models from the working repository into correct location in the new repository.
   * Depending on the model type we operate in different ways.
   * 
   * o InventoryModel: All inventory models are merged into one inventory model. There are NO UIDs
   * to offset, however. o CodeModel: The same model is used in each case, but instead of merging
   * into the CodeModel itself, we merge into its contained CodeAssembly
   * 
   * o ...?
   * 
   * @param segment
   * @throws RepositoryException
   */
  private void mergeModels() throws RepositoryException {
    // First get the top node.
    final URI segment = (URI) mergerUtils.getRootId();
    
    // Get the models
    final List<URI> children = mergerUtils.getRelated(segment, "contains");
    for (final URI child : children) {
      final String type = mergerUtils.getRDFAttribute(child, "kdmType");
      if (type == null) {
        LOG.error("Corrupt KDM file. No KDM Type for element " + child + ". Skipping.");
        continue;
      }
      LOG.debug("Merging " + type);
      
      // Get the destination model (or CodeAssembly)
      final URI dstModel = getDestinationModel(type);
      mergeModelContents(dstModel, child, config.getMergeType(type));
    }
  }
  
  /**
   * Currently I have altered the compile phase to the "file oriented" view of the code, but as soon
   * as the "object oriented" view is enabled this data will be lost.
   * 
   * The complexity is that by merging new data, the UID offsets for elements will change. Since the
   * offsets for nodes are currently written on the fly and there is no method for updating this
   * data, we are left with a situation where the UID ranges are incorrect.
   * 
   * @param node
   * @return
   * @throws RepositoryException
   */
  private void mergeNode(Stack<URI> ancestors, URI node) throws RepositoryException {
    final URI globalParent = ancestors.peek();
    if (localIdMap.containsKey(node)) {
      throw new UnsupportedOperationException("Attempting to re-merge element " + node + " into parent "
                                              + globalParent);
    }
    
    // Get the link:id
    final Value id = getLinkId(node);
    // final Value id = rdfUtils.getRDFAttributeValue(node, "link:id");
    
    // final String type = rdfUtils.getRDFAttribute(node,
    // KdmConstants.KdmPredicate.KDM_TYPE.toString());
    // if (type)
    //
    //
    // factory.createLiteral(arg0)
    
    // If there is no ID, then no merging should take place. A special case
    // is ActionElements
    // in methods which DO have an id, which is handled below.
    if (id == null) {
      noLinkId.add(node);
      switch (config.getNoIdMergeType()) {
        case MergeConfig.COPY:
          copy(globalParent, node, false);
          return;
        case MergeConfig.IGNORE:
          return;
        case MergeConfig.SINGLETON:
          return;
        case MergeConfig.MERGE:
          LOG.error("Element missing link:id, cannot merge " + node);
          return;
      }
    }
    
    // Is this id already within the global map? If it is not then
    // it should be copied there.
    if (!globals.containsId(globalParent, id)) {
      copy(globalParent, node, true);
      
      // Since we are merging with a parent which has already set a
      // UID/lastUID combo, we need to record the new ranges
      // for this element. Add the range to the deferred ranges.
      if (hasUID(node)) {
        // int uid = Integer.parseInt(rdfUtils.getRDFAttribute(node,
        // "UID"));
        int uid = IntUtils.parseInt(mergerUtils.getRDFAttribute(node, "UID"));
        final String luid = mergerUtils.getRDFAttribute(node, "lastUID");
        int lastUID = uid;
        if (luid != null) {
          // lastUID = Integer.parseInt(luid);
          lastUID = IntUtils.parseInt(luid);
        }
        uid += uidOffset;
        lastUID += uidOffset;
        if (uid > lastUID) LOG.error("Invalid UID range found for " + node + " into parent " + globalParent);
        if (lastUID < uid) {
          LOG.error("Invalid range for " + node + " (" + uid + "-" + lastUID + ")");
        }
        
        addDeferredRanges(ancestors, new Range(uid, lastUID));
      }
    }
    // Otherwise proceed with the merge
    else {
      final KdmType type = KdmType.valueOfKdmString(mergerUtils.getRDFAttribute(node, KdmConstants.KdmPredicate.KDM_TYPE
                                                                                                                        .toString()));
                                                                                                                        
      // Record the global node ID in the localMap for lookups
      // Required in both merge and singletons.
      final URI globalNode = globals.get(globalParent, id);
      localIdMap.put(node, globalNode);
      reverseLocalIdMap.put(globalNode, node);
      
      // If this is a singleton type then it should only exist ONCE in the
      // system.
      // Singletons are used when the contents cannot merge normally, for
      // example
      // with ActionElements.
      if (config.getMergeType(type.toString()) == MergeConfig.SINGLETON) {
        // If we are performing validation, we want to remember UIDs
        if (validate) {
          final Value uid = mergerUtils.getRDFAttributeValue(node, "UID");
          if (uid != null) {
            validationData.setUid(node, uid);
          }
        }
        return;
      }
      
      // Default case, not a method.
      {
        // When merging, in most cases we are merely visiting children
        // to see if they match up or new ones need copying. There are
        // some cases where the data for an element needs updating,
        // however. In these cases we need to record the statements
        // somewhere that allows them to me merged together.
        // Add them to said lists here...
        // FIXME: I said add them
        
        // If we are performing validation, we want to remember UIDs
        if (validate) {
          final Value uid = mergerUtils.getRDFAttributeValue(node, "UID");
          if (uid != null) {
            validationData.setUid(node, uid);
          }
        }
        
        // Get the children. Merge the child nodes with the global map.
        final List<URI> children = mergerUtils.getRelated(node, "contains");
        for (final URI child : children) {
          ancestors.push(globalNode);
          // If we are explicitly trying to copy, then copy
          if (config.getMergeType(type.toString()) == MergeConfig.COPY) {
            // Copy, but do not "remember" the node IDs in the
            // globals
            // map, since we will not be merging with this data.
            copy(globalNode, child, false);
          }
          // Fallback is to merge if possible
          else {
            mergeNode(ancestors, child);
          }
          ancestors.pop();
        }
      }
    }
  }
  
  /**
   * 
   * @param roots
   */
  public void setRoots(List<String> roots) {
    // // Disabled for C. May re-enable for C++
    // structure.setRoots(roots);
  }
  
  /**
   * Enable enhanced data validation. This can be used to ensure that the input KDM is well formed.
   * 
   * @param b
   */
  public void setValidation(boolean b) {
    validate = b;
  }
  
  /**
   * Return true if this predicate/object indicates that it should not be added to the link file.
   * This is done to reduce the size of the link file to something more manageable.
   * 
   * @param predicate
   * @return
   */
  private boolean skip(URI predicate, Value object) {
    // If we are not writing the lightweight link file, then
    // write everything.
    if ((format & NO_FLOWS) == 0) {
      return false;
    }
    if (object instanceof URI) {
      return false;
    }
    final String pname = predicate.getLocalName();
    if ("kdmType".equals(pname)) {
      if (object.stringValue().contains("Flow")) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Are we running in a modified mode where we should skip this child?
   * 
   * @param object
   * @return
   * @throws RepositoryException
   */
  private boolean skipChild(URI subject) throws RepositoryException {
    final String type = mergerUtils.getRDFAttribute(subject, KdmPredicate.KDM_TYPE.toString());
    if (type == null) {
      // This is actually bad, but I make too much noise on old KDM at the
      // moment. This is also
      // likely fixed, so consider making it an error later.
      LOG.debug("Contained element missing type " + subject);
      return false;
    }
    // Skip flows?
    if (((format & NO_FLOWS) != 0) && type.endsWith("Flow")) {
      return true;
    }
    // Skip local variables?
    if ((format & NO_LOCALS) != 0) {
      // Is this a local variable?
      if (isLocalVariable(subject)) {
        return true;
      }
      // Is this a relationship to a local variable?
      else {
        final URI to = mergerUtils.getRelatedValue(subject, KdmPredicate.TO.toString());
        if (to != null) {
          if (isLocalVariable(to)) {
            return true;
          }
          
        }
      }
    }
    
    // System.err.println("Check: " + type + " " + subject);
    return false;
  }
  
  /**
   * Write the deferred SourceRef information.
   * 
   * @param subject
   * @param predicate
   * @param object
   */
  private void writeDeferredSourceRef(URI subject, URI predicate, Value object) {
    final StringBuilder newref = new StringBuilder(100);
    String ref = object.stringValue();
    if (ref.trim().isEmpty()) {
      return; // Don't write empty SourceRefs
    }
    final String[] elements = commaSplitter.split(ref);
    // For each SourceRef element
    for (int i = 0; i < elements.length; i++) {
      // Translate the file reference. Copy the rest
      final String[] components = semiColonSplitter.split(elements[i]);
      if (components[0].trim().isEmpty()) {
        // Don't bother outputting source refs if there is no file
        // reference AND no snippet
        if ((components.length < 2) || !components[1].trim().isEmpty()) {
          continue;
        }
        
        newref.append(elements[i]);
      } else {
        final URI o = new MergerURI(MergerURI.KdmModelNS, components[0]);
        if (!localIdMap.containsKey(o)) {
          LOG.error("Missing translation object for URI " + o);
          dumpContextLocation(subject, (URI) object);
        } else {
          newref.append(localIdMap.get(o).getLocalName());
          // Add the connection between the CompilationUnit/ClassUnit
          // and the SourceFile
          // // Disabled for C. May re-enable for C++
          // if(structure.contains(subject))
          // structure.addReference(subject, localIdMap.get(o));
        }
        for (int j = 1; j < components.length; ++j) {
          newref.append(";").append(components[j]);
        }
      }
      if (i < elements.length - 1) {
        newref.append(",");
      }
    }
    ref = newref.toString();
    if (!ref.isEmpty()) {
      output.print(subject, predicate, new KdmLiteral(ref));
    }
  }
  
  /**
   * Write statements that were deferred until the new "object" URI could be identified.
   * 
   * If we deferred a SourceRef it needs to be decomposed, redirected, then written.
   * 
   * Other cases are more simple where the object just needs to be redirected.
   * 
   */
  private void writeDeferredStatements() {
    for (final Statement stmt : deferredStatements) {
      final URI subject = (URI) stmt.getSubject();
      final URI predicate = stmt.getPredicate();
      final Value object = stmt.getObject();
      
      final String pname = predicate.getLocalName();
      // Translate the compressed SourceRef.
      if (KdmPredicate.SOURCEREF.toString().equals(pname)) {
        writeDeferredSourceRef(subject, predicate, object);
      }
      // Default handling, object should be translated
      else {
        // Error out if the object cannot be translated
        if (!localIdMap.containsKey(object)) {
          LOG.error("Missing translation object " + stmt.getObject() + " [has link:id - " + !noLinkId.contains(object)
                    + "]");
          dumpContextLocation(subject, (URI) object);
        } else {
          output.print(subject, predicate, localIdMap.get(object));
        }
      }
    }
  }
  
  public long getId() {
    return id;
  }
}

/**
 * Perform some advanced validation of merged data.
 * 
 * @author Ken Duck
 *         
 */
class ValidationData {
  
  private static final Logger LOG = LoggerFactory.getLogger(RepositoryMerger.class);
  
  private final Map<URI, URI> validation_relationshipFrom;
  
  /**
   * 
   */
  private final Set<URI> validation_relationships;
  
  private final Map<URI, URI> validation_relationshipTo;
  
  /**
   * 
   */
  private final Map<URI, Value> validation_uidMap;
  
  public ValidationData() {
    validation_uidMap = new THashMap<URI, Value>();
    validation_relationships = new THashSet<URI>();
    validation_relationshipFrom = new THashMap<URI, URI>();
    validation_relationshipTo = new THashMap<URI, URI>();
  }
  
  private void addRelationship(URI rel) {
    validation_relationships.add(rel);
  }
  
  public void addRelationshipType(URI rel, KdmType type) {
    throw new UnsupportedOperationException();
  }
  
  public void setRelationshipFrom(URI rel, URI from) {
    addRelationship(rel);
    validation_relationshipFrom.put(rel, from);
  }
  
  public void setRelationshipTo(URI rel, URI from) {
    addRelationship(rel);
    validation_relationshipTo.put(rel, from);
  }
  
  public void setUid(URI uri, Value uid) {
    validation_uidMap.put(uri, uid);
  }
  
  /**
   * Run tests to ensure validate the results of the merge to ensure that there are no anomalies.
   */
  public void validate(Set<URI> noLinkId) {
    int relCount = 0;
    // For each found relationship, ensure there are from and to sides, and
    // that there are
    // UIDs for each of these.
    for (final URI rel : validation_relationships) {
      ++relCount;
      if (validation_relationshipFrom.containsKey(rel)) {
        final URI endpoint = validation_relationshipFrom.get(rel);
        if (!validation_uidMap.containsKey(endpoint)) {
          LOG.error("Missing UID for 'from' element " + endpoint + " [has link:id - " + !noLinkId.contains(endpoint)
                    + "]");
          LOG.error("  Relationship: " + rel);
        }
      } else {
        LOG.error("Missing from element for relationship " + rel);
      }
      if (validation_relationshipTo.containsKey(rel)) {
        final URI endpoint = validation_relationshipTo.get(rel);
        if (!validation_uidMap.containsKey(endpoint)) {
          LOG.error("Missing UID for 'to' element " + endpoint + " [has link:id - " + !noLinkId.contains(endpoint)
                    + "]");
          LOG.error("  Relationship: " + rel);
        }
      } else {
        LOG.error("Missing to element for relationship " + rel);
      }
    }
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Relationships found: " + relCount);
    }
  }
}
