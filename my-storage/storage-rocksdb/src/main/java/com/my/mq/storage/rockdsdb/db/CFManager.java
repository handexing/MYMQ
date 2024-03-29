package com.my.mq.storage.rockdsdb.db;

import com.google.common.base.Charsets;
import com.my.mq.storage.rockdsdb.enums.CFHandlerNames;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;

import java.util.ArrayList;
import java.util.List;


public class CFManager {

    private static ColumnFamilyHandle cfhDefault;

    static final List<ColumnFamilyDescriptor> CF_DESCRIPTORS = new ArrayList<>();
    static final List<ColumnFamilyHandle> CF_HANDLES = new ArrayList<>();

    static {
        CF_DESCRIPTORS.add(new ColumnFamilyDescriptor(CFHandlerNames.DEFAULT.getName().getBytes(Charsets.UTF_8),
                OptionsConfig.COLUMN_FAMILY_OPTIONS_DEFAULT));
        CF_DESCRIPTORS.add(new ColumnFamilyDescriptor(CFHandlerNames.META.getName().getBytes(Charsets.UTF_8),
                OptionsConfig.COLUMN_FAMILY_OPTIONS_DEFAULT));
    }

//    static void initCFManger() {
//        cfhDefault = CF_HANDLES.get(CFHandlerNames.DEFAULT.ordinal());
//    }

    public static ColumnFamilyHandle getColumnFamilyHandle(CFHandlerNames type) {
        return  cfhDefault = CF_HANDLES.get(type.ordinal());
    }


}
