package org.apache.thrift.j2.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TList;
import org.apache.thrift.j2.descriptor.TMap;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.util.TTypeUtils;

/** <namespace>* <include>* <declataion>* */
public class ThriftDocument
        implements TMessage<ThriftDocument>, Serializable {
    private final String mComment;
    private final String mPackage;
    private final List<String> mIncludes;
    private final Map<String,String> mNamespaces;
    private final List<Declaration> mDecl;

    private ThriftDocument(Builder builder) {
        mComment = builder.mComment;
        mPackage = builder.mPackage;
        mIncludes = Collections.unmodifiableList(new LinkedList<>(builder.mIncludes));
        mNamespaces = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mNamespaces));
        mDecl = Collections.unmodifiableList(new LinkedList<>(builder.mDecl));
    }

    public boolean hasComment() {
        return mComment != null;
    }

    /** Must come before the first statement of the header. */
    public String getComment() {
        return mComment;
    }

    public boolean hasPackage() {
        return mPackage != null;
    }

    /** Deducted from filename in .thrift IDL files. */
    public String getPackage() {
        return mPackage;
    }

    public int numIncludes() {
        return mIncludes.size();
    }

    /** include "<package>.thrift" */
    public List<String> getIncludes() {
        return mIncludes;
    }

    public int numNamespaces() {
        return mNamespaces.size();
    }

    /** namespace <key> <value> */
    public Map<String,String> getNamespaces() {
        return mNamespaces;
    }

    public int numDecl() {
        return mDecl.size();
    }

    public List<Declaration> getDecl() {
        return mDecl;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasPackage();
            case 3: return numIncludes() > 0;
            case 4: return numNamespaces() > 0;
            case 5: return numDecl() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasPackage() ? 1 : 0;
            case 3: return numIncludes();
            case 4: return numNamespaces();
            case 5: return numDecl();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getPackage();
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
        if (o == null || !(o instanceof ThriftDocument)) return false;
        ThriftDocument other = (ThriftDocument) o;
        return TTypeUtils.equals(mComment, other.mComment) &&
               TTypeUtils.equals(mPackage, other.mPackage) &&
               TTypeUtils.equals(mIncludes, other.mIncludes) &&
               TTypeUtils.equals(mNamespaces, other.mNamespaces) &&
               TTypeUtils.equals(mDecl, other.mDecl);
    }

    @Override
    public int hashCode() {
        return ThriftDocument.class.hashCode() +
               TTypeUtils.hashCode(mComment) +
               TTypeUtils.hashCode(mPackage) +
               TTypeUtils.hashCode(mIncludes) +
               TTypeUtils.hashCode(mNamespaces) +
               TTypeUtils.hashCode(mDecl);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mPackage != null;
    }

    @Override
    public TStructDescriptor<ThriftDocument> descriptor() {
        return DESCRIPTOR;
    }

    public static final TStructDescriptor<ThriftDocument> DESCRIPTOR = _createDescriptor();

    private final static class _Factory
            extends TMessageBuilderFactory<ThriftDocument> {
        @Override
        public ThriftDocument.Builder builder() {
            return new ThriftDocument.Builder();
        }
    }

    private static TStructDescriptor<ThriftDocument> _createDescriptor() {
        List<TField<?>> fieldList = new LinkedList<>();
        fieldList.add(new TField<>(null, 1, false, "comment", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 2, true, "package", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 3, false, "includes", TList.provider(TPrimitive.STRING.provider()), null));
        fieldList.add(new TField<>(null, 4, false, "namespaces", TMap.provider(TPrimitive.STRING.provider(),TPrimitive.STRING.provider()), null));
        fieldList.add(new TField<>(null, 5, false, "decl", TList.provider(Declaration.provider()), null));
        return new TStructDescriptor<>(null, "model", "ThriftDocument", fieldList, new _Factory(), false);
    }

    public static TStructDescriptorProvider<ThriftDocument> provider() {
        return new TStructDescriptorProvider<ThriftDocument>() {
            @Override
            public TStructDescriptor<ThriftDocument> descriptor() {
                return DESCRIPTOR;
            }
        };
    }

    @Override
    public ThriftDocument.Builder mutate() {
        return new ThriftDocument.Builder(this);
    }

    public static ThriftDocument.Builder builder() {
        return new ThriftDocument.Builder();
    }

    public static class Builder
            extends TMessageBuilder<ThriftDocument> {
        private String mComment;
        private String mPackage;
        private List<String> mIncludes;
        private Map<String,String> mNamespaces;
        private List<Declaration> mDecl;

        public Builder() {
            mIncludes = new LinkedList<>();
            mNamespaces = new LinkedHashMap<>();
            mDecl = new LinkedList<>();
        }

        public Builder(ThriftDocument base) {
            this();

            mComment = base.mComment;
            mPackage = base.mPackage;
            mIncludes.addAll(base.mIncludes);
            mNamespaces.putAll(base.mNamespaces);
            mDecl.addAll(base.mDecl);
        }

        /** Must come before the first statement of the header. */
        public Builder setComment(String value) {
            mComment = value;
            return this;
        }

        public Builder clearComment() {
            mComment = null;
            return this;
        }

        /** Deducted from filename in .thrift IDL files. */
        public Builder setPackage(String value) {
            mPackage = value;
            return this;
        }

        public Builder clearPackage() {
            mPackage = null;
            return this;
        }

        /** include "<package>.thrift" */
        public Builder setIncludes(Collection<String> value) {
            mIncludes.clear();
            mIncludes.addAll(value);
            return this;
        }

        /** include "<package>.thrift" */
        public Builder addToIncludes(String... values) {
            for (String item : values) {
                mIncludes.add(item);
            }
            return this;
        }

        public Builder clearIncludes() {
            mIncludes.clear();
            return this;
        }

        /** namespace <key> <value> */
        public Builder setNamespaces(Map<String,String> value) {
            mNamespaces.clear();
            mNamespaces.putAll(value);
            return this;
        }

        /** namespace <key> <value> */
        public Builder addToNamespaces(String key, String value) {
            mNamespaces.put(key, value);
            return this;
        }

        public Builder clearNamespaces() {
            mNamespaces.clear();
            return this;
        }

        public Builder setDecl(Collection<Declaration> value) {
            mDecl.clear();
            mDecl.addAll(value);
            return this;
        }

        public Builder addToDecl(Declaration... values) {
            for (Declaration item : values) {
                mDecl.add(item);
            }
            return this;
        }

        public Builder clearDecl() {
            mDecl.clear();
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setPackage((String) value); break;
                case 3: setIncludes((List<String>) value); break;
                case 4: setNamespaces((Map<String,String>) value); break;
                case 5: setDecl((List<Declaration>) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mPackage != null;
        }

        @Override
        public ThriftDocument build() {
            return new ThriftDocument(this);
        }
    }
}