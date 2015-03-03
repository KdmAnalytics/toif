/**
 * 
 */
package com.kdmanalytics.kdm.repositoryMerger.Utilities;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;


/**
 * @author adam
 *
 */
public class KdmConstants
{   
    
    public enum KdmPredicate
    {
        STEREOTYPE("stereotype"), //$NON-NLS-1$
        CONTAINS("contains"), //$NON-NLS-1$
        KDM_TYPE("kdmType"), //$NON-NLS-1$
        NAME("name"), //$NON-NLS-1$
        TYPE("type"), //$NON-NLS-1$
        PATH("path"), //$NON-NLS-1$
        GROUP("__group"), //$NON-NLS-1$
        SOURCEREF("SourceRef"), //$NON-NLS-1$
        FROM("from"), //$NON-NLS-1$
        TO("to"), //$NON-NLS-1$
        FILE("file"), //$NON-NLS-1$
        ITEM("__item"), //$NON-NLS-1$
        INDEX("__index"), //$NON-NLS-1$
        SNIPPET("snippet"), //$NON-NLS-1$
        KIND("kind"), //$NON-NLS-1$
        EXPORT("export"), UUID("UUID"); //$NON-NLS-1$
        
        private final String predicateString;
        
        private final URI predicateUri;
        
        KdmPredicate(String predicateName)
        {
            predicateString = predicateName;
            
            predicateUri = new MergerURI(MergerURI.KdmNS, predicateName);
        }
        
        @Override
        public String toString()
        {
            return predicateString;
        }
        
        public URI toURI()
        {
            return predicateUri;
        }
    }
    
    public enum KdmType
    {
        // Models
        BUILD_MODEL("build/BuildModel"), //$NON-NLS-1$
        CODE_MODEL("code/CodeModel"), //$NON-NLS-1$
        DATA_MODEL("data/DataModel"), //$NON-NLS-1$
        INVENTORY_MODEL("source/InventoryModel"), //$NON-NLS-1$
        PLATFORM_MODEL("platform/PlatformModel"), //$NON-NLS-1$
        STRUCTURE_MODEL("structure/StructureModel"), //$NON-NLS-1$
        CONCEPTUAL_MODEL("conceptual/ConceptualModel"), //$NON-NLS-1$
        EVENT_MODEL("event/EventModel"), //$NON-NLS-1$
        UI_MODEL("ui/UIModel"), //$NON-NLS-1$
        
        // Core
        ELEMENT("core/Element"), //$NON-NLS-1$
        KDM_ENTITY("core/KDMEntity"), //$NON-NLS-1$
        KDM_RELATIONSHIP("core/KDMRelationship"), //$NON-NLS-1$
        MODEL_ELEMENT("core/ModelElement"), //$NON-NLS-1$
        
        // Kdm
        ANNOTATION("kdm/Annotation"), //$NON-NLS-1$
        ATTRIBUTE("kdm/Attribute"), //$NON-NLS-1$
        AUDIT("kdm/Audit"), //$NON-NLS-1$
        EXTENDED_VALUE("kdm/ExtendedValue"), //$NON-NLS-1$
        EXTENSION_FAMILY("kdm/ExtensionFamily"), //$NON-NLS-1$
        SEGMENT("kdm/Segment"), //$NON-NLS-1$
        STEREOTYPE("kdm/Stereotype"), //$NON-NLS-1$
        TAG_DEFINITION("kdm/TagDefinition"), //$NON-NLS-1$
        TAGGED_REF("kdm/TaggedRef"), //$NON-NLS-1$
        TAGGED_VALUE("kdm/TaggedValue"), //$NON-NLS-1$
        
        // Action
        ACTION_ELEMENT("action/ActionElement"), //$NON-NLS-1$
        ACTION_RELATIONSHIP("action/ActionRelationship"), //$NON-NLS-1$
        ADDRESSES("action/Addresses"), //$NON-NLS-1$
        BLOCK_UNIT("action/BlockUnit"), //$NON-NLS-1$
        CALLS("action/Calls"), //$NON-NLS-1$
        CATCH_UNIT("action/CatchUnit"), //$NON-NLS-1$
        COMPLIES_TO("action/CompliesTo"), //$NON-NLS-1$
        CONTROL_FLOW("action/ControlFlow"), //$NON-NLS-1$
        CREATES("action/Creates"), //$NON-NLS-1$
        DISPATCHES("action/Dispatches"), //$NON-NLS-1$
        ENTRY_FLOW("action/EntryFlow"), //$NON-NLS-1$
        EXCEPTION_FLOW("action/ExceptionFlow"), //$NON-NLS-1$
        EXIT_FLOW("action/ExitFlow"), //$NON-NLS-1$
        FALSE_FLOW("action/FalseFlow"), //$NON-NLS-1$
        FINALLY_FLOW("action/FinallyFlow"), //$NON-NLS-1$
        FLOW("action/Flow"), //$NON-NLS-1$
        GUARDED_FLOW("action/GuardedFlow"), //$NON-NLS-1$
        READS("action/Reads"), //$NON-NLS-1$
        THROWS("action/Throws"), //$NON-NLS-1$
        TRUE_FLOW("action/TrueFlow"), //$NON-NLS-1$
        TRY_UNIT("action/TryUnit"), //$NON-NLS-1$
        USES_TYPE("action/UsesType"), //$NON-NLS-1$
        WRITES("action/Writes"), //$NON-NLS-1$
        
        // Code
        ARRAY_TYPE("code/ArrayType"), //$NON-NLS-1$
        BAG_TYPE("code/BagType"), //$NON-NLS-1$
        BITSTRING_TYPE("code/BitstringType"), //$NON-NLS-1$
        BIT_TYPE("code/BitType"), //$NON-NLS-1$
        BOOLEAN_TYPE("code/BooleanType"), //$NON-NLS-1$
        CALLABLE_KIND("code/CallableKind"), //$NON-NLS-1$
        CALLABLE_UNIT("code/CallableUnit"), //$NON-NLS-1$
        CHAR_TYPE("code/CharType"), //$NON-NLS-1$
        CHOICE_TYPE("code/ChoiceType"), //$NON-NLS-1$
        CLASS_UNIT("code/ClassUnit"), //$NON-NLS-1$
        CODE_ASSEMBLY("code/CodeAssembly"), //$NON-NLS-1$
        CODE_ELEMENT("code/CodeElement"), //$NON-NLS-1$
        CODE_ITEM("code/CodeItem"), //$NON-NLS-1$
        CODE_RELATIONSHIP("code/CodeRelationship"), //$NON-NLS-1$
        COMMENT_UNIT("code/CommentUnit"), //$NON-NLS-1$
        COMPILATION_UNIT("code/CompilationUnit"), //$NON-NLS-1$
        COMPOSITE_TYPE("code/CompositeType"), //$NON-NLS-1$
        COMPUTATIONAL_OBJECT("code/ComputationalObject"), //$NON-NLS-1$
        CONDITIONAL_DIRECTIVE("code/ConditionalDirective"), //$NON-NLS-1$
        CONTROL_ELEMENT("code/ControlElement"), //$NON-NLS-1$
        DATA_ELEMENT("code/DataElement"), //$NON-NLS-1$
        DATATYPE("code/Datatype"), //$NON-NLS-1$
        DATE_TYPE("code/DateType"), //$NON-NLS-1$
        DECIMAL_TYPE("code/DecimalType"), //$NON-NLS-1$
        DEFINED_TYPE("code/DefinedType"), //$NON-NLS-1$
        DERIVED_TYPE("code/DerivedType"), //$NON-NLS-1$
        ENUMERATED_TYPE("code/EnumeratedType"), //$NON-NLS-1$
        EXPANDS("code/Expands"), //$NON-NLS-1$
        EXPORT_KIND("code/ExportKind"), //$NON-NLS-1$
        EXTENDS("code/Extends"), //$NON-NLS-1$
        FLOAT_TYPE("code/FloatType"), //$NON-NLS-1$
        GENERATED_FROM("code/GeneratedFrom"), //$NON-NLS-1$
        HAS_TYPE("code/HasType"), //$NON-NLS-1$
        HAS_VALUE("code/HasValue"), //$NON-NLS-1$
        IMPLEMENTATION_OF("code/ImplementationOf"), //$NON-NLS-1$
        IMPLEMENTS("code/Implements"), //$NON-NLS-1$
        IMPORTS("code/Imports"), //$NON-NLS-1$
        INCLUDE_DIRECTIVE("code/IncludeDirective"), //$NON-NLS-1$
        INCLUDES("code/Includes"), //$NON-NLS-1$
        INDEX_UNIT("code/IndexUnit"), //$NON-NLS-1$
        INSTANCE_OF("code/InstanceOf"), //$NON-NLS-1$
        INTEGER_TYPE("code/IntegerType"), //$NON-NLS-1$
        INTERFACE_UNIT("code/InterfaceUnit"), //$NON-NLS-1$
        ITEM_UNIT("code/ItemUnit"), //$NON-NLS-1$
        LANGUAGE_UNIT("code/LanguageUnit"), //$NON-NLS-1$
        MACRO_DIRECTIVE("code/MacroDirective"), //$NON-NLS-1$
        MACRO_KIND("code/MacroKind"), //$NON-NLS-1$
        MACRO_UNIT("code/MacroUnit"), //$NON-NLS-1$
        MEMBER_UNIT("code/MemberUnit"), //$NON-NLS-1$
        METHOD_KIND("code/MethodKind"), //$NON-NLS-1$
        METHOD_UNIT("code/MethodUnit"), //$NON-NLS-1$
        MODULE("code/Module"), //$NON-NLS-1$
        NAMESPACE_UNIT("code/NamespaceUnit"), //$NON-NLS-1$
        OCTETSTRING_TYPE("code/OctetstringType"), //$NON-NLS-1$
        OCTET_TYPE("code/OctetType"), //$NON-NLS-1$
        ORDINAL_TYPE("code/OrdinalType"), //$NON-NLS-1$
        PACKAGE("code/Package"), //$NON-NLS-1$
        PARAMETER_KIND("code/ParameterKind"), //$NON-NLS-1$
        PARAMETER_TO("code/ParameterTo"), //$NON-NLS-1$
        PARAMETER_UNIT("code/ParameterUnit"), //$NON-NLS-1$
        POINTER_TYPE("code/PointerType"), //$NON-NLS-1$
        PREPROCESSOR_DIRECTIVE("code/PreprocessorDirective"), //$NON-NLS-1$
        PRIMITIVE_TYPE("code/PrimitiveType"), //$NON-NLS-1$
        RANGE_TYPE("code/RangeType"), //$NON-NLS-1$
        RECORD_TYPE("code/RecordType"), //$NON-NLS-1$
        REDEFINES("code/Redefines"), //$NON-NLS-1$
        SCALED_TYPE("code/ScaledType"), //$NON-NLS-1$
        SEQUENCE_TYPE("code/SequenceType"), //$NON-NLS-1$
        SET_TYPE("code/SetType"), //$NON-NLS-1$
        SHARED_UNIT("code/SharedUnit"), //$NON-NLS-1$
        SIGNATURE("code/Signature"), //$NON-NLS-1$
        STORABLE_KIND("code/StorableKind"), //$NON-NLS-1$
        STORABLE_UNIT("code/StorableUnit"), //$NON-NLS-1$
        STRING_TYPE("code/StringType"), //$NON-NLS-1$
        SYNONYM_TYPE("code/SynonymType"), //$NON-NLS-1$
        TEMPLATE_PARAMETER("code/TemplateParameter"), //$NON-NLS-1$
        TEMPLATE_TYPE("code/TemplateType"), //$NON-NLS-1$
        TEMPLATE_UNIT("code/TemplateUnit"), //$NON-NLS-1$
        TIME_TYPE("code/TimeType"), //$NON-NLS-1$
        TYPE_UNIT("code/TypeUnit"), //$NON-NLS-1$
        VALUE("code/Value"), //$NON-NLS-1$
        VALUE_ELEMENT("code/ValueElement"), //$NON-NLS-1$
        VALUE_LIST("code/ValueList"), //$NON-NLS-1$
        VARIANT_TO("code/VariantTo"), //$NON-NLS-1$
        VISIBLE_IN("code/VisibleIn"), //$NON-NLS-1$
        VOID_TYPE("code/VoidType"), //$NON-NLS-1$
        
        // Source
        BINARY_FILE("source/BinaryFile"), //$NON-NLS-1$
        CONFIGURATION("source/Configuration"), //$NON-NLS-1$
        DEPENDS_ON("source/DependsOn"), //$NON-NLS-1$
        DIRECTORY("source/Directory"), //$NON-NLS-1$
        EXECUTABLE_FILE("source/ExecutableFile"), //$NON-NLS-1$
        IMAGE("source/Image"), //$NON-NLS-1$
        INVENTORY_CONTAINER("source/InventoryContainer"), //$NON-NLS-1$
        INVENTORY_ELEMENT("source/InventoryElement"), //$NON-NLS-1$
        INVENTORY_ITEM("source/InventoryItem"), //$NON-NLS-1$
        INVENTORY_RELATIONSHIP("source/InventoryRelationship"), //$NON-NLS-1$
        PROJECT("source/Project"), //$NON-NLS-1$
        RESOURCE_DESCRIPTION("source/ResourceDescription"), //$NON-NLS-1$
        SOURCE_FILE("source/SourceFile"), //$NON-NLS-1$
        SOURCE_REF("source/SourceRef"), //$NON-NLS-1$
        SOURCE_REGION("source/SourceRegion"), //$NON-NLS-1$
        
        // Data
        ALL_CONTENT("data/AllContent"), //$NON-NLS-1$
        ANY_CONTENT("data/AnyContent"), //$NON-NLS-1$
        CATALOG("data/Catalog"), //$NON-NLS-1$
        CHOICE_CONTENT("data/ChoiceContent"), //$NON-NLS-1$
        COLUMN_SET("data/ColumnSet"), //$NON-NLS-1$
        COMPLEX_CONTENT_TYPE("data/ComplexContentType"), //$NON-NLS-1$
        CONTENT_ATTRIBUTE("data/ContentAttribute"), //$NON-NLS-1$
        CONTENT_ELEMENT("data/ContentElement"), //$NON-NLS-1$
        CONTENT_ITEM("data/ContentItem"), //$NON-NLS-1$
        CONTENT_REFERENCE("data/ContentReference"), //$NON-NLS-1$
        CONTENT_RESTRICTION("data/ContentRestriction"), //$NON-NLS-1$
        DATA_ACTION("data/DataAction"), //$NON-NLS-1$
        DATA_CONTAINER("data/DataContainer"), //$NON-NLS-1$
        DATA_EVENT("data/DataEvent"), //$NON-NLS-1$
        DATA_RELATIONSHIP("data/DataRelationship"), //$NON-NLS-1$
        DATA_RESOURCE("data/DataResource"), //$NON-NLS-1$
        DATA_SEGMENT("data/DataSegment"), //$NON-NLS-1$
        DATATYPE_OF("data/DatatypeOf"), //$NON-NLS-1$
        EXTENDED_DATA_ELEMENT("data/ExtendedDataElement"), //$NON-NLS-1$
        EXTENSION_TO("data/ExtensionTo"), //$NON-NLS-1$
        GROUP_CONTENT("data/GroupContent"), //$NON-NLS-1$
        HAS_CONTENT("data/HasContent"), //$NON-NLS-1$
        INDEX("data/Index"), //$NON-NLS-1$
        INDEX_ELEMENT("data/IndexElement"), //$NON-NLS-1$
        KEY_RELATION("data/KeyRelation"), //$NON-NLS-1$
        MANAGES_DATA("data/ManagesData"), //$NON-NLS-1$
        MIXED_CONTENT("data/MixedContent"), //$NON-NLS-1$
        PRODUCES_DATA_ELEMENT("data/ProducesDataElement"), //$NON-NLS-1$
        READS_COLUMN_SET("data/ReadsColumnSet"), //$NON-NLS-1$
        RECORD_FILE("data/RecordFile"), //$NON-NLS-1$
        REFERENCE_KEY("data/ReferenceKey"), //$NON-NLS-1$
        REFERENCE_TO("data/ReferenceTo"), //$NON-NLS-1$
        RELATIONAL_SCHEMA("data/RelationalSchema"), //$NON-NLS-1$
        RELATIONAL_TABLE("data/RelationalTable"), //$NON-NLS-1$
        RELATIONAL_VIEW("data/RelationalView"), //$NON-NLS-1$
        RESTRICTION_OF("data/RestrictionOf"), //$NON-NLS-1$
        SEQ_CONTENT("data/SeqContent"), //$NON-NLS-1$
        SIMPLE_CONTENT_TYPE("data/SimpleContentType"), //$NON-NLS-1$
        TRIGGER("data/Trigger"), //$NON-NLS-1$
        TYPED_BY("data/TypedBy"), //$NON-NLS-1$
        UNIQUE_KEY("data/UniqueKey"), //$NON-NLS-1$
        WRITES_COLUMN_SET("data/WritesColumnSet"), //$NON-NLS-1$
        XML_SCHEMA("data/XMLSchema"), //$NON-NLS-1$
        
        // Structure
        ARCHITECTURE_VIEW("structure/ArchitectureView"), //$NON-NLS-1$
        COMPONENT("structure/Component"), //$NON-NLS-1$
        LAYER("structure/Layer"), //$NON-NLS-1$
        SOFTWARE_SYSTEM("structure/SoftwareSystem"), //$NON-NLS-1$
        STRUCTURE_ELEMENT("structure/StructureElement"), //$NON-NLS-1$
        STRUCTURE_RELATIONSHIP("structure/StructureRelationship"), //$NON-NLS-1$
        SUBSYSTEM("structure/Subsystem"), //$NON-NLS-1$
        
        // Build
        BUILD_COMPONENT("build/BuildComponent"), //$NON-NLS-1$
        BUILD_DESCRIPTION("build/BuildDescription"), //$NON-NLS-1$
        BUILD_ELEMENT("build/BuildElement"), //$NON-NLS-1$
        BUILD_PRODUCT("build/BuildProduct"), //$NON-NLS-1$
        BUILD_RELATIONSHIP("build/BuildRelationship"), //$NON-NLS-1$
        BUILD_RESOURCE("build/BuildResource"), //$NON-NLS-1$
        BUILD_STEP("build/BuildStep"), //$NON-NLS-1$
        CONSUMES("build/Consumes"), //$NON-NLS-1$
        DESCRIBED_BY("build/DescribedBy"), //$NON-NLS-1$
        LIBRARY("build/Library"), //$NON-NLS-1$
        LINKS_TO("build/LinksTo"), //$NON-NLS-1$
        PRODUCES("build/Produces"), //$NON-NLS-1$
        SUPPLIED_BY("build/SuppliedBy"), //$NON-NLS-1$
        SUPPLIER("build/Supplier"), //$NON-NLS-1$
        SUPPORTED_BY("build/SupportedBy"), //$NON-NLS-1$
        SYMBOLIC_LINK("build/SymbolicLink"), //$NON-NLS-1$
        TOOL("build/Tool"), //$NON-NLS-1$
        
        // Conceptual
        BEHAVIOR_UNIT("conceptual/BehaviorUnit"), //$NON-NLS-1$
        CONCEPTUAL_CONTAINER("conceptual/ConceptualContainer"), //$NON-NLS-1$
        CONCEPTUAL_ELEMENT("conceptual/ConceptualElement"), //$NON-NLS-1$
        CONCEPTUAL_FLOW("conceptual/ConceptualFlow"), //$NON-NLS-1$
        CONCEPTUAL_RELATIONSHIP("conceptual/ConceptualRelationship"), //$NON-NLS-1$
        CONCEPTUAL_ROLE("conceptual/ConceptualRole"), //$NON-NLS-1$
        FACT_UNIT("conceptual/FactUnit"), //$NON-NLS-1$
        RULE_UNIT("conceptual/RuleUnit"), //$NON-NLS-1$
        SCENARIO_UNIT("conceptual/ScenarioUnit"), //$NON-NLS-1$
        TERM_UNIT("conceptual/TermUnit"), //$NON-NLS-1$
        
        // Event
        CONSUMES_EVENT("event/ConsumesEvent"), //$NON-NLS-1$
        EVENT("event/Event"), //$NON-NLS-1$
        EVENT_ACTION("event/EventAction"), //$NON-NLS-1$
        EVENT_ELEMENT("event/EventElement"), //$NON-NLS-1$
        EVENT_RELATIONSHIP("event/EventRelationship"), //$NON-NLS-1$
        EVENT_RESOURCE("event/EventResource"), //$NON-NLS-1$
        HAS_STATE("event/HasState"), //$NON-NLS-1$
        INITIAL_STATE("event/InitialState"), //$NON-NLS-1$
        NEXT_STATE("event/NextState"), //$NON-NLS-1$
        ON_ENTRY("event/OnEntry"), //$NON-NLS-1$
        ON_EXIT("event/OnExit"), //$NON-NLS-1$
        PRODUCES_EVENT("event/ProducesEvent"), //$NON-NLS-1$
        READS_STATE("event/ReadsState"), //$NON-NLS-1$
        STATE("event/State"), //$NON-NLS-1$
        TRANSITION("event/Transition"), //$NON-NLS-1$
        
        // Platform
        BINDS_TO("platform/BindsTo"), //$NON-NLS-1$
        DATA_MANAGER("platform/DataManager"), //$NON-NLS-1$
        DEFINED_BY("platform/DefinedBy"), //$NON-NLS-1$
        DEPLOYED_COMPONENT("platform/DeployedComponent"), //$NON-NLS-1$
        DEPLOYED_RESOURCE("platform/DeployedResource"), //$NON-NLS-1$
        DEPLOYED_SOFTWARE_SYSTEM("platform/DeployedSoftwareSystem"), //$NON-NLS-1$
        EXECUTION_RESOURCE("platform/ExecutionResource"), //$NON-NLS-1$
        EXTERNAL_ACTOR("platform/ExternalActor"), //$NON-NLS-1$
        FILE_RESOURCE("platform/FileResource"), //$NON-NLS-1$
        LOADS("platform/Loads"), //$NON-NLS-1$
        LOCK_RESOURCE("platform/LockResource"), //$NON-NLS-1$
        MACHINE("platform/Machine"), //$NON-NLS-1$
        MANAGES_RESOURCE("platform/ManagesResource"), //$NON-NLS-1$
        MARSHALLED_RESOURCE("platform/MarshalledResource"), //$NON-NLS-1$
        MESSAGING_RESOURCE("platform/MessagingResource"), //$NON-NLS-1$
        NAMING_RESOURCE("platform/NamingResource"), //$NON-NLS-1$
        PLATFORM_ACTION("platform/PlatformAction"), //$NON-NLS-1$
        PLATFORM_ELEMENT("platform/PlatformElement"), //$NON-NLS-1$
        PLATFORM_EVENT("platform/PlatformEvent"), //$NON-NLS-1$
        PLATFORM_RELATIONSHIP("platform/PlatformRelationship"), //$NON-NLS-1$
        PROCESS("platform/Process"), //$NON-NLS-1$
        READS_RESOURCE("platform/ReadsResource"), //$NON-NLS-1$
        REQUIRES("platform/Requires"), //$NON-NLS-1$
        RESOURCE_TYPE("platform/ResourceType"), //$NON-NLS-1$
        RUNTIME_RESOURCE("platform/RuntimeResource"), //$NON-NLS-1$
        SPAWNS("platform/Spawns"), //$NON-NLS-1$
        STREAM_RESOURCE("platform/StreamResource"), //$NON-NLS-1$
        THREAD("platform/Thread"), //$NON-NLS-1$
        WRITES_RESOURCE("platform/WritesResource"), //$NON-NLS-1$
        
        // UI
        DISPLAYS("ui/Displays"), //$NON-NLS-1$
        DISPLAYS_IMAGE("ui/DisplaysImage"), //$NON-NLS-1$
        MANAGES_UI("ui/ManagesUI"), //$NON-NLS-1$
        READS_UI("ui/ReadsUI"), //$NON-NLS-1$
        REPORT("ui/Report"), //$NON-NLS-1$
        SCREEN("ui/Screen"), //$NON-NLS-1$
        UI_ACTION("ui/UIAction"), //$NON-NLS-1$
        UI_DISPLAY("ui/UIDisplay"), //$NON-NLS-1$
        UI_ELEMENT("ui/UIElement"), //$NON-NLS-1$
        UI_EVENT("ui/UIEvent"), //$NON-NLS-1$
        UI_FIELD("ui/UIField"), //$NON-NLS-1$
        UI_FLOW("ui/UIFlow"), //$NON-NLS-1$
        UI_LAYOUT("ui/UILayout"), //$NON-NLS-1$
        UI_RELATIONSHIP("ui/UIRelationship"), //$NON-NLS-1$
        UI_RESOURCE("ui/UIResource"), //$NON-NLS-1$
        WRITES_UI("ui/WritesUI"); //$NON-NLS-1$
        
        private static final Map<String, KdmType> defaultTypeMap = new HashMap<String, KdmType>();
        
        static
        {
            for (KdmType t : KdmType.values())
            {
                defaultTypeMap.put(t.toString(), t);
            }
        }
        
        private final String typeString;
        
        private Literal typeLiteral;
        
        KdmType(String aTypename)
        {
            typeString = aTypename;
            typeLiteral = new KdmLiteral(aTypename);
        }
        
        public static KdmType valueOfKdmString(String kdmTypeString)
        {
            return defaultTypeMap.get(kdmTypeString);
        }
        
        public Literal toLiteral()
        {
            return typeLiteral;
            
        }
        
        @Override
        public String toString()
        {
            return typeString;
        }
    }
    
    public enum WorkbenchPredicate
    {
        UID("UID"), LAST_UID("lastUID"), WORKBENCH_RANGES("__KNT_UIDs"), LINK_ID("link:id"), LINK_SRC("link:src"), LINK_SNK("link:snk");
        
        private final String predicateString;
        
        private final URI predicateUri;
        
        WorkbenchPredicate(String predicateName)
        {
            predicateString = predicateName;
            predicateUri = new MergerURI(MergerURI.KdmNS, predicateName);
            // predicateUri =
            // SimpleValueFactory.getSimpleValueFactory().createURI(SimpleURI.kdmNS,
            // predicateName);
        }
        
        @Override
        public String toString()
        {
            return predicateString;
        }
        
        public URI toURI()
        {
            return predicateUri;
        }
        
    }
    
    public enum WorkbenchStereotype
    {
        /**
         * Elements that the Workbench should not display are stereotyped as
         * hidden.
         */
        HIDDEN("__HIDDEN__"),
        /**
         * KDM's specially managed Structure Models should be stereotyped in a
         * special way. This allows us to know which models we should do
         * additional processing for inside the various probes.
         */
        MANAGED_STRUCTURE_MODEL("__MANAGED__"),

        /**
         * Elements that the Workbench should not delete are stereotyped as
         * undeletable
         */
        CANNOT_DELETE_MODEL("__UNDELETABLE__"),
        /**
         * User created models are marked because they have some special
         * functionality, such as special persistence into future builds.
         */
        USER_MODEL("__USER_MODEL__"),
        /**
         * Mark a node as containing user data. This is required for proper
         * persistence between builds.
         */
        USER_DATA("__USER_DATA__"),
        /**
         * Indicate the model was created by the API agent
         */
        API_AGENT_MODEL("__API_AGENT_MODEL__"),
        /**
         * Set to indicate we are overriding the UID. On load of such a model,
         * the target UID is discovered through various heuristics.
         */
        UID_OVERRIDE("__UID_OVERRIDE__"),
        /**
         * Indicate the model was created by the Overview agent
         */
        OVERVIEW_AGENT_MODEL("__OVERVIEW_AGENT_MODEL__"),

        /**
         * Indicates that the aggregated relationships have been pre-calculated
         * for this diagram
         */
        CACHED_AGGREGATES("__CACHED_AGGREGATED_RELATIONSHIPS__"),

        /**
         * Indicates that this is a generated model such as an Overview or API
         * model.
         */
        GENERATED_MODEL("__GENERATED_MODEL__"),

        /**
         * 
         */
        REFERS_TO("__REFERS_TO__"),

        COMPILE("COMPILE"), ARCHIVE("ARCHIVE"), LINK("LINK"),

        /**
         * Indicates that the UIDs have been refined (specified) below this
         * level, where normally they would not.
         */
        REFINED("__REFINED__"),

        /**
         * Indicates that the specified platform resource has content defined by
         * the specified data resource.
         */
        HAS_CONTENT("__HAS_CONTENT__"),

        /**
         * The default context indicates to workbench which model is the
         * preferred owner model for elements that do not have a prescribed
         * context.
         */
        DEFAULT_CONTEXT("__DEFAULT_CONTEXT__");
        
        private String stereoTypeName;
        
        private Literal stereoTypeLiteral;
        
        /**
         * A map with which we can get matching Stereotypes for strings.
         */
        private static final Map<String, WorkbenchStereotype> defaultTypeMap = new HashMap<String, WorkbenchStereotype>();
        
        /**
         * Initialise the lookup table.
         */
        static
        {
            for (WorkbenchStereotype t : WorkbenchStereotype.values())
            {
                defaultTypeMap.put(t.toString(), t);
            }
        }
        
        /**
         * 
         * @param name
         */
        WorkbenchStereotype(String name)
        {
            stereoTypeName = name;
            stereoTypeLiteral = new KdmLiteral(name);
        }
        
        /**
         * Get the stereotype that matches the string
         * 
         * @param kdmTypeString
         * @return
         */
        public static WorkbenchStereotype valueOfKdmString(String kdmTypeString)
        {
            return defaultTypeMap.get(kdmTypeString);
        }
        
        public Literal toLiteral()
        {
            return stereoTypeLiteral;
        }
        
        /**
         * 
         */
        @Override
        public String toString()
        {
            return stereoTypeName;
        }
        
    }
}
