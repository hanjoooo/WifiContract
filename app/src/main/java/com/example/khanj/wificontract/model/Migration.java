package com.example.khanj.wificontract.model;

import android.util.Log;

import com.example.khanj.wificontract.model.WifiAssetModel;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        Log.d("SchemaVersion", Long.toString(oldVersion));
        if (oldVersion == 0) {
            schema.create("WifiAssetModel")
                    .addField("macAddress", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("ssid", String.class)
                    .addField("owner", String.class);

            oldVersion++;
        }
    }
}
