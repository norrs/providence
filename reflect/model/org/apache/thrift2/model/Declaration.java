package org.apache.thrift2.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift2.TMessage;
import org.apache.thrift2.TMessageBuilder;
import org.apache.thrift2.TMessageBuilderFactory;
import org.apache.thrift2.descriptor.TField;
import org.apache.thrift2.descriptor.TUnionDescriptor;
import org.apache.thrift2.descriptor.TUnionDescriptorProvider;
import org.apache.thrift2.util.TTypeUtils;

/** ( <enum> | <typedef> | <struct> | <service> | <const> ) */
public class Declaration
        implements TMessage<Declaration>, Serializable {
    private final EnumType mDeclEnum;
    private final TypedefType mDeclTypedef;
    private final StructType mDeclStruct;
    private final ServiceType mDeclService;
    private final ThriftField mDeclConst;

    private Declaration(Builder builder) {
        mDeclEnum = builder.mDeclEnum;
        mDeclTypedef = builder.mDeclTypedef;
        mDeclStruct = builder.mDeclStruct;
        mDeclService = builder.mDeclService;
        mDeclConst = builder.mDeclConst;
    }

    public boolean hasDeclEnum() {
        return mDeclEnum != null;
    }

    public EnumType getDeclEnum() {
        return mDeclEnum;
    }

    public boolean hasDeclTypedef() {
        return mDeclTypedef != null;
    }

    public TypedefType getDeclTypedef() {
        return mDeclTypedef;
    }

    public boolean hasDeclStruct() {
        return mDeclStruct != null;
    }

    public StructType getDeclStruct() {
        return mDeclStruct;
    }

    public boolean hasDeclService() {
        return mDeclService != null;
    }

    public ServiceType getDeclService() {
        return mDeclService;
    }

    public boolean hasDeclConst() {
        return mDeclConst != null;
    }

    public ThriftField getDeclConst() {
        return mDeclConst;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasDeclEnum();
            case 2: return hasDeclTypedef();
            case 3: return hasDeclStruct();
            case 4: return hasDeclService();
            case 5: return hasDeclConst();
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasDeclEnum() ? 1 : 0;
            case 2: return hasDeclTypedef() ? 1 : 0;
            case 3: return hasDeclStruct() ? 1 : 0;
            case 4: return hasDeclService() ? 1 : 0;
            case 5: return hasDeclConst() ? 1 : 0;
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getDeclEnum();
            case 2: return getDeclTypedef();
            case 3: return getDeclStruct();
            case 4: return getDeclService();
            case 5: return getDeclConst();
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Declaration)) return false;
        Declaration other = (Declaration) o;
        return TTypeUtils.equals(mDeclEnum, other.mDeclEnum) &&
               TTypeUtils.equals(mDeclTypedef, other.mDeclTypedef) &&
               TTypeUtils.equals(mDeclStruct, other.mDeclStruct) &&
               TTypeUtils.equals(mDeclService, other.mDeclService) &&
               TTypeUtils.equals(mDeclConst, other.mDeclConst);
    }

    @Override
    public int hashCode() {
        return Declaration.class.hashCode() +
               TTypeUtils.hashCode(mDeclEnum) +
               TTypeUtils.hashCode(mDeclTypedef) +
               TTypeUtils.hashCode(mDeclStruct) +
               TTypeUtils.hashCode(mDeclService) +
               TTypeUtils.hashCode(mDeclConst);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return (mDeclEnum != null ? 1 : 0) +
               (mDeclTypedef != null ? 1 : 0) +
               (mDeclStruct != null ? 1 : 0) +
               (mDeclService != null ? 1 : 0) +
               (mDeclConst != null ? 1 : 0) == 1;
    }

    @Override
    public TUnionDescriptor<Declaration> descriptor() {
        return DESCRIPTOR;
    }

    public static final TUnionDescriptor<Declaration> DESCRIPTOR = _createDescriptor();

    private final static class _Factory
            extends TMessageBuilderFactory<Declaration> {
        @Override
        public Declaration.Builder builder() {
            return new Declaration.Builder();
        }
    }

    private static TUnionDescriptor<Declaration> _createDescriptor() {
        List<TField<?>> fieldList = new LinkedList<>();
        fieldList.add(new TField<>(null, 1, false, "decl_enum", EnumType.provider(), null));
        fieldList.add(new TField<>(null, 2, false, "decl_typedef", TypedefType.provider(), null));
        fieldList.add(new TField<>(null, 3, false, "decl_struct", StructType.provider(), null));
        fieldList.add(new TField<>(null, 4, false, "decl_service", ServiceType.provider(), null));
        fieldList.add(new TField<>(null, 5, false, "decl_const", ThriftField.provider(), null));
        return new TUnionDescriptor<>(null, "model", "Declaration", fieldList, new _Factory());
    }

    public static TUnionDescriptorProvider<Declaration> provider() {
        return new TUnionDescriptorProvider<Declaration>() {
            @Override
            public TUnionDescriptor<Declaration> descriptor() {
                return DESCRIPTOR;
            }
        };
    }

    @Override
    public Declaration.Builder mutate() {
        return new Declaration.Builder(this);
    }

    public static Declaration.Builder builder() {
        return new Declaration.Builder();
    }

    public static class Builder
            extends TMessageBuilder<Declaration> {
        private EnumType mDeclEnum;
        private TypedefType mDeclTypedef;
        private StructType mDeclStruct;
        private ServiceType mDeclService;
        private ThriftField mDeclConst;

        public Builder() {
        }

        public Builder(Declaration base) {
            this();

            mDeclEnum = base.mDeclEnum;
            mDeclTypedef = base.mDeclTypedef;
            mDeclStruct = base.mDeclStruct;
            mDeclService = base.mDeclService;
            mDeclConst = base.mDeclConst;
        }

        public Builder setDeclEnum(EnumType value) {
            mDeclEnum = value;
            return this;
        }

        public Builder clearDeclEnum() {
            mDeclEnum = null;
            return this;
        }

        public Builder setDeclTypedef(TypedefType value) {
            mDeclTypedef = value;
            return this;
        }

        public Builder clearDeclTypedef() {
            mDeclTypedef = null;
            return this;
        }

        public Builder setDeclStruct(StructType value) {
            mDeclStruct = value;
            return this;
        }

        public Builder clearDeclStruct() {
            mDeclStruct = null;
            return this;
        }

        public Builder setDeclService(ServiceType value) {
            mDeclService = value;
            return this;
        }

        public Builder clearDeclService() {
            mDeclService = null;
            return this;
        }

        public Builder setDeclConst(ThriftField value) {
            mDeclConst = value;
            return this;
        }

        public Builder clearDeclConst() {
            mDeclConst = null;
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setDeclEnum((EnumType) value); break;
                case 2: setDeclTypedef((TypedefType) value); break;
                case 3: setDeclStruct((StructType) value); break;
                case 4: setDeclService((ServiceType) value); break;
                case 5: setDeclConst((ThriftField) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return (mDeclEnum != null ? 1 : 0) +
                   (mDeclTypedef != null ? 1 : 0) +
                   (mDeclStruct != null ? 1 : 0) +
                   (mDeclService != null ? 1 : 0) +
                   (mDeclConst != null ? 1 : 0) == 1;
        }

        @Override
        public Declaration build() {
            return new Declaration(this);
        }
    }
}