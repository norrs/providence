package net.morimekta.providence.model;

/**
 * &lt;namespace&gt;* &lt;include&gt;* &lt;declataion&gt;*
 */
@SuppressWarnings("unused")
public class ProgramType
        implements net.morimekta.providence.PMessage<ProgramType,ProgramType._Field>,
                   Comparable<ProgramType>,
                   java.io.Serializable {
    private final static long serialVersionUID = 2224801959218006031L;

    private final String mDocumentation;
    private final String mProgramName;
    private final java.util.List<String> mIncludes;
    private final java.util.Map<String,String> mNamespaces;
    private final java.util.List<net.morimekta.providence.model.Declaration> mDecl;

    private volatile int tHashCode;

    public ProgramType(String pDocumentation,
                       String pProgramName,
                       java.util.List<String> pIncludes,
                       java.util.Map<String,String> pNamespaces,
                       java.util.List<net.morimekta.providence.model.Declaration> pDecl) {
        mDocumentation = pDocumentation;
        mProgramName = pProgramName;
        if (pIncludes != null) {
            mIncludes = com.google.common.collect.ImmutableList.copyOf(pIncludes);
        } else {
            mIncludes = null;
        }
        if (pNamespaces != null) {
            mNamespaces = com.google.common.collect.ImmutableMap.copyOf(pNamespaces);
        } else {
            mNamespaces = null;
        }
        if (pDecl != null) {
            mDecl = com.google.common.collect.ImmutableList.copyOf(pDecl);
        } else {
            mDecl = null;
        }
    }

    private ProgramType(_Builder builder) {
        mDocumentation = builder.mDocumentation;
        mProgramName = builder.mProgramName;
        if (builder.isSetIncludes()) {
            mIncludes = builder.mIncludes.build();
        } else {
            mIncludes = null;
        }
        if (builder.isSetNamespaces()) {
            mNamespaces = builder.mNamespaces.build();
        } else {
            mNamespaces = null;
        }
        if (builder.isSetDecl()) {
            mDecl = builder.mDecl.build();
        } else {
            mDecl = null;
        }
    }

    public boolean hasDocumentation() {
        return mDocumentation != null;
    }

    /**
     * Program documentation must come before the first statement of the header.
     *
     * @return The field value
     */
    public String getDocumentation() {
        return mDocumentation;
    }

    public boolean hasProgramName() {
        return mProgramName != null;
    }

    /**
     * The program name, deducted from the .thrift IDL file name.
     *
     * @return The field value
     */
    public String getProgramName() {
        return mProgramName;
    }

    public int numIncludes() {
        return mIncludes != null ? mIncludes.size() : 0;
    }

    public boolean hasIncludes() {
        return mIncludes != null;
    }

    /**
     * List of included thrift files. Same as from the actual thrift file.
     * <p>
     * include &quot;&lt;program&gt;.thrift&quot;
     *
     * @return The field value
     */
    public java.util.List<String> getIncludes() {
        return mIncludes;
    }

    public int numNamespaces() {
        return mNamespaces != null ? mNamespaces.size() : 0;
    }

    public boolean hasNamespaces() {
        return mNamespaces != null;
    }

    /**
     * Map of language to laguage dependent namespace identifier.
     * <p>
     * namespace &lt;key&gt; &lt;value&gt;
     *
     * @return The field value
     */
    public java.util.Map<String,String> getNamespaces() {
        return mNamespaces;
    }

    public int numDecl() {
        return mDecl != null ? mDecl.size() : 0;
    }

    public boolean hasDecl() {
        return mDecl != null;
    }

    /**
     * List of declarations in the program file. Same order as in the thrift file.
     *
     * @return The field value
     */
    public java.util.List<net.morimekta.providence.model.Declaration> getDecl() {
        return mDecl;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasDocumentation();
            case 2: return hasProgramName();
            case 3: return numIncludes() > 0;
            case 4: return numNamespaces() > 0;
            case 5: return numDecl() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDocumentation() ? 1 : 0;
            case 2: return hasProgramName() ? 1 : 0;
            case 3: return numIncludes();
            case 4: return numNamespaces();
            case 5: return numDecl();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getDocumentation();
            case 2: return getProgramName();
            case 3: return getIncludes();
            case 4: return getNamespaces();
            case 5: return getDecl();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof ProgramType)) return false;
        ProgramType other = (ProgramType) o;
        return java.util.Objects.equals(mDocumentation, other.mDocumentation) &&
               java.util.Objects.equals(mProgramName, other.mProgramName) &&
               java.util.Objects.equals(mIncludes, other.mIncludes) &&
               java.util.Objects.equals(mNamespaces, other.mNamespaces) &&
               java.util.Objects.equals(mDecl, other.mDecl);
    }

    @Override
    public int hashCode() {
        if (tHashCode == 0) {
            tHashCode = java.util.Objects.hash(
                    ProgramType.class,
                    _Field.DOCUMENTATION, mDocumentation,
                    _Field.PROGRAM_NAME, mProgramName,
                    _Field.INCLUDES, mIncludes,
                    _Field.NAMESPACES, mNamespaces,
                    _Field.DECL, mDecl);
        }
        return tHashCode;
    }

    @Override
    public String toString() {
        return "model.ProgramType" + asString();
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder();
        out.append("{");

        boolean first = true;
        if (mDocumentation != null) {
            first = false;
            out.append("documentation:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mDocumentation))
               .append('\"');
        }
        if (mProgramName != null) {
            if (first) first = false;
            else out.append(',');
            out.append("program_name:")
               .append('\"')
               .append(net.morimekta.util.Strings.escape(mProgramName))
               .append('\"');
        }
        if (mIncludes != null && mIncludes.size() > 0) {
            if (first) first = false;
            else out.append(',');
            out.append("includes:")
               .append(net.morimekta.util.Strings.asString(mIncludes));
        }
        if (mNamespaces != null && mNamespaces.size() > 0) {
            if (first) first = false;
            else out.append(',');
            out.append("namespaces:")
               .append(net.morimekta.util.Strings.asString(mNamespaces));
        }
        if (mDecl != null && mDecl.size() > 0) {
            if (!first) out.append(',');
            out.append("decl:")
               .append(net.morimekta.util.Strings.asString(mDecl));
        }
        out.append('}');
        return out.toString();
    }

    @Override
    public int compareTo(ProgramType other) {
        int c;

        c = Boolean.compare(mDocumentation != null, other.mDocumentation != null);
        if (c != 0) return c;
        if (mDocumentation != null) {
            c = mDocumentation.compareTo(other.mDocumentation);
            if (c != 0) return c;
        }

        c = Boolean.compare(mProgramName != null, other.mProgramName != null);
        if (c != 0) return c;
        if (mProgramName != null) {
            c = mProgramName.compareTo(other.mProgramName);
            if (c != 0) return c;
        }

        c = Boolean.compare(mIncludes != null, other.mIncludes != null);
        if (c != 0) return c;
        if (mIncludes != null) {
            c = Integer.compare(mIncludes.hashCode(), other.mIncludes.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mNamespaces != null, other.mNamespaces != null);
        if (c != 0) return c;
        if (mNamespaces != null) {
            c = Integer.compare(mNamespaces.hashCode(), other.mNamespaces.hashCode());
            if (c != 0) return c;
        }

        c = Boolean.compare(mDecl != null, other.mDecl != null);
        if (c != 0) return c;
        if (mDecl != null) {
            c = Integer.compare(mDecl.hashCode(), other.mDecl.hashCode());
            if (c != 0) return c;
        }

        return 0;
    }

    @Override
    public _Builder mutate() {
        return new _Builder(this);
    }

    public enum _Field implements net.morimekta.providence.descriptor.PField {
        DOCUMENTATION(1, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "documentation", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        PROGRAM_NAME(2, net.morimekta.providence.descriptor.PRequirement.REQUIRED, "program_name", net.morimekta.providence.descriptor.PPrimitive.STRING.provider(), null),
        INCLUDES(3, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "includes", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
        NAMESPACES(4, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "namespaces", net.morimekta.providence.descriptor.PMap.provider(net.morimekta.providence.descriptor.PPrimitive.STRING.provider(),net.morimekta.providence.descriptor.PPrimitive.STRING.provider()), null),
        DECL(5, net.morimekta.providence.descriptor.PRequirement.DEFAULT, "decl", net.morimekta.providence.descriptor.PList.provider(net.morimekta.providence.model.Declaration.provider()), null),
        ;

        private final int mKey;
        private final net.morimekta.providence.descriptor.PRequirement mRequired;
        private final String mName;
        private final net.morimekta.providence.descriptor.PDescriptorProvider mTypeProvider;
        private final net.morimekta.providence.descriptor.PValueProvider<?> mDefaultValue;

        _Field(int key, net.morimekta.providence.descriptor.PRequirement required, String name, net.morimekta.providence.descriptor.PDescriptorProvider typeProvider, net.morimekta.providence.descriptor.PValueProvider<?> defaultValue) {
            mKey = key;
            mRequired = required;
            mName = name;
            mTypeProvider = typeProvider;
            mDefaultValue = defaultValue;
        }

        @Override
        public int getKey() { return mKey; }

        @Override
        public net.morimekta.providence.descriptor.PRequirement getRequirement() { return mRequired; }

        @Override
        public net.morimekta.providence.descriptor.PDescriptor getDescriptor() { return mTypeProvider.descriptor(); }

        @Override
        public String getName() { return mName; }

        @Override
        public boolean hasDefaultValue() { return mDefaultValue != null; }

        @Override
        public Object getDefaultValue() {
            return hasDefaultValue() ? mDefaultValue.get() : null;
        }

        @Override
        public String toString() {
            return net.morimekta.providence.descriptor.PField.toString(this);
        }

        public static _Field forKey(int key) {
            switch (key) {
                case 1: return _Field.DOCUMENTATION;
                case 2: return _Field.PROGRAM_NAME;
                case 3: return _Field.INCLUDES;
                case 4: return _Field.NAMESPACES;
                case 5: return _Field.DECL;
            }
            return null;
        }

        public static _Field forName(String name) {
            switch (name) {
                case "documentation": return _Field.DOCUMENTATION;
                case "program_name": return _Field.PROGRAM_NAME;
                case "includes": return _Field.INCLUDES;
                case "namespaces": return _Field.NAMESPACES;
                case "decl": return _Field.DECL;
            }
            return null;
        }
    }

    public static net.morimekta.providence.descriptor.PStructDescriptorProvider<ProgramType,_Field> provider() {
        return new _Provider();
    }

    @Override
    public net.morimekta.providence.descriptor.PStructDescriptor<ProgramType,_Field> descriptor() {
        return kDescriptor;
    }

    public static final net.morimekta.providence.descriptor.PStructDescriptor<ProgramType,_Field> kDescriptor;

    private static class _Descriptor
            extends net.morimekta.providence.descriptor.PStructDescriptor<ProgramType,_Field> {
        public _Descriptor() {
            super("model", "ProgramType", new _Factory(), false, false);
        }

        @Override
        public _Field[] getFields() {
            return _Field.values();
        }

        @Override
        public _Field getField(String name) {
            return _Field.forName(name);
        }

        @Override
        public _Field getField(int key) {
            return _Field.forKey(key);
        }
    }

    static {
        kDescriptor = new _Descriptor();
    }

    private final static class _Provider extends net.morimekta.providence.descriptor.PStructDescriptorProvider<ProgramType,_Field> {
        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ProgramType,_Field> descriptor() {
            return kDescriptor;
        }
    }

    private final static class _Factory
            extends net.morimekta.providence.PMessageBuilderFactory<ProgramType,_Field> {
        @Override
        public _Builder builder() {
            return new _Builder();
        }
    }

    /**
     * Make a model.ProgramType builder.
     * @return The builder instance.
     */
    public static _Builder builder() {
        return new _Builder();
    }

    /**
     * &lt;namespace&gt;* &lt;include&gt;* &lt;declataion&gt;*
     */
    public static class _Builder
            extends net.morimekta.providence.PMessageBuilder<ProgramType,_Field> {
        private java.util.BitSet optionals;

        private String mDocumentation;
        private String mProgramName;
        private net.morimekta.providence.descriptor.PList.Builder<String> mIncludes;
        private net.morimekta.providence.descriptor.PMap.Builder<String,String> mNamespaces;
        private net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.Declaration> mDecl;

        /**
         * Make a model.ProgramType builder.
         */
        public _Builder() {
            optionals = new java.util.BitSet(5);
            mIncludes = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
            mNamespaces = new net.morimekta.providence.descriptor.PMap.ImmutableMapBuilder<>();
            mDecl = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<>();
        }

        /**
         * Make a mutating builder off a base model.ProgramType.
         *
         * @param base The base ProgramType
         */
        public _Builder(ProgramType base) {
            this();

            if (base.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = base.mDocumentation;
            }
            if (base.hasProgramName()) {
                optionals.set(1);
                mProgramName = base.mProgramName;
            }
            if (base.numIncludes() > 0) {
                optionals.set(2);
                mIncludes.addAll(base.mIncludes);
            }
            if (base.numNamespaces() > 0) {
                optionals.set(3);
                mNamespaces.putAll(base.mNamespaces);
            }
            if (base.numDecl() > 0) {
                optionals.set(4);
                mDecl.addAll(base.mDecl);
            }
        }

        @Override
        public _Builder merge(ProgramType from) {
            if (from.hasDocumentation()) {
                optionals.set(0);
                mDocumentation = from.getDocumentation();
            }

            if (from.hasProgramName()) {
                optionals.set(1);
                mProgramName = from.getProgramName();
            }

            if (from.hasIncludes()) {
                optionals.set(2);
                mIncludes.clear();
                mIncludes.addAll(from.getIncludes());
            }

            if (from.hasNamespaces()) {
                optionals.set(3);
                mNamespaces.putAll(from.getNamespaces());
            }

            if (from.hasDecl()) {
                optionals.set(4);
                mDecl.clear();
                mDecl.addAll(from.getDecl());
            }
            return this;
        }

        /**
         * Program documentation must come before the first statement of the header.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDocumentation(String value) {
            optionals.set(0);
            mDocumentation = value;
            return this;
        }

        /**
         * Program documentation must come before the first statement of the header.
         *
         * @return True iff documentation has been set.
         */
        public boolean isSetDocumentation() {
            return optionals.get(0);
        }

        /**
         * Program documentation must come before the first statement of the header.
         *
         * @return The builder
         */
        public _Builder clearDocumentation() {
            optionals.clear(0);
            mDocumentation = null;
            return this;
        }

        /**
         * The program name, deducted from the .thrift IDL file name.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setProgramName(String value) {
            optionals.set(1);
            mProgramName = value;
            return this;
        }

        /**
         * The program name, deducted from the .thrift IDL file name.
         *
         * @return True iff program_name has been set.
         */
        public boolean isSetProgramName() {
            return optionals.get(1);
        }

        /**
         * The program name, deducted from the .thrift IDL file name.
         *
         * @return The builder
         */
        public _Builder clearProgramName() {
            optionals.clear(1);
            mProgramName = null;
            return this;
        }

        /**
         * List of included thrift files. Same as from the actual thrift file.
         * <p>
         * include &quot;&lt;program&gt;.thrift&quot;
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setIncludes(java.util.Collection<String> value) {
            optionals.set(2);
            mIncludes.clear();
            mIncludes.addAll(value);
            return this;
        }

        /**
         * List of included thrift files. Same as from the actual thrift file.
         * <p>
         * include &quot;&lt;program&gt;.thrift&quot;
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToIncludes(String... values) {
            optionals.set(2);
            for (String item : values) {
                mIncludes.add(item);
            }
            return this;
        }

        /**
         * List of included thrift files. Same as from the actual thrift file.
         * <p>
         * include &quot;&lt;program&gt;.thrift&quot;
         *
         * @return True iff includes has been set.
         */
        public boolean isSetIncludes() {
            return optionals.get(2);
        }

        /**
         * List of included thrift files. Same as from the actual thrift file.
         * <p>
         * include &quot;&lt;program&gt;.thrift&quot;
         *
         * @return The builder
         */
        public _Builder clearIncludes() {
            optionals.clear(2);
            mIncludes.clear();
            return this;
        }

        /**
         * List of included thrift files. Same as from the actual thrift file.
         * <p>
         * include &quot;&lt;program&gt;.thrift&quot;
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<String> mutableIncludes() {
            optionals.set(2);
            return mIncludes;
        }

        /**
         * Map of language to laguage dependent namespace identifier.
         * <p>
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setNamespaces(java.util.Map<String,String> value) {
            optionals.set(3);
            mNamespaces.clear();
            mNamespaces.putAll(value);
            return this;
        }

        /**
         * Map of language to laguage dependent namespace identifier.
         * <p>
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @param key The inserted key
         * @param value The inserted value
         * @return The builder
         */
        public _Builder putInNamespaces(String key, String value) {
            optionals.set(3);
            mNamespaces.put(key, value);
            return this;
        }

        /**
         * Map of language to laguage dependent namespace identifier.
         * <p>
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @return True iff namespaces has been set.
         */
        public boolean isSetNamespaces() {
            return optionals.get(3);
        }

        /**
         * Map of language to laguage dependent namespace identifier.
         * <p>
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @return The builder
         */
        public _Builder clearNamespaces() {
            optionals.clear(3);
            mNamespaces.clear();
            return this;
        }

        /**
         * Map of language to laguage dependent namespace identifier.
         * <p>
         * namespace &lt;key&gt; &lt;value&gt;
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PMap.Builder<String,String> mutableNamespaces() {
            optionals.set(3);
            return mNamespaces;
        }

        /**
         * List of declarations in the program file. Same order as in the thrift file.
         *
         * @param value The new value
         * @return The builder
         */
        public _Builder setDecl(java.util.Collection<net.morimekta.providence.model.Declaration> value) {
            optionals.set(4);
            mDecl.clear();
            mDecl.addAll(value);
            return this;
        }

        /**
         * List of declarations in the program file. Same order as in the thrift file.
         *
         * @param values The added value
         * @return The builder
         */
        public _Builder addToDecl(net.morimekta.providence.model.Declaration... values) {
            optionals.set(4);
            for (net.morimekta.providence.model.Declaration item : values) {
                mDecl.add(item);
            }
            return this;
        }

        /**
         * List of declarations in the program file. Same order as in the thrift file.
         *
         * @return True iff decl has been set.
         */
        public boolean isSetDecl() {
            return optionals.get(4);
        }

        /**
         * List of declarations in the program file. Same order as in the thrift file.
         *
         * @return The builder
         */
        public _Builder clearDecl() {
            optionals.clear(4);
            mDecl.clear();
            return this;
        }

        /**
         * List of declarations in the program file. Same order as in the thrift file.
         *
         * @return The field builder
         */
        public net.morimekta.providence.descriptor.PList.Builder<net.morimekta.providence.model.Declaration> mutableDecl() {
            optionals.set(4);
            return mDecl;
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.morimekta.providence.PMessageBuilder mutator(int key) {
            switch (key) {
                default: throw new IllegalArgumentException("Not a message field ID: " + key);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public _Builder set(int key, Object value) {
            if (value == null) return clear(key);
            switch (key) {
                case 1: setDocumentation((String) value); break;
                case 2: setProgramName((String) value); break;
                case 3: setIncludes((java.util.List<String>) value); break;
                case 4: setNamespaces((java.util.Map<String,String>) value); break;
                case 5: setDecl((java.util.List<net.morimekta.providence.model.Declaration>) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public _Builder addTo(int key, Object value) {
            switch (key) {
                case 3: addToIncludes((String) value); break;
                case 5: addToDecl((net.morimekta.providence.model.Declaration) value); break;
                default: break;
            }
            return this;
        }

        @Override
        public _Builder clear(int key) {
            switch (key) {
                case 1: clearDocumentation(); break;
                case 2: clearProgramName(); break;
                case 3: clearIncludes(); break;
                case 4: clearNamespaces(); break;
                case 5: clearDecl(); break;
                default: break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return optionals.get(1);
        }

        @Override
        public void validate() {
            if (!isValid()) {
                java.util.LinkedList<String> missing = new java.util.LinkedList<>();

                if (!optionals.get(1)) {
                    missing.add("program_name");
                }

                throw new java.lang.IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message model.ProgramType");
            }
        }

        @Override
        public net.morimekta.providence.descriptor.PStructDescriptor<ProgramType,_Field> descriptor() {
            return kDescriptor;
        }

        @Override
        public ProgramType build() {
            return new ProgramType(this);
        }
    }
}